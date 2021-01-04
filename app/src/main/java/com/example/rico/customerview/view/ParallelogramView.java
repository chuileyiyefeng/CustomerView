package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * create by pan yi on 2020/12/24
 * desc : 平行四边形View
 */
public class ParallelogramView extends View {
    int width, height;
    Paint paint;
    Path path;
    float scale = 2.5f;

    public ParallelogramView(Context context) {
        this(context, null);
    }

    public ParallelogramView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParallelogramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#FA8072"));
        path = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    public void setColor(int colorRes) {
        paint.setColor(colorRes);
        path.reset();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        path.moveTo(width / scale, 0);
        path.lineTo(width, 0);
        path.lineTo(width - width / scale, height);
        path.lineTo(0, height);
        path.close();
        canvas.drawPath(path, paint);
    }
}
