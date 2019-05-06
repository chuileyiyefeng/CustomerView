package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

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
    }

    //    camera与matrix x轴的坐标是相同方向  y坐标则是相反
    //    camera的相机机位默认为左上角
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Camera camera = new Camera();
        Matrix matrix = new Matrix();
        camera.save();
        camera.rotateY(45);
        camera.getMatrix(matrix);
        camera.restore();
        matrix.preTranslate(-width / 2, -height / 2);
        matrix.postTranslate(width / 2, height / 2);
        canvas.concat(matrix);
        canvas.drawRect(width / 4, height / 4, width / 4 * 3, height / 4 * 3, paint);
    }
}
