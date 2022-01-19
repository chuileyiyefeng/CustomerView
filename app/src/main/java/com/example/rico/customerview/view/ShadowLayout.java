package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Description:
 * @Author: pan yi
 * @Date: 2022/1/18
 */
public class ShadowLayout extends FrameLayout {
    public ShadowLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public ShadowLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShadowLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ShadowLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private Paint paint;
    private RectF rectF;
    private int width, height;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        rectF = new RectF();
    }

    public int shadowDistance = 10;

    private int dpToPx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        float paddingInside = dpToPx(shadowDistance);
        left = setPaddingDefault(left, paddingInside);
        top = setPaddingDefault(top, paddingInside);
        right = setPaddingDefault(top, paddingInside);
        bottom = setPaddingDefault(bottom, paddingInside);
        super.setPadding(left, top, right, bottom);
    }

    private int setPaddingDefault(int defaultValue, float paddingInside) {
        if (defaultValue > paddingInside) {
            return defaultValue;
        } else {
            return (int) paddingInside;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        float rd = dpToPx(shadowDistance);
//        paint.setMaskFilter(new BlurMaskFilter(rd, BlurMaskFilter.Blur.SOLID));//设置发光样式,NORMAL是内外发光
        paint.setShadowLayer(rd, 0, 0, Color.parseColor("#0f000000"));//绘制阴影的主要方法
        rectF.set(rd, rd, width - rd, height - rd);
        canvas.drawRoundRect(rectF, rd, rd, paint);
        canvas.save();
        super.dispatchDraw(canvas);
    }
}
