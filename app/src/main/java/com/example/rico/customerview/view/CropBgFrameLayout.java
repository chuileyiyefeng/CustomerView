package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Description: 只是为了画背景色
 * @Author: pan yi
 * @Date: 2021/12/24
 */
public class CropBgFrameLayout extends FrameLayout {
    private Paint paint;
    private Path path, clipPath;
    private int width, height;

    public CropBgFrameLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public CropBgFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CropBgFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        path.moveTo(0, 0);
        path.lineTo(0, w);
        path.lineTo(w, h);
        path.lineTo(0, h);
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.parseColor("#66000000"));
        path = new Path();
        clipPath = new Path();
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (getChildCount() > 0) {
            clipPath.reset();
            path.reset();
            path.addRect(0,0,width,height, Path.Direction.CCW);
            View view = getChildAt(0);
            int childWidth = view.getMeasuredWidth();
            int childHeight = view.getMeasuredHeight();
            int left = (width - childWidth) / 2;
            int top = (height - childHeight) / 2;
            int right = (width - childWidth) / 2 + childWidth;
            int bottom = (height - childHeight) / 2 + childHeight;
            clipPath.addRect(left,top,right,bottom, Path.Direction.CCW);
            path.op(clipPath, Path.Op.DIFFERENCE);
            canvas.drawPath(path, paint);
        }
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
