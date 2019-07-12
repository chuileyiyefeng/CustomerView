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


    //    RecyclerView的宽高
    private int parentWidth, parentHeight;


    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0 && state.isPreLayout()) {
            return;
        }
        detachAndScrapAttachedViews(recycler);
        parentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        parentHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        lastPos = getItemCount();
        rectArray = new SparseArray<>();
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
                if (top + maxHeight > getHeight() - getPaddingBottom()) {
                    lastPos = i;
                    break;
                }
                top += maxHeight;
                layout(i, childView, getPaddingLeft(), top, width, height);
                left = width;
                maxHeight = height;
            }
        }
    }
    int scrollY;
    private SparseArray<Rect> rectArray;

    private void layout(int i, View childView, int left, int top, int width, int height) {
        layoutDecorated(childView, left, top, left + width, top + height);
        Rect rect = rectArray.get(i);
        if (rect == null) {
            rect = new Rect(left, top+scrollY, left + width, top+scrollY + height);
            rectArray.put(i, rect);
        } else {
            rect.set(left, top+scrollY, left + width, top+scrollY + height);
        }
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
        fillViews(recycler, dy);
        offsetChildrenVertical(-dy);
        scrollY+=dy;
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
                removeAndRecycleView(childView, recycler);
                firstPos++;
            } else if (dy < 0 && getDecoratedTop(childView) - dy > bottom) {
                removeAndRecycleView(childView, recycler);
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
                measureChildWithMargins(childView, 0, 0);
                int width = getDecoratedMeasuredWidth(childView);
                int height = getDecoratedMeasuredHeight(childView);
                if (left + width <= parentWidth) {
                    addView(childView);
                    layout(i, childView, left, top, width, height);
                    left += width;
                    maxHeight = Math.max(maxHeight, height);
                } else {
                    if (top + maxHeight - dy > bottom) {
                        removeAndRecycleView(childView, recycler);
                        break;
                    }
                    addView(childView);
                    top += maxHeight;
                    layout(i, childView, getPaddingLeft(), top, width, height);
                    left = width;
                    maxHeight = height;
                }
            }
        } else {
            int maxPos = getItemCount() - 1;
            firstPos = 0;
            if (getChildCount() > 0) {
                View firstView = getChildAt(0);
                maxPos = getPosition(firstView) - 1;
            }
            for (int i = maxPos; i >= firstPos; i--) {
                Rect rect = rectArray.get(i);
                if (rect.bottom - scrollY - dy < getPaddingTop()) {
                    firstPos = i + 1;
                    break;
                } else {
                    View child = recycler.getViewForPosition(i);
                    addView(child, 0);//将View添加至RecyclerView中，childIndex为1，但是View的位置还是由layout的位置决定
                    measureChildWithMargins(child, 0, 0);
                    layoutDecoratedWithMargins(child, rect.left, rect.top - scrollY, rect.right, rect.bottom - scrollY);
                }
            }
        }
    }
}
