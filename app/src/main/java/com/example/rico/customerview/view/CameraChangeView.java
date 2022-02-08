package com.example.rico.customerview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

import com.example.rico.customerview.R;

/**
 * create by pan yi on 2021/3/26
 * desc : 使用camera波浪效果
 */
public class CameraChangeView extends BaseCustomerView {
    private Camera camera;
    private Bitmap bitmap;
    private Paint paint;
    private float transLateX, transLateY;
    public int top = 100, left = 100;
    private float cameraDegree = 0, canvasDegree = 0;
    // 图片的宽高
    private int bitmapW, bitmapH;

    public CameraChangeView(Context context) {
        super(context);
    }

    public CameraChangeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraChangeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context) {
        camera = new Camera();
        paint = new Paint();
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.dog);
        bitmapW = bitmap.getWidth();
        bitmapH = bitmap.getHeight();
        camera.setLocation(0, 0, -8 * getResources().getDisplayMetrics().density);
        transLateX = bitmapW / 2f;
        transLateY = bitmapH / 2f;
    }

    private ValueAnimator cameraAnimator, canvasAnimator;

    // 动画开始
    public void startAnim() {
        if (cameraAnimator == null) {
            cameraAnimator = ValueAnimator.ofFloat(0, 60, 0, 45);
            cameraAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    cameraDegree = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            cameraAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    startCanvasAnimator();
                }
            });
            cameraAnimator.setInterpolator(new DecelerateInterpolator());
            cameraAnimator.setDuration(3000);
        }
        cameraAnimator.start();
    }

    private void startCanvasAnimator() {
        if (canvasAnimator == null) {
            canvasAnimator = ValueAnimator.ofFloat(0, 360, 0);
            canvasAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    canvasDegree = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            canvasAnimator.setInterpolator(new DecelerateInterpolator());
            canvasAnimator.setDuration(3000);
            canvasAnimator.setRepeatCount(10);
        }
        canvasAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (cameraAnimator != null) {
            cameraAnimator.cancel();
        }
        if (canvasAnimator != null) {
            canvasAnimator.cancel();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        camera.save();

        canvas.save();
        canvas.translate(transLateX + left, transLateY + top);
        canvas.rotate(-canvasDegree);
        canvas.clipRect(-bitmapW, -bitmapH, bitmapW, 0);
        canvas.rotate(canvasDegree);
        canvas.translate(-(transLateX + left), -(transLateY + top));
        canvas.drawBitmap(bitmap, left, top, paint);
        canvas.restore();

        canvas.save();
        camera.rotateX(cameraDegree);
        canvas.translate(transLateX + left, transLateY + top);
        canvas.rotate(-canvasDegree);
        camera.applyToCanvas(canvas);
        canvas.clipRect(-bitmapW, 0, bitmapW, bitmapH);
        canvas.rotate(canvasDegree);
        canvas.translate(-(transLateX + left), -(transLateY + top));
        canvas.drawBitmap(bitmap, left, top, paint);
        canvas.restore();
        camera.restore();
    }
}
