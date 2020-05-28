package com.example.rico.customerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.example.rico.customerview.R;

/**
 * Created by Tmp on 2018/12/18.
 * 从左到右动态绘制一张图片
 */
public class DrawBitmapView extends View {
    Paint paint;
    RectF dst;
    Rect rect;
    Bitmap bitmap;

    public DrawBitmapView(Context context) {
        super(context);
        initView();
    }


    public DrawBitmapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }


    public void setBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        float widthScale = (float) radius * 2 / bitmapWidth;
        float heightScale = (float) radius * 2 / bitmapHeight;
        float scale = widthScale > heightScale ? heightScale : widthScale;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        this.bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
        startAnimator();
    }

    public void clearBitmap() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        invalidate();
    }

    ValueAnimator animator;

    private void startAnimator() {
        rect.set(0, 0, 0, 0);
        if (animator == null) {
            animator = ValueAnimator.ofFloat(bitmap.getWidth());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    rightX = (int) value;
                    rect.set(0, 0, rightX, bitmap.getHeight());
                    dst.set(-bitmap.getWidth() / 2, -bitmap.getHeight() / 2, -bitmap.getWidth() / 2 + rightX, bitmap.getHeight() / 2);
                    invalidate();
                }
            });
            animator.setDuration(300);
            animator.setInterpolator(new DecelerateInterpolator());
        }
        animator.setObjectValues(bitmap.getWidth());
        animator.start();
    }

    private void initView() {
        rect = new Rect();
        dst = new RectF();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(R.color.bottom_bg));
        paint.setStyle(Paint.Style.FILL);
    }

    int width, height, radius;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        radius = Math.min(w, h) / 2;
    }

    int rightX;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(width / 2, height / 2);
        canvas.drawCircle(0, 0, radius, paint);
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, rect, dst, paint);
        }
    }

}
