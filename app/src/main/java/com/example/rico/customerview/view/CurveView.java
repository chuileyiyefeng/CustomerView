package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.example.rico.customerview.bean.CurveData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tmp on 2019/8/5.
 * 曲线统计图
 */
public class CurveView extends BaseCustomerView {


    public CurveView(Context context) {
        super(context);
    }

    public CurveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CurveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //    文字画笔，线画笔，虚线画笔，曲线画笔，渐变画笔，点画笔 1 2，点击显示的文字画笔
    Paint textPaint, linePaint, dottedLinePaint,
            curvePaint, gDPaint, pointPaint, pointPaint2, detailPaint;
    private Paint.FontMetrics textMetrics, detailMetrics;
    //    view的高度为统计图里最大的数值
    int maxNumerical;
    //    数据集合
    private ArrayList<CurveData> list;

    private int baseLineDp, baseHeadDp;
    private int everyLength;
    private ArrayList<PointF> pointList;
    float scale;
    float everyHeight;
    int everyLevel;

    // 框的高度
    float rectHeight;
    // 指示点区域突出的高度
    float protrudingHeight = 20;

    @Override
    protected void init(Context context) {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#6E7984"));
        textPaint.setTextSize(spToPx(12));
        textMetrics = textPaint.getFontMetrics();

        dottedLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dottedLinePaint.setColor(Color.parseColor("#D4D8DF"));
        // float两个参数：虚线长度、虚线间距
        DashPathEffect pathEffect = new DashPathEffect(new float[]{6, 10}, 20);
        dottedLinePaint.setPathEffect(pathEffect);
        dottedLinePaint.setStyle(Paint.Style.STROKE);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.parseColor("#D4D8DF"));

        curvePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        curvePaint.setColor(Color.parseColor("#F0008A"));
        curvePaint.setStyle(Paint.Style.STROKE);
        curvePaint.setStrokeWidth(3f);


        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setColor(Color.parseColor("#ffffff"));
        pointPaint2.setColor(Color.parseColor("#F0008A"));

        gDPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gDPaint.setAlpha(180);
        gDPaint.setStyle(Paint.Style.FILL);

        detailPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        detailPaint.setTextSize(spToPx(14));
        detailPaint.setColor(Color.parseColor("#ffffff"));
        detailMetrics = detailPaint.getFontMetrics();
        rectHeight = (detailMetrics.bottom - detailMetrics.top) * 2;
        list = new ArrayList<>();
        pointList = new ArrayList<>();
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        gtPath = new Path();
        curvePath = new Path();
    }

    public void setData(List<CurveData> dataList) {
        list.clear();
        list.addAll(dataList);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int oWidth = MeasureSpec.getSize(widthMeasureSpec);
        for (int i = 0; i < list.size(); i++) {
            maxNumerical = Math.max(list.get(i).getNumerical(), maxNumerical);
        }
        everyLevel = getEveryLevel(maxNumerical);
        baseLineDp = (int) ((textMetrics.bottom - textMetrics.top) * 3);
        everyLength = oWidth / 6;
        int width = MeasureSpec.makeMeasureSpec(everyLength * (list.size()), MeasureSpec.EXACTLY);
        baseHeadDp = 20;

        scale = (height - baseHeadDp - baseLineDp - rectHeight - protrudingHeight) / maxNumerical;
        everyHeight = everyLevel * scale;
        setMeasuredDimension(width, height);
    }

    private int getEveryLevel(int maxNumerical) {
        for (int i = 1; i < 6; i++) {
            if (maxNumerical >= Math.pow(10, i) && maxNumerical <= Math.pow(10, i + 1)) {
                maxNumerical = (int) Math.pow(10, i);
                return maxNumerical;
            }
        }
        return 100;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        LinearGradient gradient = new LinearGradient(0, height - baseLineDp - maxNumerical * scale, 0, height - baseLineDp, arcColors, null, Shader.TileMode.CLAMP);
        gDPaint.setShader(gradient);
    }

    int[] arcColors = new int[]{
            Color.parseColor("#fabfde"),
            Color.parseColor("#ffffff"),

    };


    float downX, downY;
    float touchSlop;
    float clickArea = dpToPx(14);
    final int noClick = -1;
    int clickPosition = noClick;

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
                if (Math.abs(downX - upX) <= touchSlop && Math.abs(downY - upY) <= touchSlop) {
                    clickPosition = noClick;
                    for (int i = 0; i < pointList.size(); i++) {
                        PointF pointF = pointList.get(i);
                        if (Math.abs(pointF.x - upX) <= clickArea && Math.abs(pointF.y - upY) <= clickArea) {
                            clickPosition = i;
                            break;
                        }
                    }
                    invalidate();
                    return true;
                } else {
                    return false;
                }
        }
        return true;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawLine(canvas);
        drawCurve(canvas);
        drawPoint(canvas);
        drawText(canvas);
        drawClickInfo(canvas);
    }


    //    画线
    private void drawLine(Canvas canvas) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int baseHeight = height - baseLineDp;
        //        画基线
        canvas.drawLine(0, baseHeight, width, baseHeight, linePaint);
//        画虚线
        int drawTime = maxNumerical % everyLevel == 0 ? maxNumerical / everyLevel : maxNumerical / everyLevel + 1;
        if (drawTime > 0) {
            for (int i = 1; i <= drawTime; i++) {
                int realHeight = (int) (baseHeight - everyHeight * i);
                Path dottedPath = new Path();
                dottedPath.moveTo(0, realHeight);
                dottedPath.lineTo(width, realHeight);
                canvas.drawPath(dottedPath, dottedLinePaint);
            }
        }
    }

    //  画点
    private void drawPoint(Canvas canvas) {
        for (int i = 0; i < pointList.size(); i++) {
            PointF pointF = pointList.get(i);
            canvas.drawCircle(pointF.x, pointF.y, 8, pointPaint);
            canvas.drawCircle(pointF.x, pointF.y, 5, pointPaint2);
        }
    }

    //    画曲线,顺便画渐变的path
    Path gtPath;
    Path curvePath;

    private void drawCurve(Canvas canvas) {
        pointList.clear();
        gtPath.reset();
        curvePath.reset();
        for (int i = 0; i < list.size(); i++) {
            CurveData data = list.get(i);
            PointF point = new PointF();
            point.x = (i + 0.5f) * everyLength;
            point.y = height - baseLineDp - data.getNumerical() * scale;
            pointList.add(point);
        }
        for (int i = 0; i < pointList.size() - 1; i++) {
            PointF startP = pointList.get(i);
            PointF endP = pointList.get(i + 1);
            float wt = (startP.x + endP.x) / 2;
            PointF p3 = new PointF();
            PointF p4 = new PointF();
            p3.y = startP.y;
            p3.x = wt;
            p4.y = endP.y;
            p4.x = wt;
            // 渐变的path比曲线path低的距离
            int distanceY = 10;
            // 渐变的path比曲线path窄的距离
            int distanceX;
            if (startP.y > endP.y) {
                distanceX = 10;
            } else {
                distanceX = -10;
            }
            if (i == 0) {
                curvePath.moveTo(startP.x, startP.y);
                gtPath.moveTo(startP.x, height - baseLineDp);
                gtPath.lineTo(startP.x, startP.y + distanceY);
            }
            curvePath.cubicTo(p3.x, p3.y, p4.x, p4.y, endP.x, endP.y);
            gtPath.cubicTo(p3.x + distanceX, p3.y + distanceY, p4.x + distanceX, p4.y + distanceY, endP.x, endP.y + distanceY);
            if (i == pointList.size() - 2) {
                gtPath.lineTo(endP.x, height - baseLineDp);
                gtPath.close();
            }
        }
        canvas.drawPath(curvePath, curvePaint);
        canvas.drawPath(gtPath, gDPaint);
    }

    // 画底部文字
    private void drawText(Canvas canvas) {
        float y = height - (baseLineDp / 2 - textMetrics.bottom);
        for (int i = 0; i < list.size(); i++) {
            CurveData data = list.get(i);
            float textLength = textPaint.measureText(data.getName());
            float x = (everyLength - textLength) / 2 + everyLength * i;
            canvas.drawText(data.getName(), x, y, textPaint);
        }
    }

    // 画指点击信息
    private void drawClickInfo(Canvas canvas) {
        //  说明点到指示点区域了
        float textMargin = 20;
        float protrudingWidth = 20;
        if (clickPosition != noClick) {
            PointF pointF = pointList.get(clickPosition);
            Path rectPath = new Path();
            float x = pointF.x;
            float y = pointF.y - baseHeadDp;
            rectPath.moveTo(x, y);
            String text = list.get(clickPosition).getData();
            float textLength = detailPaint.measureText(text);
            float rectWidth = textLength + textMargin * 2;
            //  框在左边
            float starX, startY;
            if (x + rectWidth >= width) {
                rectWidth = -rectWidth;
                protrudingWidth = -protrudingWidth;
                starX = x - textLength - textMargin;
            } else {
                starX = x + textMargin;
            }
            rectPath.lineTo(x, y - rectHeight);
            rectPath.lineTo(x + rectWidth, y - rectHeight);
            rectPath.lineTo(x + rectWidth, y - protrudingHeight);
            rectPath.lineTo(x + protrudingWidth, y - protrudingHeight);
            canvas.drawPath(rectPath, pointPaint2);
            float baseLine = (y * 2 - protrudingHeight - rectHeight) / 2;
            startY = baseLine + (detailMetrics.bottom - detailMetrics.top) / 2 - detailMetrics.bottom;
            canvas.drawText(text, starX, startY, detailPaint);
        }
    }

    public int dpToPx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    private float spToPx(float size) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, getResources().getDisplayMetrics());
    }
}
