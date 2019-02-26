package com.example.rico.customerview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by Tmp on 2019/2/21.
 * 无限循环view
 */
public class CircleLayoutView extends ViewGroup {
    Scroller scroller;

    public CircleLayoutView(Context context) {
        super(context);
        initView(context);
    }


    public CircleLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CircleLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        scroller = new Scroller(context);
    }

    int width, height;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            measureChild(v, widthMeasureSpec, heightMeasureSpec);
            height = Math.max(height, v.getMeasuredHeight());
            width = Math.max(width, v.getMeasuredWidth());
        }
        width = widthMode == MeasureSpec.EXACTLY ? widthSize : width;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        int top = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != VISIBLE) {
                continue;
            }
            int wDistance = -(child.getMeasuredWidth() - width) / 2;
            int hDistance = -(child.getMeasuredHeight() - height) / 2;
            //设置View的左边、上边、右边底边位置
            child.layout(wDistance, top + hDistance, child.getMeasuredWidth() + wDistance, top + child.getMeasuredHeight() + hDistance);
            top = top + height;
        }
    }

    float lastX, lastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(event.getX() - lastX) < 10 && Math.abs(event.getY() - lastY) < 10) {
                    scrollLast();
                }
                break;
        }
        return true;
    }

    int startY = 0;

    boolean firstMove = true;

    private void scrollLast() {
        if (!firstMove) {
            computePosition();
        }
        firstMove = false;
        scroller.startScroll(0,
                startY,
                0,
                height, 1000);
//        startY += height;
        //通过重绘不断调用computeScroll
        invalidate();
    }

    private void computePosition() {
        int count = getChildCount();
        View v = getChildAt(0);
        removeViewAt(0);
        addView(v, count - 1);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }
}
