package com.example.rico.customerview.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.example.rico.customerview.R;

/**
 * @Description: 水波纹
 * @Author: pan yi
 * @Date: 2022/9/22
 */
public class SpreadViewActivity extends BaseActivity {
    ImageView iv1;
    ImageView ivCenter;
    ValueAnimator animatorLoading, animatorEnd;

    @Override
    public int bindLayout() {
        return R.layout.activity_spread_view;
    }

    @Override
    public void doBusiness() {
        iv1 = findViewById(R.id.iv_love1);
        ivCenter = findViewById(R.id.iv_center);
        animatorLoading = ValueAnimator.ofFloat(0, 50f, 0);
        animatorLoading.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                iv1.setTranslationY(value);
            }
        });
        animatorLoading.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorLoading.setDuration(3000);
        animatorLoading.setRepeatCount(1);
        animatorLoading.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                    startEndAnimator(iv1);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorLoading.start();
    }

    private void startEndAnimator(View view) {
        float centerX = ivCenter.getX() + ivCenter.getMeasuredWidth() / 2f;
        float centerY = ivCenter.getY() + ivCenter.getMeasuredHeight() / 2f;

        float startX=view.getX() + view.getMeasuredWidth() / 2f;
        float startY=view.getY() + view.getMeasuredHeight() / 2f;

        float distanceX=centerX-startX;
        float distanceY=centerY-startY;

        float scaleY=distanceX/distanceY;

        animatorEnd=ValueAnimator.ofFloat(0,distanceX);
        animatorEnd.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            view.setTranslationX(value);
            view.setTranslationY(value/scaleY);
        });
        animatorEnd.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorEnd.setDuration(2000);
        animatorEnd.start();
    }
}
