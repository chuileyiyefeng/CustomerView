package com.example.rico.customerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tmp on 2019/3/8.
 * 画一条鱼
 */
public class FishSwimView extends View {
    private int width, height, rectLength;
    private Point centerPoint;
    private Rect rect;
    private Paint rectPaint, fishPaint;
    //    鱼鳍的path、鱼身体的path、圆的path、尾巴的path相交要填充，所以搞了多个path
    private Path fishPath, finPath, circlePath, tailPath;
    //    身体点、节肢点1、2，尾巴点1、2
    private List<PointF> bodyPoints, artPoints1, artPoints2,
            tailPoints1, tailPoints2, finPoints1, finPoints2;
    private List<CirclePoint> circlePoints;

    public FishSwimView(Context context) {
        super(context);
        init(context);
    }

    public FishSwimView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FishSwimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setColor(Color.parseColor("#f5f5f5"));

        fishPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fishPaint.setStyle(Paint.Style.STROKE);
        fishPaint.setColor(Color.parseColor("#FF4081"));
        fishPath = new Path();
        circlePath = new Path();
        finPath = new Path();
        tailPath = new Path();

        fishPath.op(circlePath, Path.Op.UNION);
        fishPath.op(finPath, Path.Op.UNION);
        fishPath.op(tailPath, Path.Op.UNION);

        bodyPoints = new ArrayList<>();
        artPoints1 = new ArrayList<>();
        artPoints2 = new ArrayList<>();
        circlePoints = new ArrayList<>();
        tailPoints1 = new ArrayList<>();
        tailPoints2 = new ArrayList<>();
        finPoints1 = new ArrayList<>();
        finPoints2 = new ArrayList<>();
        animator = new ValueAnimator();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        centerPoint = new Point(width / 2, height / 2);
        rectLength = Math.min(width, height);
        rect = new Rect(centerPoint.x - rectLength / 2, centerPoint.y - rectLength / 2, centerPoint.x + rectLength / 2, centerPoint.y + rectLength / 2);
        //      4个圆，所有的圆心X坐标是一样的
        centerX = width / 2;
        centerY = height / 2;
        initRadius();
        createFishPoint();
        addPath();
        startAnimator();
    }

    float centerX, centerY;
    float everyRotate = 10;
    float allRotate, shouldRotate, lastRotate;
    boolean needRotate, isUp;

    public void setAngle(float x, float y, float rotateX, float rotateY) {
        needRotate = true;
        calculationAngle(x, y, rotateX, rotateY);
    }

    //        点击的坐标点x,y 以哪个点为参考旋转 rotateX,rotateY是旋转的中心点
    private void calculationAngle(float x, float y, float rotateX, float rotateY) {
        float xSquare = (x - rotateX) * (x - rotateX);
        float ySquare = (y - rotateY) * (y - rotateY);
        double distance = Math.pow(xSquare + ySquare, 0.5);
        float distanceX = x - rotateX;

        float angleSinX = (float) Math.toDegrees(Math.asin((float) (distanceX / distance)));
        if (y > rotateY) {
            angleSinX = 180 - angleSinX;
        }
        if (angleSinX <= 0) {
            angleSinX += 360;
        }
        allRotate = angleSinX;
        if (allRotate > shouldRotate) {
            isUp = true;
        } else {
            isUp = false;
        }
        if (allRotate > 270 && lastRotate < 90) {
            isUp = false;
            allRotate = allRotate - 360;
            shouldRotate = lastRotate;
        } else if (allRotate < 90 && lastRotate > 270) {
            isUp = true;
            shouldRotate = lastRotate - 360;
        } else if (lastRotate < 0 && allRotate < 270 && allRotate > 180) {
            shouldRotate = 360 + lastRotate;
            isUp = false;
        }
        lastRotate = allRotate;
        invalidate();
    }

    private int circleX, distance, radius1, radius2, radius3, radius4;

    private void initRadius() {
        circleX = rect.left + (rect.right - rect.left) / 2;
//        头尾距离矩形的距离
        distance = (rect.bottom - rect.top) / 7;
//        设置圆心的半径比例为 5:4:3:1，最大的圆的半径就为distance
        radius1 = distance;
        radius2 = distance / 5 * 4;
        radius3 = distance / 5 * 3;
        radius4 = distance / 5;
    }

    //    生成小鱼的点 参考图R.mipmap.fish
    private void createFishPoint() {
        circlePoints.clear();
        bodyPoints.clear();
        artPoints1.clear();
        artPoints2.clear();
        tailPoints1.clear();
        tailPoints2.clear();
        finPoints1.clear();
        finPoints2.clear();

        int circleY1 = rect.top + radius1;
        int circleY2 = circleY1 + 2 * radius1 + radius2;
        int circleY3 = circleY2 + radius2 + radius3;
        int circleY4 = rect.bottom - radius4;

        circlePoints.add(new CirclePoint(circleX, circleY1, radius1));
        circlePoints.add(new CirclePoint(circleX, circleY2, radius2));
        circlePoints.add(new CirclePoint(circleX, circleY3, radius3));
        circlePoints.add(new CirclePoint(circleX, circleY4, radius4));
        int difference = radius1 - radius2;
//        鱼身体 俩个二阶贝塞尔曲线
//        左边
        int leftX1 = circleX - radius1;
        int leftX2 = circleX - radius2;
        int controlLeftX = leftX1 - difference;
        int controlLeftY = circleY1 + radius1 + 2 * difference;
//        右边
        int rightX1 = circleX + radius1;
        int rightX2 = circleX + radius2;
        int controlRightX = rightX1 + difference;

        bodyPoints.add(new PointF(leftX1, circleY1));
        bodyPoints.add(new PointF(controlLeftX, controlLeftY));
        bodyPoints.add(new PointF(leftX2, circleY2));
        bodyPoints.add(new PointF(rightX2, circleY2));
        bodyPoints.add(new PointF(controlRightX, controlLeftY));
        bodyPoints.add(new PointF(rightX1, circleY1));

//        两段节肢
        artPoints1.add(new PointF(circleX - radius2, circleY2));
        artPoints1.add(new PointF(circleX - radius3, circleY3));
        artPoints1.add(new PointF(circleX + radius3, circleY3));
        artPoints1.add(new PointF(circleX + radius2, circleY2));

        artPoints2.add(new PointF(circleX - radius3, circleY3));
        artPoints2.add(new PointF(circleX - radius4, circleY4));
        artPoints2.add(new PointF(circleX + radius4, circleY4));
        artPoints2.add(new PointF(circleX + radius3, circleY3));

//        尾巴 第三个圆的中心开始
        tailPoints1.add(new PointF(circleX, circleY3));
        tailPoints1.add(new PointF(circleX - radius3, circleY3 + radius3 * 3 / 2));
        tailPoints1.add(new PointF(circleX + radius3, circleY3 + radius3 * 3 / 2));

        tailPoints2.add(new PointF(circleX, circleY3));
        tailPoints2.add(new PointF(circleX - radius3 * 3 / 2, circleY3 + radius3 * 2));
        tailPoints2.add(new PointF(circleX + radius3 * 3 / 2, circleY3 + radius3 * 2));

//        画两条鱼鳍 左，右
        finPoints1.add(new PointF(circleX - radius1 * 8 / 10, circleY1 + radius1 / 5));
        finPoints1.add(new PointF(circleX - radius1 * 3, circleY1 + radius1));
        finPoints1.add(new PointF(circleX - radius1 * 8 / 10, circleY1 + radius1 / 5 + radius1));

        finPoints2.add(new PointF(circleX + radius1 * 8 / 10, circleY1 + radius1 / 5));
        finPoints2.add(new PointF(circleX + radius1 * 3, circleY1 + radius1));
        finPoints2.add(new PointF(circleX + radius1 * 8 / 10, circleY1 + radius1 / 5 + radius1));
    }

    private void addPath() {
        pathReset();
        for (int i = 0; i < circlePoints.size(); i++) {
            CirclePoint point = circlePoints.get(i);
            circlePath.addCircle(point.x, point.y, point.radius, Path.Direction.CCW);
        }

//        添加身体
        fishPath.moveTo(bodyPoints.get(0).x, bodyPoints.get(0).y);
        fishPath.quadTo(bodyPoints.get(1).x, bodyPoints.get(1).y, bodyPoints.get(2).x, bodyPoints.get(2).y);
        fishPath.lineTo(bodyPoints.get(3).x, bodyPoints.get(3).y);
        fishPath.quadTo(bodyPoints.get(4).x, bodyPoints.get(4).y, bodyPoints.get(5).x, bodyPoints.get(5).y);
        fishPath.lineTo(bodyPoints.get(0).x, bodyPoints.get(0).y);


//        添加两截节肢
        for (int i = 0; i < artPoints1.size(); i++) {
            PointF point = artPoints1.get(i);
            if (i == 0) {
                fishPath.moveTo(point.x, point.y);
            } else {
                fishPath.lineTo(point.x, point.y);
            }
        }
        fishPath.lineTo(artPoints1.get(0).x, artPoints1.get(0).y);

        for (int i = 0; i < artPoints2.size(); i++) {
            PointF point = artPoints2.get(i);
            if (i == 0) {
                fishPath.moveTo(point.x, point.y);
            } else {
                fishPath.lineTo(point.x, point.y);
            }
        }
        fishPath.lineTo(artPoints2.get(0).x, artPoints2.get(0).y);
//        两条尾巴
        for (int i = 0; i < tailPoints1.size(); i++) {
            PointF point = tailPoints1.get(i);
            if (i == 0) {
                tailPath.moveTo(point.x, point.y);
            } else {
                tailPath.lineTo(point.x, point.y);
            }
            if (i == tailPoints1.size() - 1) {
                tailPath.lineTo(tailPoints1.get(0).x, tailPoints1.get(0).y);
            }
        }

        for (int i = 0; i < tailPoints2.size(); i++) {
            PointF point = tailPoints2.get(i);
            if (i == 0) {
                tailPath.moveTo(point.x, point.y);
            } else {
                tailPath.lineTo(point.x, point.y);
            }
            if (i == tailPoints2.size() - 1) {
                tailPath.lineTo(tailPoints2.get(0).x, tailPoints2.get(0).y);
            }
        }
//      两边的鱼鳍
        finPath.moveTo(finPoints1.get(0).x, finPoints1.get(0).y);
        finPath.quadTo(finPoints1.get(1).x, finPoints1.get(1).y, finPoints1.get(2).x, finPoints1.get(2).y);

        finPath.moveTo(finPoints2.get(0).x, finPoints2.get(0).y);
        finPath.quadTo(finPoints2.get(1).x, finPoints2.get(1).y, finPoints2.get(2).x, finPoints2.get(2).y);

    }


    private void setRotateAngle(int angle) {
//        以第三个圆旋转，以第二个圆的圆心点旋转
        float rotateCircleX = circlePoints.get(1).x, rotateCircleY = circlePoints.get(1).y;
        CirclePoint circlePoint = circlePoints.get(2);
        float originX = circlePoint.x;
        float originY = circlePoint.y;
        PointF newPoint = getPointF(rotateCircleX, rotateCircleY, originX, originY, angle);
        circlePoint.setX(newPoint.x);
        circlePoint.setY(newPoint.y);


//        节肢动 节肢1也是以第二个圆的圆心旋转
        for (int i = 0; i < artPoints1.size(); i++) {
            PointF point = artPoints1.get(i);
            float x = point.x;
            float y = point.y;
            PointF artPoint = getPointF(rotateCircleX, rotateCircleY, x, y, angle);
            point.set(artPoint.x, artPoint.y);
        }

//        第三个圆的圆心位移的距离，以这个点为中心旋转的都要加上这段位移
        float distanceX = circlePoint.x - originX, distanceY = circlePoint.y - originY;


//        第三个圆的圆心坐标
        float rotateArtX = circlePoints.get(2).x, rotateArtY = circlePoints.get(2).y;
        angle = angle * 2;

//         节肢2 是以第三个圆的圆心旋转,尾巴也是一样，由于第三个圆旋转了，所以本来的坐标也要变
        for (int i = 0; i < artPoints2.size(); i++) {
            PointF point = artPoints2.get(i);
            float x = point.x;
            float y = point.y;
            PointF artPoint = getPointF(rotateArtX, rotateArtY, x + distanceX, y + distanceY, angle);
            point.set(artPoint.x, artPoint.y);
        }
//        尾巴动
        for (int i = 0; i < tailPoints1.size(); i++) {
            PointF point = tailPoints1.get(i);
            float x = point.x;
            float y = point.y;

            PointF tailPoint = getPointF(rotateArtX, rotateArtY, x + distanceX, y + distanceY, angle);
            point.set(tailPoint.x, tailPoint.y);
        }
        for (int i = 0; i < tailPoints2.size(); i++) {
            PointF point = tailPoints2.get(i);
            float x = point.x;
            float y = point.y;
            PointF tailPoint = getPointF(rotateArtX, rotateArtY, x + distanceX, y + distanceY, angle);
            point.set(tailPoint.x, tailPoint.y);
        }

        //        第四个圆旋转，以第三个圆为中心
        CirclePoint circlePoint3 = circlePoints.get(3);
        float originX3 = circlePoint3.x;
        float originY3 = circlePoint3.y;
        PointF circleP = getPointF(rotateArtX, rotateArtY, originX3 + distanceX, originY3 + distanceY, angle);
        circlePoint3.setX(circleP.x);
        circlePoint3.setY(circleP.y);
        addPath();
        invalidate();
    }

    private void pathReset() {
        fishPath.reset();
        circlePath.reset();
        finPath.reset();
        tailPath.reset();
    }


    //        鱼头摇动，以第一个圆圆心摇动
    ValueAnimator animator;
    //    摆动幅度
    int amplitudeAngle = 20;
    boolean isLeftSide;
    final int stateLeft = 1, stateRight = 2;
    //    鱼摇动系数
    int coefficient = 1;
    int tailAngle = 0;

    public void startAnimator() {
        if (null != animator && animator.isRunning()) {
            return;
        }
        animator = ValueAnimator.ofFloat(0, width);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int currentAngel, state = stateRight;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isLeftSide) {
                    currentAngel--;
                } else {
                    currentAngel++;
                }
                if (currentAngel >= amplitudeAngle) {
                    state = stateLeft;
                    isLeftSide = true;
                }
                if (currentAngel <= -amplitudeAngle) {
                    state = stateRight;
                    isLeftSide = false;
                }
                switch (state) {
                    case stateRight:
                        tailAngle++;
                        setRotateAngle(coefficient);
                        break;
                    case stateLeft:
                        tailAngle--;
                        setRotateAngle(-coefficient);
                        break;
                }
            }
        });
        animator.setDuration(100);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }


    private PointF getPointF(float rotateX, float rotateY, float x, float y, float angle) {
        if (rotateX == x && rotateY == y) {
            return new PointF(rotateX, rotateY);
        }
        float xSquare = Math.abs(x - rotateX) * Math.abs(x - rotateX);
        float ySquare = Math.abs(y - rotateY) * Math.abs(y - rotateY);

        double distance = Math.pow(xSquare + ySquare, 0.5);
        float distanceX = x - rotateX;
        float thisAngleX = (float) Math.toDegrees(Math.acos((float) (distanceX / distance)));
        if (y < rotateY) {
            thisAngleX = 360 - thisAngleX;
        }
        double sinX = distance * Math.sin(Math.toRadians(angle + thisAngleX));
        double cosX = distance * Math.cos(Math.toRadians(angle + thisAngleX));

//
        float realX = (float) (rotateX + cosX);
        float realY = (float) (rotateY + sinX);
        return new PointF(realX, realY);
    }




    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        drawPoints(canvas);
        if (isUp) {
            if (shouldRotate + everyRotate > allRotate) {
                needRotate = false;
                shouldRotate = shouldRotate + (allRotate - shouldRotate);
            } else {
                shouldRotate += everyRotate;
                needRotate = true;
            }
        } else {
            if (shouldRotate - everyRotate <= allRotate) {
                needRotate = false;
                shouldRotate = shouldRotate - (shouldRotate - allRotate);
            } else {
                shouldRotate -= everyRotate;
                needRotate = true;
            }
        }

        canvas.rotate(shouldRotate, centerX, centerY);
        canvas.drawPath(fishPath, fishPaint);
        canvas.drawPath(finPath, fishPaint);
        canvas.drawPath(circlePath, fishPaint);
        canvas.drawPath(tailPath, fishPaint);
    }

    private void drawPoints(Canvas canvas) {
        Paint pointPaint = new Paint();
        pointPaint.setColor(Color.parseColor("#000000"));
        pointPaint.setStrokeWidth(6);
        for (CirclePoint circlePoint : circlePoints) {
            canvas.drawPoint(circlePoint.x, circlePoint.y, pointPaint);
        }
        for (PointF point : artPoints1) {
            canvas.drawPoint(point.x, point.y, pointPaint);
        }
        for (PointF point : artPoints2) {
            canvas.drawPoint(point.x, point.y, pointPaint);
        }
        for (PointF point : tailPoints1) {
            canvas.drawPoint(point.x, point.y, pointPaint);
        }
        for (PointF point : tailPoints2) {
            canvas.drawPoint(point.x, point.y, pointPaint);
        }
        for (PointF point : finPoints1) {
            canvas.drawPoint(point.x, point.y, pointPaint);
        }
        for (PointF point : finPoints2) {
            canvas.drawPoint(point.x, point.y, pointPaint);
        }
    }


    private class CirclePoint {
        public float x, y;
        public int radius;

        private CirclePoint(int x, int y, int radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.cancel();
        }
    }
}
