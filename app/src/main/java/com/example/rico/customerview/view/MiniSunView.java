package com.example.rico.customerview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;

import com.example.rico.customerview.R;

import java.util.Arrays;
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
        whitePaint.setColor(getResources().getColor(R.color.azure));

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(getResources().getColor(R.color.gold));

        arcPaint = new Paint();
        arcPaint.setStrokeCap(Paint.Cap.ROUND);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setColor(circlePaint.getColor());

        lightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        cloudPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cloudPaint.setColor(getResources().getColor(R.color.beige));

        cloudShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cloudShadowPaint.setColor(getResources().getColor(R.color.goldenrod));

        sunShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sunShadowPaint.setColor(getResources().getColor(R.color.colorBtnClick));

        circlePath = new Path();
        rectPath1 = new Path();
        rectPath2 = new Path();
        cloudPath = new Path();
        cloudShadowPath = new Path();
        rotatePointF = new PointF();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setBackgroundColor(getResources().getColor(R.color.azure));
        radius = Math.min(w, h) / 4f;
        tinyRadius = radius / 25;
        circlePath.addCircle(0, 0, radius, Path.Direction.CW);
        arcPaint.setStrokeWidth(tinyRadius);
        centerP = new Point(w / 2, h / 2);
        initArcAngle();
        float f = (float) (radius / Math.cos(Math.toRadians(45)));
        gradient = new LinearGradient(-f, -f, f, f, getResources().getColor(R.color.ivory), getResources().getColor(R.color.goldenrod), Shader.TileMode.CLAMP);
        lightPaint.setShader(gradient);

        float distance = radius / 28f;
        sunShadowRectF = new RectF();

        shadowRectFLeft = -radius * 0.7f;
        shadowRectFTop = radius * 2;
        shadowRectFRight = radius * 0.7f;
        shadowRectFBottom = radius * 2.2f;
//       圆的排序点为从上到下
//        底下一排圆的半径比例为5：5：3
//        上面一排圆的半径比例为5：4
        cloudRadius[0] = distance * 10;
        cloudRadius[1] = distance * 10;
        cloudRadius[2] = distance * 8;
        cloudRadius[3] = distance * 10;
        cloudRadius[4] = distance * 9;

        cloudPoint[0] = new PointF(-radius + cloudRadius[0] + distance * 4 + cloudRadius[2], cloudRadius[0]);
        cloudPoint[1] = new PointF(cloudPoint[0].x + cloudRadius[0] + cloudRadius[1] - distance * 2, cloudPoint[0].y);
        cloudPoint[2] = new PointF(cloudPoint[1].x + cloudRadius[1] + cloudRadius[2] - distance * 2, cloudPoint[1].y);
        cloudPoint[3] = new PointF(cloudPoint[0].x - distance * 2 + cloudRadius[3], cloudPoint[0].y - cloudRadius[0]);
        cloudPoint[4] = new PointF(cloudPoint[3].x + cloudRadius[3] + cloudRadius[4] - distance * 2, cloudPoint[3].y);
        clipCloudPath = new Path();
        clipCloudRectF = new RectF(cloudPoint[0].x - cloudRadius[0] * 2, cloudPoint[0].y + distance * 3, cloudPoint[2].x + cloudRadius[2], radius * 2);
        clipCloudPath.addRect(clipCloudRectF, Path.Direction.CW);
        cloudShadowPath.moveTo(cloudPoint[0].x - cloudRadius[0] / 2, cloudPoint[0].y);
        cloudShadowPath.lineTo(cloudPoint[2].x, cloudPoint[2].y);
        cloudShadowPath.lineTo(cloudPoint[2].x, clipCloudRectF.bottom);
        cloudShadowPath.close();
    }

    //    大圆的半径,圆环的宽度，中间白色圆的半径,太阳半径
    float radius, tinyRadius, smallRadius, sunRadius;
    Point centerP;
    //    初始小白点paint，大圆的paint，圆弧的paint，阳光的paint，云的paint,云阴影的paint,太阳阴影paint
    Paint whitePaint, circlePaint, arcPaint, lightPaint, cloudPaint, cloudShadowPaint, sunShadowPaint;

    ValueAnimator circleAnim, arcAnim, sunAnim, lightAnim;
    final int BORDER = 1, ARC = 2, SUN = 3, LIGHT = 4;
    int animType;
    //    圆环扫过的角度
    float sweepAngle;
    long duration = 300;

    //    渐变gradient
    LinearGradient gradient;
    //    太阳光区域,云的path,需要裁剪的云的path,云朵阴影的绘制path
    Path circlePath, rectPath1, rectPath2, cloudPath, clipCloudPath, cloudShadowPath;
    //    云朵阴影裁剪区域，太阳阴影区域
    RectF clipCloudRectF, sunShadowRectF;
    //    太阳阴影区域的坐上右下是个点坐标 ，当前左坐标，当前右坐标，阴影是由小变大的
    float shadowRectFTop, shadowRectFLeft, shadowRectFRight, shadowRectFBottom, currentShadowRectFLeft, currentShadowRectFRight;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animIsRunning()) {
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            cancelAllAnim();
            startBorderAnim();
        }
        return true;
    }

    private void cancelAllAnim() {
        cancelAnim(circleAnim, arcAnim, sunAnim, lightAnim);
    }
    public  boolean animIsRunning(){
        if (circleAnim!=null&&circleAnim.isRunning()) {
            return true;
        }
        if (arcAnim!=null&&arcAnim.isRunning()) {
            return true;
        }
        return sunAnim != null && sunAnim.isRunning();
    }

    private void cancelAnim(Animator... animator) {
        for (Animator animator1 : animator) {
            if (animator1 != null) {
                animator1.cancel();
            }
        }
    }

    //    从小到大的圆环动画
    private void startBorderAnim() {
        animType = BORDER;
        if (circleAnim == null) {
            circleAnim = ValueAnimator.ofFloat(tinyRadius, radius);
            circleAnim.setInterpolator(new DecelerateInterpolator());
            circleAnim.setDuration(duration);
            circleAnim.addUpdateListener(animation -> {
                smallRadius = (float) animation.getAnimatedValue();
                invalidate();
            });
            circleAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    animType = 0;
                    startArcAnim();
                }
            });

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
            arcAnim.setDuration(duration);
            arcAnim.addUpdateListener(animation -> {
                sweepAngle = (float) animation.getAnimatedValue();
                invalidate();
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
    //    矩形对角线长度
    float rectDiagonal;

    //    太阳出现动画
    private void startSunAnim() {
        animType = SUN;
        lightShow = false;
        rectPath1.reset();
        rectPath2.reset();
        currentShadowRectFLeft = 0;
        currentShadowRectFRight = 0;
        rectRadius = (float) (radius * Math.sqrt(2) / 2);
        if (sunAnim == null) {
            sunAnim = ValueAnimator.ofFloat(radius / 10 * 9, radius / 10 * 11, radius, rectRadius);
            sunAnim.setDuration(duration * 2);
            sunAnim.addUpdateListener(animation -> {
//                    阳光变换相关
                float value = (float) animation.getAnimatedValue();
                if (value < radius && value < lastValue) {
                    lightShow = true;
                    float f = (radius - value) + rectRadius;
                    f = f - radius / 10;
                    rectPath1.moveTo(-f, -f);
                    rectPath1.lineTo(f, -f);
                    rectPath1.lineTo(f, f);
                    rectPath1.lineTo(-f, f);
                    rectDiagonal = (float) (f / Math.cos(Math.toRadians(45)));
                    rectPath2.moveTo(-rectDiagonal, 0);
                    rectPath2.lineTo(0, -rectDiagonal);
                    rectPath2.lineTo(rectDiagonal, 0);
                    rectPath2.lineTo(0, rectDiagonal);
                } else {
                    sunRadius = value;
                }
                lastValue = value;
//                    阴影变换相关
                changShadow();
                invalidate();
            });
            sunAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    animType = 0;
                    lightShow = false;
                    startLightAnim();
                }
            });
        }
        sunAnim.setFloatValues(radius / 10 * 9, radius / 10 * 11, radius, rectRadius);
        sunAnim.start();
    }

    //    变换阴影大小
    private void changShadow() {
        float eachChange = shadowRectFRight / 50;
        boolean a = false, b = false;
        if (currentShadowRectFLeft > shadowRectFLeft) {
            currentShadowRectFLeft -= eachChange;
            a = true;
        }
        if (currentShadowRectFRight < shadowRectFRight) {
            currentShadowRectFRight += eachChange;
            b = true;
        }
        if (a || b) {
            sunShadowRectF.set(currentShadowRectFLeft, shadowRectFTop, currentShadowRectFRight, shadowRectFBottom);
        }
    }

    float lightAngle;
    PointF rotatePointF;
    //    云是否完整出现
    boolean cloudIsFull;
    //    当前画第几朵云
    int cloudPosition;
    //    当前云朵半径
    float currentCloudRadius;

    //    阳光旋转动画
    private void startLightAnim() {
        animType = LIGHT;
        lightAngle = 0;
        cloudPosition = 0;
        currentCloudRadius = 0;
        //        重置云朵半径
        Arrays.fill(smallCloudRadius, 0);
        isClip = false;
        cloudIsFull = false;
        cloudShadowPaintAlpha = 0;
        if (lightAnim == null) {
            lightAnim = ValueAnimator.ofFloat(cloudRadius[0]);
            lightAnim.setInterpolator(new DecelerateInterpolator());
            lightAnim.setDuration(duration);
            lightAnim.setRepeatCount(ValueAnimator.INFINITE);
            lightAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
//                    太阳光相关代码
                    float x, y;
                    lightAngle += 0.2f;
                    if (lightAngle >= 45) {
                        lightAngle = 0;
                    }
                    rectPath1.reset();
                    rectPath2.reset();
                    changeRotatePoint(lightAngle);
                    x = rotatePointF.x;
                    y = rotatePointF.y;
                    rectPath1.moveTo(x, y);
                    rectPath1.lineTo(-y, x);
                    rectPath1.lineTo(-x, -y);
                    rectPath1.lineTo(y, -x);
                    changeRotatePoint(lightAngle + 45);
                    x = rotatePointF.x;
                    y = rotatePointF.y;
                    rectPath2.moveTo(x, y);
                    rectPath2.lineTo(-y, x);
                    rectPath2.lineTo(-x, -y);
                    rectPath2.lineTo(y, -x);

//                    云朵相关代码
//                    云一朵一朵出来 第一朵的云半径画满之后画第二朵云以此类推
                    if (!cloudIsFull) {
                        cloudPath.reset();
                        currentCloudRadius +=cloudRadius[cloudPosition]/8;
                        boolean b=false;
                        for (int i = 0; i < cloudPosition + 1; i++) {
                            smallCloudRadius[i] = cloudRadius[i];
                            if (currentCloudRadius <= cloudRadius[cloudPosition]) {
                                smallCloudRadius[cloudPosition] = currentCloudRadius;
                            }else {
                                b=true;
                            }
                            Path path = new Path();
                            PointF p = cloudPoint[i];
                            path.addCircle(p.x, p.y, smallCloudRadius[i], Path.Direction.CW);
                            cloudPath.op(path, Path.Op.UNION);
                        }
                        cloudIsFull=cloudPosition==cloudRadius.length-1&&b;
                        if (currentCloudRadius >= cloudRadius[cloudPosition] && cloudPosition < cloudRadius.length - 1) {
                            currentCloudRadius = 0;
                            cloudPosition++;
                        }
                        cloudPath.op(clipCloudPath, Path.Op.DIFFERENCE);
//                    云层阴影
                    } else {
                        clipCloudShadow();
                    }
                    changShadow();
                    invalidate();
                }
            });
        }
        lightAnim.setFloatValues(cloudRadius[0]);
        lightAnim.start();
    }

    boolean isClip;

    private void clipCloudShadow() {
        if (!isClip) {
            cloudShadowPath.reset();
            cloudShadowPath.moveTo(cloudPoint[0].x - cloudRadius[0] / 2, cloudPoint[0].y);
            cloudShadowPath.lineTo(cloudPoint[2].x, cloudPoint[2].y);
            cloudShadowPath.lineTo(cloudPoint[2].x, clipCloudRectF.bottom);
            cloudShadowPath.close();
            cloudShadowPath.op(circlePath, Path.Op.INTERSECT);
            isClip = true;
        }
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
                drawLight(canvas);
                break;
        }
    }


    //    画圆环
    private void drawBorder(Canvas canvas) {
        canvas.drawPath(circlePath, circlePaint);
        canvas.drawCircle(0, 0, smallRadius, whitePaint);
    }

    //    初始的五个角度
    float[] angles = new float[5];
    //    五个圆弧的绘画区域
    RectF[] arcRectS = new RectF[5];
    //    圆环滑动总角度
    float rotateBound = 180;


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
        canvas.drawPath(circlePath, circlePaint);
        canvas.drawCircle(0, 0, radius - tinyRadius, whitePaint);
        float currentSweep, startAngle;
        for (int i = 0; i < angles.length; i++) {
            canvas.save();
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
            canvas.rotate(i % 2 == 0 ? -sweepAngle / 2 : sweepAngle / 2);
            canvas.drawArc(arcRectS[i], startAngle, currentSweep, false, arcPaint);
            canvas.restore();
        }
    }


    //    画太阳
    private void drawSun(Canvas canvas) {
        if (lightShow) {
            canvas.drawPath(rectPath1, lightPaint);
            canvas.drawPath(rectPath2, lightPaint);
        }
        canvas.drawCircle(0, 0, sunRadius, circlePaint);
    }


    //    制造云的五个圆圆心
    PointF[] cloudPoint = new PointF[5];
    //    五个云的半径,从小到大的云的半径
    float[] cloudRadius = new float[5], smallCloudRadius = new float[5];

    //    画太阳光 云朵 及阴影
    int cloudShadowPaintAlpha;

    private void drawLight(Canvas canvas) {
        canvas.drawPath(rectPath1, lightPaint);
        canvas.drawPath(rectPath2, lightPaint);
        canvas.drawPath(circlePath, circlePaint);
        if (cloudIsFull) {
            if (cloudShadowPaintAlpha < 255) {
                cloudShadowPaintAlpha += 10;
                if (cloudShadowPaintAlpha > 255) {
                    cloudShadowPaintAlpha = 255;
                }
                cloudShadowPaint.setAlpha(cloudShadowPaintAlpha);
            }
            canvas.drawPath(cloudShadowPath, cloudShadowPaint);
        }
        canvas.drawPath(cloudPath, cloudPaint);
        canvas.drawOval(sunShadowRectF, sunShadowPaint);
    }


    //    重置太阳光矩形点的位置
    private void changeRotatePoint(float rotateAngle) {
        float x = (float) (rectDiagonal * Math.cos(Math.toRadians(rotateAngle)));
        float y = (float) (rectDiagonal * Math.sin(Math.toRadians(rotateAngle)));
        rotatePointF.set(x, y);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeAnim(circleAnim, arcAnim, sunAnim, lightAnim);
    }

    //    关闭动画
    private void removeAnim(Animator... animator) {
        for (Animator animator1 : animator) {
            if (animator1 != null) {
                animator1.cancel();
            }
        }
    }
}
