package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

/**
 * @Description: 裁剪框view
 * @Author: pan yi
 * @Date: 2021/12/17
 */
public class CropView extends BaseCustomerView {
    private Paint paint;
    private int rectWidth;
    private int rectHeight;
    private int cornerLength = 50;
    private Path linePath;

    public void setCornerLength(int cornerLength) {
        this.cornerLength = cornerLength;
        invalidate();
    }


    public CropView(Context context) {
        super(context);
    }

    public CropView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CropView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#ffffff"));
        linePath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rectWidth = width;
        rectHeight = height;
    }

    public void setColor(int color) {
        paint.setColor(getResources().getColor(color));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCorner(canvas);

        drawLine(canvas);
    }

    private void drawLine(Canvas canvas) {
        paint.setStrokeWidth(dpToSp(1));
        canvas.drawLine(0,rectHeight/3f,rectWidth,rectHeight/3f,paint);
        canvas.drawLine(0,rectHeight/3f*2,rectWidth,rectHeight/3f*2,paint);
        canvas.drawLine(rectWidth/3f,stokeWidth,rectWidth/3f,rectHeight-stokeWidth,paint);
        canvas.drawLine(rectWidth/3f*2,stokeWidth,rectWidth/3f*2,rectHeight-stokeWidth,paint);
        canvas.drawPath(linePath, paint);
    }

    private float stokeWidth;
    private void drawCorner(Canvas canvas) {
        float paintDistance = dpToSp(2);
        stokeWidth=paintDistance*2;
        paint.setStrokeWidth(stokeWidth);
        int length = dpToSp(cornerLength);
        // 画横
        canvas.drawLine(0, paintDistance, length, paintDistance, paint);
        canvas.drawLine(0, rectHeight - paintDistance, length, rectHeight - paintDistance, paint);
        canvas.drawLine(rectWidth, paintDistance, rectWidth - length, paintDistance, paint);
        canvas.drawLine(rectWidth, rectHeight - paintDistance, rectWidth - length, rectHeight - paintDistance, paint);

        // 画竖
        canvas.drawLine(paintDistance, 0, paintDistance, length, paint);
        canvas.drawLine(paintDistance, rectHeight - paintDistance, paintDistance, rectHeight - paintDistance - length, paint);
        canvas.drawLine(rectWidth - paintDistance, 0, rectWidth - paintDistance, length, paint);
        canvas.drawLine(rectWidth - paintDistance, rectHeight, rectWidth - paintDistance, rectHeight - length, paint);
    }

    private int dpToSp(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        float px = scale * dp;
        return (int) (px + 0.5f);
    }
}
