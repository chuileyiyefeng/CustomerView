package com.example.rico.customerview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
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

        lightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lightPaint.setColor(getResources().getColor(R.color.line_color));
        sunRectF = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        radius = Math.min(w, h) / 4;
        tinyRadius = radius / 25;
        arcPaint.setStrokeWidth(tinyRadius);
        centerP = new Point(w / 2, h / 2);
        initArcAngle();
//        gradient = new LinearGradient(0,0,w,h, null);
    }

    //    大圆的半径,圆环的宽度，中间白色圆的半径,太阳半径
    float radius, tinyRadius, smallRadius, sunRadius;
    Point centerP;
    //    初始小白点paint，大圆的paint，圆弧的paint，阳光的paint
    Paint whitePaint, circlePaint, arcPaint, lightPaint;

    ValueAnimator circleAnim, arcAnim, sunAnim, lightAnim;
    final int BORDER = 1, ARC = 2, SUN = 3, LIGHT = 4;
    int animType;
    //    圆环扫过的角度
    float sweepAngle;

    //    渐变gradient
    LinearGradient gradient;


    //    太阳光区域
    RectF sunRectF;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                startBorderAnim();
                break;
        }
        return true;
    }

    //    从小到大的圆环动画
    private void startBorderAnim() {
        animType = BORDER;
        if (circleAnim == null) {
            circleAnim = ValueAnimator.ofFloat(tinyRadius, radius);
            circleAnim.setInterpolator(new DecelerateInterpolator());
            circleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    smallRadius = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            circleAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    animType = 0;
                    startArcAnim();
                }
            });
            circleAnim.setDuration(300);
        }
        circleAnim.setFloatValues(tinyRadius, radius);
        circleAnim.start();
    }

    //    圆环旋转动画
    private void startArcAnim() {
        animType = ARC;
        if (arcAnim == null) {
            arcAnim = ValueAnimator.ofFloat(rotateBound);
            arcAnim.setInterpolator(new DecelerateInterpolator());
            arcAnim.setDuration(500);
            arcAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    sweepAngle = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            arcAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    animType = 0;
                    startSunAnim();

                }
            });
        }
        arcAnim.setFloatValues(rotateBound);
        arcAnim.start();
    }

    boolean lightShow;
    float lastValue, rectRadius;

    //    太阳出现动画
    private void startSunAnim() {
        animType = SUN;
        lightShow = false;
        rectRadius = (float) (radius * Math.sqrt(2) / 2);
        if (sunAnim == null) {
            sunAnim = ValueAnimator.ofFloat(radius / 8 * 7, radius / 8 * 9, radius, rectRadius);
            sunAnim.setInterpolator(new DecelerateInterpolator());
            sunAnim.setDuration(500);
            sunAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    if (value < radius && value < lastValue) {
                        lightShow = true;
//                         100-(100...50) 0...50+50
                        float f = (radius - value) + rectRadius;
                        f = f - radius / 12;
                        sunRectF.set(-f, -f, f, f);
                    } else {
                        sunRadius = value;
                    }
                    lastValue = value;
                    invalidate();
                }
            });
            sunAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
//                    animType = 0;
//                    lightShow = false;
//                    startLightAnim();
                }
            });
        }
        sunAnim.setFloatValues(radius / 8 * 7, radius / 8 * 9, radius, rectRadius);
        sunAnim.start();
    }

    //    阳光旋转动画
    private void startLightAnim() {
        animType = LIGHT;
        if (lightAnim == null) {
            lightAnim = ValueAnimator.ofFloat(radius - 20, radius);
            lightAnim.setInterpolator(new DecelerateInterpolator());
            lightAnim.setDuration(500);
            lightAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    sunRadius = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            lightAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    animType = 0;
                }
            });
            lightAnim.setRepeatCount(ValueAnimator.INFINITE);
        }
        lightAnim.setFloatValues(radius - 20, radius);
        lightAnim.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(centerP.x, centerP.y);
        switch (animType) {
            case BORDER:
                drawBorder(canvas);
                break;
            case ARC:
                drawArc(canvas);
                break;
            case SUN:
                drawSun(canvas);
                break;
            case LIGHT:
                break;
        }
    }

    //    画圆环
    private void drawBorder(Canvas canvas) {
        canvas.drawCircle(0, 0, smallRadius, circlePaint);
        canvas.drawCircle(0, 0, tinyRadius, whitePaint);
    }

    //    初始的五个角度
    float[] angles = new float[5];
    //    五个圆弧的绘画区域
    RectF[] arcRectS = new RectF[5];
    //    圆环滑动总角度
    float rotateBound = 240;

    //    设置圆弧的绘制区域，因为旋转圆环要把画布居中，所以圆弧的中心区域也在中心点
    private void initArcAngle() {
        Random random = new Random();
        for (int i = 0; i < angles.length; i++) {
            angles[i] = random.nextInt(360) - 180;
            float arcRadius = radius / 6 * (i + 1);
            arcRectS[i] = new RectF(-arcRadius,
                    -arcRadius,
                    +arcRadius,
                    +arcRadius);
        }
    }

    //    画圆弧
    private void drawArc(Canvas canvas) {
        canvas.drawCircle(0, 0, radius, circlePaint);
        canvas.drawCircle(0, 0, radius - tinyRadius, whitePaint);
        float currentSweep, startAngle;
        for (int i = 0; i < angles.length; i++) {
            startAngle = angles[i];
            currentSweep = sweepAngle;
            if (sweepAngle > rotateBound / 2) {
                if (i % 2 == 0) {
                    startAngle -= rotateBound / 2;
                } else {
                    startAngle += rotateBound / 2;
                }
                currentSweep = sweepAngle - rotateBound;
            }
            currentSweep = i % 2 == 0 ? -currentSweep : currentSweep;
            canvas.rotate(i % 2 == 0 ? -sweepAngle : sweepAngle);
            canvas.drawArc(arcRectS[i], startAngle, currentSweep, false, arcPaint);
        }
    }


    //    画太阳
    private void drawSun(Canvas canvas) {
        if (lightShow) {
            canvas.drawRect(sunRectF, lightPaint);
            canvas.rotate(45);
            canvas.drawRect(sunRectF, lightPaint);
        }
        canvas.drawCircle(0, 0, sunRadius, circlePaint);
    }
}
