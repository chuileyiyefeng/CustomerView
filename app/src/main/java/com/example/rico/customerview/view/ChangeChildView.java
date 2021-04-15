package com.example.rico.customerview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * create by pan yi on 2021/4/2
 * desc :
 */
public class ChangeChildView extends ViewGroup {
    private static final String TAG = "ChangeChildView";
    protected int width, height;
    private List<RectF> originLeftRS, originRightRS, changeRectS;// 默认的控件左边位置rect,右边位置rect,变动的rect
    private int topRightDistance = 30;// 上面右边的view突出距离左边view的距离
    private int topBottomDistance = 20;// 上下两个view的间距

    // 当前模式 左边|右边
    private final int leftMode = 1, rightMode = 2;
    private int currentMode = leftMode;

    // 上面的view滑动的scale
    private float topLeftScale, topRightScale;

    public ChangeChildView(Context context) {
        this(context, null);
    }

    public ChangeChildView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChangeChildView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        changeRectS = new ArrayList<>();
        originLeftRS = new ArrayList<>();
        originRightRS = new ArrayList<>();
    }

    private float dpToPx(int dp) {
        return getResources().getDisplayMetrics().density * dp + 0.5f;
    }

    // 当前测量的子 view
    private int currentMeasureChildP = 0;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        int count = getChildCount();
        if (count != 4) {
            return;
        }
        // 1、3子view为上面的view，2、4为底下的view
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            currentMeasureChildP = i;
            measureChild(child, w, h);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            changeRectS.add(new RectF(0, 0, childWidth, childHeight));
            originLeftRS.add(new RectF());
            originRightRS.add(new RectF());
        }
    }

    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        if (currentMeasureChildP == 0 || currentMeasureChildP == 2) {
            super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);
        } else {
            final LayoutParams lp = child.getLayoutParams();
            final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                    getPaddingLeft() + getPaddingRight(), width);
            final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                    getPaddingTop() + getPaddingBottom(), lp.height);
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        if (count != 4) {
            return;
        }
        setRectValue();
        int index = 0;
        View child1 = getChildAt(index);
        RectF rect1 = changeRectS.get(index);
        child1.layout((int) rect1.left, (int) rect1.top, (int) rect1.right, (int) rect1.bottom);

        index = 1;
        View child2 = getChildAt(index);
        RectF rect2 = changeRectS.get(index);
        child2.layout((int) rect2.left, (int) rect2.top, (int) rect2.right, (int) rect2.bottom);

        index = 2;
        View child3 = getChildAt(index);
        RectF rect3 = changeRectS.get(index);
        child3.layout((int) rect3.left, (int) rect3.top, (int) rect3.right, (int) rect3.bottom);

        index = 3;
        View child4 = getChildAt(index);
        RectF rect4 = changeRectS.get(index);
        child4.layout((int) rect4.left, (int) rect4.top, (int) rect4.right, (int) rect4.bottom);
    }

    // 给rect设置值
    boolean isSetRectValue = false;

    private void setRectValue() {
        if (isSetRectValue) {
            return;
        }
        isSetRectValue = true;
        RectF changeR1 = changeRectS.get(0);
        RectF changeR2 = changeRectS.get(1);
        RectF changeR3 = changeRectS.get(2);
        RectF changeR4 = changeRectS.get(3);


        // 第一个控件位置
        originLeftRS.get(0).set(changeR1.left, changeR1.top, changeR1.right, changeR1.bottom);
        // 不考虑上面的view是否超过屏幕
        originRightRS.get(0).set(width - dpToPx(topRightDistance) - changeR3.width() - changeR1.width(), changeR1.top, width - dpToPx(topRightDistance) - changeR3.width(), changeR1.bottom);

        //第二个控件位置
        changeR2.set(changeR2.left, changeR1.bottom + dpToPx(topBottomDistance), changeR2.right, changeR1.bottom + changeR2.bottom + dpToPx(topBottomDistance));
        originLeftRS.get(1).set(changeR2.left, changeR2.top, changeR2.right, changeR2.bottom);
        originRightRS.get(1).set(-width, changeR2.top, 0, changeR2.bottom);


        //第三个控件位置
        changeR3.set(changeR1.right + dpToPx(topRightDistance), changeR3.top, changeR1.right+ dpToPx(topRightDistance) + changeR3.right, changeR3.bottom);
        originLeftRS.get(2).set(changeR3.left, changeR3.top, changeR3.right, changeR3.bottom);
        // 不考虑上面的view是否超过屏幕
        originRightRS.get(2).set(width - (changeR3.width()), changeR3.top, width, changeR3.bottom);

        //第四个控件位置
        changeR4.set(width, changeR3.bottom + dpToPx(topBottomDistance), changeR4.right + width, changeR4.bottom + changeR3.bottom + dpToPx(topBottomDistance));
        originLeftRS.get(3).set(changeR4.left, changeR4.top, changeR4.right, changeR4.bottom);
        originRightRS.get(3).set(0, changeR4.top, width, changeR4.bottom);

        topLeftScale = (changeRectS.get(2).right - width) / (float) width;
        topRightScale = -originRightRS.get(0).left / (float) width;
    }

    private float downX, lastMoveX;
    private boolean isInAnim; //是否在动画中


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isInAnim) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                lastMoveX = downX;
                break;
            case MotionEvent.ACTION_MOVE:
                float thisMoveX = event.getX() - lastMoveX;
                //限制滑动距离，view滑动不能脱离最左边和最右边
                lastMoveX = event.getX();
                Log.e(TAG, "onTouchEvent: " + currentMode);
                for (int i = 0; i < changeRectS.size(); i++) {
                    RectF rect = changeRectS.get(i);
                    if (currentMode == leftMode) {
//                        if (i == 1) {
                            if (thisMoveX > 0) {
                                // 向右移动
//                                if (rect.left + thisMoveX > 0) {
//                                    thisMoveX = -rect.left + 0.5f;
//                                }
                                return false;
                            }
//                        }
                    } else {
//                        if (i == 1) {
                            if (thisMoveX < 0) {
                                // 向左移动
//                                if (rect.right + thisMoveX < 0) {
//                                    thisMoveX = -rect.right + 0.5f;
//                                }
                                return false;
                            }
//                        }
                    }
                    if (i == 1 || i == 3) {
                        rect.set(rect.left + thisMoveX, rect.top, rect.right + thisMoveX, rect.bottom);
//                        Log.e(TAG, "onTouchEvent: " + thisMoveX + " " + rect + "  " + i);
                    } else {
                        float moveDistance = 0;
                        switch (currentMode) {
                            case  leftMode:
                                moveDistance=topLeftScale*thisMoveX;
                                break;
                            case rightMode:
                                moveDistance=topRightScale*thisMoveX;
                                break;
                        }
                        Log.e(TAG, "onTouchEvent: "+thisMoveX+"  "+topLeftScale+"  "+topRightScale);
                        rect.set(rect.left + moveDistance, rect.top, rect.right + moveDistance, rect.bottom);
//                        int topMoveX = (int) (thisMoveX * 0.8f);
//                        rect.set(rect.left + topMoveX, rect.top, rect.right + topMoveX, rect.bottom);
                    }
                }
                requestLayout();
                break;
            case MotionEvent.ACTION_UP:
                float moveDistance = event.getX() - downX;
                if (moveDistance < 0) {
                    // 手指向左滑动
                    if (currentMode == leftMode) {
                        if (Math.abs(moveDistance) > width / 3f) {
                            moveDistance = -(width + moveDistance);
                            currentMode = rightMode;
                        } else {
                            moveDistance = -moveDistance;
                        }
                    } else {
                        moveDistance = 0;
                    }

                } else {
                    // 手指向右滑动
                    if (currentMode == rightMode) {
                        if (Math.abs(moveDistance) > width / 3f) {
                            moveDistance = width - Math.abs(moveDistance);
                            currentMode = leftMode;
                        } else {
                            moveDistance = -moveDistance;
                        }
                    } else {
                        moveDistance = 0;
                    }

                }
                startEndAnimator(moveDistance);
                break;
        }
        return true;
    }

    private ValueAnimator animator;// 手指放手后的滑动动画
    private float animValue;

    private void startEndAnimator(float distance) {
        animValue = 0;
        isInAnim = true;
        if (animator == null) {
            animator = ValueAnimator.ofFloat(0, distance);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();// -1 -2 -3
                    float thisMoveX = value - animValue;
                    for (int i = 0; i < changeRectS.size(); i++) {
                        RectF rect = changeRectS.get(i);
                        if (i == 1 || i == 3) {
                            rect.set(rect.left + thisMoveX, rect.top, rect.right + thisMoveX, rect.bottom);
                        } else {
                            float moveDistance = 0;
                            switch (currentMode) {
                                case  leftMode:
                                    moveDistance=topLeftScale*thisMoveX;
                                    break;
                                case rightMode:
                                    moveDistance=topRightScale*thisMoveX;
                                    break;
                            }
                            rect.set(rect.left + moveDistance, rect.top, rect.right + moveDistance, rect.bottom);
                        }
                    }
                    animValue = value;
                    requestLayout();
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // 可能会有精度丢失问题，还原位置
                    isInAnim = false;
                    switch (currentMode) {
                        case leftMode:
                            for (int i = 0; i < changeRectS.size(); i++) {
                                RectF leftOR = originLeftRS.get(i);
                                changeRectS.get(i).set(leftOR.left, leftOR.top, leftOR.right, leftOR.bottom);
                            }
                            Log.e(TAG, "onAnimationEnd: left  " + changeRectS);
                            break;
                        case rightMode:
                            for (int i = 0; i < changeRectS.size(); i++) {
                                RectF rightOR = originRightRS.get(i);
                                changeRectS.get(i).set(rightOR.left, rightOR.top, rightOR.right, rightOR.bottom);
                            }
                            Log.e(TAG, "onAnimationEnd: right  " + changeRectS);
                            break;
                    }
                    requestLayout();
                }
            });
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setDuration(300);
        } else {
            animator.setFloatValues(0, distance);
        }
        animator.start();
    }
}
