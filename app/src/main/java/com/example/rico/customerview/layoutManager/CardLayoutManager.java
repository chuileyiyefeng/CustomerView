package com.example.rico.customerview.layoutManager;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Tmp on 2019/7/26.
 */
public class CardLayoutManager extends RecyclerView.LayoutManager {


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
        //    view叠加的次数
        int superPosition = 5;
        int parentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int parentHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int time = superPosition - 1;
        float minScale = 0.8f;
        float everyScale = (1 - minScale) / (superPosition - 1);
//        当child在移动时，底下应该有个和child一样大小的view
//        这样视觉不会突兀
        for (int i = 0; i < getItemCount(); i++) {
            View child = recycler.getViewForPosition(i);
            measureChildWithMargins(child, 0, 0);
            int width = getDecoratedMeasuredWidth(child);
            int height = getDecoratedMeasuredHeight(child);
            float everyHeight = height * everyScale * 0.7f;
            int top = (parentHeight - height) / 2 + (int) (everyHeight * (superPosition - time));
            int left = (parentWidth - width) / 2 + getPaddingLeft();
            layoutDecorated(child, left, top, left + width, top + height);
//            这里time初始值为大值 因为第一个item是1f缩放值大小
            float scale = minScale + time * everyScale;
            child.setScaleY(scale);
            child.setScaleX(scale);

            addView(child, 0);
            if (time > 0) {
                if (i != 0) {
                    time--;
                }
            } else {
                break;
            }
        }
    }
}
