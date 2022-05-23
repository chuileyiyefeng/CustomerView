package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;

import com.example.rico.customerview.R;

/**
 * @Description: 圆角控件 outline方式 只能设置统一圆角度数
 * @Author: pan yi
 * @Date: 2022/5/10
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
        path2 = new Path();
        paint=new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.darkorchid));
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }

    int width;
    int height;
    int radiusX = 45, radiusY = 45;
    private Path path;
    private Path path2;
    float[] radii;
    Paint paint;


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
//        path.reset();
//        path2.addRect(0,0,width,height, Path.Direction.CW);
//        rectFCrop.set(0, 0, width, height);
//        path.addRoundRect(rectFCrop, radiusX, radiusY, Path.Direction.CW);
//        path.op(path2, Path.Op.INTERSECT);
//        canvas.clipPath(path);
        super.dispatchDraw(canvas);
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(new Rect(0,0,width,height),radiusX);
            }
        });
        setClipToOutline(true);
    }
}
