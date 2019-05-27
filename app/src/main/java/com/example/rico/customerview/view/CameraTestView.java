package com.example.rico.customerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import com.example.rico.customerview.R;

/**
 * Created by Tmp on 2019/5/6.
 */
public class CameraTestView extends BaseCustomerView {
    public CameraTestView(Context context) {
        super(context);
    }

    public CameraTestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    Paint paint;

    @Override
    protected void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(R.color.colorAccent));
        camera = new Camera();
        matrix = new Matrix();
        // 获取手机像素密度 （即dp与px的比例）
        matrixValues = new float[9];
    }

    Camera camera;
    Matrix matrix;
    ValueAnimator animator;
    //    camera与matrix x轴的坐标是相同方向  y坐标则是相反
    //    camera的相机机位默认为左上角
    float rotateAngle, scale = 1;
    float[] matrixValues;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        scale = getResources().getDisplayMetrics().density;
        animator = ValueAnimator.ofFloat(0, 180);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                rotateAngle = value;
                invalidate();
            }
        });
        animator.setDuration(3000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        camera.save();
        camera.rotateY(rotateAngle);
        camera.getMatrix(matrix);
        matrix.getValues(matrixValues);
        matrixValues[6] = matrixValues[6] / scale;
        matrixValues[7] = matrixValues[7] / scale;
        matrix.setValues(matrixValues);
        camera.restore();
        matrix.postTranslate(width / 2, height / 2);
        matrix.preTranslate(-width / 2, -height / 2);
        canvas.concat(matrix);
//        canvas.drawRect(width / 4, height / 4, width / 4 * 3, height / 4 * 3, paint);
        canvas.drawRect(200, 200, width - 200, height - 200, paint);

    }
}
