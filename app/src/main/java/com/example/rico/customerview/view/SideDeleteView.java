package com.example.rico.customerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Tmp on 2019/5/16.
 */
public class SideDeleteView extends ViewGroup {
    public SideDeleteView(Context context) {
        super(context);
        init(context);
    }

    public SideDeleteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() > 1) {
            View child_0 = getChildAt(0);
            View child_1 = getChildAt(1);
            child_0.layout(l, getPaddingTop(), r, height + getPaddingTop());
            child_1.layout(r, getPaddingTop(), r + child_0.getMeasuredWidth(), height + getPaddingTop());
        }
    }

    int width, height, sideWidth, moveLimit;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View view = getChildAt(0);
        measureChild(view, widthMeasureSpec, heightMeasureSpec);
        width = view.getMeasuredWidth();

        if (getChildCount() > 1) {
            for (int i = 0; i < getChildCount(); i++) {
                View childView = getChildAt(i);
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);
                height = Math.max(height, childView.getMeasuredHeight());
                if (i == 1) {
                    sideWidth = childView.getMeasuredWidth();
                }
            }
        }
        moveLimit = sideWidth / 5;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int realWidth = MeasureSpec.makeMeasureSpec(width, widthMode);
        int realHeight = MeasureSpec.makeMeasureSpec(height + getPaddingTop() + getPaddingBottom(), heightMode);
        setMeasuredDimension(realWidth, realHeight);
    }

    float downX, downY, lastMoveX, moveDistance;
    int scrollDx, scrollX;
    boolean toRight;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animator != null && animator.isRunning()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                lastMoveX=downX;
                break;
            case MotionEvent.ACTION_MOVE:
                moveDistance = (int) (lastMoveX - event.getX());
                if (moveDistance > 0) {
                    if (getScrollX() + moveDistance > sideWidth) {
                        moveDistance = sideWidth - getScrollX();
                    }
                } else if (moveDistance < 0) {
                    if (getScrollX() + moveDistance < 0) {
                        moveDistance = -getScrollX();
                    }
                }
                scrollBy((int) moveDistance, 0);
                lastMoveX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                toRight=moveDistance>0;
                if (getScrollX() > moveLimit&&toRight) {
//                    打开右侧
                    scrollDx = sideWidth;
                    startMove();
                }else if (sideWidth-getScrollX()>moveLimit&&!toRight){
//                    关闭右侧
                    scrollDx=0;
                    startMove();
                }
                break;
        }
        return true;
    }

    ValueAnimator animator;
    long duration;

    private void startMove() {
        if (animator == null) {
            animator = ValueAnimator.ofInt(getScrollX(), scrollDx);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    scrollTo(value, 0);
                }
            });
        }
        animator.setDuration(500);
        animator.start();
    }

    public void closeSide() {
        toRight = false;
        scrollDx = 0;
        scrollX = sideWidth;
        duration = 100;
        startMove();
    }
}
