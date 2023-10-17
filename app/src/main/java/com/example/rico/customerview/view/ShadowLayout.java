package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Description: 阴影控件
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


    private Paint paint;
    private RectF rectF;
    private int width, height;
    private float radius;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        BitmapDrawable drawable = new BitmapDrawable(getResources(), getBgDrawable());
        setBackground(drawable);
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        rectF = new RectF();
        radius = dpToPx(shadowDistance);
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

    // 此种方式也可以实现阴影效果 但是会有兼容性问题
//    @Override
//    protected void dispatchDraw(Canvas canvas) {
//        setLayerType(LAYER_TYPE_SOFTWARE, null);
//        float radius = dpToPx(shadowDistance);
//        paint.setMaskFilter(new BlurMaskFilter(radius, BlurMaskFilter.Blur.SOLID));//设置发光样式,NORMAL是内外发光
//        paint.setShadowLayer(radius, 0, 0, Color.parseColor("#0f000000"));//绘制阴影
//        rectF.set(radius, radius, width - radius, height - radius);
//        canvas.drawRoundRect(rectF, radius, radius, paint);
//        canvas.save();
//        super.dispatchDraw(canvas);
//    }


    private Bitmap getBgDrawable() {
        paint.setShadowLayer(radius, 0, 0, Color.parseColor("#11000000"));//设置阴影
        rectF.set(radius, radius, width - radius, height - radius);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        return bitmap;
    }
}
