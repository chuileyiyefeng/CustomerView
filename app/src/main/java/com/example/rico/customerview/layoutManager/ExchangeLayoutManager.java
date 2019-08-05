package com.example.rico.customerview.layoutManager;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Tmp on 2019/7/31.
 */
public class ExchangeLayoutManager extends RecyclerView.LayoutManager {
    private int parentWidth, parentHeight, middleLeft, defaultDis;
    private float scale = 0.9f, alpha = 0.5f;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }
        detachAndScrapAttachedViews(recycler);
        parentWidth = getWidth();
        parentHeight = getHeight();

//       这里只显示三个view
        for (int i = 0; i < getItemCount(); i++) {
            View child = recycler.getViewForPosition(i);
            measureChildWithMargins(child, 0, 0);
            int width = getDecoratedMeasuredWidth(child);
            int height = getDecoratedMeasuredHeight(child);
            int left, right, top, bottom;
            switch (i) {
                case 0:
                    left = getPaddingLeft();
                    child.setScaleX(scale);
                    child.setScaleY(scale);
                    addView(child);
                    child.setAlpha(alpha);
                    break;
                case 1:
                    middleLeft = left = getPaddingLeft() + (parentWidth - width) / 2;
                    defaultDis = middleLeft - getPaddingLeft();
                    addView(child);
                    break;
                case 2:
                    left = parentWidth - getPaddingRight() - width;
                    child.setScaleX(scale);
                    child.setScaleY(scale);
                    addView(child, 0);
                    child.setAlpha(alpha);
                    break;
                default:
                    left = getPaddingLeft();
                    break;
            }
            right = left + width;
            top = getPaddingTop() + (parentHeight - height) / 2;
            bottom = top + height;
            layoutDecorated(child, left, top, right, bottom);
        }
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    private int scrollX;

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        View child = recycler.getViewForPosition(1);
        int left=getDecoratedLeft(child);
        int right=getDecoratedRight(child);
        if (left-dx>=getPaddingLeft()&&right+dx<=parentWidth-getPaddingRight()) {
            dx = layoutChild(dx, recycler);
            scrollX += dx;
        }
        return dx;
    }

    private int layoutChild(int dx, RecyclerView.Recycler recycler) {
        removeAndRecycleAllViews(recycler);
        for (int i = 0; i < getItemCount(); i++) {
            View child = recycler.getViewForPosition(i);
            measureChildWithMargins(child, 0, 0);
            int width = getDecoratedMeasuredWidth(child);
            int height = getDecoratedMeasuredHeight(child);
            int left, right, top, bottom;
            switch (i) {
                case 0:
                    if (scrollX + dx > 0) {
                        left = getDecoratedLeft(child) + dx;
                    } else {
                        left = getDecoratedLeft(child) - dx;
                    }
                    addView(child);
                    child.setAlpha(alpha);
                    break;
                case 1:
                    left = getDecoratedLeft(child) - dx;
                    if (dx > 0 && left < 0) {
                        left = getPaddingLeft();
                        dx = 0;
                        scrollX = 0;
                    } else if (dx < 0 && left > (parentWidth - getPaddingRight() - width)) {
                        left = parentWidth - getPaddingRight() - width;
                        dx = 0;
                        scrollX = 0;
                    }
                    addView(child);

                    break;
                case 2:
                    if (scrollX + dx > 0) {
                        left = getDecoratedLeft(child) - dx;
                    } else {
                        left = getDecoratedLeft(child) + dx;
                    }
                    addView(child, 0);
                    child.setAlpha(alpha);
                    break;
                default:
                    left = getPaddingLeft();
                    break;
            }
            float scale = getRealScale(left, child, dx);
            child.setScaleX(scale);
            child.setScaleY(scale);
            right = left + width;
            top = getPaddingTop() + (parentHeight - height) / 2;
            bottom = top + height;
            layoutDecorated(child, left, top, right, bottom);
        }
        return dx;
    }

    private float getRealScale(int left, View child, int dx) {

//                    当前滑动的距离占总要滑动距离的比例
        float disPercent;
        int currentDis = left - getPaddingLeft();
        if (currentDis > defaultDis) {
            currentDis = parentWidth - getPaddingRight() - getDecoratedRight(child) + dx;
        } else {
            currentDis = left - getPaddingLeft();
        }
//                    disPercent趋势为100%-0%
        disPercent = (float) currentDis / defaultDis;
        return 1 - (1 - scale) * (1 - disPercent);
    }
}
