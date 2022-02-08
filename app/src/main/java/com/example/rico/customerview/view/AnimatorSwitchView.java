package com.example.rico.customerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

import com.example.rico.customerview.R;

/**
 * Created by Tmp on 2019/6/17.
 * 带动画的switch
 */
public class AnimatorSwitchView extends BaseCustomerView {
    public AnimatorSwitchView(Context context) {
        super(context);
    }

    public AnimatorSwitchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private Paint textPaint, borderPaint, circlePaint;


    @Override
    protected void init(Context context) {

        leftColor = getResources().getColor(R.color.lightpink);
        rightColor = getResources().getColor(R.color.button_bg);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        textPaint.setColor(getResources().getColor(R.color.blue_thumb));
        borderPaint.setColor(getResources().getColor(R.color.blue_thumb));
        circlePaint.setColor(leftColor);

        borderPaint.setStrokeWidth(6);
        borderPaint.setStyle(Paint.Style.STROKE);
        path = new Path();
        clipPath = new Path();
        touchSloop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    float left, top, radius;
    RectF rectF;
    Path path, clipPath;
    PathMeasure pathMeasure;
    float[] tan = new float[2];
    int leftColor, rightColor;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (width < height) {
            return;
        }
        left = top = borderPaint.getStrokeWidth()/2;
        rectF = new RectF(left, top, width - top, height - top);
        textPaint.setTextSize(dpToPx(20));
        RectF rect2 = new RectF(height / 2f, 0, width - height / 2f, height);
        clipPath.addRect(rect2, Path.Direction.CW);
        clipPath.addCircle(height/2f, height / 2f, height / 2f, Path.Direction.CW);
        clipPath.addCircle(width - height / 2f, height / 2f, height / 2f, Path.Direction.CW);
        measureText();

        radius = width / 2f;
        path.moveTo(0, height);
        path.quadTo(width / 2f, height + radius * 2, width, height);
        tan[0] = 0;
        tan[1] = height;
        pathMeasure = new PathMeasure();
        pathMeasure.setPath(path, false);
    }

    public int dpToPx(float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    private String leftStr = "leftStr", rightStr = "rightStr";

    float leftStrLength, rightStrLength, leftStrStart, rightStrStart, strHeight;

    public void setLeftStr(String leftStr, String rightStr) {
        this.leftStr = leftStr;
        this.rightStr = rightStr;
        measureText();
        invalidate();
    }

    private void measureText() {
        leftStrLength = textPaint.measureText(leftStr);
        rightStrLength = textPaint.measureText(rightStr);
        leftStrStart = ((width / 2f) - leftStrLength) / 2;
        rightStrStart = ((width / 2f) - leftStrLength) / 2 + width / 2f;
        strHeight = textPaint.getFontMetrics().bottom;
    }

    float downX, downY, touchSloop;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (width < height) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if ((Math.abs(event.getX() - downX) < touchSloop && Math.abs(event.getY() - downY) < touchSloop)) {
                    isLeft = !(event.getX() > width / 2f);
                    if (!lastIsLeft == isLeft) {
                        startAnimator();
                    }
                    lastIsLeft = isLeft;
                }
                break;
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return !isLeft && super.performClick();
    }

    ValueAnimator animator;
    boolean isLeft, lastIsLeft = true;

    private void startAnimator() {
        if (animator == null) {
            animator = ValueAnimator.ofFloat(pathMeasure.getLength());
            animator.setInterpolator(new DecelerateInterpolator());
            animator.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                pathMeasure.getPosTan(value, tan, null);
                if (value >= width / 2f) {
                    circlePaint.setColor(rightColor);
                } else {
                    circlePaint.setColor(leftColor);
                }
                invalidate();
            });
            animator.setDuration(300);
        }
        if (isLeft) {
            animator.setFloatValues(pathMeasure.getLength(), 0);
        } else {
            animator.setFloatValues(pathMeasure.getLength());
        }
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (width < height) {
            return;
        }
        canvas.clipPath(clipPath);
        canvas.drawCircle(tan[0], tan[1], radius, circlePaint);
        canvas.drawRoundRect(rectF, height / 2f, height / 2f, borderPaint);
        canvas.drawText(leftStr, leftStrStart, height / 2f + strHeight, textPaint);
        canvas.drawText(rightStr, rightStrStart, height / 2f + strHeight, textPaint);
    }
}
