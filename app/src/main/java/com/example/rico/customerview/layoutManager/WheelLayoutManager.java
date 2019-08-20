package com.example.rico.customerview.layoutManager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Tmp on 2019/8/19.
 * 滚轮recyclerView layoutManager
 */
public class WheelLayoutManager extends RecyclerView.LayoutManager {


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
        parentHeight = getHeight() - getPaddingBottom();
        centerY = parentHeight / 2;
        int top = firstTop;
        int left = getPaddingLeft();
        distanceHeight = 0;
        lastPos = getItemCount();
        int bottom = 0;
        for (int i = firstPos; i < lastPos; i++) {
            if (i > firstPos + 1) {
                break;
            }
            View child = recycler.getViewForPosition(i);
            addView(child);
            measureChildWithMargins(child, 0, 0);
            int height = getDecoratedMeasuredHeight(child);
            int width = getDecoratedMeasuredWidth(child);
            if (i == firstPos) {
                top = firstTop + (parentHeight - height) / 2;
                bottom = top + height;
            } else {
                top = parentHeight - height;
                distanceHeight = top - bottom;
                bottom = top + height;
                lastPos = i;
            }
            layoutDecorated(child, left, top, left + width, bottom);
        }
        scaleView();
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    private int firstPos, lastPos, parentHeight, centerY, firstTop, distanceHeight;
    private int scrollY;

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0) {
            return 0;
        }
        if (dy > 0) {
            View lastView = getChildAt(getChildCount() - 1);
            if (lastView != null) {
                int height = getDecoratedMeasuredHeight(lastView);
                int top = parentHeight - ((parentHeight - height) / 2);
                if (getPosition(lastView) == getItemCount() - 1) {
                    if (getDecoratedBottom(lastView) - dy < top) {
                        dy = getDecoratedBottom(lastView) - top;
                    }
                }
            }
        } else if (scrollY + dy < 0) {
            dy = -scrollY;
        }
        offsetChildrenVertical(-dy);
        dy = fillViews(dy, recycler);
        scrollY += dy;
        return dy;
    }


    private int fillViews(int dy, RecyclerView.Recycler recycler) {
//        先回收后布局
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child != null) {
                if (dy > 0) {
                    if (getDecoratedBottom(child) + dy < getPaddingBottom()) {
                        removeAndRecycleView(child, recycler);
                        firstPos++;
                    }
                } else if (dy < 0) {
                    if (getDecoratedTop(child) + dy > parentHeight) {
                        removeAndRecycleView(child, recycler);
                        lastPos--;
                    }
                }
            }
        }

        if (dy < 0) {
            View firstView = getChildAt(0);
            if (firstView != null) {
                int top = getDecoratedTop(firstView);
                if (top > getPaddingTop() + distanceHeight && firstPos > 0) {
                    firstPos--;
                    View child = recycler.getViewForPosition(firstPos);
                    measureChildWithMargins(child, 0, 0);
                    int height = getDecoratedMeasuredHeight(child);
                    int width = getDecoratedMeasuredWidth(child);
                    addView(child, 0);
                    layoutDecorated(child, getPaddingLeft(), top - distanceHeight - height, getPaddingLeft() + width, top - distanceHeight);
                }
            }
        } else {
            View lastView = getChildAt(getChildCount() - 1);
            if (lastView != null) {
                int bottom = getDecoratedBottom(lastView);
                if (bottom + distanceHeight < parentHeight && lastPos < getItemCount() - 1) {
                    lastPos++;
                    View child = recycler.getViewForPosition(lastPos);
                    measureChildWithMargins(child, 0, 0);
                    int height = getDecoratedMeasuredHeight(child);
                    int width = getDecoratedMeasuredWidth(child);
                    addView(child);
                    layoutDecorated(child, getPaddingLeft(), bottom + distanceHeight, getPaddingLeft() + width, bottom + distanceHeight + height);
                }
            }
        }
        scaleView();
        View firstView = getChildAt(0);
        if (firstView != null) {
            firstPos = getPosition(firstView);
            firstTop = getDecoratedTop(firstView) - getPaddingTop();
        }
        return dy;
    }

    private void scaleView() {
        double scaleF = 0.7f, alpha = 0.5f;
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            int height = getDecoratedMeasuredHeight(view);
            //        分两种情况，从右边到中间为变大，即是 scaleF到1.0f,从中间到左边即是1.0f到scaleF
            int centerViewY = view.getTop() + height / 2;
            int viewCenter=(view.getTop()+view.getBottom());
//        子view的中心点到控件中心的距离
            float distance = Math.abs(centerY - centerViewY);

//        子view的中心点到控件中心的距离所占总view宽度的百分比
            double percent = distance / (getWidth() / 2);
            float realScaleF = getRealScaleF(scaleF, height, percent);
            float realAlpha = getRealScaleF(alpha, height, percent);
//        X是1-100 而Y的值是80到100
            view.setScaleX(realScaleF);
            view.setScaleY(realScaleF);
            view.setAlpha(realAlpha);
        }
    }

    private float getRealScaleF(double scaleF, int height, double percent) {
        double lastHeight = height * (1 - scaleF);
        float realScaleF = (float) ((height - percent * lastHeight) / height);
        if (realScaleF < scaleF) {
            realScaleF = (float) scaleF;
        } else if (realScaleF > 1.0f) {
            realScaleF = 1.0f;
        }
        return realScaleF;
    }

    interface PositionSelectChanger {
        void select(int position);
    }
}
