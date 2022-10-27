package com.example.rico.customerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
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
    Paint paint, paint2;
    Bitmap drawBitmap, bgBitmap;
    private Path wavePath, wavePath2, rectPath;

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
        wavePath2 = new Path();
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
        if (speed < 0.5f) {
            speed = 0.5f;
        }
        this.speed = speed;

    }


    private void setBitmap(Bitmap bgBitmap, Bitmap drawBitmap) {
        float bgBitmapWidth = bgBitmap.getWidth();
        float bgBitmapHeight = bgBitmap.getHeight();
        float bgWidthScale = width / bgBitmapWidth;
        float bgHeightScale = height / bgBitmapHeight;
        float bgScale = Math.min(bgWidthScale, bgHeightScale);
        Matrix bgMatrix = new Matrix();
        bgMatrix.postScale(bgScale, bgScale);
        this.bgBitmap = Bitmap.createBitmap(bgBitmap, 0, 0, (int) bgBitmapWidth, (int) bgBitmapHeight, bgMatrix, true);

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

    public void setProgress(int progressValue) {
        post(new Runnable() {
            @Override
            public void run() {
                if (drawBitmap == null || bgBitmap == null) {
                    return;
                }
                int progress = progressValue;

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
        });
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
            float start = i * width / 2f + offsetX;
            wavePath.quadTo(start + width / 4f, getY(i), start + width / 2f, baseLine);
        }
        wavePath.lineTo(width, underLine);
        wavePath.lineTo(0, underLine);

        wavePath2.reset();
        wavePath2.moveTo(-width, baseLine);
        for (int i = -3; i < 2; i++) {
            float start = i * width / 2f + offsetX + width / 4f;
            wavePath2.quadTo(start + width / 4f, getY(i), start + width / 2f, baseLine);
        }
        wavePath2.lineTo(width, underLine);
        wavePath2.lineTo(0, underLine);

    }

    private void initView() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setAlpha(200);
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
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearBitmap();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bgBitmap == null || drawBitmap == null) {
            return;
        }
        canvas.drawBitmap(bgBitmap, 0, 0, paint);
        canvas.save();
        canvas.clipPath(wavePath);
        canvas.drawBitmap(drawBitmap, 0, 0, paint);
        canvas.restore();


        canvas.clipPath(wavePath2);
        canvas.drawBitmap(drawBitmap, 0, 0, paint2);

    }
}
