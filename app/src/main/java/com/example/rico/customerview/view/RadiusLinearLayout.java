package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Description: 圆角控件 drawPath方式
 * @Author: pan yi
 * @Date: 2022/5/10
 */
public class RadiusLinearLayout extends LinearLayout {
    private final int INVALID_VALUE = -1;
    //圆角效果主要存在这个路径里
    private Path mPath;
    //圆角矩形描述
    private RectF mRectF;
    //画笔
    private Paint mPaint;

    //绘制缓冲，避免击穿整个 window 的画布出现黑色背景的情况
    private Bitmap mBitmap = null;
    private final Canvas mCanvas = new Canvas();
    private final Matrix mMatrix = new Matrix();

    public RadiusLinearLayout(@NonNull Context context) {
        this(context, null);
    }

    public RadiusLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public RadiusLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        mPath = new Path();
        mRectF = new RectF();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0) {
            mPath.reset();
            //Path 的填充模式为反奇偶规则
            mPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);
            if (getRadius() < 0) {
                mPath.addCircle((float) (w / 2), (float) (h / 2), (float) (Math.min(w, h) / 2), Path.Direction.CW);
            } else {
                mRectF.set(0, 0, w, h);
                mPath.addRoundRect(mRectF, getRadiusList(), Path.Direction.CW);
            }

            if (mBitmap == null || mBitmap.getWidth() != w || mBitmap.getHeight() != h) {
                mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                mCanvas.setBitmap(mBitmap);
            }
        }
    }

    private float getRadius() {
        return Math.min(radiusX, radiusY);
    }

    private int dpToPx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    float[] radii;
    int radiusX = dpToPx(15), radiusY = dpToPx(15);

    private float[] getRadiusList() {
        return radii = new float[]{dpToPx(10), dpToPx(10),
                radiusX, radiusY,
                radiusX, radiusY,
                radiusX, radiusY};
    }

    public void setRadius(int radiusX, int radiusY) {
        this.radiusX = radiusX;
        this.radiusY = radiusY;
        update();
    }

    @Override
    public void draw(Canvas canvas) {
        mBitmap.eraseColor(Color.TRANSPARENT);
        super.draw(mCanvas);
        mCanvas.drawPath(mPath, mPaint);
        canvas.drawBitmap(mBitmap, mMatrix, null);
    }

    //绘制刷新时的必要逻辑
    private void update() {
        mPath.reset();
        //Path 的填充模式为反奇偶规则
        mPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);
        if (getRadius() < 0) {
            mPath.addCircle((float) (getWidth() / 2), (float) (getHeight() / 2), (float) (Math.min(getWidth(), getHeight()) / 2), Path.Direction.CW);
        } else {
            mPath.addRoundRect(mRectF, getRadiusList(), Path.Direction.CW);
        }
        invalidate();
    }

}
