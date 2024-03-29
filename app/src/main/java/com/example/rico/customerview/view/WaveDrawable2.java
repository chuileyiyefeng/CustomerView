package com.example.rico.customerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Choreographer;
import android.view.animation.DecelerateInterpolator;

/**
 * @author pan yi
 * @date 2022/8/8
 * @desc:
 */
public class WaveDrawable2 extends Drawable implements Animatable {

    private static final float WAVE_AMPLITUDE_FACTOR = 0.16f;
    private static final float WAVE_SPEED_FACTOR = 0.01f;
    private Drawable mDrawable;
    private int mWidth, mHeight;
    private int mWaveAmplitude, mWaveLength, mWaveOffset, mBgWaveOffset, mWaveStep;
    private ValueAnimator mAnimator;
    private float mProgress = 0.3f;
    private float mWaveLevel;
    private Paint mPaint;
    private Paint mBgPaint;
    private Bitmap mMask, mBgMask;
    private Matrix mMatrix = new Matrix();
    private Matrix mBgMatrix = new Matrix();
    private boolean mRunning = false;
    private boolean mIndeterminate = false;

    private int mMax = 100;

    private static final PorterDuffXfermode sXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private static ColorFilter sGrayFilter = new ColorMatrixColorFilter(new float[]{
            0.264F, 0.472F, 0.088F, 0, 0,
            0.264F, 0.472F, 0.088F, 0, 0,
            0.264F, 0.472F, 0.088F, 0, 0,
            0, 0, 0, 1, 0
    });
    private ColorFilter mCurFilter = null;

    private Choreographer.FrameCallback mFrameCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long l) {
            invalidateSelf();
            if (mRunning) {
                Choreographer.getInstance().postFrameCallback(this);
            }
        }
    };

    public WaveDrawable2(Drawable drawable) {
        init(drawable);
    }

    public WaveDrawable2(Context context, int imgRes) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = context.getDrawable(imgRes);
        } else {
            drawable = context.getResources().getDrawable(imgRes);
        }

        init(drawable);
    }

    private void init(Drawable drawable) {
        mDrawable = drawable;
        mWidth = mDrawable.getIntrinsicWidth();
        mHeight = mDrawable.getIntrinsicHeight();
        mWaveAmplitude = Math.max(8, (int) (mHeight * WAVE_AMPLITUDE_FACTOR));
        mWaveLength = mWidth;
        mWaveStep = Math.max(1, (int) (mWidth * WAVE_SPEED_FACTOR));
        mWaveStep = 1;

        mMatrix.reset();
        mBgMatrix.reset();
        mMask = createMask(mWidth, mWaveLength, mWaveAmplitude);
        mBgMask = createMaskBg(mWidth, mWaveLength, mWaveAmplitude);
        mPaint = new Paint();
        mPaint.setFilterBitmap(false);
        mPaint.setColor(Color.BLACK);
        mPaint.setXfermode(sXfermode);

        mBgPaint = new Paint();
        mBgPaint.setFilterBitmap(false);
        mBgPaint.setColor(Color.BLACK);
        mBgPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setDuration(5000);
        mAnimator.addUpdateListener(valueAnimator -> {
            setPercentProgress((float) valueAnimator.getAnimatedValue());
            if (!mRunning) {
                invalidateSelf();
            }
        });

        setPercentProgress(0);
        start();
    }

    /**
     * Set wave move distance (in pixels) in very animation frame
     *
     * @param step distance in pixels
     */
    public void setWaveSpeed(int step) {
        mWaveStep = Math.min(step, mWidth / 2);
    }

    /**
     * Set wave amplitude (in pixels)
     *
     * @param amplitude
     */
    public void setWaveAmplitude(int amplitude) {
        amplitude = Math.max(0, Math.min(amplitude, mHeight / 2));
        if (mWaveAmplitude != amplitude) {
            mWaveAmplitude = amplitude;
            mMask = createMask(mWidth, mWaveLength, mWaveAmplitude);
            mBgMask = createMaskBg(mWidth, mWaveLength, mWaveAmplitude);
            invalidateSelf();
        }
    }

    /**
     * Set wave length (in pixels)
     *
     * @param length
     */
    public void setWaveLength(int length) {
        length = Math.max(8, Math.min(mWidth * 2, length));
        if (length != mWaveLength) {
            mWaveLength = length;
            mMask = createMask(mWidth, mWaveLength, mWaveAmplitude);
            mBgMask = createMaskBg(mWidth, mWaveLength, mWaveAmplitude);
            invalidateSelf();
        }
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        mDrawable.setBounds(left, top, right, bottom);
    }

    @Override
    public int getIntrinsicHeight() {
        return mHeight;
    }

    @Override
    public int getIntrinsicWidth() {
        return mWidth;
    }

    @Override
    public void draw(Canvas canvas) {
//        mDrawable.setColorFilter(sGrayFilter);
//        mDrawable.draw(canvas);
        canvas.save();
        mDrawable.setColorFilter(mCurFilter);

        if (mProgress <= 0.001f) {
            return;
        }

        int sc = canvas.saveLayer(0, 0, mWidth, mHeight, null, Canvas.ALL_SAVE_FLAG);

        mDrawable.draw(canvas);

        if (mProgress >= 0.999f) {
            return;
        }

        mWaveOffset += mWaveStep;
        mBgWaveOffset += mWaveStep * 1.2f;
        if (mWaveOffset > mWaveLength) {
            mWaveOffset -= mWaveLength;
        }

        if (mBgWaveOffset > mWaveLength) {
            mBgWaveOffset -= mWaveLength;
        }
        if (mWaveLevel > 0) {
            mPaint.setColor(Color.TRANSPARENT);
            mBgPaint.setColor(Color.TRANSPARENT);
            canvas.drawRect(0, 0, mWidth, mWaveLevel, mPaint);
            canvas.drawRect(0, 0, mWidth, mWaveLevel, mBgPaint);
            mPaint.setColor(Color.WHITE);
            mBgPaint.setColor(Color.WHITE);
        }

        mBgMatrix.setTranslate(-mBgWaveOffset, mWaveLevel);
        canvas.drawBitmap(mBgMask, mBgMatrix, mBgPaint);

        mMatrix.setTranslate(-mWaveOffset, mWaveLevel);
        canvas.drawBitmap(mMask, mMatrix, mPaint);

        canvas.restoreToCount(sc);

        canvas.restore();


//        mDrawable.setColorFilter(mCurFilter);
//        int sc2 = canvas.saveLayer(0, 0, mWidth, mHeight, null, Canvas.ALL_SAVE_FLAG);
//
//        mDrawable.draw(canvas);
//        if (mWaveLevel > 0) {
//            mPaint.setColor(Color.TRANSPARENT);
//            mBgPaint.setColor(Color.TRANSPARENT);
//            canvas.drawRect(0, 0, mWidth, mWaveLevel, mPaint);
//            canvas.drawRect(0, 0, mWidth, mWaveLevel, mBgPaint);
//            mPaint.setColor(Color.WHITE);
//            mBgPaint.setColor(Color.WHITE);
//        }
//        mMatrix.setTranslate(-mWaveOffset, mWaveLevel);
//        canvas.drawBitmap(mMask, mMatrix, mPaint);
//        canvas.restoreToCount(sc2);

    }

    public boolean isBg = false;

    public void setBg(boolean bg) {
        isBg = bg;
    }


    @Override
    protected boolean onLevelChange(int level) {
//        setPercentProgress(level / 10000f);
        return true;
    }

    @Override
    public void setAlpha(int i) {
        mDrawable.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mCurFilter = colorFilter;
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void start() {
        mRunning = true;
        Choreographer.getInstance().postFrameCallback(mFrameCallback);
    }

    @Override
    public void stop() {
        mRunning = false;
        Choreographer.getInstance().removeFrameCallback(mFrameCallback);
    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }

    public boolean isIndeterminate() {
        return mIndeterminate;
    }

    public void setIndeterminate(boolean indeterminate) {
        mIndeterminate = indeterminate;
        if (mIndeterminate) {
            mAnimator.start();
        } else {
            mAnimator.cancel();
        }
    }

    private void setPercentProgress(float progress) {
        mProgress = progress;
        if (mProgress < 0.15f) {
            mProgress = 0.2f;
        }
        mWaveLevel = mHeight - (int) ((mHeight + mWaveAmplitude * 2) * mProgress);
        mWaveLevel = mHeight - mHeight * mProgress;//测试
        invalidateSelf();
    }

    public void setProgress(float progress) {
        float percent = progress / mMax;
        setPercentProgress(percent);
    }

    public void setMax(int mMax) {
        this.mMax = mMax;
    }

    private static Bitmap createMask(int width, int length, int amplitude) {

        final int count = (int) Math.ceil((width + length) / (float) length);

        Bitmap bm = Bitmap.createBitmap(length * count, amplitude * 2, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        Path path = new Path();
        path.moveTo(0, amplitude);

        final float stepX = length / 4f;
        float x = 0;
        float y = 0;
        for (int i = 0; i < count * 2; i++) {
            x += stepX;
            path.quadTo(x, y, x + stepX, amplitude);
            x += stepX;
            y = bm.getHeight() - y;
        }
        path.lineTo(bm.getWidth(), bm.getHeight());
        path.lineTo(0, bm.getHeight());
        path.close();
        c.drawPath(path, p);
        return bm;
    }

    private static Bitmap createMaskBg(int width, int length, int amplitude) {

        final int count = (int) Math.ceil((width + length) / (float) length);

        Bitmap bm = Bitmap.createBitmap(length * count, amplitude * 2, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setAlpha(100);
        Path path = new Path();
        path.moveTo(0, amplitude);

        final float stepX = length / 4f;
        float x = 0;
        float y = 0;
        for (int i = 0; i < count * 2; i++) {
            x += stepX;
            path.quadTo(x, y, x + stepX, amplitude);
            x += stepX;
            y = bm.getHeight() - y;
        }
        path.lineTo(bm.getWidth(), bm.getHeight());
        path.lineTo(0, bm.getHeight());
        path.close();
        c.drawPath(path, p);

        return bm;
    }
}
