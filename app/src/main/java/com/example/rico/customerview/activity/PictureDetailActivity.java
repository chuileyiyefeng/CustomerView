package com.example.rico.customerview.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.rico.customerview.R;
import com.example.rico.customerview.StatusBarUtil;
import com.example.rico.customerview.bean.PicInfoBean;

/**
 * Created by Tmp on 2019/11/4.
 */
public class PictureDetailActivity extends BaseActivity {
    ImageView iv;
    PicInfoBean bean;

    @Override
    public int bindLayout() {
        return R.layout.activity_picture_detail;
    }

    @Override
    public void doBusiness() {
        bean = (PicInfoBean) getIntent().getSerializableExtra("picInfoBean");
        iv = findViewById(R.id.iv);
        Glide.with(this).load(bean.getUrl()).into(iv);
        colorDrawable = new ColorDrawable(Color.BLACK);
        enterAnimation();
    }

    int DURATION = 3000;
    ColorDrawable colorDrawable;

    public void enterAnimation() {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        float scaleX = (bean.getRight() - bean.getLeft()) / (float) point.x;
        float scaleY = (bean.getBottom() - bean.getTop()) / (float) point.y;
        //设置imageview动画的初始值
        iv.setPivotX((bean.getRight() - bean.getLeft()) / 2);
        iv.setPivotY((bean.getBottom() - bean.getTop()) / 2);
        iv.setTranslationX(bean.getLeft());
        iv.setTranslationY(bean.getTop());
        iv.setScaleX(scaleX);
        iv.setScaleY(scaleY);
        //设置动画
        TimeInterpolator sDecelerator = new DecelerateInterpolator();
        //设置imageview缩放动画
        iv.animate().setDuration(DURATION).scaleX(1).scaleY(1).
                translationX(0).translationY(0).setInterpolator(sDecelerator);

        // 设置activity主布局背景颜色DURATION毫秒内透明度从透明到不透明
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(colorDrawable, "alpha", 0, 255);
        bgAnim.setDuration(DURATION);
        bgAnim.start();
    }

    public void exitAnimation(final Runnable endAction) {

//        TimeInterpolator sInterpolator = new AccelerateInterpolator();
//        //设置imageview缩放动画，以及缩放结束位置
//        iv.animate().setDuration(DURATION).scaleX(mWidthScale).scaleY(mHeightScale).
//                translationX(mLeftDelta).translationY(mTopDelta)
//                .setInterpolator(sInterpolator).withEndAction(endAction);
//
//        // 设置activity主布局背景颜色DURATION毫秒内透明度从不透明到透明
//        ObjectAnimator bgAnim = ObjectAnimator.ofInt(colorDrawable, "alpha", 0);
//        bgAnim.setDuration(DURATION);
//        bgAnim.start();
    }
}
