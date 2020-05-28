package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by Tmp on 2019/7/31.
 */
public class CornerLinearLayout extends LinearLayout {
    public CornerLinearLayout(Context context) {
        super(context);
        init();
    }

    public CornerLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CornerLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        path = new Path();
    }

    int width;
    int height;
    int radiusX = 45, radiusY = 45;
    private Path path;
    float[] radii;


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        getRadius();
        rectFCrop = new RectF();
    }

    @Override
    public float getScaleX() {
        return super.getScaleX();
    }

    RectF rectFCrop;

    //    设置圆角大小
    private void getRadius() {
        radii = new float[]{radiusX, radiusY,
                radiusX, radiusY,
                radiusX, radiusY,
                radiusX, radiusY};
    }

    //    viewGroup有背景的时候调用onDraw，然后调用dispatchDraw,没有背景直接调用dispatchDraw
    @Override
    protected void dispatchDraw(Canvas canvas) {
        path.reset();
        rectFCrop.set(0, 0, width, height);
        path.addRoundRect(rectFCrop, radiusX, radiusY, Path.Direction.CCW);
        canvas.clipPath(path);
        super.dispatchDraw(canvas);
    }
}
