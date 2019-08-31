package com.example.rico.customerview.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tmp on 2019/8/31.
 */
public class ExchangeView extends ViewGroup {
    public ExchangeView(Context context) {
        super(context);
        init();
    }

    public ExchangeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExchangeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private List<Rect> rectS;

    private void init() {
        rectS = new ArrayList<>();
        integers = new ArrayList<>();
    }

    private int parentWidth, centerX;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    // 父控件的左padding 中间的view左边到父控件的距离
    private int parentLeft, middleDistance;

    // 滑动的的距离要比手指的距离小，这个变量的缩放比例
    float moveScale;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 默认的子控件布局为左、中间、右
        rectS.clear();
        integers.clear();
        parentLeft = getPaddingLeft();
        addPosition(2, 1, 0);
        parentWidth = w;
        centerX = w / 2;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, w, h);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            Rect rect = new Rect();
            int left = getPaddingLeft();
            int top = (h - childHeight) / 2;
            switch (i) {
                case 0:
                    left = getPaddingLeft();
                    break;
                case 1:
                    left = (w - childWidth) / 2;
                    middleDistance = left - getPaddingLeft();
                    moveScale = middleDistance / (float) w;
                    break;
                case 2:
                    left = w - getPaddingRight() - childWidth;
                    break;
            }
            rect.set(left, top, left + childWidth, top + childHeight);
            rectS.add(rect);
        }
    }

    private void addPosition(int j, int k, int l) {
        integers.add(j);
        integers.add(k);
        integers.add(l);
    }

    int lastPosition = -1;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        integers.clear();
        int nearestPos = 0;
        float nearestDistance = parentWidth;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            Rect rect = rectS.get(i);
            child.layout(rect.left, rect.top, rect.right, rect.bottom);
            int left = child.getLeft();
            float centerViewX = left + (float) centerX / 2;
            float currentDistance = Math.abs(centerViewX - centerX);
            if (nearestDistance > currentDistance) {
                nearestPos = i;
                nearestDistance = currentDistance;
            }
            scaleView(child);
        }
        // 这里按照view切换时候的效果来看添加顺序
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
        if (lastPosition != nearestPos) {
            toTopView();
            lastPosition = nearestPos;
        }
    }

    // 置顶view，使用translation属性来实现顶部view效果
    private void toTopView() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(integers.get(i));
            child.setTranslationZ(i);
        }
    }

    private void scaleView(View view) {
        int width = view.getMeasuredWidth();
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

    float downX, downY;
    private ArrayList<Integer> integers;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float distance = event.getX() - downX;
                distance = distance * moveScale;
                for (int i = 0; i < rectS.size(); i++) {
                    Rect rect = rectS.get(i);
                    int width = rect.right - rect.left;
                    rect.left = (int) (rect.left + distance);
                    rect.right = rect.left + width;
                }
                requestLayout();
                downX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;

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
