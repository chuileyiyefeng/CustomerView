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
import java.util.List;

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
    int maxNumerical, baseHeadDp, protrudingHeight = 20;
    float maxLength, rectHeight;
    float scale;
    float everyHeight;
    int everyLevel = 100;
    Paint textPaint;
    Paint.FontMetrics metrics;
    ArrayList<CurveData> list;
    private int baseLineDp;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        for (int i = 0; i < list.size(); i++) {
            maxNumerical = Math.max(list.get(i).getNumerical(), maxNumerical);
        }
        everyLevel = getEveryLevel(maxNumerical);
        maxLength = textPaint.measureText(maxNumerical + "");
        baseLineDp = (int) ((metrics.bottom - metrics.top) * 3);
        int width = MeasureSpec.makeMeasureSpec((int) (maxLength * 2), MeasureSpec.EXACTLY);
        baseHeadDp = 50;
        int height = MeasureSpec.getSize(heightMeasureSpec);
        scale = (height - baseHeadDp - baseLineDp - rectHeight - protrudingHeight) / maxNumerical;
        everyHeight = everyLevel * scale;
        setMeasuredDimension(width, height);
    }

    private int getEveryLevel(int maxNumerical) {
        for (int i = 1; i < 6; i++) {
            if (maxNumerical>Math.pow(10,i)&&maxNumerical<Math.pow(10,i+1)) {
                maxNumerical= (int) Math.pow(10,i);
                return  maxNumerical;
            }
        }
        return 100;
    }

    @Override
    protected void init(Context context) {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#6E7984"));
        textPaint.setTextSize(spToPx(12));
        metrics = textPaint.getFontMetrics();

        Paint detailPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        detailPaint.setTextSize(spToPx(14));
        detailPaint.setColor(Color.parseColor("#ffffff"));
        Paint.FontMetrics detailMetrics = detailPaint.getFontMetrics();
        rectHeight = (detailMetrics.bottom - detailMetrics.top) * 2;
        list = new ArrayList<>();
    }

    public void setData(List<CurveData> dataList) {
        list.clear();
        list.addAll(dataList);
        requestLayout();
    }

    private float spToPx(float textSize) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, getResources().getDisplayMetrics());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawText(canvas);
    }


    //    画线
    private void drawText(Canvas canvas) {
        int height = getMeasuredHeight();
        int baseHeight = height - baseLineDp;
        float distance = (metrics.bottom - metrics.top) / 2 - metrics.bottom;
        int drawTime = maxNumerical % everyLevel == 0 ? maxNumerical / everyLevel : maxNumerical / everyLevel + 1;
        if (drawTime > 0) {
            for (int i = 0; i <= drawTime; i++) {
                String text = i * everyLevel + "";
                float textLength = textPaint.measureText(text) + width / 4;
                int realHeight = (int) (baseHeight - everyLevel * i * scale);
                canvas.drawText(text, width - textLength, realHeight + distance, textPaint);
            }
        }
    }
}
