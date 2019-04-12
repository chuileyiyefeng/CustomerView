package com.example.rico.customerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by Tmp on 2019/4/12.
 * 滚动的文字view
 */
public class ScrollTextView extends BaseCustomerView {
    private Paint lastPaint, thisPaint;
    private String text;
    private float textLength;
    private float thisHeight, lastHeight;
    private ValueAnimator animator;
    private Paint.FontMetrics metrics;
    private float everyHeightMove;

    public ScrollTextView(Context context) {
        super(context);
    }

    public ScrollTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        float textSize = 100;
        lastPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lastPaint.setTextSize(textSize);
        lastPaint.setColor(Color.BLACK);

        thisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        thisPaint.setTextSize(textSize);
        thisPaint.setColor(Color.BLACK);
        metrics = lastPaint.getFontMetrics();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightMode = MeasureSpec.EXACTLY;
        int realHeight = MeasureSpec.makeMeasureSpec((int) (metrics.bottom - metrics.top + 0.5f) * 2, heightMode);
        setMeasuredDimension(widthMeasureSpec, realHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        lastHeight = height / 2 + metrics.bottom ;
        thisHeight = lastHeight*2+metrics.bottom;
        everyHeightMove = lastHeight / 20;
    }

    public void setText(String text) {
        this.text = text;
        textLength = lastPaint.measureText(text);
    }

    int alphaValue;

    public void startAnimator() {
        if (animator == null) {
            animator = ValueAnimator.ofInt(width);
        }
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (lastHeight > -metrics.bottom) {
                    lastHeight -= everyHeightMove;
                    thisHeight -= everyHeightMove;
                    invalidate();
                } else {
                    animation.cancel();
                }
            }
        });
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(1000);
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float textStart = (width - textLength) / 2;
        canvas.drawText(text, textStart, lastHeight, lastPaint);
        canvas.drawText(text, textStart, thisHeight, thisPaint);
        canvas.drawLine(0,height/2,width,height/2,lastPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.cancel();
        }
    }
}
