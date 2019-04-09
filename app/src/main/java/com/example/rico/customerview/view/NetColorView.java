package com.example.rico.customerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Tmp on 2019/4/8.
 * 网状view,动态、色彩变化
 */
public class NetColorView extends BaseCustomerView {

    public NetColorView(Context context) {
        super(context);
    }

    public NetColorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    ArrayList<MovePoint> pointList;
    final int speed = 1;
    int width, height;
    Paint paint;
    Random random;
    ValueAnimator animator;
    Path linePath;

    @Override
    protected void init(Context context) {
        pointList = new ArrayList<>();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        random = new Random();
        linePath = new Path();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (animator == null || !animator.isRunning()) {
                    changePosition();
                }
                break;
        }
        return true;
    }

    private void changePosition() {
        animator = ValueAnimator.ofInt(width);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                getRealPoint();
                invalidate();
            }
        });
        animator.setDuration(5000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        createPoints();
    }

    private void createPoints() {
        int area;
        for (int i = 0; i < 100; i++) {
            if (i >= 0 && i < 25) {
                area = 1;
            } else if (i >= 25 && i < 50) {
                area = 2;
            } else if (i >= 50 && i < 75) {
                area = 3;
            } else {
                area = 4;
            }
            MovePoint point = new MovePoint();
            point.x = getRandomX(area);
            point.y = getRandomY(area);
            Log.e("create", "createPoints: " + area);
            if (point.x == 0 || point.y == 0) {
                continue;
            }
            point.color = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
            pointList.add(point);
        }
    }



    private int getRandomX(int area) {
        int pieceX = (width - 100) / 2;
        int x = 0;
        switch (area) {
            case 1:
            case 3:
                x = random.nextInt(pieceX) + 50;
                break;
            case 2:
            case 4:
                x = random.nextInt(pieceX) + pieceX + 50;
                break;
        }

        return x;
    }



    private int getRandomY(int area) {
        int y = 0;
        int pieceY = (height - 200) / 2;
        switch (area) {
            case 1:
            case 2:
                y = random.nextInt(pieceY) + 100;
                break;
            case 3:
            case 4:
                y = random.nextInt(pieceY) + pieceY + 100;
                break;
        }
        return y;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int index = 0;
        for (int i = 0; i < pointList.size(); i++) {
            MovePoint point = pointList.get(i);
            paint.setColor(point.color);
//            canvas.drawCircle(point.x,point.y,20,paint);
            for (int k = index; k < pointList.size(); k++) {
                MovePoint inPoint = pointList.get(k);
                if (point != inPoint) {
                    int distance = (int) (Math.pow((inPoint.x - point.x), 2) + Math.pow((inPoint.y - point.y), 2));
                    if (distance < 50000) {
                        canvas.drawLine(point.x, point.y, inPoint.x, inPoint.y, paint);
                    }
                }
            }
            index++;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
    }

    private void getRealPoint() {
        linePath.reset();
        for (int i = 0; i < pointList.size(); i++) {
            MovePoint point = pointList.get(i);
            if (point.trendX == 1) {
                point.x += speed;
                point.moveX += speed;
                if (point.moveX >= point.boundX) {
                    point.trendX = 0;
                }
            } else {
                point.x -= speed;
                point.moveX -= speed;
                if (point.moveX <= -point.boundX) {
                    point.trendX = 1;
                }
            }
            if (point.trendY == 1) {
                point.y += speed;
                point.moveY += speed;
                if (point.moveY >= point.boundY) {
                    point.trendY = 0;
                }
            } else {
                point.y -= speed;
                point.moveY -= speed;
                if (point.moveY <= -point.boundY) {
                    point.trendY = 1;
                }
            }
        }
    }

    private class MovePoint {
        int x, y, moveX, moveY, trendX, trendY, boundX, boundY;
        int color;

        private MovePoint() {
            trendX = random.nextInt(2);
            trendY = random.nextInt(2);
            boundX = random.nextInt(60) + 40;
            boundY = random.nextInt(100) + 100;
        }
    }
}
