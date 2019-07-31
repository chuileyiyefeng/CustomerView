package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
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
    int radiusX = 20, radiusY = 20;
    private Path path;
    float[] radii;



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setBackgroundColor(Color.TRANSPARENT);
        width = w;
        height = h;
        getRadius();
        RectF rectFCrop = new RectF(getPaddingLeft(), getPaddingTop(), width - getPaddingRight(), height - getPaddingBottom());
        path.addRoundRect(rectFCrop, radii, Path.Direction.CW);
    }

    //    设置圆角大小
    private void getRadius() {
        radii = new float[]{radiusX, radiusY,
                radiusX, radiusY,
                radiusX, radiusY,
                radiusX, radiusY};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}
