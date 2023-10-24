package com.example.rico.customerview.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: pan yi
 * @Date: 2023/10/23
 */
public class FlowExpandView extends ViewGroup {
    private List<ChildPos> list;
    private int maxLine = 2;
    private View expandView;
    private boolean isExpand = false; //是否是展开
    private int expandViewWidth;
    private int expandViewMeasureTime = 0;


    // 左右间距、上下间距
    int verticalDis, horizontalDis;


    //    设置上下间距，左右间距
    public void setMargin(int vertical, int horizontal) {
        this.verticalDis = dpToPx(vertical);
        this.horizontalDis = dpToPx(horizontal);
    }

    public void setMaxLine(int maxLine) {
        this.maxLine = maxLine;
        lastPosition = -1;
        requestLayout();
    }

    public FlowExpandView(Context context) {
        super(context);
        init();
    }

    public FlowExpandView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    boolean isChange = false;
    int lastBottom;

    private void init() {
        list = new ArrayList<>();
    }

    private int lastPosition = -1;


    @SuppressLint("DrawAllocation")
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
        int currentLine = 1;
        boolean nextChildClose = false;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize - 2 * horizontalDis, widthMode);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (child == expandView) {
                expandViewWidth = childWidth;
                expandViewMeasureTime++;
            }
//            当前子控件不超过规定宽度
            int left, top, right, bottom;
            if (child.getVisibility() != VISIBLE) {
                list.add(null);
                continue;
            }
            if (currentWidth + childWidth + horizontalDis * 2 <= widthSize) {
                isChange = false;
                left = currentWidth + horizontalDis;
                top = currentTop + verticalDis;
                right = left + childWidth;
                bottom = top + childHeight;

                currentWidth += childWidth + horizontalDis;
                if (widthMode != MeasureSpec.EXACTLY) {
                    realWidth = Math.max(currentWidth + verticalDis, realWidth);
                } else {
                    realWidth = widthSize;
                }
                lineHeight = Math.max(currentTop + childHeight + verticalDis, lineHeight);
                realHeight = heightMode == MeasureSpec.EXACTLY ? heightSize : lineHeight + verticalDis;

                ChildPos pos = new ChildPos(left, top, right, bottom);
                if (isClose() && currentLine == maxLine && child != expandView) {
                    if (right + expandViewWidth + horizontalDis * 2 > widthSize && list.size() > 1) {
                        pos = new ChildPos(0, 0, 0, 0);
                        currentWidth -= (childWidth + verticalDis);
                        nextChildClose = true;
                    }
                }

                if (isClose() && currentLine <= maxLine && child == expandView) {
                    pos = new ChildPos(0, 0, 0, 0);
                }
                if (nextChildClose && child != expandView) {
                    currentWidth -= (childWidth + verticalDis);
                    pos = new ChildPos(0, 0, 0, 0);
                }

                list.add(pos);
            } else {
                if (currentLine >= maxLine && isClose()) {
                    nextChildClose = true;
                    if (currentLine == maxLine) {
                        lastPosition = i;
                        Log.e("currentItemPosition", i + "  " + childWidth);
                    }
                    list.add(new ChildPos(0, 0, 0, 0));
                    currentLine++;
                    continue;
                }
                currentLine++;
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

//        if (listener != null && list.size() > lastPosition && lastPosition > 0) {
//            int lastRight = list.get(lastPosition).right;
//            if (lastRight + expandViewSize > widthSize) {
//                lastPosition--;
//            }
//            if (lastPosition > 0) {
//                listener.maxSize(lastPosition);
//            }
//        }
        if (!isExpand && expandViewMeasureTime == 1) {
            requestLayout();
        }

    }

    private int dpToPx(float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != VISIBLE) {
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
    private static class ChildPos {
        int left, top, right, bottom;

        private ChildPos(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
    }

    public void close() {
        isExpand = false;
        expandViewMeasureTime = 0;
        requestLayout();
    }

    public void expand() {
        isExpand = true;
        requestLayout();
    }


    private boolean isClose() {
        return !isExpand;
    }

    public void addExpandView(View expandView) {
        this.expandView = expandView;
        addView(expandView);
    }
}
