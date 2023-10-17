package com.example.rico.customerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Choreographer;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

/**
 * @Description:
 * @Author: pan yi
 * @Date: 2023/9/14
 */
public class LoadView2 extends BaseCustomerView {
    public LoadView2(Context context) {
        super(context);
    }

    public LoadView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Paint paint, bgPaint;
    private RectF rect;
    private float strokeSize;

    @Override
    protected void init(Context context) {
        strokeSize = dpToPx(8);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#A96FFF"));
        paint.setStrokeWidth(strokeSize);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.parseColor("#F5EEFF"));
        bgPaint.setStrokeWidth(strokeSize);
        bgPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rect = new RectF(strokeSize, strokeSize, w - strokeSize, h - strokeSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(rect, 0, 360, false, bgPaint);
        canvas.drawArc(rect, -80, 45, false, paint);
    }

    public int dpToPx(float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }


}
