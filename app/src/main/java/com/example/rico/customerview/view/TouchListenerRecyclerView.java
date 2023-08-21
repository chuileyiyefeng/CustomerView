package com.example.rico.customerview.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Description: recyclerview滑动到顶部或底部继续滑动
 * @Author: pan yi
 * @Date: 2023/3/1
 */
public class TouchListenerRecyclerView extends FrameLayout {
    public TouchListenerRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public TouchListenerRecyclerView(@NonNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchListenerRecyclerView(@NonNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    private RecyclerView recyclerView;
    private float interceptDownY;
    private boolean isIntercept = true;
    private float mTouchSlop;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (getChildCount() > 0) {
            if (getChildAt(0) instanceof RecyclerView) {
                recyclerView = (RecyclerView) getChildAt(0);
            }
        }
    }

    float downY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getChildCount() > 1) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = downY - event.getY();
                moveView(moveY);
                downY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;

    }


    private void moveView(float moveY) {
        if (recyclerScrollEndListener != null) {
            if (moveY > 0) {//滑动到底部
                recyclerScrollEndListener.scrollBottomEnd();
            } else {//滑动到顶部
                recyclerScrollEndListener.scrollTopEnd();
            }
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isIntercept = false;
                interceptDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                downY = ev.getY();
                if (Math.abs(interceptDownY - ev.getY()) < mTouchSlop) {
                    return false;
                }
                if (recyclerView != null) {
                    // 向下滑动
                    // rv滑动的距离
                    int offsetY = recyclerView.computeVerticalScrollOffset();
                    //   竖直方向的范围
                    int extentY = recyclerView.computeVerticalScrollExtent();
                    // 可以滑动的Y范围
                    int scrollRangeY = recyclerView.computeVerticalScrollRange();
                    if (ev.getY() - interceptDownY > 0) {
                        if (offsetY == 0) {
                            isIntercept = true;
                        }
                    }
                    // 向上滑动
                    else {
                        if (offsetY + extentY >= scrollRangeY) {
                            isIntercept = true;
                        }
                    }
                }
                interceptDownY = ev.getY();
                break;
        }

        return isIntercept;
    }

    private RecyclerScrollEndListener recyclerScrollEndListener;

    public void setRecyclerScrollEndListener(RecyclerScrollEndListener recyclerScrollEndListener) {
        this.recyclerScrollEndListener = recyclerScrollEndListener;
    }

    interface RecyclerScrollEndListener {
        void scrollTopEnd();

        void scrollBottomEnd();
    }
}
