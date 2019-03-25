package com.example.rico.customerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

/**
 * Created by Tmp on 2019/3/22.
 */
public class ViewSwimView extends RelativeLayout {

    public ViewSwimView(Context context) {
        super(context);
        init(context);
    }

    public ViewSwimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ViewSwimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    PathMeasure mPathMeasure;

    private void init(Context context) {
        mPathMeasure = new PathMeasure();
        centerPoint = new PointF();
    }

    int width, height;
    PointF centerPoint;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        centerPoint.set(width / 2, height / 2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private float[] mCurrentPosition = new float[2];

    public void setPathAnimation(long duration) {
        // 0 － getLength()
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
        valueAnimator.setDuration(duration);
        // 减速插值器
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                // 获取当前点坐标封装到mCurrentPosition
                mPathMeasure.getPosTan(value, mCurrentPosition, null);
                postInvalidate();
            }
        });
        valueAnimator.start();
    }

    private float downX, downY;
    private boolean isFinsMoving;

    //    鱼头上次旋转的角度，也就是象限
    int lastQuadrant;
    //        鱼头摇动，以第一个圆圆心摇动
    float centerX;
    float centerY;
    float shouldRotate, lastAngleSinX, lastAngleSinY, everyRotate;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(event.getX() - downX) < 10 && Math.abs(event.getY() - downY) < 10) {
//                  先把鱼头方向转至点击的点
                    Point point = getCenterPoint(getChildAt(0));
                    View childView = getChildAt(0);
                    centerX = childView.getLeft() + (childView.getRight() - childView.getLeft()) / 2;
                    centerY = childView.getTop() + (childView.getBottom() - childView.getTop()) / 2;
                    calculationAngle(event.getX(), event.getY(), point.x, point.y);
                }
                break;
        }
        return true;
    }

    //        点击的坐标点x,y 以哪个点为参考旋转 rotateX,rotateY是旋转的中心点
    private void calculationAngle(float x, float y, float rotateX, float rotateY) {
        int xSquare = (int) ((x - rotateX) * (x - rotateX));
        int ySquare = (int) ((y - rotateY) * (y - rotateY));
        double distance = Math.pow(xSquare + ySquare, 0.5);
        int distanceY = (int) Math.abs(y - rotateY);
        int distanceX = (int) Math.abs(x - rotateX);

        float angleSinX = (float) Math.toDegrees(Math.asin(distanceX / distance));
        float angleSinY = (float) Math.toDegrees(Math.asin(distanceY / distance));
//         1 右上 和3 左下 象限


        int thisQuadrant = 0;
        if (x > rotateX && y < rotateY) {
            thisQuadrant = 1;
        }
        if (x < rotateX && y > rotateY) {

            thisQuadrant = 3;
        }
//        2 左上和4 右下象限
        if (x < rotateX && y < rotateY) {
            thisQuadrant = 2;
        }
        if (x > rotateX && y > rotateY) {
            thisQuadrant = 4;
        }

//        上次鱼头的方向和这次的朝向有多种情况 1-2 1-3 1-4 2-3 2-4 3-4

        switch (lastQuadrant) {
            case 0:
                switch (thisQuadrant) {
                    case 1:
                        shouldRotate = angleSinX;
                        break;
                    case 2:
                        shouldRotate = -angleSinX;
                        break;
                    case 3:
                        shouldRotate = -angleSinY - 90;
                        break;
                    case 4:
                        shouldRotate = angleSinY + 90;
                        break;
                }
                break;
            case 1:
                switch (thisQuadrant) {
                    case 1:
                        shouldRotate = angleSinX - lastAngleSinX;
                        break;
                    case 2:
                        shouldRotate = -angleSinX - lastAngleSinX;
                        break;
                    case 3:
                        shouldRotate = -lastAngleSinX - 90 - angleSinY;
                        break;
                    case 4:
                        shouldRotate = 180 - angleSinX - lastAngleSinX;
                        break;
                }
                break;
            case 2:
                switch (thisQuadrant) {
                    case 1:
                        shouldRotate = lastAngleSinX + angleSinX;
                        break;
                    case 2:
                        shouldRotate = lastAngleSinX - angleSinX;
                        break;
                    case 3:
                        shouldRotate = lastAngleSinX - 90 - angleSinY;
                        break;
                    case 4:
                        shouldRotate = -((90 - lastAngleSinX) + 90 + angleSinX);
                        break;
                }
                break;
            case 3:
                switch (thisQuadrant) {
                    case 1:
                        shouldRotate = lastAngleSinY + angleSinX + 90;
                        break;
                    case 2:
                        shouldRotate = lastAngleSinY + 90 - angleSinX;
                        break;
                    case 3:
                        shouldRotate = angleSinX - lastAngleSinX;
                        break;
                    case 4:
                        shouldRotate = -lastAngleSinX - angleSinX;
                        break;
                }
                break;
            case 4:
                switch (thisQuadrant) {
                    case 1:
                        shouldRotate = angleSinX + lastAngleSinX - 180;
                        break;
                    case 2:
                        shouldRotate = lastAngleSinX + 90 + (90 - angleSinX);
                        break;
                    case 3:
                        shouldRotate = lastAngleSinX + (90 - angleSinY);
                        break;
                    case 4:
                        shouldRotate = lastAngleSinX - angleSinX;
                        break;
                }
                break;
            default:
                break;
        }
        lastAngleSinX = angleSinX;
        lastAngleSinY = angleSinY;
        everyRotate = Math.abs(shouldRotate) / 10;
        lastQuadrant = thisQuadrant;
        invalidate();
    }

    float allRotate, currentRotate;
    boolean needRotate;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (shouldRotate > 0 && currentRotate < shouldRotate) {
            allRotate += everyRotate;
            currentRotate += everyRotate;
            needRotate = true;
            if (currentRotate > shouldRotate) {
                float distance = shouldRotate - currentRotate + everyRotate;
                allRotate -= distance;
                needRotate = false;
            }
        }

        if (shouldRotate < 0 && currentRotate > shouldRotate) {
            allRotate -= everyRotate;
            currentRotate -= everyRotate;
            needRotate = true;
            if (currentRotate < shouldRotate) {
                float distance = shouldRotate - (currentRotate + everyRotate);
                allRotate -= distance;
                needRotate = false;
            }
        }
        allRotate = allRotate % 360;
        canvas.rotate(allRotate, centerX, centerY);
        if (needRotate) {
            invalidate();
        }
    }

    private Point getCenterPoint(View view) {
        int x = view.getLeft() + (view.getRight() - view.getLeft()) / 2;
        int y = view.getTop() + (view.getBottom() - view.getTop()) / 2;
        return new Point(x, y);
    }
}
