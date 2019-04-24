package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.example.rico.customerview.R;

/**
 * Created by Tmp on 2019/4/24.
 */
public class MatrixSetPolyView extends BaseCustomerView {
    public MatrixSetPolyView(Context context) {
        super(context);
    }

    public MatrixSetPolyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    Bitmap bitmap;
    Matrix matrix;
    Paint paint;

    @Override
    protected void init(Context context) {
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.dog);
        matrix = new Matrix();
        paint = new Paint();
//        float src[] = {0, 0, bitmap.getWidth(), 0
//                , bitmap.getWidth(), bitmap.getHeight(), 0, bitmap.getHeight()};
//        float des[] = {0, 0, bitmap.getWidth(), 400
//                , bitmap.getWidth(), bitmap.getHeight() - 400, 0, bitmap.getHeight()};
//        matrix.setPolyToPoly(src, 0, des, 0, 4);
//        matrix.postTranslate(0, 200);
//        matrix.postScale(0.5f, 0.5f);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF src = new RectF(0, 0, bitmap.getWidth() , bitmap.getHeight());
        RectF dst = new RectF(0, 0, width, height);
        matrix.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);
        canvas.drawBitmap(bitmap, matrix, paint);
    }
}
