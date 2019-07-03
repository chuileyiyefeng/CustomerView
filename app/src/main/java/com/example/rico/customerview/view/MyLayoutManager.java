package com.example.rico.customerview.view;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Created by Tmp on 2019/7/3.
 */
public class MyLayoutManager extends RecyclerView.LayoutManager {
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return null;
    }

    private int mVerticalOffset;//竖直偏移量 每次换行时，要根据这个offset判断
    private int mFirstVisitPos;// 屏幕可见的第一个View的Position
    private int mLastVisitPos;// 屏幕可见的最后一个View的Position

    //    y方向滑动距离
    private int moveY;

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0) {
            detachAndScrapAttachedViews(recycler);
            return;
        }
        if (getChildCount() == 0 && state.isPreLayout()) {
            return;
        }
        detachAndScrapAttachedViews(recycler);
        mVerticalOffset = 0;
        mFirstVisitPos = 0;
        mLastVisitPos = getItemCount();
        layout(recycler, state);
    }

    private void layout(RecyclerView.Recycler recycler, RecyclerView.State state) {
        int top = getPaddingTop();
        int left = getPaddingLeft();
        int lineMaxHeight = 0;
        int minPost = mFirstVisitPos;
        mLastVisitPos = getItemCount() - 1;
        int parentWidth = getHorizontalSpace();

        for (int i = minPost; i < mLastVisitPos; i++) {

            View view = recycler.getViewForPosition(i);
            addView(view);
            measureChildWithMargins(view, 0, 0);
            int width = getViewWidth(view);
            int height = getViewHeight(view);
            if (left + width < parentWidth) {
                int right = left + width;
                int bottom = top + height;
                layoutDecoratedWithMargins(view, left, top, right, bottom);
                left = right;
                lineMaxHeight = Math.max(lineMaxHeight, height);
            } else {
                left = getPaddingLeft();
                top += lineMaxHeight;
                lineMaxHeight = 0;
                if (top - moveY > getHeight() - getPaddingBottom()) {
                    removeAndRecycleView(view, recycler);
                    mLastVisitPos = i - 1;
                } else {
                    layoutDecoratedWithMargins(view, left, top, left + width, top + height); //改变 left  lineHeight
                    left += width;
                    lineMaxHeight = Math.max(lineMaxHeight, height);
                }
            }
        }
    }


    /**
     * 获取某个childView在水平方向所占的空间
     *
     * @param view
     * @return
     */
    public int getViewWidth(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        return getDecoratedMeasuredWidth(view) + params.leftMargin + params.rightMargin;
    }

    /**
     * 获取某个childView在竖直方向所占的空间
     *
     * @param view
     * @return
     */
    public int getViewHeight(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        return getDecoratedMeasuredHeight(view) + params.topMargin + params.bottomMargin;
    }

    public int getVerticalSpace() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    public int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }


    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (dy == 0 || getChildCount() == 0) {
            return 0;
        }
        moveY = dy;

//        向上滑
        if (mVerticalOffset + dy < 0) {
            moveY = -mVerticalOffset;
//            向下滑
        } else if (dy > 0) {

        }
        return dy;
    }
}
