package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Tmp on 2019/2/11.
 * 写字版
 */
public class HandWritingView extends View {
    Paint paint;
    Path path;
    PathEffect effect;

    public HandWritingView(Context context) {
        super(context);
        init();
    }

    public HandWritingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HandWritingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        path = new Path();
        effect = new CornerPathEffect(200);
        paint.setPathEffect(effect);
    }

    public void reset() {
        path.reset();
        invalidate();
    }
    float downX,downY;
    long time;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX=event.getX();
                downY=event.getY();
                path.moveTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(event.getX() - downX) < 10 && Math.abs(event.getY() - downY) < 30) {
                    if (time==0) {
                        time=System.currentTimeMillis();
                    }else if (System.currentTimeMillis()-time<300){
                        reset();
                    }
                    time=System.currentTimeMillis();
                }
                break;
        }
        postInvalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }
}
