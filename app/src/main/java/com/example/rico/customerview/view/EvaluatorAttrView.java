package com.example.rico.customerview.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.rico.customerview.evaluator.ColorEvaluator;

/**
 * Created by Tmp on 2019/3/30.
 * 自定义view的属性
 * 结合自定义的Evaluator实现动画
 */
public class EvaluatorAttrView extends View {
    Paint paint;
    String color;
    int width, height, radius;

    public EvaluatorAttrView(Context context) {
        super(context);
        init();
    }

    public EvaluatorAttrView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#ffffff"));
    }

    public String getColor() {
        return color == null ? "#ffffff" : color;
    }

    public void setColor(String color) {
        this.color = color;
        paint.setColor(Color.parseColor(color));
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        radius = Math.min(w, h) / 4;
        ObjectAnimator objectAnimator= ObjectAnimator.ofObject(this,"color",new ColorEvaluator(),"#0000ff","#ff0000");
        objectAnimator.setDuration(2000);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(width / 2, height / 2, radius, paint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }
}
