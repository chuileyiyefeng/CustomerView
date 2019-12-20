package com.example.rico.customerview.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
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
import com.example.rico.customerview.StatusBarUtil;
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
        StatusBarUtil.setTransparent(this);
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
    int DURATION_IN = 300, DURATION_OUT = 300;
    ColorDrawable colorDrawable;
    float scaleX, scaleY;
    int width, height;

    public void enterAnimation(View view) {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        width = point.x;
        height = point.y;
        scaleX = (bean.getMyRectList().get(lastPosition).getRight() - bean.getMyRectList().get(lastPosition).getLeft()) / (float) point.x;
        scaleY = (bean.getMyRectList().get(lastPosition).getBottom() - bean.getMyRectList().get(lastPosition).getTop()) / (float) point.y;
        //设置view缩放中心
        view.setPivotX(0);
        view.setPivotY(0);
        view.setTranslationX(bean.getMyRectList().get(lastPosition).getLeft());
        view.setTranslationY(bean.getMyRectList().get(lastPosition).getTop() - (point.y - point.x) / 2f * scaleX);
        float realScale = scaleX;
        view.setScaleX(realScale);
        view.setScaleY(realScale);
        //设置动画
        TimeInterpolator sDecelerator = new DecelerateInterpolator();
        //设置imageView缩放动画
        view.animate().setDuration(DURATION_IN).scaleX(1).scaleY(1).
                translationX(0).translationY(0).setInterpolator(sDecelerator);

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

    public void exitAnimation(View view) {
        if (bean == null) {
            return;
        }
        rvPoint.setVisibility(View.GONE);
        MyRect myRect = bean.getMyRectList().get(lastPosition);
        Rect rect = new Rect();
        rect.set(0, (height - width) / 2, width, (height - width) / 2 + width);
        view.setClipBounds(rect);
        int transLateX = myRect.getLeft();
        int transLateY = (int) (myRect.getTop() - (height - width) / 2f * scaleX);
        float newScale = scaleX;
        TimeInterpolator sInterpolator = new DecelerateInterpolator();
        //设置imageView缩放动画
        view.animate().setDuration(DURATION_OUT).scaleX(scaleX).scaleY(newScale).
                translationX(transLateX).translationY(transLateY)
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

    float allY;

    @Override
    public void change(float x, float y, int heightDistance) {
        allY += y;
        if (allY > 0) {
            if (allY > heightDistance) {
                allY = heightDistance;
            }
            colorDrawable.setAlpha((int) (255 - 255 * (allY / heightDistance)));
        } else {
            colorDrawable.setAlpha(255);
        }
        Log.e("activity", "initView: " + x + " " + y + " " + allY);
    }
}
