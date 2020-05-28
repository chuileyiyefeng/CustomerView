package com.example.rico.customerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.rico.customerview.evaluator.PointEvaluator;
import com.example.rico.customerview.R;

/**
 * Created by Tmp on 2019/3/29.
 * 使用Evaluator动画
 */
public class EvaluatorMoveView extends View {
    Paint paint;
    int radius, width, height;
    Point startPoint, endPoint, valuePoint;

    public EvaluatorMoveView(Context context) {
        super(context);
        init();
    }

    public EvaluatorMoveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(R.color.button_bg));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        radius = Math.max(w, h) / 10;
        width = w;
        height = h;
        startPoint = new Point(radius, radius);
        endPoint = new Point(width - radius, height - radius);
        valuePoint = new Point();
        ValueAnimator animator = ValueAnimator.ofObject(new PointEvaluator(), startPoint, endPoint);
        animator.setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Point point = (Point) animation.getAnimatedValue();
                valuePoint.x = point.x;
                valuePoint.y = point.y;
                invalidate();
            }
        });
        animator.start();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(valuePoint.x, valuePoint.y, radius, paint);
    }
}
