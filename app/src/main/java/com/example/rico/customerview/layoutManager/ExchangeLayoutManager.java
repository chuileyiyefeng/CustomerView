package com.example.rico.customerview.layoutManager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Tmp on 2019/7/31.
 */
public class ExchangeLayoutManager extends LinearLayoutManager {
    private int centerX;

    public ExchangeLayoutManager(Context context) {
        super(context);
        setOrientation(LinearLayoutManager.HORIZONTAL);
    }

    public ExchangeLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public ExchangeLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

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
        integers = new ArrayList<>();
        addPosition(0, 1, 2);
        int parentWidth = getWidth();
        int parentHeight = getHeight();
        centerX = parentWidth / 2;
//       这里只显示三个view
        for (int i = 0; i < getItemCount(); i++) {
            View child = recycler.getViewForPosition(i);
            measureChildWithMargins(child, 0, 0);
            int width = getDecoratedMeasuredWidth(child);
            int height = getDecoratedMeasuredHeight(child);
            int left = getPaddingLeft(), right;
            switch (i) {
                case 0:
                    left = getPaddingLeft();
                    addView(child);
                    break;
                case 1:
                    left = getPaddingLeft() + (parentWidth - width) / 2;
                    addView(child);
                    break;
                case 2:
                    left = parentWidth - getPaddingRight() - width;
                    addView(child, 0);
                    break;
            }
            right = left + width;
            top = getPaddingTop() + (parentHeight - height) / 2;
            bottom = top + height;
            layoutDecorated(child, left, top, right, bottom);
            scaleView(child);
        }

    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    private int scrollX, top, bottom;

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        offsetChildrenHorizontal(-dx);
        dx = layoutChild(dx, recycler);
        return dx;
    }

    private int layoutChild(int dx, RecyclerView.Recycler recycler) {
        // 先计算三个view的添加顺序
        int nearestPos = 0, nearestDistance = getWidth();
        removeAndRecycleAllViews(recycler);
        for (int i = 0; i < getItemCount(); i++) {
            View child = recycler.getViewForPosition(i);
            int width = getDecoratedMeasuredWidth(child);
            int left = getDecoratedLeft(child);
            int centerViewX = left + width / 2;
            int currentDistance = Math.abs(centerViewX - centerX);
            if (nearestDistance > currentDistance) {
                nearestPos = i;
                nearestDistance = currentDistance;
            }
            layoutDecorated(child, left, top, left + width, bottom);
            addView(child);
        }
        integers.clear();
        switch (nearestPos) {
            case 0:
                addPosition(2, 1, 0);
                break;
            case 1:
                addPosition(0, 2, 1);
                break;
            case 2:
                addPosition(0, 1, 2);
                break;
        }
        removeAndRecycleAllViews(recycler);
        for (int i = 0; i < getItemCount(); i++) {
            View child = recycler.getViewForPosition(integers.get(i));
            int width = getDecoratedMeasuredWidth(child);
            int left = getDecoratedLeft(child);
            layoutDecorated(child, left, top, left + width, bottom);
            child.setClickable(i == getItemCount() - 1);
            addView(child);
            scaleView(child);

        }
        return dx;
    }

    private void addPosition(int j, int k, int l) {
        integers.add(j);
        integers.add(k);
        integers.add(l);
    }

    private ArrayList<Integer> integers;

    private void scaleView(View view) {
        int width = getDecoratedMeasuredWidth(view);
        //        分两种情况，从右边到中间为变大，即是 scaleF到1.0f,从中间到左边即是1.0f到scaleF
        int centerViewX = view.getLeft() + width / 2;
//        子view的中心点到控件中心的距离
        float distance = Math.abs(centerX - centerViewX);

//        子view的中心点到控件中心的距离所占总view宽度的百分比
        double percent = distance / (getWidth() / 2 - width / 2);

        float scaleF = 0.8f;
        float alpha = 0.7f;
        float realScaleF = getReal(width, percent, scaleF);
        float realAlpha = getReal(width, percent, alpha);
//        X是1-100 而Y的值是80到100
        view.setScaleX(realScaleF);
        view.setScaleY(realScaleF);
        view.setAlpha(realAlpha);
    }

    private float getReal(int width, double percent, float scaleF) {
        double lastWidth = width * (1 - scaleF);
        float realScaleF = (float) ((width - percent * lastWidth) / width);
        if (realScaleF < scaleF) {
            realScaleF = scaleF;
        } else if (realScaleF > 1.0f) {
            realScaleF = 1.0f;
        }
        return realScaleF;
    }
}
