package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.example.rico.customerview.R;

import java.util.ArrayList;

/**
 * Created by Tmp on 2019/8/9.
 */
public class RingView extends BaseCustomerView {


    public RingView(Context context) {
        super(context);
    }

    public RingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Paint paint;
    private RectF rectS, rectB, rectMin, rectMax;
    private ArrayList<Path> pathList;
    private ArrayList<Integer> colors, numbers;
    private int allNumber, maxNumber, minR, maxR;
    //    最大的圆环起始角度，扫过的角度，大圆环与其他圆环的间距
    private float maxStartAngle, maxSweepAngle, stoke;
    int cX, cY;

    float distanceAngle = 2.0f;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float percent = (float) Math.min(w, h) / 50 / 2;
        Point centerPoint = new Point(width / 2, height / 2);
        cX = centerPoint.x;
        cY = centerPoint.y;
        int sR = (int) (percent * 32);
        int bR = (int) (percent * 48);
        minR = (int) (percent * 30);
        maxR = (int) (percent * 50);
        rectS.set(cX - sR, cY - sR, cX + sR, cY + sR);
        rectB.set(cX - bR, cY - bR, cX + bR, cY + bR);
        rectMin.set(cX - minR, cY - minR, cX + minR, cY + minR);
        rectMax.set(cX - maxR, cY - maxR, cX + maxR, cY + maxR);
        stoke = (float) (distanceAngle / 360 * 2 * Math.PI * maxR);
    }

    @Override
    protected void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(R.color.orange));
        rectS = new RectF();
        rectB = new RectF();
        rectMin = new RectF();
        rectMax = new RectF();
        pathList = new ArrayList<>();
        colors = new ArrayList<>();
        numbers = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < pathList.size(); i++) {
            paint.setColor(getResources().getColor(colors.get(i)));
            canvas.drawPath(pathList.get(i), paint);
        }
    }

    public RingView addData(int colorRes, int number) {
        colors.add(colorRes);
        numbers.add(number);
        allNumber += number;
        maxNumber = Math.max(number, maxNumber);
        return this;
    }


    public void refreshView() {
        post(new Runnable() {
            @Override
            public void run() {
                float lastAngle = 0;
                int clipPosition = 0;
                for (int i = 0; i < numbers.size(); i++) {
                    int number = numbers.get(i);
                    float percentage = (float) number / allNumber;
                    float angle = 360 * percentage;
                    Path path = new Path();
                    if (number == maxNumber) {
                        maxStartAngle = lastAngle;
                        maxSweepAngle = angle;
                        path.addArc(rectMin, lastAngle, angle);
                        path.arcTo(rectMax, lastAngle + angle, -angle);
                        path.close();
                        path.op(getPath(lastAngle), Path.Op.DIFFERENCE);
                        path.op(getPath(lastAngle + angle - distanceAngle), Path.Op.DIFFERENCE);
                        clipPosition = i;
                    } else {
                        path.addArc(rectS, lastAngle, angle);
                        path.arcTo(rectB, lastAngle + angle, -angle);
                        path.close();
//                把最大的圆环后一个圆环也做处理
                        if (maxStartAngle != 0 && maxSweepAngle != 0) {
                            if (i > clipPosition) {
                                path.op(getPath(maxStartAngle), Path.Op.DIFFERENCE);
                                path.op(getPath(maxStartAngle + maxSweepAngle - distanceAngle), Path.Op.DIFFERENCE);
                            }
                        }
                    }
                    lastAngle += angle;
                    pathList.add(path);
                }
                invalidate();
            }
        });
    }

    @NonNull
//    获取空白区域的path
    private Path getPath(float angle) {
        double sin = Math.sin(Math.toRadians(angle));
        double cos = Math.cos(Math.toRadians(angle));
        Path rectPath = new Path();
//                4个原始点 minR,0  maxR,0  maxR,stoke minR,stoke
        float value1 = (float) (minR * cos);
        float value2 = (float) (minR * sin);

        float value3 = (float) (maxR * cos);
        float value4 = (float) (maxR * sin);

        float rr = (float) Math.sqrt(maxR * maxR + stoke * stoke);
        sin = stoke / rr;
        float lastAngle = (float) Math.toDegrees(Math.asin(sin));
        sin = Math.sin(Math.toRadians(angle + lastAngle));
        cos = Math.cos(Math.toRadians(angle + lastAngle));
        float value5 = (float) (rr * cos);
        float value6 = (float) (rr * sin);


        float rr2 = (float) Math.sqrt(minR * minR + stoke * stoke);
        sin = stoke / rr2;
        lastAngle = (float) Math.toDegrees(Math.asin(sin));
        sin = Math.sin(Math.toRadians(angle + lastAngle));
        cos = Math.cos(Math.toRadians(angle + lastAngle));
        float value7 = (float) (rr2 * cos);
        float value8 = (float) (rr2 * sin);

        PointF point1 = new PointF(value1 + cX, value2 + cY);
        PointF point2 = new PointF(value3 + cX, value4 + cY);
        PointF point3 = new PointF(value5 + cX, value6 + cY);
        PointF point4 = new PointF(value7 + cX, value8 + cY);
        rectPath.moveTo(point1.x, point1.y);
        rectPath.lineTo(point2.x, point2.y);
        rectPath.lineTo(point3.x, point3.y);
        rectPath.lineTo(point4.x, point4.y);
        rectPath.close();
        return rectPath;
    }
}
