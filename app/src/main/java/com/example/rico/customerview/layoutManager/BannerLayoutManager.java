package com.example.rico.customerview.layoutManager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Tmp on 2019/7/16.
 */
public class BannerLayoutManager extends LinearLayoutManager {


    public BannerLayoutManager(Context context) {
        super(context);
        setOrientation(LinearLayoutManager.HORIZONTAL);
    }

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
        detachAndScrapAttachedViews(recycler);
        parentRight = getWidth() - getPaddingRight();
        centerX = parentRight / 2;
        lastPos = getItemCount();
        int left = firstLeft;
        for (int i = firstPos; i < lastPos; i++) {
            View child = recycler.getViewForPosition(i);
            addView(child);
            measureChildWithMargins(child, 0, 0);
            int height = getDecoratedMeasuredHeight(child);
            int width = getDecoratedMeasuredWidth(child);
            if (i == 0) {
                left = (parentRight - width) / 2;
            }
            layoutDecorated(child, left, 0, left + width, height);
            left += width;
            if (left > parentRight) {
                lastPos = i;
                break;
            }
        }
        scaleView();
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    private int firstPos, lastPos, parentRight, centerX,firstLeft;
    private int scrollX;

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0) {
            return 0;
        }
        if (dx > 0) {
            View lastView = getChildAt(getChildCount() - 1);
            if (lastView != null) {
                int width = getDecoratedMeasuredWidth(lastView);
                int right = parentRight - ((parentRight - width) / 2);
                if (getPosition(lastView) == getItemCount() - 1) {
                    if (getDecoratedRight(lastView) - dx < right) {
                        dx = getDecoratedRight(lastView) - right;
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
                    addView(child, 0);
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
        scaleView();
        View firstView = getChildAt(0);
        if (firstView != null) {
            firstPos = getPosition(firstView);
            firstLeft = getDecoratedLeft(firstView) - getPaddingLeft();
        }
        return dx;
    }

    private void scaleView() {
        float scaleF = 0.8f;
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            int width = getDecoratedMeasuredWidth(view);
            //        分两种情况，从右边到中间为变大，即是 scaleF到1.0f,从中间到左边即是1.0f到scaleF
            int centerViewX = view.getLeft() + width / 2;
//        子view的中心点到控件中心的距离
            float distance = Math.abs(centerX - centerViewX);

//        子view的中心点到控件中心的距离所占总view宽度的百分比
            double percent = distance / (getWidth() / 2);
            double lastWidth = width * (1 - scaleF);
            float realScaleF = (float) ((width - percent * lastWidth) / width);
            if (realScaleF < scaleF) {
                realScaleF = scaleF;
            } else if (realScaleF > 1.0f) {
                realScaleF = 1.0f;
            }
//        X是1-100 而Y的值是80到100
            view.setScaleX(realScaleF);
            view.setScaleY(realScaleF);
//            break;
        }
    }
}
