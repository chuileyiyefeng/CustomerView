package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.util.ArrayList;

/**
 * Created by Tmp on 2019/8/5.
 * 曲线统计图
 */
public class CurveView extends BaseCustomerView {


    //    view的高度为统计图里最大的数值,数值1代表0.5dp
    int maxNumerical = 150;
    //    统计的次数，假如为月份，半年占满剩下的宽度
    int defaultTimes = 6, realTimes = 12;

    //    底部线的高度，每多少数值增加一条横向虚线
    int baseLineHeight, percentHeight = 50;

    public CurveView(Context context) {
        super(context);
    }

    public CurveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CurveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setMaxNumerical(int maxNumerical) {
        this.maxNumerical = maxNumerical;
    }

    public void setDefaultTimes(int defaultTimes) {
        this.defaultTimes = defaultTimes;
    }

    public void setRealTimes(int realTimes) {
        this.realTimes = realTimes;
    }

    //    文字画笔，线画笔，内容画笔
    Paint textPaint, linePaint, contentPaint;

    @Override
    protected void init(Context context) {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        contentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        DashPathEffect pathEffect = new DashPathEffect(new float[]{20, 20}, 20);
        textPaint.setColor(Color.parseColor("#BBBBBB"));
        linePaint.setColor(Color.parseColor("#BBBBBB"));
        contentPaint.setColor(Color.parseColor("#FF4081"));

        linePaint.setPathEffect(pathEffect);
        linePaint.setStyle(Paint.Style.STROKE);
    }

    int baseLineDp = 70, baseHeadDp = 20;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int defaultWidth = MeasureSpec.getSize(widthMeasureSpec);
        int width = MeasureSpec.makeMeasureSpec(defaultWidth / defaultTimes * realTimes, MeasureSpec.EXACTLY);
//        这里加70的原因：分为50和20两个部分，50为底部文字，20为顶部ui
        int height = MeasureSpec.makeMeasureSpec(dpToPx(maxNumerical + baseLineDp + baseHeadDp), MeasureSpec.EXACTLY);
        baseLineHeight = dpToPx(baseLineDp);
        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        drawLine(canvas);
    }

    //    画线
    private void drawLine(Canvas canvas) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int baseHeight = height - baseLineHeight;
        //        画基线
        canvas.drawLine(0, baseHeight, width, baseHeight, textPaint);
//        画虚线
        int drawTime;
        if (maxNumerical % percentHeight == 0) {
            drawTime = maxNumerical / percentHeight;
        } else {
            drawTime = maxNumerical / percentHeight + 1;
        }
        if (drawTime > 0) {
            int addHeight = dpToPx(percentHeight);
            for (int i = 1; i <= drawTime; i++) {
                int realHeight = baseHeight - addHeight * i;
                Path path = new Path();
                path.moveTo(0, realHeight);
                path.lineTo(width, realHeight);
                canvas.drawPath(path, linePaint);
//                canvas.drawLine(, , linePaint);
            }
        }
    }

    public int dpToPx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    private ArrayList<CurveData> list;

    public class CurveData {
        int numerical;
        String name;

        public int getNumerical() {
            return numerical;
        }

        public void setNumerical(int numerical) {
            this.numerical = numerical;
        }

        public String getName() {
            return name == null ? "" : name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
