package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rico.customerview.R;
import com.example.rico.customerview.bean.SignData;

import java.util.ArrayList;
import java.util.List;

public class ScrollSignView extends ViewGroup {
    GestureDetector simpleDetector;
    int defaultMargin = dpToPx(20);
    int leftMargin = defaultMargin, topMargin = defaultMargin, rightMargin = defaultMargin, bottomMargin = defaultMargin;

    public ScrollSignView(Context context) {
        this(context, null);
    }

    public ScrollSignView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollSignView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // 当前view绘制的rect,view移动时的参照rect
    private ArrayList<RectF> rectS;

    // 显示的数据

    private ArrayList<SignData> signDataList;

    // 最左、最上、最右、最下的点
    private float minLeftPoint = Integer.MAX_VALUE, minTopPoint = Integer.MAX_VALUE, maxRightPoint = Integer.MIN_VALUE, maxBottomPoint = Integer.MIN_VALUE;
    // 最小的左边距、上边距 最大的右边距、下边距
    private int minLeftPosition, minTopPosition, maxRightPosition, maxBottomPosition;

    private RectF itemRectF;
    // 父控件是否包含子控件,即子控件都在父控件范围内
    boolean isContainChild;
    // 子控件范围大于父控件,即子控件都在父控件范围外
    boolean isLargeParent;

    // 子控件横跨的范围

    float itemSpanX, itemSpanY;


    public void setSignDataList(ArrayList<SignData> signDataList) {
        this.signDataList = signDataList;
//        post(() -> {
            for (int i = 0; i < signDataList.size(); i++) {
                SignData data = signDataList.get(i);
                data.setTop(dpToPx(data.getTop()));
                data.setLeft(dpToPx(data.getLeft()));
                View view = inflate(getContext(), R.layout.item_sign, null);
                TextView tv = view.findViewById(R.id.tv_content);
                tv.setText(data.getMessage());
                addView(view);
                measureChild(view, parentWidth, parentHeight);
                int childWidth = view.getMeasuredWidth();
                int childHeight = view.getMeasuredHeight();
                if (minLeftPoint >= data.getLeft()) {
                    minLeftPoint = data.getLeft();
                    minLeftPosition = i;
                }
                if (maxRightPoint <= data.getLeft() + childWidth) {
                    maxRightPoint = data.getLeft() + childWidth;
                    maxRightPosition = i;
                }
                if (minTopPoint >= data.getTop()) {
                    minTopPoint = data.getTop();
                    minTopPosition = i;
                }
                if (maxBottomPoint <= data.getTop() + childHeight) {
                    maxBottomPoint = data.getTop() + childHeight;
                    maxBottomPosition = i;
                }
                RectF rect = new RectF();
                int left = leftMargin + data.getLeft();
                int top = topMargin + data.getTop();
                rect.set(left, top, left + childWidth, top + childHeight);
                rectS.add(rect);
            }
            itemRectF = new RectF(minLeftPoint + leftMargin, minTopPoint + topMargin, maxRightPoint + rightMargin, maxBottomPoint + bottomMargin);
            isContainChild = itemRectF.left >= leftMargin && itemRectF.right <= parentWidth - rightMargin
                    && itemRectF.top >= topMargin && itemRectF.bottom <= parentHeight - bottomMargin;

            isLargeParent = itemRectF.left < leftMargin && itemRectF.right > parentWidth - rightMargin
                    && itemRectF.top < topMargin && itemRectF.bottom > parentHeight - bottomMargin;
            itemSpanX = itemRectF.right - itemRectF.left;
            itemSpanY = itemRectF.bottom - itemRectF.top;
//            Log.e("isValue1", "setSignDataList: "+itemRectF.toString() );
//            Log.e("isValue2", "setSignDataList: "+( itemRectF.left < leftMargin) );
//            Log.e("isValue3", "setSignDataList: "+(  itemRectF.right > parentWidth - rightMargin) );
//            Log.e("isValue4", "setSignDataList: "+(  itemRectF.top < topMargin) );
//            Log.e("isValue5", "setSignDataList: "+( itemRectF.bottom > parentHeight - bottomMargin) );
            requestLayout();
            invalidate();
//        });
    }

    private void init() {
        rectS = new ArrayList<>();
        signDataList = new ArrayList<>();
        simpleDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                moveView(-distanceX, -distanceY);
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                return false;
            }

        });
    }

    int parentWidth, parentHeight;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        parentWidth = w;
        parentHeight = h;
    }


    //  用view的TranslationZ属性来实现层叠效果
    //  onLayout先排列view，然后置顶view
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            RectF rect = rectS.get(i);
            child.layout((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);
        }
    }


    float downX, downY;

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        simpleDetector.onTouchEvent(event);
        return true;

    }

    private int dpToPx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    // 改变view的位置  手指向右下参数为正数 左上为负数
    private void moveView(float distanceX, float distanceY) {
        Log.e("moveView", "moveView: " + distanceX + " " + distanceY);
        for (int i = 0; i < getChildCount(); i++) {
            RectF rect = rectS.get(i);
            float left = rect.left;
            float top = rect.top;
            float right = rect.right;
            float bottom = rect.bottom;
            // 如果子view在父控件里面
            if (isContainChild) {
                if (i == minLeftPosition) {
                    if (left + distanceX < leftMargin) {
                        distanceX = defaultMargin - left;
                    }
                }
                if (i == minTopPosition) {
                    if (top + distanceY < topMargin) {
                        distanceY = topMargin - top;
                    }
                }
                if (i == maxRightPosition) {
                    if (right + distanceX > parentWidth - rightMargin) {
                        distanceX = parentWidth - defaultMargin - right;
                    }
                }
                if (i == maxBottomPosition) {
                    if (bottom + distanceY > parentHeight - bottomMargin) {
                        distanceY = parentHeight - bottomMargin - bottom;
                    }
                }
            }// 如果父控件小于子控件
            else if (isLargeParent) {
                if (i == minLeftPosition) {
                    if (left + distanceX > leftMargin) {
                        distanceX = defaultMargin - left;
                    }
                }
                if (i == minTopPosition) {
                    if (top + distanceY > topMargin) {
                        distanceY = topMargin - top;
                    }
                }
                if (i == maxRightPosition) {
                    if (right + distanceX < parentWidth - rightMargin) {
                        distanceX = parentWidth - defaultMargin - right;
                    }
                }
                if (i == maxBottomPosition) {
                    if (bottom + distanceY < parentHeight - bottomMargin) {
                        distanceY = parentHeight - bottomMargin - bottom;
                    }
                }
            } else {
                // 左在屏幕内，右在屏幕外
//                if (i == maxRightPosition) {
//                    boolean isLeft;
//                    if (itemRectF.left > leftMargin && itemRectF.right > parentWidth - rightMargin) {
//                        if (right + distanceX < parentWidth - rightMargin) {
//                            distanceX = parentWidth - defaultMargin - right;
//                        }
//                    }
//                }
                // 右在屏幕内，左在屏幕外
//                if (i == minLeftPosition) {
//                    if (itemRectF.left < leftMargin && itemRectF.right > parentWidth - rightMargin) {
//
//                    }
//                }
                // 左右都在屏幕外

                // 左右都在屏幕内
            }
        }
        for (int i = 0; i < getChildCount(); i++) {
            RectF rect = rectS.get(i);
            float width = rect.right - rect.left;
            float height = rect.bottom - rect.top;
            rect.left = rect.left + distanceX;
            rect.right = rect.left + width;
            rect.top = rect.top + distanceY;
            rect.bottom = rect.top + height;
        }
        requestLayout();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    /**
     * 让ViewGroup能够支持margin属性
     */
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ViewGroup.MarginLayoutParams(getContext(), attrs);
    }
}
