package com.example.rico.customerview.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tmp on 2019/3/8.
 */
public class FishSwimView extends View {
    private int width, height, rectLength;
    private Point centerPoint;
    private Rect rect;
    private Paint rectPaint, fishPaint;
    //    鱼鳍的path、鱼身体的path、圆的path、尾巴的path相交要填充，所以搞了多个path
    private Path fishPath, finPath, circlePath, tailPath;
    //    身体点、节肢点1、2，尾巴点1、2
    private List<Point> bodyPoints, artPoints1, artPoints2,
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
        scroller = new Scroller(context);
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
        movePath = new Path();

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
        rectLength = (int) (Math.min(width, height) / 2.0);
        rect = new Rect(centerPoint.x - rectLength / 2, centerPoint.y - rectLength / 2, centerPoint.x + rectLength / 2, centerPoint.y + rectLength / 2);
        //      4个圆，所有的圆心X坐标是一样的
        initRadius();
        createFishPoint();
        addPath();
    }

    int circleX, distance, radius1, radius2, radius3, radius4;

    private void initRadius() {
        circleX = rect.left + (rect.right - rect.left) / 2;
//        头尾距离矩形的距离
        distance = (rect.bottom - rect.top) / 10;
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

        int circleY1 = rect.top + radius1 + distance;
        int circleY2 = circleY1 + 2 * radius1 + radius2;
        int circleY3 = circleY2 + radius2 + radius3;
        int circleY4 = circleY3 + 3 * radius3 + radius4;

        circlePoints.add(new CirclePoint(circleX, circleY1, radius1));
        circlePoints.add(new CirclePoint(circleX, circleY2, radius2));
        circlePoints.add(new CirclePoint(circleX, circleY3, radius3));
        circlePoints.add(new CirclePoint(circleX, circleY4, radius4));

//        整个鱼的旋转中心
        allDegreesX = circleX;
        allDegreesY = circleY1 + (circleY4 - circleY1) / 2;


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

        bodyPoints.add(new Point(leftX1, circleY1));
        bodyPoints.add(new Point(controlLeftX, controlLeftY));
        bodyPoints.add(new Point(leftX2, circleY2));
        bodyPoints.add(new Point(rightX2, circleY2));
        bodyPoints.add(new Point(controlRightX, controlLeftY));
        bodyPoints.add(new Point(rightX1, circleY1));

//        两段节肢
        artPoints1.add(new Point(circleX - radius2, circleY2));
        artPoints1.add(new Point(circleX - radius3, circleY3));
        artPoints1.add(new Point(circleX + radius3, circleY3));
        artPoints1.add(new Point(circleX + radius2, circleY2));

        artPoints2.add(new Point(circleX - radius3, circleY3));
        artPoints2.add(new Point(circleX - radius4, circleY4));
        artPoints2.add(new Point(circleX + radius4, circleY4));
        artPoints2.add(new Point(circleX + radius3, circleY3));

//        尾巴 第三个圆的中心开始
        tailPoints1.add(new Point(circleX, circleY3));
        tailPoints1.add(new Point(circleX - radius3, circleY3 + radius3 * 3 / 2));
        tailPoints1.add(new Point(circleX + radius3, circleY3 + radius3 * 3 / 2));

        tailPoints2.add(new Point(circleX, circleY3));
        tailPoints2.add(new Point(circleX - radius3 * 3 / 2, circleY3 + radius3 * 2));
        tailPoints2.add(new Point(circleX + radius3 * 3 / 2, circleY3 + radius3 * 2));

//        画两条鱼鳍 左，右
        finPoints1.add(new Point(circleX - radius1 * 8 / 10, circleY1 + radius1 / 5));
        finPoints1.add(new Point(circleX - radius1 * 3, circleY1 + radius1));
        finPoints1.add(new Point(circleX - radius1 * 8 / 10, circleY1 + radius1 / 5 + radius1));

        finPoints2.add(new Point(circleX + radius1 * 8 / 10, circleY1 + radius1 / 5));
        finPoints2.add(new Point(circleX + radius1 * 3, circleY1 + radius1));
        finPoints2.add(new Point(circleX + radius1 * 8 / 10, circleY1 + radius1 / 5 + radius1));
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
            Point point = artPoints1.get(i);
            if (i == 0) {
                fishPath.moveTo(point.x, point.y);
            } else {
                fishPath.lineTo(point.x, point.y);
            }
        }
        for (int i = 0; i < artPoints2.size(); i++) {
            Point point = artPoints2.get(i);
            if (i == 0) {
                fishPath.moveTo(point.x, point.y);
            } else {
                fishPath.lineTo(point.x, point.y);
            }
        }
//        两条尾巴
        for (int i = 0; i < tailPoints1.size(); i++) {
            Point point = tailPoints1.get(i);
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
            Point point = tailPoints2.get(i);
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
        int rotateCircleX = circlePoints.get(1).x, rotateCircleY = circlePoints.get(1).y;
        CirclePoint circlePoint = circlePoints.get(2);
        int originX = circlePoint.x;
        int originY = circlePoint.y;
        circlePoint.setX(getRotateX(rotateCircleX, rotateCircleY, originX, originY, angle));
        circlePoint.setY(getRotateY(rotateCircleX, rotateCircleY, originX, originY, angle));


//        节肢动 节肢1也是以第二个圆的圆心旋转
        for (int i = 0; i < artPoints1.size(); i++) {
            Point point = artPoints1.get(i);
            int x = point.x;
            int y = point.y;
            point.set(getRotateX(rotateCircleX, rotateCircleY, x, y, angle),
                    getRotateY(rotateCircleX, rotateCircleY, x, y, angle));

        }

//        第三个圆的圆心位移的距离，以这个点为中心旋转的都要加上这段位移
        int distanceX = circlePoint.x - originX, distanceY = circlePoint.y - originY;


//        第三个圆的圆心坐标
        int rotateArtX = circlePoints.get(2).x, rotateArtY = circlePoints.get(2).y;
        angle = angle * 2;

//         节肢2 是以第三个圆的圆心旋转,尾巴也是一样，由于第三个圆旋转了，所以本来的坐标也要变
        for (int i = 0; i < artPoints2.size(); i++) {
            Point point = artPoints2.get(i);
            if (i == 1 || i == 2) {
                int x = point.x;
                int y = point.y;
                point.set(getRotateX(rotateArtX, rotateArtY, x + distanceX, y + distanceY, angle),
                        getRotateY(rotateArtX, rotateArtY, x + distanceX, y + distanceY, angle));
            } else {
                int x;
                if (i == 0) {
                    x = rotateArtX - radius3;
                } else {
                    x = rotateArtX + radius3;
                }
                point.set(x, rotateArtY);
            }

        }
//        尾巴动
        for (int i = 0; i < tailPoints1.size(); i++) {
            Point point = tailPoints1.get(i);
            int x = point.x;
            int y = point.y;
            point.set(getRotateX(rotateArtX, rotateArtY, x + distanceX, y + distanceY, angle),
                    getRotateY(rotateArtX, rotateArtY, x + distanceX, y + distanceY, angle));
        }
        for (int i = 0; i < tailPoints2.size(); i++) {
            Point point = tailPoints2.get(i);
            int x = point.x;
            int y = point.y;
            point.set(getRotateX(rotateArtX, rotateArtY, x + distanceX, y + distanceY, angle),
                    getRotateY(rotateArtX, rotateArtY, x + distanceX, y + distanceY, angle));
        }

        //        第四个圆旋转，以第三个圆为中心
        CirclePoint circlePoint3 = circlePoints.get(3);
        int originX3 = circlePoint3.x;
        int originY3 = circlePoint3.y;
        circlePoint3.setX(getRotateX(rotateArtX, rotateArtY, originX3 + distanceX, originY3 + distanceY, angle));
        circlePoint3.setY(getRotateY(rotateArtX, rotateArtY, originX3 + distanceX, originY3 + distanceY, angle));
        addPath();
        invalidate();
    }

    private void pathReset() {
        fishPath.reset();
        circlePath.reset();
        finPath.reset();
        tailPath.reset();
    }

    float downX, downY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (scroller.computeScrollOffset()) {
                    return false;
                }
                if (Math.abs(event.getX() - downX) < 10 && Math.abs(event.getY() - downY) < 10) {
//                  先把鱼头方向转至点击的点
                    aimClickPoint(event.getX(), event.getY());
                    startAnimator();
//                    setRotateAngle(-10);
                }
                break;
        }
        return true;
    }

    //    鱼头上次旋转的角度，也就是象限
    int lastQuadrant;
    //        鱼头摇动，以第一个圆圆心摇动
    int centerX;
    int centerY;
    Scroller scroller;

    private void aimClickPoint(float x, float y) {
//        先旋转后位移

        centerX = circlePoints.get(0).x;
        centerY = circlePoints.get(0).y;

        //        点击的点分为8个方向

        calculationAngle(x, y, allDegreesX, allDegreesY);
    }

    ValueAnimator animator;
    //    摆动幅度
    int amplitudeAngle = 20;
    int moveWay;
    final int inX = 1, inY = 2;
    boolean isLeftSide;
    final int stateLeft = 1, stateRight = 2;
    float canvasDegrees;
    //    鱼整个身体摇动系数
    final double coefficient = 0.5;

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
                        setRotateAngle(1);
                        canvasDegrees += coefficient;
                        break;
                    case stateLeft:
                        setRotateAngle(-1);
                        canvasDegrees -= coefficient;
                        break;
                }
            }
        });
        animator.setDuration(100);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }

    //        点击的坐标点x,y 以哪个点为参考旋转
    private void calculationAngle(float x, float y, int rotateX, int rotateY) {
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
        moveWay = inX;
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
                        moveWay = inY;
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
                        moveWay = inY;
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
                        moveWay = inY;
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
                        moveWay = inY;
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
        currentRotate = 0;

//        小鱼沿贝塞尔曲线移动
        movePath.reset();
        movePath.moveTo(x, y);
        int quadX, quadY;
        quadX = (int) (x +(x - rotateX) / 2);
        quadY = (int) (y +(y - rotateY) / 2);
        RectF rect=new RectF(x,y,rotateX,rotateY);

        movePath.quadTo(rect.left, rect.bottom, rotateX, rotateY);
    }

    Path movePath;

    private int getRotateX(int rotateX, int rotateY, int x, int y, float angle) {
//        首先求两个点连接起来的线段垂直于x轴之间的夹角

//        两个点之间的距离
        int xSquare = (x - rotateX) * (x - rotateX);
        int ySquare = (y - rotateY) * (y - rotateY);
        double distance = Math.pow(xSquare + ySquare, 0.5);
        if (rotateX == x && rotateY == y) {
            return rotateX;
        }

//        要求出来的那个角度的对边长
//        当方向是朝下时候，就是在-90到90度之间
        if (y >= rotateY) {
            int sinLength = Math.abs(rotateX - x);
            double angleSin = Math.toDegrees(Math.asin(sinLength / distance));
            if (x > rotateX) {
                angleSin = -angleSin;
            }
            double sinValue = Math.sin(Math.toRadians(Math.abs(angle + angleSin)));
            if (angle + angleSin > 0) {
                return (int) (rotateX - sinValue * distance + 0.5f);
            } else {
                return (int) (rotateX + sinValue * distance + 0.5f);
            }
        } else {
            int cosLength = Math.abs(rotateX - x);
            double angleSin = Math.toDegrees(Math.acos(cosLength / distance));
            if (x > rotateX) {
                angleSin = -angleSin;
            }
            double cosValue = Math.cos(Math.toRadians(Math.abs(angle + angleSin)));
            if (angle + angleSin > 0) {
                return (int) (rotateX - cosValue * distance + 0.5f);
            } else {
                return (int) (rotateX + cosValue * distance + 0.5f);
            }
        }


    }

    private int getRotateY(int rotateX, int rotateY, int x, int y, float angle) {
        //        首先求两个点连接起来的线段垂直于y轴之间的夹角
//        两个点之间的距离
        int xSquare = Math.abs(x - rotateX) * Math.abs(x - rotateX);
        int ySquare = Math.abs(y - rotateY) * Math.abs(y - rotateY);
        double distance = Math.pow(xSquare + ySquare, 0.5);
        if (rotateX == x && rotateY == y) {
            return rotateY;
        }
        if (y >= rotateY) {
            int cosLength = Math.abs(rotateY - y);
            double angleCos = Math.toDegrees(Math.acos(cosLength / distance));
            if (x > rotateX) {
                angleCos = -angleCos;
            }
            double cosValue = Math.cos(Math.toRadians(Math.abs(angle + angleCos)));

            return (int) (rotateY + cosValue * distance + 0.5f);
        } else {
            int sinLength = Math.abs(rotateY - y);
            double angleSin = Math.toDegrees(Math.asin(sinLength / distance));
            if (x > rotateX) {
                angleSin = -angleSin;
            }
            double sinValue = Math.sin(Math.toRadians(Math.abs(angle + angleSin)));
            return (int) (rotateY - sinValue * distance + 0.5f);
        }
    }

    //    鱼身体全部的旋转角度
    float allRotate = 0, currentRotate;
    int allDegreesX;
    int allDegreesY;
    float shouldRotate, lastAngleSinX, lastAngleSinY, everyRotate;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (shouldRotate > 0 && currentRotate < shouldRotate) {
            allRotate += everyRotate;
            currentRotate += everyRotate;
            if (currentRotate > shouldRotate) {
                float distance = shouldRotate - currentRotate + everyRotate;
                allRotate -= distance;
            }
        }

        if (shouldRotate < 0 && currentRotate > shouldRotate) {
            allRotate -= everyRotate;
            currentRotate -= everyRotate;
            if (currentRotate < shouldRotate) {
                float distance = shouldRotate - (currentRotate + everyRotate);
                allRotate -= distance;
            }
        }
        canvas.drawPath(movePath,fishPaint);
        allRotate = allRotate % 360;
        canvas.drawRect(rect, rectPaint);
        canvas.rotate(allRotate, allDegreesX, allDegreesY);

//        centerX = circlePoints.get(0).x;
//        centerY = circlePoints.get(0).y;
//        canvas.rotate(canvasDegrees, centerX, centerY);

        canvas.drawPath(fishPath, fishPaint);
        canvas.drawPath(finPath, fishPaint);
        canvas.drawPath(circlePath, fishPaint);
        canvas.drawPath(tailPath, fishPaint);

//        drawPoints(canvas);
    }

    private void drawPoints(Canvas canvas) {
        Paint pointPaint = new Paint();
        pointPaint.setColor(Color.parseColor("#000000"));
        pointPaint.setStrokeWidth(6);
        for (CirclePoint circlePoint : circlePoints) {
            canvas.drawPoint(circlePoint.x, circlePoint.y, pointPaint);
        }
        for (Point point : artPoints1) {
            canvas.drawPoint(point.x, point.y, pointPaint);
        }
        for (Point point : artPoints2) {
            canvas.drawPoint(point.x, point.y, pointPaint);
        }
        for (Point point : tailPoints1) {
            canvas.drawPoint(point.x, point.y, pointPaint);
        }
        for (Point point : tailPoints2) {
            canvas.drawPoint(point.x, point.y, pointPaint);
        }
        for (Point point : finPoints1) {
            canvas.drawPoint(point.x, point.y, pointPaint);
        }
        for (Point point : finPoints2) {
            canvas.drawPoint(point.x, point.y, pointPaint);
        }
    }

    private class CirclePoint {
        public int x, y, radius;

        private CirclePoint(int x, int y, int radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
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

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
        }
    }
}
