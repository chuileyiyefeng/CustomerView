package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @Description: 水波
 * @Author: pan yi
 * @Date: 2022/9/22
 */
public class SpreadView extends View {

    private RectF rectF1, rectF2, rectF3;
    private int width, height;
    private float rectF1Width, rectF1Height, rectF2Width, rectF2Height, rectF3Width, rectF3Height;
    private Paint paint1, paint2, paint3;
    private RadialGradient gradient1, gradient2, gradient3;

    public SpreadView(Context context) {
        super(context);
        init();
    }

    public SpreadView(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpreadView(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        rectF1 = new RectF();
        rectF2 = new RectF();
        rectF3 = new RectF();
        paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint3 = new Paint(Paint.ANTI_ALIAS_FLAG);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        rectF1Width = width;
        rectF1Height = w;

        rectF2Width = rectF1Width * 0.7f;
        rectF2Height = rectF1Height * 0.7f;

        rectF3Width = rectF1Width * 0.45f;
        rectF3Height = rectF1Height * 0.45f;
        setRectFSize();

    }
    int[] arcColors1 = new int[]{
            Color.parseColor("#90FFB6C1"),
            Color.parseColor("#95FFB6C1"),
            Color.parseColor("#FFB6C1"),
    };
    int[] arcColors2 = new int[]{
            Color.parseColor("#90FFC0CB"),
            Color.parseColor("#95FFC0CB"),
            Color.parseColor("#FFC0CB"),
    };
    int[] arcColors3 = new int[]{
            Color.parseColor("#90F08080"),
            Color.parseColor("#99F08080"),
            Color.parseColor("#F08080"),
    };

    private void setRectFSize() {
        rectF1.set((width - rectF1Width) / 2f, 0, rectF1Width + (width - rectF1Width) / 2f, rectF1Height);
        rectF2.set((width - rectF2Width) / 2f, 0, rectF2Width + (width - rectF2Width) / 2f, rectF2Height);
        rectF3.set((width - rectF3Width) / 2f, 0, rectF3Width + (width - rectF3Width) / 2f, rectF3Height);

        gradient1 = new RadialGradient(rectF1.left + (rectF1.right - rectF1.left) / 2, rectF1.top + (rectF1.bottom - rectF1.top) / 2, (rectF1.right - rectF1.left) / 2, arcColors1, null, Shader.TileMode.CLAMP);
        paint1.setShader(gradient1);
        gradient2 = new RadialGradient(rectF2.left + (rectF2.right - rectF2.left) / 2, rectF2.top + (rectF2.bottom - rectF2.top) / 2, (rectF2.right - rectF2.left) / 2, arcColors2, null, Shader.TileMode.CLAMP);
        paint2.setShader(gradient2);
        gradient3 = new RadialGradient(rectF3.left + (rectF3.right - rectF3.left) / 2, rectF3.top + (rectF3.bottom - rectF3.top) / 2, (rectF3.right - rectF3.left) / 2, arcColors3, null, Shader.TileMode.CLAMP);
        paint3.setShader(gradient3);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.scale(1f,(float) height/width);
        canvas.drawOval(rectF1, paint1);
        canvas.drawOval(rectF2, paint2);
        canvas.drawOval(rectF3, paint3);
        canvas.restore();
    }
}
