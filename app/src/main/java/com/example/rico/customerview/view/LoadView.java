package com.example.rico.customerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

import com.example.rico.customerview.R;

/**
 * create by pan yi on 2020/10/21
 * desc :
 */
public class LoadView extends BaseCustomerView {
    public LoadView(Context context) {
        super(context);
    }

    public LoadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Paint paint;
    private RectF rect;
    private float strokeSize;

    int defaultStartAngle = 0;
    int minSwipeAngle = 30;
    int maxSwipeAngle = 300;

    private int currentStartAngle = defaultStartAngle;
    private int currentSwipeAngle = maxSwipeAngle;

    @Override
    protected void init(Context context) {
        strokeSize = dpToPx(2);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(R.color.black));
        paint.setStrokeWidth(strokeSize);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rect = new RectF(strokeSize, strokeSize, w - strokeSize, h - strokeSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(rect, currentStartAngle, currentSwipeAngle, false, paint);
    }

    public int dpToPx(float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    private ValueAnimator circleAnim;
    private float increaseAngle = 3f;
    private boolean isIncrease = true;

    public void startLoading() {
        if (circleAnim == null) {
            circleAnim = ValueAnimator.ofInt(360);
            circleAnim.setInterpolator(new DecelerateInterpolator());
            int duration = 360;
            circleAnim.setDuration(duration);
            circleAnim.setRepeatCount(ValueAnimator.INFINITE);
            circleAnim.addUpdateListener(animation -> {
                if (isIncrease) {
                    currentSwipeAngle += increaseAngle;
                    currentStartAngle += increaseAngle;
                } else {
                    currentStartAngle += increaseAngle * 4;
                    currentSwipeAngle -= increaseAngle * 3;
                }
                if (currentSwipeAngle > maxSwipeAngle) {
                    isIncrease = false;
                } else if (currentSwipeAngle < minSwipeAngle) {
                    isIncrease = true;
                }
                invalidate();
            });

        }
        circleAnim.start();
    }

    public void stopLoading() {
        if (circleAnim != null) {
            circleAnim.cancel();
        }
    }
}
