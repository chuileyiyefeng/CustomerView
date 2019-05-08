package com.example.rico.customerview.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by Tmp on 2019/4/29.
 */
public class MatrixAndCameraTestView extends ViewGroup {
    private Context context;

    public MatrixAndCameraTestView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public MatrixAndCameraTestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    Camera camera;
    Matrix matrix;

    private void init() {
        camera = new Camera();
        matrix = new Matrix();
        matrixValues = new float[9];
    }

    //    宽、高、滑动临界值
    int width, height;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            measureChild(v, widthMeasureSpec, heightMeasureSpec);
            height = Math.max(height, v.getMeasuredHeight());
            width = Math.max(width, v.getMeasuredWidth());
        }
        width = widthMode == MeasureSpec.EXACTLY ? widthSize : width;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        布局类似于Linear Layout的vertical模式
        int count = getChildCount();
        if (count > 5) {
            count = 5;
        } else if (count < 5) {
            return;
        }
        int top = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != VISIBLE) {
                continue;
            }
            int wDistance = -(child.getMeasuredWidth() - width) / 2;
            int hDistance = -(child.getMeasuredHeight() - height) / 2;
            //设置View的左边、上边、右边底边位置
            child.layout(wDistance, top + hDistance, child.getMeasuredWidth() + wDistance, top + child.getMeasuredHeight() + hDistance);
            top = top + height;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        for (int i = 0; i < getChildCount(); i++) {
            drawChild(canvas, i, getChildAt(i));
        }
    }

    float[] matrixValues;
    float scale;

    //
    private void drawChild(Canvas canvas, int index, View child) {
        int childTop = height * index;

        if (getScrollY() + height < childTop) {
            return;
        }
        if (childTop < getScrollY() - height) {
            return;
        }
        float centerX = width / 2;
        float centerY = (getScrollY() > childTop) ? childTop + height : childTop;
        float degree = 90 * (getScrollY() - childTop) / height;
        if (degree >= 90 || degree <= -90 ) {
            return;
        }
        canvas.save();
        camera.save();
        camera.rotateX(degree);
        camera.getMatrix(matrix);
        camera.restore();


        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);

        canvas.concat(matrix);
        drawChild(canvas, child, getDrawingTime());
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        scale = getResources().getDisplayMetrics().density;
        if (getChildCount() >= 3) {
            startAnimator();
        }
    }

    ValueAnimator animator;
    Scroller scroller;

    private void startAnimator() {
        if (animator == null) {
            scroller = new Scroller(context);
            animator = ValueAnimator.ofInt(0, height * 4);
            animator.setDuration(5000);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    value = height * 4 - value;
                    scrollTo(0, value);
                }
            });
            animator.start();
        } else {
            animator.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.cancel();
        }
    }
}
