package com.example.rico.customerview.layoutManager;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Tmp on 2019/8/19.
 * 滚轮recyclerView layoutManager
 */
public class WheelLayoutManager extends LinearLayoutManager {
    private String tag = "not";

    private Context context;


    public WheelLayoutManager(Context context) {
        super(context);
        this.context = context;
    }

    public WheelLayoutManager(Context context, String tag) {
        super(context);
        this.tag = tag;
        this.context = context;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public void scrollToPosition(int position) {
        if (position == 0) {
            firstPos = 0;
            layoutType = halfType;
        } else {
            layoutType = allType;
            if (position > 0) {
                firstPos = position - 1;
                scrollY = parentHeight * position / 2;
            }
        }
        requestLayout();
    }


    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0 && state.isPreLayout()) {
            removeAndRecycleAllViews(recycler);
            firstPos = 0;
            firstTop = 0;
            return;
        }
        detachAndScrapAttachedViews(recycler);
        parentHeight = getHeight() - getPaddingBottom();
        centerY = parentHeight / 2;
        int top;
        int left = getPaddingLeft();
        lastPos = getItemCount();
        int bottom = 0;
        if (layoutType == halfType) {
            firstTop = 0;
            scrollY = 0;
            for (int i = 0; i < lastPos; i++) {
                View child = recycler.getViewForPosition(i);
                addView(child);
                measureChildWithMargins(child, 0, 0);
                int height = getDecoratedMeasuredHeight(child);
                int width = getDecoratedMeasuredWidth(child);
                if (i == firstPos) {
                    top = firstTop + (parentHeight - height) / 2;
                } else {
                    top = parentHeight - height;
                    distanceHeight = top - bottom;
                    lastPos = i;
                }
                bottom = top + height;
                layoutDecorated(child, left, top, left + width, bottom);
            }
        } else {
            for (int i = firstPos; i < lastPos; i++) {
                View child = recycler.getViewForPosition(i);
                addView(child);
                measureChildWithMargins(child, 0, 0);
                int height = getDecoratedMeasuredHeight(child);
                int width = getDecoratedMeasuredWidth(child);
                if (i == firstPos) {
                    top = firstTop + getPaddingTop();

                } else if (i == firstPos + 1) {
                    top = bottom + distanceHeight;

                } else {
                    top = bottom + distanceHeight;

                    lastPos = i;
                }
                bottom = top + height;
                layoutDecorated(child, left, top, left + width, bottom);
            }
        }

        scaleView();
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    public static final int allType = 1, halfType = 2;
    private int firstPos, lastPos, parentHeight, centerY, firstTop, distanceHeight, layoutType = halfType;
    private int scrollY;


    public void setLayoutType(int layoutType) {
        this.layoutType = layoutType;
    }


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
        dy = fillViews(dy, recycler);
        offsetChildrenVertical(-dy);
        scrollY += dy;
        return dy;
    }


    private int fillViews(int dy, RecyclerView.Recycler recycler) {
//        先回收后布局
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child != null) {
                if (dy > 0) {
                    if (getChildCount() == 2) {
                        if (getDecoratedBottom(child) + distanceHeight + dy > parentHeight) {
                            layoutType = allType;
                        }
                    } else {
                        if (getDecoratedBottom(child) + dy < getPaddingBottom()) {
                            removeAndRecycleView(child, recycler);
                            firstPos++;
                            layoutType = allType;
                        }
                    }

                } else if (dy < 0) {
                    if (getDecoratedTop(child) + dy > parentHeight) {
                        removeAndRecycleView(child, recycler);
                        lastPos--;
                        if (lastPos == 0) {
                            layoutType = halfType;
                        }
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
            int top = getDecoratedTop(firstView);
            int height = getDecoratedMeasuredHeight(firstView);
            if (dy <= 0) {
                if (top + height / 2 - dy > parentHeight / 2 && firstPos == 0) {
                    dy = 0;
                    scrollY = 0;
                }
            }


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
            int viewCenter = (view.getTop() + view.getBottom());
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
}
