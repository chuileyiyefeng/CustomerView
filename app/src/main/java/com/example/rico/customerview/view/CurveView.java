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

import com.example.rico.customerview.CurveData;

import java.util.ArrayList;

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

    //    文字画笔，线画笔，虚线画笔，曲线画笔，点画笔，渐变画笔
    Paint textPaint, linePaint, dottedLinePaint, curvePaint, gDPaint, pointPaint, pointPaint2;
    private Paint.FontMetrics metrics;
    //    view的高度为统计图里最大的数值
    int maxNumerical;

    //    底部线的高度，每多少数值增加一条横向虚线
    int percentHeight = 100;
    //    数据集合
    private ArrayList<CurveData> list;

    @Override
    protected void init(Context context) {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#6E7984"));
        textPaint.setTextSize(spToPx());
        metrics = textPaint.getFontMetrics();

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.parseColor("#D4D8DF"));
        // float两个参数：虚线长度、虚线间距
        DashPathEffect pathEffect = new DashPathEffect(new float[]{6, 10}, 20);
        linePaint.setPathEffect(pathEffect);
        linePaint.setStyle(Paint.Style.STROKE);

        dottedLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dottedLinePaint.setColor(Color.parseColor("#D4D8DF"));

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

        list = new ArrayList<>();
        list.add(new CurveData(300, "一月"));
        list.add(new CurveData(400, "二月"));
        list.add(new CurveData(200, "三月"));
        list.add(new CurveData(600, "四月"));
        list.add(new CurveData(200, "五月"));
        list.add(new CurveData(280, "六月"));
        list.add(new CurveData(650, "七月"));
        list.add(new CurveData(320, "八月"));
        list.add(new CurveData(150, "九月"));
        list.add(new CurveData(300, "十月"));
        list.add(new CurveData(121, "十一月"));
        list.add(new CurveData(221, "十二月"));
        pointList = new ArrayList<>();
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private int baseLineDp;
    private int everyLength = 200;
    private ArrayList<PointF> pointList;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        for (int i = 0; i < list.size(); i++) {
            maxNumerical = Math.max(list.get(i).getNumerical(), maxNumerical);

        }
        baseLineDp = (int) ((metrics.bottom - metrics.top) * 3);
        int width = MeasureSpec.makeMeasureSpec(everyLength * (list.size()), MeasureSpec.EXACTLY);
//        这里加另外高度的原因：分为两个部分，baseLineDp为底部文字，baseHeadDp为顶部ui
        int baseHeadDp = percentHeight;
        int height = MeasureSpec.makeMeasureSpec(maxNumerical + baseLineDp + baseHeadDp, MeasureSpec.EXACTLY);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        LinearGradient gradient = new LinearGradient(0, height - baseLineDp - maxNumerical, 0, height - baseLineDp, arcColors, null, Shader.TileMode.CLAMP);
        gDPaint.setShader(gradient);
    }

    int[] arcColors = new int[]{
            Color.parseColor("#fabfde"),
            Color.parseColor("#ffffff"),

    };


    float downX, downY;
    float touchSlop;
    float clickArea = dpToPx(10);
    int clickPosition;

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
                    for (int i = 0; i < pointList.size(); i++) {
                        PointF pointF = pointList.get(i);
                        if (Math.abs(pointF.x - upX) <= clickArea && Math.abs(pointF.y - upY) <= clickArea) {
                            clickPosition = i;
                            invalidate();
                            break;
                        }
                    }
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
        canvas.drawLine(0, baseHeight, width, baseHeight, dottedLinePaint);
//        画虚线
        int drawTime = maxNumerical / percentHeight;
        if (drawTime > 0) {
            for (int i = 1; i <= drawTime; i++) {
                int realHeight = baseHeight - percentHeight * i;
                Path path = new Path();
                path.moveTo(0, realHeight);
                path.lineTo(width, realHeight);
                canvas.drawPath(path, linePaint);
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

    private void drawCurve(Canvas canvas) {
        pointList.clear();
        for (int i = 0; i < list.size(); i++) {
            CurveData data = list.get(i);
            PointF point = new PointF();
            point.x = (i + 0.5f) * everyLength;
            point.y = height - baseLineDp - data.getNumerical();
            pointList.add(point);
            canvas.drawCircle(point.x, point.y, 8, pointPaint);
            canvas.drawCircle(point.x, point.y, 5, pointPaint2);
        }
        if (gtPath == null) {
            gtPath = new Path();
        } else {
            gtPath.reset();
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
            Path path = new Path();
            path.moveTo(startP.x, startP.y);
            path.cubicTo(p3.x, p3.y, p4.x, p4.y, endP.x, endP.y);
            canvas.drawPath(path, curvePaint);


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
                gtPath.moveTo(startP.x, height - baseLineDp);
                gtPath.lineTo(startP.x, startP.y + distanceY);
            }
            gtPath.cubicTo(p3.x + distanceX, p3.y + distanceY, p4.x + distanceX, p4.y + distanceY, endP.x, endP.y + distanceY);
            if (i == pointList.size() - 2) {
                gtPath.lineTo(endP.x, height - baseLineDp);
                gtPath.close();
            }
        }
        canvas.drawPath(gtPath, gDPaint);
    }

    // 画底部文字
    private void drawText(Canvas canvas) {
        float y = height - (baseLineDp / 2 - metrics.bottom);
        for (int i = 0; i < list.size(); i++) {
            CurveData data = list.get(i);
            float textLength = textPaint.measureText(data.getName());
            float x = (everyLength - textLength) / 2 + everyLength * i;
            canvas.drawText(data.getName(), x, y, textPaint);
        }
    }

    // 画指点击信息
    private void drawClickInfo(Canvas canvas) {

    }

    public int dpToPx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    private float spToPx() {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
    }
}
