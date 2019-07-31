package com.example.rico.customerview.layoutManager;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Tmp on 2019/7/31.
 */
public class ExchangeLayoutManager extends RecyclerView.LayoutManager {
    int parentWidth, parentHeight;

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
        parentWidth = getWidth();
        parentHeight = getHeight();
        float scale = 0.8f, alpha = 0.6f;

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
                    left = getPaddingLeft() + (parentWidth - width) / 2;
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

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        layoutChild(dx);
        return 0;
    }

    private void layoutChild(int dx) {

    }
}
