package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
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

    // 子控件之间边距
    int childRightMargin, childTopMargin, childBottomMargin;

    // 父控件宽度中点、高度中点
    int centerX, centerY;

    public void setSignDataList(ArrayList<SignData> signDataList) {
        this.signDataList = signDataList;
        childRightMargin = dpToPx(10);
        childTopMargin = dpToPx(30);
        childBottomMargin = dpToPx(30);

        post(() -> {
            parentWidth = getRight() - getLeft();
            parentHeight = getBottom() - getTop();
            centerX = parentWidth / 2;
            centerY = parentHeight / 2;

            for (int i = 0; i < signDataList.size(); i++) {
                // 先得到子view的宽高，再来排布
                SignData data = signDataList.get(i);
                View view = inflate(getContext(), R.layout.item_sign, null);
                TextView tvContentTop = view.findViewById(R.id.tv_content_top);
                TextView tvContentBottom = view.findViewById(R.id.tv_content_bottom);
                tvContentTop.setText(data.getMessage());
                tvContentBottom.setText(data.getMessage());
                int randomValue = get20Random();
                if (randomValue > dpToPx(10)) {
                    tvContentBottom.setVisibility(GONE);
                }// 在下半部分
                else {
                    tvContentTop.setVisibility(GONE);
                }
                addView(view);
                measureChild(view, parentWidth, parentHeight);
                int childWidth = view.getMeasuredWidth();
                int childHeight = view.getMeasuredHeight();
                // 设置子view的起始位置
                if (i == 0) {
                    data.setLeft(-leftMargin);
                    data.setTop((parentHeight - childHeight) / 2 + get20Random());
                }//后续子view的位置
                else {
                    // 上一个子view的位置
                    RectF lastRect = rectS.get(i - 1);
                    // 随机排序在父view的上半部分还是下半部分

                    // 把当前这个view放在上半部分

                    if (randomValue > dpToPx(10)) {
                        //
                        if (lastRect.top <= centerY) {
                            data.setTop((int) (lastRect.top - childHeight - get20Random() - childTopMargin));
                            data.setLeft((int) lastRect.right + childRightMargin);
                        } else {
                            data.setTop(centerY - childHeight - get20Random() - childTopMargin);
                            data.setLeft((int) ((int) lastRect.left + childRightMargin + (lastRect.right - lastRect.left) / 2));
                            resetContain(i, data, childWidth, childHeight);
                        }

                    }// 在下半部分
                    else {
                        if (lastRect.bottom >= centerY) {
                            data.setTop((int) (lastRect.bottom + get20Random() + childBottomMargin));
                            data.setLeft((int) lastRect.right + childRightMargin);
                        } else {
                            data.setTop(centerY + get20Random() + childBottomMargin);
                            data.setLeft((int) ((int) lastRect.left + childRightMargin + (lastRect.right - lastRect.left) / 2));
                            resetContain(i, data, childWidth, childHeight);
                        }
                    }
                }
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
                int left = data.getLeft();
                int top = data.getTop();
                rect.set(left, top, left + childWidth, top + childHeight);

                rectS.add(rect);
            }
            checkContain();

            // 添加完子view后要判断子view的范围，然后让子view是否在屏幕外或者在屏幕内
            // 都不在的话，重新设置范围
            if (!isContainChild && !isLargeParent) {
                Log.e("inValue", "setSignDataList: false");
                float itemRowWidth = itemRectF.right - itemRectF.left;
                float itemRowHeight = itemRectF.bottom - itemRectF.top;
                // 每个子view需要移动的距离
                float distanceX, distanceY;

                // 如果item范围比父view小，只需要整体移动就行，并把子view居中放置
                // 居中起始点 x、y
                float startX = (parentWidth - itemRowWidth) / 2;
                float startY = (parentHeight - itemRowHeight) / 2;

                if (itemRowWidth <= parentWidth && itemRowHeight <= parentHeight) {
                    centerItem(startX, startY);
                    isContainChild = true;
                } else {
                    if (rectS.size() < 1) {
                        return;
                    }
                    boolean needStretchX = false, needStretchY = false;

                    // y轴的拉伸距离，分两个方向
                    float distanceTopY = 0, distanceBottomY = 0;

                    // 需要拉伸宽度
                    if (parentWidth > itemRowWidth) {
                        needStretchX = true;
                        distanceX = (parentWidth - itemRowWidth) / rectS.size() - 1;
                    }// 不需要拉伸
                    else {
                        distanceX = startX - rectS.get(minLeftPosition).left;
                    }
                    // 需要拉伸高度
                    if (parentHeight > itemRowHeight) {
                        needStretchY = true;
                        float minTop = rectS.get(minTopPosition).top;
                        float maxBottom = rectS.get(maxBottomPosition).bottom;
                        if (minTop > centerY || maxBottom < centerY) {
                            centerY = (int) ((maxBottom + minTop) / 2);
                        }
                        distanceTopY = (getTop() - topMargin) - minTop;
                        distanceBottomY = getBottom() + bottomMargin - maxBottom;
                        distanceY = distanceTopY + distanceBottomY;
                    }// 不需要拉伸
                    else {
                        distanceY = startY - rectS.get(minTopPosition).top;
                    }
                    Log.e("moveDistance", "setSignDataList: " + distanceX + " " + distanceTopY + " " + distanceBottomY + " " + centerY);
                    for (int i = 0; i < rectS.size(); i++) {
                        RectF rectF = rectS.get(i);
                        if (rectF.right < centerX && needStretchX) {
                            distanceX = -distanceX;
                        }
                        if (needStretchY) {
                            if (rectF.bottom < centerY) {

                                distanceY = distanceTopY;
                            } else {
                                distanceY = distanceBottomY;
                            }
                        }
                        rectF.left = rectF.left + distanceX;
                        rectF.right = rectF.right + distanceX;
                        rectF.top = rectF.top + distanceY;
                        rectF.bottom = rectF.bottom + distanceY;

                    }

                    isLargeParent = true;
                }
            }

            itemSpanX = itemRectF.right - itemRectF.left;
            itemSpanY = itemRectF.bottom - itemRectF.top;
            Log.e("isValue1", "setSignDataList: " + itemRectF.toString());
//            Log.e("isValue2", "setSignDataList: "+( itemRectF.left < leftMargin) );
//            Log.e("isValue3", "setSignDataList: "+(  itemRectF.right > parentWidth - rightMargin) );
//            Log.e("isValue4", "setSignDataList: "+(  itemRectF.top < topMargin) );
//            Log.e("isValue5", "setSignDataList: "+( itemRectF.bottom > parentHeight - bottomMargin) );
            requestLayout();
            invalidate();
        });
    }

    // 把子view居中
    private void centerItem(float startX, float startY) {
        float distanceX;
        float distanceY;
        distanceX = startX - rectS.get(minLeftPosition).left;
        distanceY = startY - rectS.get(minLeftPosition).top;
        for (int i = 0; i < rectS.size(); i++) {
            RectF rectF = rectS.get(i);
            rectF.left = rectF.left + distanceX;
            rectF.right = rectF.right + distanceX;
            rectF.top = rectF.top + distanceY;
            rectF.bottom = rectF.bottom + distanceY;
            if (minLeftPoint >= rectF.left) {
                minLeftPoint = rectF.left;
                minLeftPosition = i;
            }
            if (maxRightPoint <= rectF.right) {
                maxRightPoint = rectF.right;
                maxRightPosition = i;
            }
            if (minTopPoint >= rectF.top) {
                minTopPoint = rectF.top;
                minTopPosition = i;
            }
            if (maxBottomPoint <= rectF.bottom) {
                maxBottomPoint = rectF.bottom;
                maxBottomPosition = i;
            }
        }
        checkContain();
    }

    // 判断子view对于父view属性
    private void checkContain() {
        itemRectF = new RectF(minLeftPoint, minTopPoint, maxRightPoint, maxBottomPoint);

        isContainChild = itemRectF.left >= leftMargin && itemRectF.right <= parentWidth - rightMargin
                && itemRectF.top >= topMargin && itemRectF.bottom <= parentHeight - bottomMargin;

        isLargeParent = itemRectF.left < leftMargin && itemRectF.right > parentWidth - rightMargin
                && itemRectF.top < topMargin && itemRectF.bottom > parentHeight - bottomMargin;
    }

    // 判断是否有相交点，然后重新设置位置
    private void resetContain(int i, SignData data, int childWidth, int childHeight) {
        boolean isIntersect = false;
        int maxRight = Integer.MIN_VALUE;
        // 判断矩形是否相交 可能有多个相交点
        // 当前view的左上 右上 左下 右下4个点
        Point point1 = new Point(data.getLeft(), data.getTop());
        Point point2 = new Point(data.getLeft() + childWidth, data.getTop());
        Point point3 = new Point(data.getLeft(), data.getTop() + childHeight);
        Point point4 = new Point(data.getLeft() + childWidth, data.getTop() + childHeight);
        for (int k = 0; k < i; k++) {
            RectF rectF = rectS.get(k);
            boolean b1 = rectContainPoint(rectF, point1);
            boolean b2 = rectContainPoint(rectF, point2);
            boolean b3 = rectContainPoint(rectF, point3);
            boolean b4 = rectContainPoint(rectF, point4);
            // 有一个点在，说明包含了
            if (!isIntersect) {
                isIntersect = b1 || b2 || b3 || b4;
            }
            if (maxRight <= rectF.right) {
                maxRight = (int) (rectF.right + (rectF.right - rectF.left) / 2);
            }
        }
        if (isIntersect) {
            data.setLeft(maxRight + childRightMargin);
        }
    }

    private boolean rectContainPoint(RectF rectF, Point point) {
        return point.x >= rectF.left && point.x <= rectF.right && point.y >= rectF.top && point.y <= rectF.bottom;
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

    // 获取 dp 20以内的随机距离
    private int get20Random() {
        return dpToPx((int) (Math.random() * 20));
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
