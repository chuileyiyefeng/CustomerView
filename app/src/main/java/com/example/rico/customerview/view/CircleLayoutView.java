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
 * Created by Tmp on 2019/2/21.
 * 无限循环view
 */
public class CircleLayoutView extends ViewGroup {
    private static final String TAG = "CircleLayoutView";
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

    int mTouchSlop;

    private void initView(Context context) {
        scroller = new Scroller(context);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    //    宽、高、滑动临界值
    int width, height, threshold;

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
        threshold = height / 3;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        布局类似于Linear Layout的vertical模式
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

    float lastX, lastY, lastMoveY;
    boolean isCanSliding = false;

//    viewGroup事件分发流程 dispatchTouchEvent-onInterceptTouchEvent-onTouchEvent

//    dispatch
//    为true直接会去调用子view的dispatch
//    为false会调用父类的onTouchEvent
//    为super会调用 onInterceptTouchEvent

//    onInterceptTouchEvent
//    为false或者super，表示不拦截事件，会调用子view的dispatch
//    为true表示拦截事件，事件交给onTouchEvent处理

    float mDownX, mDownY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isCanSliding = false;
                mDownX = x;
                mDownY = y;
//               有可能这里调用了down，isCanSliding又为true的同时，onTouchEvent的down不会调用，所以这里也给lastY赋值一下
                lastY=ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                isCanSliding = isCanSliding(ev);
//                isCanSliding为true时，会直接调用onTouchEvent的ACTION_MOVE，所以这里改变lastMoveY的值
                lastMoveY = ev.getY();
                break;
        }
        return isCanSliding;
    }

    public boolean isCanSliding(MotionEvent ev) {
        float moveX = ev.getX();
        float moveY = ev.getY();
        return (Math.abs(moveY - mDownY) > mTouchSlop && (Math.abs(moveY - mDownY) > (Math.abs(moveX - mDownX))));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!scroller.isFinished()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                lastMoveY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
//                跟随手指滑动
                moveFollow((int) (lastMoveY - event.getY()));
                lastMoveY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float distance = event.getY() - lastY;
                if (Math.abs(distance) > mTouchSlop) {
                    goScroll(distance);
                }
        }
        return true;
    }


    //    跟随手指的滑动
    private void moveFollow(int moveOn) {
        scrollBy(0, moveOn);
//        当上面添加一个view时，当前显示的view会向下移动height的距离，这时候scroll 正height高度
//        这样就形成了view没动，上面出现一个view的视觉效果
        if (getScrollY() < 5) {
            addLast();
            scrollBy(0, height);
//            这里调用了scrollBy方法，所以getScrollY的值变了，所以if的满足条件值满足一次，不会多次调用
        }
        if (getScrollY() > (getChildCount() - 1) * height) {
            addNext();
            scrollBy(0, -height);
        }
    }

    int duration = 300;

    //    放手之后的滑动选项
    private void goScroll(float distance) {
//        因为有addLast和addNext方法存在，getScrollY范围会在 0-height*getChildCount 这个范围
        int level = (getScrollY() % height);
        int shouldScroll;
        if (distance > 0) {
            if (Math.abs(distance) % height >= threshold) {
                shouldScroll = -level;
            } else {
                shouldScroll = height - level;
            }
        } else {
            if (Math.abs(distance) % height >= threshold) {
                shouldScroll = height - level;
            } else {
                shouldScroll = -level;
            }
        }
        scroller.startScroll(0,
                getScrollY(),
                0,
                shouldScroll, duration);
        postInvalidate();
    }

    public void moveNext() {
        addNext();
        scrollBy(0, -height);
//        这里滚动会有误差，所以加上一个差值，moveLast同理
        int deviation = getScrollY() % height;
        scroller.startScroll(0,
                getScrollY(),
                0,
                height - deviation, duration);
        invalidate();

    }

    public void moveLast() {
        addLast();
        scrollBy(0, height);
        int deviation = getScrollY() % height;
        scroller.startScroll(0,
                getScrollY(),
                0,
                -height - deviation, duration);
        invalidate();

    }

    //    移形换位，最上面的view挪到最底下
    private void addNext() {
        int count = getChildCount();
        View v = getChildAt(0);
        removeViewAt(0);
        addView(v, count - 1);
    }

    //    移形换位，最底下的view挪到最上
    private void addLast() {
        int count = getChildCount();
        View v = getChildAt(count - 1);
        removeViewAt(count - 1);
        addView(v, 0);
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
