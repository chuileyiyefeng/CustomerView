package com.example.rico.customerview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

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

    private Scroller scroller;
    private int duration = 300;
    private int scrollDx;

    private int height, sideWidth, moveLimit;
    private float downX, downY, lastMoveX, moveDistance;

    //    Java中多个实例的static变量会共享同一块内存区域
    //     用静态变量控制上一个侧滑的view
    private static boolean isOpen;
    private boolean hasOpen;
    private static SideDeleteView deleteView;
    private float touchSlop;

    private void init(Context context) {
        scroller = new Scroller(context);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int lastWidth = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.layout(l + lastWidth, getPaddingTop(), r + lastWidth, getPaddingTop() + view.getMeasuredHeight());
            lastWidth += view.getMeasuredWidth();
        }

    }

    //    重写测量child方法，使子view的高度一样，都为第一个view高度
    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        final LayoutParams lp = child.getLayoutParams();
        int mHeight = lp.height;
        if (height != 0) {
            mHeight = height;
        }
        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight(), lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                getPaddingTop() + getPaddingBottom(), mHeight);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setClickable(true);
        int child0Width = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
//            childView.setClickable(true);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            if (i == 0) {
                height = childView.getMeasuredHeight();
                child0Width = childView.getMeasuredWidth();
            } else if (i == 1) {
                sideWidth = childView.getMeasuredWidth();
            }
        }
        moveLimit = sideWidth / 5;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int realWidth = MeasureSpec.makeMeasureSpec(child0Width, widthMode);
        int realHeight = MeasureSpec.makeMeasureSpec(height + getPaddingTop() + getPaddingBottom(), heightMode);
        setMeasuredDimension(realWidth, realHeight);
    }

    @Override
    public boolean performClick() {
//        如果是禁用父类拦截或者有sideView打开
        return !isScroll && !hasOpen && super.performClick();
    }

    boolean isScroll;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!scroller.isFinished()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                hasOpen = false;
                isScroll = false;
                if (deleteView != null && deleteView != this) {
                    if (isOpen) {
                        deleteView.closeSide();
                        disallowParent(true);
                        hasOpen = true;
                    }
                }
                downX = event.getX();
                downY = event.getY();
                lastMoveX = downX;
                break;
            case MotionEvent.ACTION_MOVE:
//                是否禁用父类的拦截功能
                if (hasOpen) {
                    return false;
                }
                boolean interceptParent = Math.abs(downX - event.getX()) > Math.abs(downY - event.getY());
                if (!interceptParent &&getScrollX()==0) {
                    break;
                }
                moveDistance = (int) (lastMoveX - event.getX());
                if (!isScroll) {
                    if (Math.abs(moveDistance) > touchSlop) {
                        isScroll = true;
                    }
                }
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
                disallowParent(true);
                break;
            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
                boolean toRight = moveDistance > 0;
                if (getScrollX() >= moveLimit && toRight) {
//                    打开右侧
                    scrollDx = sideWidth;
                } else if (sideWidth - getScrollX() >= moveLimit && !toRight) {
//                    关闭右侧
                    scrollDx = 0;
                } else if (getScrollX() < moveLimit && toRight) {
                    scrollDx = 0;
                } else if (sideWidth - getScrollX() < moveLimit && !toRight) {
                    scrollDx = sideWidth;
                }
                upResult();
                scroller.startScroll(getScrollX(), 0, scrollDx - getScrollX(), 0, duration);
                invalidate();
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    //    是否禁用父类的拦截功能
    private void disallowParent(boolean b) {
        getParent().requestDisallowInterceptTouchEvent(b);
    }


    //    侧滑view是否是打开状态
    private void upResult() {
        boolean b = sideWidth == scrollDx;
        isOpen = b;
        if (b) {
            deleteView = this;
            disallowParent(true);
        }
    }

    //    关闭侧滑view
    public void closeSide() {
        deleteView = null;
        scrollDx = 0;
        scroller.startScroll(getScrollX(), 0, -getScrollX(), 0, duration);
        isOpen = false;
        invalidate();
    }

    //    是否拦截
    boolean isIntercept;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isIntercept = false;
                downX = ev.getX();
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                isIntercept = Math.abs(downX - ev.getX()) > Math.abs(downY - ev.getY());
                break;

        }
        return isIntercept;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }

    }
}
