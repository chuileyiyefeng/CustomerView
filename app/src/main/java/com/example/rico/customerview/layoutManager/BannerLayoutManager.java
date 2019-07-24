package com.example.rico.customerview.layoutManager;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Tmp on 2019/7/16.
 */
public class BannerLayoutManager extends RecyclerView.LayoutManager {


    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }
        parentRight = getWidth() - getPaddingRight();
        lastPos = getItemCount();
        int left = 0;
        detachAndScrapAttachedViews(recycler);
        for (int i = 0; i < lastPos; i++) {
            View child = recycler.getViewForPosition(i);
            addView(child);
            measureChildWithMargins(child, 0, 0);
            int height = getDecoratedMeasuredHeight(child);
            int width = getDecoratedMeasuredWidth(child);
            layoutDecorated(child, left, 0, left + width, height);
            left += width;
            if (left > parentRight) {
                lastPos = i;
                break;
            }
        }
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    int firstPos, lastPos, parentRight;
    private int scrollX;

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0) {
            return 0;
        }
        if (dx > 0) {
            View lastView = getChildAt(getChildCount() - 1);
            if (lastView != null) {
                if (getPosition(lastView) == getItemCount() - 1) {
                    if (getDecoratedRight(lastView) - dx < parentRight) {
                        dx = getDecoratedRight(lastView) - parentRight;
                    }
                }
            }
        } else if (scrollX + dx < 0) {
            dx = -scrollX;
        }
        offsetChildrenHorizontal(-dx);
        dx = fillViews(dx, recycler);
        scrollX += dx;
        return dx;
    }

    private int fillViews(int dx, RecyclerView.Recycler recycler) {
//        先回收后布局
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child != null) {
                if (dx > 0) {
                    if (getDecoratedRight(child) + dx < getPaddingLeft()) {
                        removeAndRecycleView(child, recycler);
                        firstPos++;
                    }
                } else if (dx < 0) {
                    if (getDecoratedLeft(child) + dx > parentRight) {
                        removeAndRecycleView(child, recycler);
                        lastPos--;
                    }
                }
            }
        }
        if (dx < 0) {
            View firstView = getChildAt(0);
            if (firstView != null) {
                int left = getDecoratedLeft(firstView);
                if (left > getPaddingLeft() && firstPos > 0) {
                    firstPos--;
                    View child = recycler.getViewForPosition(firstPos);
                    measureChildWithMargins(child, 0, 0);
                    int height = getDecoratedMeasuredHeight(child);
                    int width = getDecoratedMeasuredWidth(child);
                    addView(child,0);
                    layoutDecorated(child, left - width, getPaddingTop(), left, getPaddingTop() + height);
                }
            }
        } else {
            View lastView = getChildAt(getChildCount() - 1);
            if (lastView != null) {
                int right = getDecoratedRight(lastView);
                if (right < parentRight && lastPos < getItemCount() - 1) {
                    lastPos++;
                    View child = recycler.getViewForPosition(lastPos);
                    measureChildWithMargins(child, 0, 0);
                    int height = getDecoratedMeasuredHeight(child);
                    int width = getDecoratedMeasuredWidth(child);
                    addView(child);
                    layoutDecorated(child, right, getPaddingTop(), right + width, getPaddingTop() + height);
                }
            }
        }

        return dx;
    }
}
