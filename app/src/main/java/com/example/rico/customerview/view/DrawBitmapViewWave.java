package com.example.rico.customerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

import com.example.rico.customerview.R;

/**
 * @Description:
 * @Author: pan yi
 * @Date: 2022/5/30
 */
public class DrawBitmapViewWave extends View {
    Paint paint;
    RectF dst;
    Rect rect;
    Bitmap bitmap;
    private Path wavePath, rectPath;

    private final int UP = 1;
    private final int DOWN = 2;
    private int currentType = UP;
    private int offsetX;


    private float progressEnd;

    public DrawBitmapViewWave(Context context) {
        super(context);
        initView();
    }


    public DrawBitmapViewWave(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }


    public void setBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        float bitmapWidth = bitmap.getWidth();
        float bitmapHeight = bitmap.getHeight();
        float widthScale = width / bitmapWidth;
        float heightScale = height / bitmapHeight;
        float scale = Math.min(widthScale, heightScale);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        progressEnd = heightScale * scale + waveHeight;
        this.bitmap = Bitmap.createBitmap(bitmap, 0, 0, (int) bitmapWidth, (int) bitmapHeight, matrix, true);
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
        if (animator == null) {
            animator = ValueAnimator.ofFloat(width);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    offsetX += 15;
                    if (offsetX >= width) {
                        offsetX = 0;
                    }
                    if (currentType == UP) {
                        baseLine--;
                    } else {
                        baseLine++;
                    }
                    setPath();
                    //     画波浪线的path与矩形path相交的值为波浪线的path
                    //    原波浪线的path包含的内容太多，影响性能
                    wavePath.op(rectPath, Path.Op.INTERSECT);
                    invalidate();
                }
            });
            animator.setDuration(500);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setRepeatCount(ValueAnimator.INFINITE);
        }
        animator.setObjectValues(bitmap.getWidth());
        animator.start();
    }

    private void setPath() {
        if (currentType == UP) {
            if (baseLine < 0) {
                animator.cancel();
                return;
            }
        } else {
            if (baseLine > progressEnd) {
                animator.cancel();
                return;
            }
        }

        wavePath.reset();
        wavePath.moveTo(-width, baseLine);
        for (int i = -3; i < 2; i++) {
            int start = i * width / 2 + offsetX;
            wavePath.quadTo(start + width / 4f, getY(i), start + width / 2f, baseLine);
        }
        wavePath.lineTo(width, height);
        wavePath.lineTo(0, height);
    }

    private void initView() {
        rect = new Rect();
        dst = new RectF();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(R.color.bottom_bg));
        paint.setStyle(Paint.Style.FILL);
    }

    int width, height, mCircleRadius;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        mCircleRadius = Math.min(w, h) / 2;
        rect.set(0, 0, width, height);
        dst.set(0, 0, width, height);
        wavePath = new Path();
        rectPath = new Path();
        rectPath.addRect(0, 0, width, height, Path.Direction.CW);

        waveHeight = mCircleRadius / 2f;
        baseLine = height;
        underLine = baseLine;
    }

    float baseLine;
    float underLine;
    private float waveHeight;

    private float getY(int position) {
        if (position % 2 == 0) {
            return baseLine - waveHeight;
        } else {
            return baseLine + waveHeight;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipPath(wavePath);
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, rect, dst, paint);
        }

    }
}