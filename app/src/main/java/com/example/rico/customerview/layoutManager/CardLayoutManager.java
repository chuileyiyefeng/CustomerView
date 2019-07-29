package com.example.rico.customerview.layoutManager;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Tmp on 2019/7/26.
 */
public class CardLayoutManager extends RecyclerView.LayoutManager {

    //    view叠加的次数
    private final int superPosition = 5;
    //    底下最小的view缩放值
    private final float minScale = 0.8f;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    private int parentWidth, parentHeight;

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }
        detachAndScrapAttachedViews(recycler);
        parentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        parentHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int time = superPosition;
        int top = getPaddingTop();
        int allHeight = 0;
        float everyHeight = 0.0f;
        float everyScale = (1 - minScale) / (superPosition - 1);
//        当child在移动时，底下应该有个和child一样大小的view
//        这样视觉不会突兀
        for (int i = getItemCount() - 1; i >= 0; i--) {
            View child = recycler.getViewForPosition(i);
            measureChildWithMargins(child, 0, 0);
            int width = getDecoratedMeasuredWidth(child);
            int height = getDecoratedMeasuredHeight(child);
            if (allHeight == 0) {
                allHeight = height;
                everyHeight = allHeight /5/ superPosition;
            }
            top = (parentHeight - height) / 2+(int) (everyHeight * time);
            int left = (parentWidth - width) / 2 + getPaddingLeft();
            layoutDecorated(child, left, top, left + width, top + height);
            float scale = 1 - time * everyScale;
            child.setScaleY(scale);
            child.setScaleX(scale);
            addView(child);
            if (time > 0) {
                time--;
            } else {
                break;
            }
        }
    }
}
