package com.example.rico.customerview.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tmp on 2019/8/31.
 */
public class ExchangeView extends ViewGroup {
    public ExchangeView(Context context) {
        this(context, null);
    }

    public ExchangeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExchangeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // 当前view绘制的rect,view移动时的参照rect，精准位置的rect
    private List<RectF> rectS, orRectList;

    private void init() {
        integers = new ArrayList<>();
        rectS = new ArrayList<>();
        orRectList = new ArrayList<>();
        setPosition(0, 2, 1);
    }

    private int parentWidth, centerX;

    private int moveMaxDistance;

    // 滑动的的距离要比手指的距离小，这个变量的缩放比例
    float moveScale;
    // 边界的view移动的距离比实际距离大
    float disMoveScale;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 默认的子控件布局为左、中间、右
        rectS.clear();
        orRectList.clear();
        parentWidth = w;
        centerX = w / 2;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, w, h);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            RectF rect = new RectF();
            int left = getPaddingLeft();
            int top = (h - childHeight) / 2;
            switch (i) {
                case 0:
                    left = getPaddingLeft();
                    break;
                case 1:
                    disMoveScale = (parentWidth - (left + childWidth)) / ((w - childWidth) / 2);
                    left = (w - childWidth) / 2;
                    int middleDistance = left - getPaddingLeft();
                    moveScale = middleDistance / (float) w;
                    break;
                case 2:
                    left = w - getPaddingRight() - childWidth;
                    break;
            }
            rect.set(left, top, left + childWidth, top + childHeight);
            rectS.add(rect);
            RectF newRect = new RectF(rect.left, rect.top, rect.right, rect.bottom);
            orRectList.add(newRect);
        }
    }

    //  三个参数是view叠加顺序
    private void setPosition(int j, int k, int l) {
        integers.clear();
        integers.add(j);
        integers.add(k);
        integers.add(l);
    }

    //  用view的TranslationZ属性来实现层叠效果
    //  onLayout先排列view，然后置顶view
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            RectF rect = rectS.get(i);
            child.layout((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);
            if (i == integers.get(0)) {
                scaleViewMin(child);
            } else {
                scaleView(child);
            }
            if (i == getChildCount() - 1) {
                moveMaxDistance = (parentWidth - child.getMeasuredWidth()) / 2;
            }
        }
        toTopView();
    }

    // 置顶view，使用translation属性来实现顶部view效果
    private void toTopView() {
        // 这里直接用i值来setTranslationZ
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(integers.get(i));
            child.setTranslationZ(i);
        }
    }

    float scaleF = 0.7f;
    float alpha = 0.6f;

    private void scaleViewMin(View view) {
        view.setScaleX(scaleF);
        view.setScaleY(scaleF);
        view.setAlpha(alpha);
    }

    private void scaleView(View view) {
        int width = view.getMeasuredWidth();
        //        分两种情况，从右边到中间为变大，即是 scaleF到1.0f,从中间到左边即是1.0f到scaleF
        int centerViewX = view.getLeft() + width / 2;
//        子view的中心点到控件中心的距离
        float distance = Math.abs(centerX - centerViewX);
//        子view的中心点到控件中心的距离所占总view宽度的百分比
        double percent = distance / (getWidth() / 2 - width / 2);
        float realScaleF = getReal(width, percent, scaleF);
        float realAlpha = getReal(width, percent, alpha);
//        X是1-100 而Y的值是80到100
        view.setScaleX(realScaleF);
        view.setScaleY(realScaleF);
        view.setAlpha(realAlpha);
    }

    float downX, downY;
    private ArrayList<Integer> integers;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animator != null && animator.isRunning()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveView(event);
                break;
            case MotionEvent.ACTION_UP:
                scrollToCenter();
                break;
        }
        return true;

    }

    // 顶部view下标，最底部view下标
    int topPos, bottomPos;

    // 改变view的位置

    private void moveView(MotionEvent event) {
        float distance = event.getX() - downX;
        distance = distance * moveScale;
        if (Math.abs(distance) > moveMaxDistance) {
            return;
        }
        //  通过view的层叠顺序来控制移动的距离
        for (int i = 0; i < getChildCount(); i++) {
            RectF originRect = orRectList.get(i);
            RectF rect = rectS.get(i);
            float width = rect.right - rect.left;
            rect.left = originRect.left + distance;
            if (originRect.right + distance > parentWidth || originRect.left + distance < getPaddingLeft()) {
                rect.left = originRect.left - distance * disMoveScale;
                bottomPos = i;
            }
            rect.right = rect.left + width;
        }
        topPos = getNearestPos(bottomPos);
        int over = getChildCount() - bottomPos - topPos;
        setPosition(bottomPos, over, topPos);
        requestLayout();
    }

    private ValueAnimator animator;

    //松手后将view滚动到中间

    // 顶部view到中间点的距离，底部view到边界的距离
    float topDis, bottomDis;
    // 上面两个变量距离比例
    float endScale;

    private void scrollToCenter() {
        changeData();
        RectF topRect = rectS.get(topPos);
        RectF bottomRect = rectS.get(bottomPos);
        topDis = centerX - ((topRect.left + topRect.right) / 2);
        bottomDis = Math.min(bottomRect.left, parentWidth - bottomRect.right);
        endScale = Math.abs(bottomDis / topDis);
        if (animator == null) {
            animator = ValueAnimator.ofFloat(0, topDis);
            animator.setDuration(300);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    // 这里直接算三个view距离他们要到的位置的距离
                    for (int i = 0; i < rectS.size(); i++) {
                        RectF rect = rectS.get(i);
                        RectF oR = orRectList.get(i);
                        if (i == bottomPos) {
                            float realValue = (int) (value * endScale);
                            rect.left = oR.left - realValue;
                            rect.right = oR.right - realValue;
                        } else {
                            rect.left = oR.left + value;
                            rect.right = oR.right + value;
                        }
                    }
                    // 移动会有误差，这里把位置精准还原
                    if (value == topDis) {
                        reduction();
                        changeData();
                    }
                    requestLayout();
                }
            });
        } else {
            animator.setFloatValues(0, topDis);
        }
        animator.start();
    }

    // 还原位置
    private void reduction() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            RectF rect = rectS.get(i);
            int childWidth = child.getMeasuredWidth();
            int centerViewX = child.getLeft() + childWidth / 2;
            if (i == topPos) {
                rect.left = (parentWidth - childWidth) / 2;
                rect.right = rect.left + childWidth;
            } else {
                if (centerViewX > centerX) {
                    rect.right = parentWidth - getPaddingRight();
                    rect.left = rect.right - childWidth;
                } else {
                    rect.left = getPaddingLeft();
                    rect.right = rect.left + childWidth;
                }
            }
        }
    }

    // 数据变换
    private void changeData() {
        for (int i = 0; i < rectS.size(); i++) {
            RectF rect = rectS.get(i);
            RectF oR = orRectList.get(i);
            oR.left = rect.left;
            oR.right = rect.right;
        }
    }

    // 获取离中心点最近的那个view,排除下标为notThis的view
    private int getNearestPos(int notThis) {
        int nearestPos = 0, nearestDistance = getWidth();
        for (int i = 0; i < rectS.size(); i++) {
            RectF rect = rectS.get(i);
            int width = (int) (rect.right - rect.left);
            int left = (int) rect.left;
            int centerViewX = left + width / 2;
            int currentDistance = Math.abs(centerViewX - centerX);
            if (nearestDistance >= currentDistance && i != notThis) {
                nearestPos = i;
                nearestDistance = currentDistance;
            }
        }

        return nearestPos;
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

    /**
     * 让ViewGroup能够支持margin属性
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
