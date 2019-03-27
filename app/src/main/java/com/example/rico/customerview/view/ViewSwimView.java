package com.example.rico.customerview.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

/**
 * Created by Tmp on 2019/3/22.
 * 点击旋转并移动的view Group
 */
public class ViewSwimView extends RelativeLayout {

    public ViewSwimView(Context context) {
        super(context);
        init(context);
    }

    public ViewSwimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ViewSwimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private PathMeasure mPathMeasure;
    private Paint paint;

    private void init(Context context) {
        centerPoint = new PointF();
        setBackgroundColor(Color.parseColor("#ffffff"));
        movePath = new Path();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#ff0000"));
    }

    int width, height;
    PointF centerPoint;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        centerPoint.set(width / 2, height / 2);
        allLength = Math.pow(width * width + height * height, 0.5);
    }

    private float downX, downY, lastDownX, lastDownY,radius;

    //        鱼转方向圆心
    private float centerX, centerY;

    private int alpha=100;

    public void setRadius(float currentValue) {
        alpha = (int) (100 * (1 - currentValue) / 2);
        radius = 100 * currentValue;
        invalidate();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_UP:

                if (Math.abs(event.getX() - downX) < 10 && Math.abs(event.getY() - downY) < 10) {
//                  先把鱼头方向转至点击的点
                    if (animator != null && animator.isRunning()) {
                        return false;
                    }
                    ObjectAnimator rippleAnimator = ObjectAnimator.ofFloat(this, "radius", 0.9f, 1f).setDuration(1000);
                    rippleAnimator.start();
                    invalidate();
                    if (lastDownX == 0 && lastDownY == 0) {
                        Point point = getCenterPoint(getChildAt(0));
                        centerX = point.x;
                        centerY = point.y;
                    } else {
                        centerX = lastDownX;
                        centerY = lastDownY;
                    }
                    FishSwimView view = (FishSwimView) getChildAt(0);
                    view.setAngle(event.getX(), event.getY(), centerX, centerY);
                    startMove();

                    lastDownX = downX;
                    lastDownY = downY;
                }
                break;
        }
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        canvas.drawCircle(downX, downY, 20, paint);
        View childView = getChildAt(0);
        centerX = childView.getLeft() + (childView.getRight() - childView.getLeft()) / 2;
        centerY = childView.getTop() + (childView.getBottom() - childView.getTop()) / 2;
//        canvas.drawLine(downX, downY, centerX, centerY, paint);
        paint.setARGB(alpha, 255, 0, 0);
        canvas.drawCircle(downX, downY, radius, paint);
    }

    private ObjectAnimator animator;
    private Path movePath;
    private PathMeasure pathMeasure;
    double allLength;

    private void startMove() {
        if (animator != null) {
            animator.cancel();
        }
        movePath.reset();
        final float[] tan = new float[2];
        View childView = getChildAt(0);
        movePath.moveTo(centerX - childView.getWidth() / 2, centerY - childView.getHeight() / 2);
        movePath.lineTo(downX - childView.getWidth() / 2, downY - childView.getHeight() / 2);
        pathMeasure = new PathMeasure(movePath, false);
        animator = ObjectAnimator.ofFloat(getChildAt(0), "x", "y", movePath);
        float xSquare = Math.abs(centerX - downX) * Math.abs(centerX - downX);
        float ySquare = Math.abs(centerY - downY) * Math.abs(centerY - downY);
        double distance = Math.pow(xSquare + ySquare, 0.5);

        animator.setDuration((long) (3000 * distance / allLength));
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = animation.getAnimatedFraction();
                pathMeasure.getPosTan(pathMeasure.getLength() * value, null, tan);
            }
        });
        animator.start();
    }

    private Point getCenterPoint(View view) {
        int x = view.getLeft() + (view.getRight() - view.getLeft()) / 2;
        int y = view.getTop() + (view.getBottom() - view.getTop()) / 2;
        return new Point(x, y);
    }
}
