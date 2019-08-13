package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.example.rico.customerview.CurveData;

import java.util.ArrayList;

/**
 * Created by Tmp on 2019/8/13.
 */
public class CurveMarkView extends BaseCustomerView {
    public CurveMarkView(Context context) {
        super(context);
    }

    public CurveMarkView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CurveMarkView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //    view的高度为统计图里最大的数值
    int maxNumerical;
    float maxLength;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        for (int i = 0; i < list.size(); i++) {
            maxNumerical = Math.max(list.get(i).getNumerical(), maxNumerical);
        }
        maxLength = textPaint.measureText(maxNumerical + "");
        baseLineDp = (int) ((metrics.bottom - metrics.top) * 3);
        int width = MeasureSpec.makeMeasureSpec((int) (maxLength * 2), MeasureSpec.EXACTLY);
//        这里加另外高度的原因：分为两个部分，baseLineDp为底部文字，baseHeadDp为顶部ui
        int baseHeadDp = percentHeight;
        int height = MeasureSpec.makeMeasureSpec(maxNumerical + baseLineDp + baseHeadDp, MeasureSpec.EXACTLY);
        setMeasuredDimension(width, height);
    }

    Paint textPaint;
    private Paint.FontMetrics metrics;
    private ArrayList<CurveData> list;

    @Override
    protected void init(Context context) {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#6E7984"));
        textPaint.setTextSize(spToPx());
        metrics = textPaint.getFontMetrics();

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
    }

    private float spToPx() {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawText(canvas);
    }

    private int baseLineDp;
    //    底部线的高度，每多少数值增加一条横向虚线
    int percentHeight = 100;

    //    画线
    private void drawText(Canvas canvas) {
        int height = getMeasuredHeight();
        int baseHeight = height - baseLineDp;
        float distance = (metrics.bottom - metrics.top) / 2 - metrics.bottom;
//        画虚线
        int drawTime = maxNumerical / percentHeight;
        if (drawTime > 0) {
            for (int i = 0; i <= drawTime; i++) {
                String text = i * percentHeight + "";
                float textLength = textPaint.measureText(text) + width / 4;
                int realHeight = baseHeight - percentHeight * i;
                canvas.drawText(text, width - textLength, realHeight + distance, textPaint);
            }
        }
    }
}
