package com.example.rico.customerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

/**
 * @Description: 贝塞尔path加图片
 * @Author: pan yi
 * @Date: 2022/5/30
 */
public class DrawBitmapWaveView extends View {
    Paint paint;
    Bitmap drawBitmap, bgBitmap;
    private Path wavePath, rectPath;

    private final int UP = 1;
    private final int DOWN = 2;
    private int currentType = UP;
    private int offsetX;
    private float everyMoveX = 15f;
    private float speed = 1f; //波浪线的速度


    private float progressEnd;
    private float progressDistance;

    public DrawBitmapWaveView(Context context) {
        super(context);
        initView();
    }


    public DrawBitmapWaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    int width, height;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        wavePath = new Path();
        rectPath = new Path();
        rectPath.addRect(0, 0, width, height, Path.Direction.CW);

        waveHeight = height / 2f;
        baseLine = height;
        underLine = baseLine;
        progressDistance = underLine - progressEnd;
    }

    public void setDrawBitmap(Bitmap bgBitmap, Bitmap drawBitmap) {
        if (bgBitmap == null || drawBitmap == null) {
            return;
        }
        post(() -> setBitmap(bgBitmap, drawBitmap));

    }

    //设置升降速度
    public void setSpeed(float speed) {
        if(speed<0.5f){
            speed=0.5f;
        }
        this.speed = speed;

    }


    private void setBitmap(Bitmap bgBitmap, Bitmap drawBitmap) {
        float bitmapWidth = drawBitmap.getWidth();
        float bitmapHeight = drawBitmap.getHeight();
        float widthScale = width / bitmapWidth;
        float heightScale = height / bitmapHeight;
        float scale = Math.min(widthScale, heightScale);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        waveHeight = (bitmapHeight * scale) / 8f;
        everyMoveX = width / 50f;
        progressEnd = heightScale * scale + waveHeight;
        this.drawBitmap = Bitmap.createBitmap(drawBitmap, 0, 0, (int) bitmapWidth, (int) bitmapHeight, matrix, true);
        this.bgBitmap = Bitmap.createBitmap(bgBitmap, 0, 0, (int) bitmapWidth, (int) bitmapHeight, matrix, true);
        invalidate();
    }

    public void clearBitmap() {
        if (drawBitmap != null) {
            drawBitmap.recycle();
            drawBitmap = null;
        }
        if (bgBitmap != null) {
            bgBitmap.recycle();
            bgBitmap = null;
        }
        invalidate();
    }

    private int lastProgress = 0;
    private float lastProgressEnd = 0;

    public void setProgress(int progress) {
        if (progress == lastProgress) {
            return;
        }
        if (animator != null) {
            animator.cancel();
        }
        if (progress > 100) {
            progress = 100;
        }
        if (progress < 0) {
            progress = 0;
        }
        //修正底部和顶部波浪效果不明显
        if (progress <= 10 && progress > 0) {
            progress = 15;
        }
        if (progress >= 90 && progress < 100) {
            progress = 85;
        }

        if (lastProgress == 0) {
            progressEnd = lastProgressEnd + (progress - lastProgress) * progressDistance / 100f;
        } else {
            if (progress > lastProgress) {
                currentType = UP;
                progressEnd = lastProgressEnd - (progress - lastProgress) * progressDistance / 100f;
            } else {
                currentType = DOWN;
                progressEnd = lastProgressEnd + (lastProgress - progress) * progressDistance / 100f;
            }
        }

        lastProgress = progress;
        lastProgressEnd = progressEnd;
        startAnimator();
    }

    ValueAnimator animator;

    private void startAnimator() {
        if (animator == null) {
            animator = ValueAnimator.ofFloat(width);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    offsetX += everyMoveX;
                    if (offsetX >= width) {
                        offsetX = 0;
                    }
                    if (currentType == UP) {
                        baseLine -= speed;
                    } else {
                        baseLine += speed;
                    }
                    setPath();
                    wavePath.op(rectPath, Path.Op.INTERSECT);
                    invalidate();
                }
            });
            animator.setDuration(500);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setRepeatCount(ValueAnimator.INFINITE);
        }
        animator.setObjectValues(drawBitmap.getWidth());
        animator.start();
    }

    private void setPath() {
        if (currentType == UP) {
            if (baseLine < progressEnd) {
                baseLine = progressEnd;
                animator.cancel();
            }
        } else {
            if (baseLine > progressEnd) {
                baseLine = progressEnd;
                animator.cancel();
            }
        }

        wavePath.reset();
        wavePath.moveTo(-width, baseLine);
        for (int i = -3; i < 2; i++) {
            int start = i * width / 2 + offsetX;
            wavePath.quadTo(start + width / 4f, getY(i), start + width / 2f, baseLine);
        }
        wavePath.lineTo(width, underLine);
        wavePath.lineTo(0, underLine);
    }

    private void initView() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
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
        if (bgBitmap != null) {
            canvas.drawBitmap(bgBitmap, 0, 0, paint);
        }
        canvas.clipPath(wavePath);
        if (drawBitmap != null) {
            canvas.drawBitmap(drawBitmap, 0, 0, paint);
        }

    }
}
