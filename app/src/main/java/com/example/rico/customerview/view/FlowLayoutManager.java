package com.example.rico.customerview.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Tmp on 2019/7/11.
 */
public class FlowLayoutManager extends RecyclerView.LayoutManager {

    public FlowLayoutManager() {

    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    //    屏幕上显示的第一个child的下标，最后一个child的下标
    private int firstPos, lastPos;

    //    scrollY的距离
    int scrollY;
    //    RecyclerView的宽高
    private int parentWidth, parentHeight;

    private SparseArray<Rect> rectArray;

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0 && state.isPreLayout()) {
            return;
        }
        detachAndScrapAttachedViews(recycler);
        rectArray = new SparseArray<>();
        parentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        parentHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        lastPos = getItemCount();

        int left = getPaddingLeft();
        int top = getPaddingTop();
        int maxHeight = 0;
        for (int i = firstPos; i < lastPos; i++) {
            View childView = recycler.getViewForPosition(i);
            addView(childView);
            measureChildWithMargins(childView, 0, 0);
            int width = getDecoratedMeasuredWidth(childView);
            int height = getDecoratedMeasuredHeight(childView);
            if (left + width <= parentWidth) {
                layout(i, childView, left, top, width, height);
                left += width;
                maxHeight = Math.max(maxHeight, height);
            } else {
                top += maxHeight;
                layout(i, childView, getPaddingLeft(), top, width, height);
                left = width;
                maxHeight = height;
                if (top + maxHeight > getHeight() - getPaddingBottom()) {
                    lastPos = i;
                }
            }
        }
    }

    private void layout(int position, View childView, int left, int top, int right, int bottom) {
        layoutDecorated(childView, left, top, left + right, top + bottom);
        Rect rect = new Rect(left, top + scrollY, left + right, top + bottom + scrollY);
        rectArray.put(position, rect);
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }


    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0) {
            return 0;
        }
        scrollY += dy;
        offsetChildrenVertical(-dy);
        fillViews(recycler, dy);
        return dy;
    }

    private void fillViews(RecyclerView.Recycler recycler, int dy) {
        int top = getPaddingTop();
        int bottom = getHeight() - getPaddingBottom();
        int left = getPaddingLeft();
        //        先回收，后布局
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View childView = getChildAt(i);
            assert childView != null;
            if (dy > 0 && getDecoratedBottom(childView) - dy < top) {
                detachAndScrapView(childView, recycler);
                firstPos++;
            } else if (dy < 0 && getDecoratedTop(childView) - dy > bottom) {
                detachAndScrapView(childView, recycler);
                lastPos--;
            }
        }

//        手指向上
        if (dy > 0) {
            int minPos = 0;
            View lastView = getChildAt(getChildCount() - 1);
            if (lastView != null) {
                top = getDecoratedBottom(lastView);
                minPos = getPosition(lastView) + 1;
            }
            int maxHeight = 0;
            for (int i = minPos; i < getItemCount(); i++) {
                View childView = recycler.getViewForPosition(i);
                addView(childView);
                measureChildWithMargins(childView, 0, 0);
                int width = getDecoratedMeasuredWidth(childView);
                int height = getDecoratedMeasuredHeight(childView);
                if (left + width <= parentWidth) {
                    layout(i, childView, left, top, width, height);
                    left += width;
                    maxHeight = Math.max(maxHeight, height);
                } else {
                    if (top + maxHeight - dy > bottom) {
                        detachAndScrapView(childView, recycler);
                        continue;
                    }
                    top += maxHeight;
                    layout(i, childView, getPaddingLeft(), top, width, height);
                    left = width;
                    maxHeight = height;
                }
            }
        } else  {

        }
    }
}
