package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Tmp on 2018/12/19.
 * 六边形雷达图
 */
public class HexagonPathView extends View {
    Paint textPaint, coverPaint, borderPaint;
    Path path;
    int allRadius;

    public HexagonPathView(Context context) {
        super(context);
        init();
    }

    public HexagonPathView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setColor(Color.parseColor("#3e3a39"));
        textPaint.setTextSize(spToPx(14));
        Paint.FontMetrics metrics = textPaint.getFontMetrics();
        textH = metrics.descent - metrics.ascent;

        coverPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        coverPaint.setStyle(Paint.Style.FILL);
        coverPaint.setColor(Color.parseColor("#7014a4f4"));

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.parseColor("#f6564d"));
        path=new Path();
    }

    //    宽度、高度、绘制区域宽高、多边形的层级
    int width, height, standWidth, levelCount = 5;
    //    文字高度
    float textH;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        standWidth = w > h ? h : w;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (dataList.size() == 0) {
            return;
        }
        allRadius = (int) (standWidth / 2 - dpToPx(20) - maxTextLength);
        canvas.translate(width / 2, height / 2);
        int count = dataList.size();
        int fullAngle = 360;
        double area = fullAngle / count;
        int everyLess = allRadius / levelCount;
        //        画多边形

        //       角度旋转时候精度丢失的总共角度
        float lackAngle = fullAngle - (float) area * count;
        //        修正后的角度
        area = (float) area + lackAngle / count;
        //        画多边形
        for (int k = 0; k <= allRadius; k += everyLess) {
            canvas.save();
            int radius = allRadius - k;
            if (radius<everyLess) {
                break;
            }
            double sideLength = (radius * Math.sin(Math.toRadians(area / 2)) * 2 + 0.5);
            int y = (int) (radius * Math.cos(Math.toRadians(area / 2)) + 0.5);
            for (int i = 0; i < count; i++) {
                canvas.drawLine((int) (-sideLength / 2 + 0.5), y, (int) (sideLength / 2 + 0.5), y, borderPaint);
                canvas.rotate((float) area);
            }
            canvas.restore();
        }
        canvas.save();
        //        划连接线，旋转角度，然后画一条线
        for (int i = 0; i < count; i++) {

            if (i == 0) {
                canvas.rotate((float) (90 + area / 2));
            } else {
                canvas.rotate((float) area);
            }
            canvas.drawLine(everyLess, 0, allRadius, 0, borderPaint);

        }
        canvas.restore();

        //        画覆盖色
        path.reset();
        path.incReserve(count);
        int firstX = 0, firstY = 0;
        for (int i = 0; i < count; i++) {
            double currentLength = everyLess+(allRadius-everyLess) * dataList.get(i).percent;
            double sin = Math.sin(Math.toRadians(area / 2 + area * i));
            double cos = Math.cos(Math.toRadians(area / 2 + area * i));
            int x = (int) (currentLength * sin + 0.5);
            int y = (int) (currentLength * cos + 0.5);
            int textX = (int) ((allRadius + dpToPx(10)) * sin + 0.5);
            int textY = (int) ((allRadius + dpToPx(10)) * cos + 0.5);
            //            画文字，有8种情况
            drawText(canvas, -textX, textY, dataList.get(i).text);
            if (i == 0) {
                firstX = -x;
                firstY = y;
            }
            path.lineTo(-x, y);
        }
        path.lineTo(firstX, firstY);
        canvas.drawPath(path, coverPaint);
    }


    private int dpToPx(float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }

    private int spToPx(int sp) {
        return (int) (getResources().getDisplayMetrics().scaledDensity * sp);
    }

    private void drawText(Canvas canvas, int x, int y, String s) {
//        坐标都不为0，坐标系一二三四象限
        float textLength = textPaint.measureText(s);
        if (x != 0 && y != 0) {
//            第一象限
            if (x > 0 && y < 0) {
                canvas.drawText(s, x, y + textH / 2, textPaint);
            }
//            第二象限
            else if (x > 0 && y > 0) {
                canvas.drawText(s, x, y + textH / 2, textPaint);
            }
//            第三象限
            if (x < 0 && y > 0) {
                canvas.drawText(s, x - textLength, y + textH / 2, textPaint);
            }
//            第四象限
            else if (x < 0 && y < 0) {
                canvas.drawText(s, x - textLength, y + textH / 2, textPaint);
            }
        } else {
//            正X轴
            if (x > 0 ) {
                canvas.drawText(s, x, y + textH / 2, textPaint);
            }
//            正Y轴 应该不存在这种情况 因为canvas是以正Y轴为起点旋转的
            else if (x == 0 && y > 0) {
                canvas.drawText(s, x - textLength / 2, y, textPaint);
            }
//            负X轴
            else if (x < 0) {
                canvas.drawText(s, x - textLength, y + textH / 2, textPaint);
            }
//            负Y轴
            else if ( y < 0) {
                canvas.drawText(s, x - textLength / 2, y, textPaint);
            }
        }
    }

    ArrayList<Data> dataList = new ArrayList<>();
    float maxTextLength;

    public void addData(String text, double percent) {
        if (textPaint.measureText(text) > maxTextLength) {
            maxTextLength = textPaint.measureText(text);
        }
        dataList.add(new Data(text, percent));
    }

    public void clearData() {
        dataList.clear();
        maxTextLength = 0;
        invalidate();
    }

    //    设置网格层级
    public void setLevelCount(int levelCount) {
        this.levelCount = levelCount;
        invalidate();
    }

    //    设置文字颜色
    public void setTextColor(String color) {
        textPaint.setColor(Color.parseColor(color));
        invalidate();
    }

    //    设置文字大小
    public void setTextSize(int size) {
        textPaint.setTextSize(spToPx(size));
        Paint.FontMetrics metrics = textPaint.getFontMetrics();
        textH = metrics.descent - metrics.ascent;
        invalidate();
    }

    //    设置雷达覆盖色
    public void setCoverColor(String color) {
        coverPaint.setColor(Color.parseColor(color));
        invalidate();
    }

    //     设置网格颜色
    public void setBorderColor(String color) {
        borderPaint.setColor(Color.parseColor(color));
        invalidate();
    }

    public void draw() {
        invalidate();
    }

    private class Data {
        public Data(String text, double percent) {
            this.text = text;
            this.percent = percent;
        }

        String text;
        double percent;
    }
}
