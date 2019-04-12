package com.example.rico.customerview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

import java.lang.reflect.Field;

/**
 * Created by Tmp on 2019/4/9.
 * 圆角imageView,关键是canvas.clipPath()方法
 */
public class CornerImageView extends AppCompatImageView {
    Context context;

    public CornerImageView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CornerImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    int width;
    int height;
    int radiusX = 50, radiusY = 50;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        getRadius();
        rectFCrop = new RectF(getPaddingLeft(), getPaddingTop(), width - getPaddingRight(), height - getPaddingBottom());
        path.addRoundRect(rectFCrop, radii, Path.Direction.CW);
    }

    //    设置圆角大小
    private void getRadius() {
        radii = new float[]{radiusX, radiusY,
                radiusX, radiusY,
                radiusX, radiusY,
                radiusX, radiusY};
    }

    private Path path;
    private RectF rectFCrop;
    float[] radii;

    protected void init() {
        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}
