package com.example.rico.customerview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;

import com.example.rico.customerview.R;

import java.util.Random;

/**
 * Created by Tmp on 2019/6/19.
 * 太阳天气动画
 */
public class MiniSunView extends BaseCustomerView {
    public MiniSunView(Context context) {
        super(context);
    }

    public MiniSunView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        whitePaint = new Paint();
        whitePaint.setColor(getResources().getColor(R.color.white));

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(getResources().getColor(R.color.gold));

        arcPaint = new Paint();
        arcPaint.setStrokeCap(Paint.Cap.ROUND);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setColor(circlePaint.getColor());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        radius = Math.min(w, h) / 3;
        tinyRadius = radius / 25;
        arcPaint.setStrokeWidth(tinyRadius);
        centerP = new Point(w / 2, h / 2);
        initArcAngle();
    }

    //    大圆的半径,圆环的宽度，中间白色圆的半径
    float radius, tinyRadius, smallRadius;
    Point centerP;
    Paint whitePaint, circlePaint, arcPaint;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                startBorderAnim();
                break;
        }
        return true;
    }

    ValueAnimator circleAnim, arcAnim;
    boolean circleAnimIsEnd, arcAnimIsStart;
    //    圆环扫过的角度
    float sweepAngle;

    //    从小到大的圆环动画
    private void startBorderAnim() {
        arcAnimIsStart = false;
        if (circleAnim == null) {
            circleAnim = ValueAnimator.ofFloat(tinyRadius, radius);
            circleAnim.setInterpolator(new DecelerateInterpolator());
            circleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    smallRadius = value;
                    circleAnimIsEnd = false;
                    Log.e("smallRadius", "onAnimationUpdate: " + value);
                    invalidate();
                }
            });
            circleAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    circleAnimIsEnd = true;
                    invalidate();
                    startArcAnim();
                }
            });
            circleAnim.setDuration(500);
        }
        circleAnim.setFloatValues(tinyRadius, radius);
        circleAnim.start();
    }

    //    圆环旋转动画
    private void startArcAnim() {
        if (arcAnim == null) {
            arcAnim = ValueAnimator.ofFloat(180);
            arcAnim.setInterpolator(new DecelerateInterpolator());
            arcAnim.setDuration(3000);
            arcAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    arcAnimIsStart = true;
                    sweepAngle = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            arcAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    arcAnimIsStart = false;
                }
            });
        }
        arcAnim.setFloatValues(180);
        arcAnim.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBorder(canvas);
        if (arcAnimIsStart) {
            drawArc(canvas);
        }
    }

    //    初始的五个角度
    float[] angles = new float[5];

    //    五个圆弧的绘画区域
    RectF[] arcRect = new RectF[5];

    //    设置圆弧的绘制区域，因为旋转圆环要把画布居中，所以圆弧的中心区域也在中心点
    private void initArcAngle() {
        Random random = new Random();
        for (int i = 0; i < angles.length; i++) {
            angles[i] = random.nextInt(360) - 180;
            float arcRadius = radius / 6 * (i + 1);
            arcRect[i] = new RectF(-arcRadius,
                    -arcRadius,
                    +arcRadius,
                    +arcRadius);
        }
    }

    //    画圆弧
    private void drawArc(Canvas canvas) {
        canvas.save();
        canvas.translate(centerP.x, centerP.y);
        float currentSweep,startAngle;
        for (int i = 0; i < angles.length; i++) {
            currentSweep = i % 2 == 0 ? -sweepAngle : sweepAngle;
            canvas.rotate(currentSweep / 2);
            if (sweepAngle>90) {
                
            }
            canvas.drawArc(arcRect[i], angles[i], currentSweep, false, arcPaint);
        }
        canvas.restore();
        invalidate();
    }

    //    画圆环
    private void drawBorder(Canvas canvas) {
        canvas.drawCircle(centerP.x, centerP.y, smallRadius, circlePaint);
        if (circleAnimIsEnd) {
            canvas.drawCircle(centerP.x, centerP.y, radius - tinyRadius, whitePaint);
        } else {
            canvas.drawCircle(centerP.x, centerP.y, tinyRadius, whitePaint);
        }
    }
}
