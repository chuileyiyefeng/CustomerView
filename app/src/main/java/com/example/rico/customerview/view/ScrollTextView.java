package com.example.rico.customerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Tmp on 2019/4/12.
 * 滚动的文字view
 */
public class ScrollTextView extends BaseCustomerView {
    private Paint lastPaint, thisPaint;
    private String lastText, newText;
    private float lastTextLength, newTextLength;
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

    int moveCount = 20;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initBeginValue();
        everyHeightMove = (lastHeight + metrics.bottom) / moveCount;
    }

    private void initBeginValue() {
        lastHeight = height / 2 + metrics.bottom;
        thisHeight = lastHeight * 2 + metrics.bottom;
        lastAlpha = 255;
        thisAlpha = 255 - moveCount * alphaValue;
        lastPaint.setAlpha(lastAlpha);
        thisPaint.setAlpha(thisAlpha);
    }

    public void setText() {
        if (strings.size() == 0) {
            return;
        } else if (strings.size() == 1) {
            lastP = thisP = 0;
        }
        lastText = strings.get(lastP);
        newText = strings.get(thisP);
        lastTextLength = lastPaint.measureText(lastText);
        newTextLength = lastPaint.measureText(newText);
        lastP = thisP;
        thisP++;
        if (thisP > strings.size() - 1) {
            thisP = 0;
        }
    }

    int lastP, thisP = 1;
    ArrayList<String> strings;

    public void addText(ArrayList<String> strings) {
        this.strings = strings;
        setText();
    }

    int alphaValue = 10, lastAlpha, thisAlpha;

    public void startAnimator() {
        if (strings == null || strings.size() < 2) {
            return;
        }
        if (animator == null) {
            animator = ValueAnimator.ofInt(width);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (lastHeight > -metrics.bottom) {
                        if (lastHeight - everyHeightMove < -metrics.bottom) {
                            float distance = lastHeight + metrics.bottom;
                            lastHeight -= distance;
                            thisHeight -= distance;
                        } else {
                            lastHeight -= everyHeightMove;
                            thisHeight -= everyHeightMove;
                        }
                        lastAlpha -= alphaValue;
                        thisAlpha += alphaValue;
                        if (lastAlpha < 0) {
                            lastAlpha = 0;
                        }
                        if (thisAlpha > 255) {
                            thisAlpha = 255;
                        }
                        lastPaint.setAlpha(lastAlpha);
                        thisPaint.setAlpha(thisAlpha);
                        invalidate();
                    } else {
                        animation.cancel();
                        initBeginValue();
                        setText();
                    }
                }
            });
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setDuration(1000);
        }
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (TextUtils.isEmpty(lastText) || TextUtils.isEmpty(newText)) {
            return;
        }
        float lastTextStart = (width - lastTextLength) / 2;
        float newTextStart = (width - newTextLength) / 2;
        canvas.drawText(lastText, lastTextStart, lastHeight, lastPaint);
        canvas.drawText(newText, newTextStart, thisHeight, thisPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.cancel();
        }
    }
}
