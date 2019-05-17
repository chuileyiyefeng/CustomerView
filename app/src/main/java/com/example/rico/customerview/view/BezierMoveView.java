package com.example.rico.customerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

import com.example.rico.customerview.R;

/**
 * Created by Tmp on 2019/5/14.
 * 贝塞尔曲线画圆，原理看图 R.mipmap.circle_bezier
 * 变化原理看图 R.mipmap.circle_bezier_change
 */
public class BezierMoveView extends BaseCustomerView {
    Paint paint;
    PointF startP, endP;
    Path path;
    Matrix matrix;

    public BezierMoveView(Context context) {
        super(context);
    }

    public BezierMoveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    int radius;
    PointF[] control = new PointF[12],
            originControl = new PointF[12];

    @Override
    protected void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.blue_y));
        path = new Path();
        tapSlop = ViewConfiguration.get(context).getScaledDoubleTapSlop();
    }

    int centerX, centerY;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        matrix = new Matrix();
        radius = Math.min(width, height) / 10;
        centerX = radius * 2;
        centerY = radius * 4;
//        0.551915024494f 画圆所需数值是这样，大概是个固定值
        float halfRadius = (float) (radius * 0.551915024494f + 0.5);
        startP = new PointF(centerX - radius, centerY);
        endP = new PointF(centerX, centerY - radius);


//        绘画顺序从左上开始画,也就是第四象限
//        8个控制点
        originControl[11] = new PointF(centerX - halfRadius, centerY - radius);
        originControl[0] = new PointF(centerX, centerY - radius);
        originControl[1] = new PointF(centerX + halfRadius, centerY - radius);

        originControl[2] = new PointF(centerX + radius, centerY - halfRadius);
        originControl[3] = new PointF(centerX + radius, centerY);
        originControl[4] = new PointF(centerX + radius, centerY + halfRadius);

        originControl[5] = new PointF(centerX + halfRadius, centerY + radius);
        originControl[6] = new PointF(centerX, centerY + radius);
        originControl[7] = new PointF(centerX - halfRadius, centerY + radius);

        originControl[8] = new PointF(centerX - radius, centerY + halfRadius);
        originControl[9] = new PointF(centerX - radius, centerY);
        originControl[10] = new PointF(centerX - radius, centerY - halfRadius);
        for (int i = 0; i < originControl.length; i++) {
            control[i] = new PointF(originControl[i].x, originControl[i].y);
        }
        changePath();
    }

    float downX = 0, downY = 0;
    int tapSlop;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float upX = event.getX();
                float upY = event.getY();
                if (Math.abs(upX - downX) < tapSlop && Math.abs(upY - downY) < tapSlop) {
                    startAnimator();
                }
                break;
        }
        return true;
    }

    //    形变动画
    ValueAnimator deformAnimator;

    //    从图原理可知 点 2、3、4 x半径是从r-2r-1.5r-r
    //     点 8、9、10 x半径是从r-1.5r-r
    //     点 5、6、7 11、0、1 y半径是从r-0.8r-r 变化的值不一定

    //     点2、3、4  8、9、10  x轴的变换值
    //     点11、0、1  5、6、7  x轴的变换值
    float lastRightX, rightX, leftX, underY;
    //    左边点x值及上下y值变化的临界值
    float value, scale, moveDistance, defaultMove = 1800f / (2000f / 16);
    boolean rightIsFull, isDown;
    int addTime, lastTrend;
    final int add = 1, decrease = 2;

    //    要移动200，1000秒移动完
    private void startAnimator() {
        matrix.reset();
        value = 0;
        addTime = 0;
        lastTrend = 0;
        isDown = false;
        rightIsFull = false;
        moveDistance = defaultMove;
        scale = (float) radius / 100;
        if (deformAnimator == null) {
            deformAnimator = ValueAnimator.ofInt(100, 200, 300, 150, 170, 150);
            deformAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int newValue = (int) animation.getAnimatedValue();
                    if (newValue > value) {
                        if (lastTrend != add) {
                            addTime++;
                            lastTrend = add;
                        }
                    } else if (newValue < value) {
                        if (lastTrend != decrease) {
                            addTime++;
                            lastTrend = decrease;
                        }
                    }
                    if (addTime > 2) {
                        moveDistance = 0;
                        //                    已经到终点，缓冲动画
                        leftX = ((newValue - 50) * scale - radius);
                        //                    左边的x值变化是右边的过了临界值的时候开始变
                        underY = leftX * 0.2f;
                    }
                    if (addTime < 3 && newValue <= 200) {
                        if (moveDistance != defaultMove) {
                            moveDistance = 0;
                        }
                        if (newValue > value && newValue <= 150) {
                            rightX = newValue * scale - radius;

                        } else if (newValue < value) {
                            rightX = (newValue - 50) * scale - radius;
                            moveDistance = lastRightX - rightX;

                        }
//                    左边的x值变化是右边的过了临界值的时候开始变
                        if (newValue > value && newValue >= 150) {
                            if (!rightIsFull) {
                                rightIsFull = true;
                            }
                            //                        这个时候满足move条件
//                        右边是150到200时，左边是100到150
                            leftX = -((newValue - 50) * scale - radius); // 0.5*radius-0
//                        上下分别加减0.8r
                            underY = -((newValue - 50) * scale - radius) * 0.2f;

                        } else if (newValue < value) {
//                        右边是200到100时，左边是150到100
                            leftX = -((newValue - 50) * scale - radius);
                            underY = -((newValue - 50) * scale - radius) * 0.2f;

                        }
                        value = newValue;
                        lastRightX = rightX;
                    }
                    changePath();
//                    结束缓冲动画
                }
            });
            deformAnimator.setDuration(2000);
            deformAnimator.setInterpolator(new DecelerateInterpolator());
        }
        deformAnimator.start();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    private void changePath() {
        for (int i = 2; i < 5; i++) {
            control[i].x = originControl[i].x + rightX;
        }
        for (int i = 8; i < 11; i++) {
            control[i].x = originControl[i].x + leftX;
        }
        for (int i = 5; i < 8; i++) {
            control[i].y = originControl[i].y + underY;
        }
        control[0].y = originControl[0].y - underY;
        control[11].y = originControl[11].y - underY;
        control[1].y = originControl[1].y - underY;

        path.reset();
        path.moveTo(control[0].x, control[0].y);
        path.cubicTo(control[1].x, control[1].y, control[2].x, control[2].y, control[3].x, control[3].y);
        path.cubicTo(control[4].x, control[4].y, control[5].x, control[5].y, control[6].x, control[6].y);
        path.cubicTo(control[7].x, control[7].y, control[8].x, control[8].y, control[9].x, control[9].y);
        path.cubicTo(control[10].x, control[10].y, control[11].x, control[11].y, control[0].x, control[0].y);
        if (rightIsFull) {
            matrix.postTranslate(moveDistance, 0);
            path.transform(matrix);
        }
        invalidate();
    }
}
