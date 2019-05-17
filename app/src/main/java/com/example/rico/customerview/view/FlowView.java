package com.example.rico.customerview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tmp on 2019/2/19.
 * 流式布局view
 */
public class FlowView extends ViewGroup {
    private List<ChildPos> list;

    int verticalDis, horizontalDis;


    //    设置上下间距，左右间距
    public void setMargin(int vertical, int horizontal) {
        this.verticalDis = dpToPx(vertical);
        this.horizontalDis = dpToPx(horizontal);
    }


    public FlowView(Context context) {
        super(context);
        init();
    }

    public FlowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FlowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    boolean isChange = false;
    int lastBottom;

    private void init() {
        list = new ArrayList<>();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        list.clear();
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        //获取流式布局的高度和模式
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int realWidth = 0, realHeight = 0;
//            当前控件所占宽高,当前顶部的高度，当前行最高高度
        int currentWidth = 0, currentTop = 0, lineHeight = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize - 2 * horizontalDis, widthMode);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
//            当前子控件不超过规定宽度
            int left, top, right, bottom;
            if (child.getVisibility()!=VISIBLE) {
                list.add(null);
                continue;
            }
            if (currentWidth + childWidth + horizontalDis * 2 <= widthSize) {
                isChange = false;
                left = currentWidth + horizontalDis;
                top = currentTop + verticalDis;
                right = left + childWidth;
                bottom = top + childHeight;

                currentWidth += childWidth + verticalDis;
                if (widthMode != MeasureSpec.EXACTLY) {
                    realWidth = Math.max(currentWidth + verticalDis, realWidth);
                } else {
                    realWidth = widthSize;
                }
                lineHeight = Math.max(currentTop + childHeight + verticalDis, lineHeight);
                realHeight = heightMode == MeasureSpec.EXACTLY ? heightSize : lineHeight + verticalDis;

                list.add(new ChildPos(left, top, right, bottom));
            } else {
                realWidth = widthSize;
                //               换行了，但是这一行占满了整行，要继续换行,也就是连续换行的情况
                if (isChange) {
                    lineHeight = lastBottom;
                }
                currentTop = lineHeight;
                isChange = true;

                left = horizontalDis;
                top = lineHeight + verticalDis;
                right = left + childWidth;
                bottom = top + childHeight;
                lastBottom = bottom;
                currentWidth = childWidth + horizontalDis;
                realHeight = heightMode == MeasureSpec.EXACTLY ? heightSize : bottom + verticalDis;

                list.add(new ChildPos(left, top, right, bottom));
            }
        }
        realWidth = widthMode == MeasureSpec.EXACTLY ? widthSize : realWidth;

        setMeasuredDimension(realWidth, realHeight);
    }

    private int dpToPx(float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility()!=VISIBLE) {
                continue;
            }
            //设置View的左边、上边、右边底边位置
            ChildPos pos = list.get(i);
            child.layout(pos.left, pos.top, pos.right, pos.bottom);
        }
    }

    /**
     * 让ViewGroup能够支持margin属性
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    //    子控件位置信息
    private class ChildPos {
        int left, top, right, bottom;

        public ChildPos(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
    }
}
