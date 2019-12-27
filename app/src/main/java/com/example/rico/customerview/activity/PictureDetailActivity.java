package com.example.rico.customerview.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.example.rico.customerview.R;
import com.example.rico.customerview.adapter.PointAdapter;
import com.example.rico.customerview.adapter.ViewPagerAdapter;
import com.example.rico.customerview.bean.MyRect;
import com.example.rico.customerview.bean.PicInfoBean;
import com.example.rico.customerview.fragment.ImageFragment;

import java.util.ArrayList;

/**
 * Created by Tmp on 2019/11/4.
 */
public class PictureDetailActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, ImageFragment.PicLayoutChange {
    PicInfoBean bean;
    RecyclerView rvPoint;
    PointAdapter pointAdapter;

    View contentView;
    ViewPager ivPager;
    ViewPagerAdapter pagerAdapter;
    ArrayList<Fragment> fragments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StatusBarUtil.setTransparent(this);
        setContentView(bindLayout());
        doBusiness();
    }

    public int bindLayout() {
        return R.layout.activity_picture_detail;
    }

    public void doBusiness() {
        rvPoint = findViewById(R.id.rv_point);
        pointAdapter = new PointAdapter(this);
        bean = (PicInfoBean) getIntent().getSerializableExtra("picInfoBean");
        lastPosition = bean.getPosition();

        rvPoint.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        for (int i = 0; i < bean.getStrings().size(); i++) {
            pointAdapter.addItem(bean.getPosition() == i);
        }
        rvPoint.setAdapter(pointAdapter);
        fragments = new ArrayList<>();
        for (int i = 0; i < bean.getStrings().size(); i++) {
            ImageFragment fragment = new ImageFragment();
            Bundle bundle = new Bundle();
            bundle.putString("url", bean.getStrings().get(i));
            fragment.setArguments(bundle);
            fragment.setChange(this);
            fragments.add(fragment);
        }
        ivPager = findViewById(R.id.iv_pager);
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments);
        ivPager.setAdapter(pagerAdapter);
        colorDrawable = new ColorDrawable(Color.BLACK);
        contentView = findViewById(android.R.id.content);
        contentView.setBackground(colorDrawable);
        ivPager.setCurrentItem(bean.getPosition());
        ivPager.addOnPageChangeListener(this);
        enterAnimation(ivPager);
    }

    int lastPosition;
    int DURATION_IN = 300, DURATION_OUT = 300, CLIP_DURATION = 300;
    ColorDrawable colorDrawable;
    float originScale;
    int width, height;
    float translateX, translateY;
    // 显示图片的rect从方形到适应屏幕
    Rect clipRect;

    // 裁剪的类别，宽还是高
    int type = 0;
    // 裁剪的数值 初始到最终
    int clipSize = 0;
    ValueAnimator enterAnimator;
    int lastClipValue;

    private void enterAnimation(View view) {
        colorDrawable.setAlpha(0);
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        width = point.x;
        height = point.y;
        MyRect myRect = bean.getMyRectList().get(lastPosition);
        // 图片的宽高比
        float widthHeightScale = myRect.getWidthHeightScale();
        float bigScale = 1f;
        if (widthHeightScale > 1f) {
            bigScale = widthHeightScale - 1;
        } else if (widthHeightScale < 1f) {
            bigScale = 1 - widthHeightScale;
        }
        // 图片显示要铺满宽度
        originScale = myRect.getWidth() / (float) width;
        translateX = myRect.getLeft();
        translateY = myRect.getTop() - (height - width) / 2f * originScale;
        if (widthHeightScale > 1f) {
            // 此时图片宽度大于高度
            originScale *= widthHeightScale;
            translateX -= myRect.getWidth() * (bigScale) / 2;
            translateY = myRect.getTop() - (height - width / widthHeightScale) / 2f * originScale;
        } else if (widthHeightScale < 1f) {
            // 此时图片宽度小于高度
            // 图片显示的宽度
            float scaleWidth = height * originScale * widthHeightScale;
            // 图片显示的宽度小于myRect的宽度
            if (scaleWidth <= myRect.getWidth()) {
                float s = myRect.getWidth() / scaleWidth;
                originScale *= s;
                // originScale可能有两种情况 大于1或者小于等于1
                // 大于1的情况是图片很长，高度适配height的时候宽度比myRect宽度都要小
                translateX = myRect.getLeft() - (width - height * widthHeightScale) / 2 * originScale;
                translateY = myRect.getTop() - (height - height * widthHeightScale) / 2 * originScale;
            }
        }
        //缩放view
        view.setPivotX(0);
        view.setPivotY(0);
        view.setTranslationX(translateX);
        view.setTranslationY(translateY);
        float realScale = originScale;
        view.setScaleX(realScale);
        view.setScaleY(realScale);

        // 裁剪显示区域
        clipRect = new Rect();
        //裁剪宽度
        if (widthHeightScale > 1) {
            type = 1;
            clipSize = (int) (width - width / widthHeightScale) / 2;
            clipRect.set((int) (width - width / widthHeightScale) / 2, 0, (int) (width + width / widthHeightScale) / 2, height);
            view.setClipBounds(clipRect);
            // 裁剪高度
        } else if (widthHeightScale < 1) {
            type = 2;
            if (height * widthHeightScale > width) {
                clipSize = ((height - width) / 2);
                clipRect.set(0, ((height - width) / 2), width, ((height + width) / 2));
            } else {
                clipSize = (int) ((height - width * widthHeightScale) / 2);
                clipRect.set(0, (int) ((height - width * widthHeightScale) / 2), width, (int) ((height + height * widthHeightScale) / 2));
            }
            view.setClipBounds(clipRect);
        }
        if (clipSize != 0) {
            enterAnimator = ValueAnimator.ofInt(clipSize);
            enterAnimator.setDuration(CLIP_DURATION);
            enterAnimator.addUpdateListener(animation -> {
                int value = (int) animation.getAnimatedValue();
                int left = clipRect.left;
                int top = clipRect.top;
                int right = clipRect.right;
                int bottom = clipRect.bottom;
                switch (type) {
                    case 1:
                        left -= value-lastClipValue;
                        right += value-lastClipValue;
                        break;
                    case 2:
                        top -= value-lastClipValue;
                        bottom += value-lastClipValue;
                        break;
                }
                clipRect.set(left, top, right, bottom);
                Log.e("clipRect", "enterAnimation: "+clipRect.toString() );
                view.setClipBounds(clipRect);
                lastClipValue=value;
            });
            enterAnimator.setInterpolator(new DecelerateInterpolator());
            enterAnimator.start();
        }
        //设置动画
        TimeInterpolator sDecelerator = new DecelerateInterpolator();
        //设置imageView缩放动画
        view.animate().setDuration(DURATION_IN)
                .scaleX(1).scaleY(1)
                .translationX(0).translationY(0)
                .setInterpolator(sDecelerator);

        // 设置activity主布局背景颜色DURATION毫秒内透明度从透明到不透明
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(colorDrawable, "alpha", 0, 255);
        bgAnim.setDuration(DURATION_IN);
        bgAnim.start();
        bgAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rvPoint.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void finish() {
        if (isAnimationEnd) {
            setResult(RESULT_OK);
            super.finish();
            overridePendingTransition(0, 0);
        } else {
            exitAnimation(ivPager);
        }
    }


    boolean isAnimationEnd;

    // 默认退出动画
    public void exitAnimation(View view) {
        if (bean == null) {
            return;
        }
        rvPoint.setVisibility(View.GONE);
        if (clipSize!=0) {
            enterAnimator.setIntValues(clipSize,0);
            enterAnimator.start();
        }
        TimeInterpolator sInterpolator = new DecelerateInterpolator();
        //设置imageView缩放动画
        view.animate().setDuration(DURATION_OUT).scaleX(originScale).scaleY(originScale).
                translationX(translateX).translationY(translateY)
                .setInterpolator(sInterpolator);

        // 设置activity主布局背景颜色DURATION毫秒内透明度从不透明到透明
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(colorDrawable, "alpha", 0);
        bgAnim.setDuration(DURATION_OUT);
        bgAnim.start();
        bgAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimationEnd = true;
                finish();
            }
        });
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
        pointAdapter.notifyItemChanged(i, true);
        pointAdapter.notifyItemChanged(lastPosition, false);
        lastPosition = i;
    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }


    int firstMoveDown;
    int distance;

    float allChangeX, allChangeY;

    @Override
    public void change(float x, float y, int heightDistance) {
//        heightDistance越趋于屏幕宽度，透明度越低，以首次的值为临界值
        if (firstMoveDown == 0) {
            firstMoveDown = heightDistance;
            distance = height - heightDistance;
        }
        allChangeX += x;
        allChangeY += y;
        int alpha = 255;
        if (heightDistance > firstMoveDown) {
            if (height <= heightDistance) {
                alpha = 0;
            } else {
//                heightDistance到 displayHeight是从255到0
                alpha = (int) (((float) height - heightDistance) / distance * 255f);
            }
        }
        colorDrawable.setAlpha(alpha);
        ivPager.layout(ivPager.getLeft() + (int) x, ivPager.getTop() + (int) y, ivPager.getRight() + (int) x, ivPager.getBottom() + (int) y);
    }

    @Override
    public void release() {
        firstMoveDown = 0;
        distance = 0;
        exitAnimationFrom(ivPager);
    }

    // 移动图片后复原
    private void exitAnimationFrom(View view) {
        MyRect myRect = bean.getMyRectList().get(lastPosition);
        if (bean == null) {
            return;
        }
        Rect rect = new Rect();
        rect.set(0, (height - width) / 2, width, (height - width) / 2 + width);
        view.setClipBounds(rect);
        Rect ivRect = new Rect();
        ivRect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        float newScale = originScale;
        view.setPivotX(view.getWidth() / 2);
        view.setPivotY(view.getHeight() / 2);
        int transLateX = myRect.getCenterX() - (ivRect.left + ivRect.right) / 2;
        int transLateY = myRect.getCenterY() - (ivRect.top + ivRect.bottom) / 2;
        TimeInterpolator sInterpolator = new DecelerateInterpolator();
        //设置imageView缩放动画
        view.animate().setDuration(DURATION_OUT)
                .scaleX(originScale).scaleY(newScale)
                .translationX(transLateX)
                .translationY(transLateY)
                .setInterpolator(sInterpolator);

        // 设置activity主布局背景颜色DURATION毫秒内透明度从不透明到透明
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(colorDrawable, "alpha", 0);
        bgAnim.setDuration(DURATION_OUT);
        bgAnim.start();
        bgAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimationEnd = true;
                finish();
            }
        });
    }
}
