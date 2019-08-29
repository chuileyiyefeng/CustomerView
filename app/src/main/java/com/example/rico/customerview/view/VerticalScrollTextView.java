package com.example.rico.customerview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;

/**
 * Created by Tmp on 2019/8/28.
 */
public class VerticalScrollTextView extends BaseCustomerView {
    public VerticalScrollTextView(Context context) {
        super(context);
    }

    public VerticalScrollTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalScrollTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int textSize;
    private Paint textPaint;
    private Paint.FontMetrics metrics;
    private ArrayList<String> strings;
    private float centerY, currentY;
    private int position;
    ValueAnimator animator, waitAnimator;
    float endLength;
    private String end;
    private float everyLength = 2;

    @Override
    protected void init(Context context) {
        end = "...";
        textSize = dpToSp(20);
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#000000"));
        textPaint.setTextSize(textSize);
        metrics = textPaint.getFontMetrics();
        endLength = textPaint.measureText(end);
        strings = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = (int) (metrics.bottom - metrics.top + 0.5);
        currentY = centerY = height / 2;
        setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onSizeChanged(int w, final int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        animator = ValueAnimator.ofInt(0, 100);
        waitAnimator = ValueAnimator.ofInt(1, 10);
        animator.setDuration(500);
        waitAnimator.setDuration(1000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentY -= everyLength;
                if (currentY <= -height / 2) {
                    currentY = centerY;
                    position++;
                    if (position == strings.size()) {
                        position = 0;
                    }
                    animation.cancel();
                    waitAnimator.start();
                }
                invalidate();
            }
        });
        waitAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                waitAnimator.cancel();
                animator.start();
            }
        });
    }

    public void setTextSize(int textSize) {
        everyLength=textSize/10;
        this.textSize = dpToSp(textSize);
        textPaint.setTextSize(this.textSize);
        metrics = textPaint.getFontMetrics();
        requestLayout();
    }

    public void setTextColor(int color) {
        textPaint.setColor(getResources().getColor(color));
    }

    public void setStrings(ArrayList<String> strings) {
        this.strings = strings;
        if (animator != null) {
            animator.cancel();
        }
        currentY = centerY;
        position = 0;
    }

    public void startAnimator() {
        animator.start();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        drawCurrentText(canvas);
        drawNextText(canvas);
    }


    private void drawCurrentText(Canvas canvas) {

        if (strings.size() > position) {
            String str = strings.get(position);
            float textLength = textPaint.measureText(str);
            boolean isLargeLength = false;
            while (textLength > width) {
                isLargeLength = true;
                str = str.substring(0, str.length() - 1);
                textLength = textPaint.measureText(str) + endLength;
            }
            if (isLargeLength) {
                str = str + end;
            }
            canvas.drawText(str, 0, currentY + (metrics.bottom - metrics.top) / 2 - metrics.bottom, textPaint);
        }
    }

    private void drawNextText(Canvas canvas) {
        String str = "";
        if (strings.size() > position + 1) {
            str = strings.get(position + 1);

        } else if (strings.size() == position + 1) {
            str = strings.get(0);
        }
        if (str.equals("")) {
            return;
        }
        float textLength = textPaint.measureText(str);
        boolean isLargeLength = false;
        while (textLength > width) {
            isLargeLength = true;
            str = str.substring(0, str.length() - 1);
            textLength = textPaint.measureText(str) + endLength;
        }
        if (isLargeLength) {
            str = str + end;
        }
        canvas.drawText(str, 0, currentY + height / 2 - metrics.top, textPaint);
    }


    private int dpToSp(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        float px = scale * dp;
        return (int) (px + 0.5f);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.cancel();
        }
        if (waitAnimator != null) {
            waitAnimator.cancel();
        }
    }
}
