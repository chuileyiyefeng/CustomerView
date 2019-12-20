package com.example.rico.customerview.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by Tmp on 2019/12/18.
 */
public class DragFrameLayout extends FrameLayout {
    public DragFrameLayout(Context context) {
        this(context, null);
    }

    public DragFrameLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    int width, height;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        originLeft = getLeft();
        originTop = getTop();
    }

    float currentLeft, currentTop;


    private void initView() {

    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    float lastDownX, lastDownY;
    int originTop, originLeft;

    // 拦截竖向滑动

    boolean isInterceptVertical;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastDownX = event.getRawX();
                lastDownY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float distanceX = event.getRawX() - lastDownX;
                float distanceY = event.getRawY() - lastDownY;
                isInterceptVertical = Math.abs(distanceX) < Math.abs(distanceY);
                break;
            case MotionEvent.ACTION_UP:
                disallowIntercept = true;
                break;
        }
        return false;
    }
    boolean disallowIntercept;



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastDownX = event.getRawX();
                lastDownY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float distanceX = event.getRawX() - lastDownX;
                float distanceY = event.getRawY() - lastDownY;
                currentLeft = getLeft() + distanceX;
                currentTop = getTop() + distanceY;
                layout((int) currentLeft, (int) currentTop, (int) currentLeft + width, (int) currentTop + height);
                lastDownX = event.getRawX();
                lastDownY = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                // 还原
                disallowIntercept = false;
                break;
        }
        return true;
    }
}
