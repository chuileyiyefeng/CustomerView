package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.rico.customerview.R;

import java.lang.ref.WeakReference;

/**
 * Created by Tmp on 2019/2/11.
 * 圆弧SeekBar
 */
public class ArcSeekBarView extends View {
    RectF rectF;
    int paintWidth = 100;
    int width, height, radius;
    Paint paint, borderPaint, circlePaint, boldPaint;
    Path stokePath, borderPath, boldPath;
    SweepGradient mGradient;
    MyHandler handler;
    int[] arcColors = new int[]{
            Color.parseColor("#99cccc"),
            Color.parseColor("#ccffff"),
            Color.parseColor("#ffcccc"),
            Color.parseColor("#6699cc"),
            Color.parseColor("#99ccff"),
            Color.parseColor("#6699cc"),
            Color.parseColor("#cc6699"),
            Color.parseColor("#ffff00"),
            Color.parseColor("#99cccc"),

    };

    public ArcSeekBarView(Context context) {
        super(context);
        init(context);
    }

    public ArcSeekBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ArcSeekBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        radius = Math.min(w, h) / 2;
        rectF = new RectF(w / 2 - radius + paintWidth * 2, h / 2 - radius + paintWidth * 2, w / 2 + radius - paintWidth * 2, h / 2 + radius - paintWidth * 2);
        mGradient = new SweepGradient(width / 2, height / 2, arcColors, null);
    }

    private void init(Context context) {
        handler = new MyHandler(this);
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boldPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        circlePaint.setStyle(Paint.Style.STROKE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(paintWidth);
//        添加圆角
        paint.setStrokeCap(Paint.Cap.ROUND);
        boldPaint.setStrokeCap(Paint.Cap.ROUND);
        boldPaint.setStrokeWidth(paintWidth - 4);
        boldPaint.setStyle(Paint.Style.STROKE);
        boldPaint.setColor(getResources().getColor(R.color.button_bg));

        stokePath = new Path();
        borderPath = new Path();
        boldPath = new Path();
    }

    float x, y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(x - event.getX()) <= 10 && Math.abs(y - event.getY()) <= 10) {
                    if (degrees >= 300) {
                        reset();
                    } else {
                        startProgress();
                    }
                }
                break;

        }
        return true;
    }

    private void startProgress() {
        handler.sendEmptyMessage(1);
    }

    private void reset() {
        degrees = 30;
        invalidate();
    }

    int degrees = 30;
    final int cRotate = 330;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        stokePath.reset();
//        borderPath.reset();
        initPath();
        canvas.drawPath(borderPath, borderPaint);
        canvas.drawPath(boldPath, boldPaint);
//        path里面画球
        if (degrees > cRotate) {
            degrees = cRotate;
        }
        canvas.rotate(degrees, width / 2, height / 2);
        canvas.drawCircle(width / 2, height - rectF.top, paintWidth / 2 - 4, circlePaint);
        degrees += 4;

    }

    int k = 0;

    private void initPath() {
        if (k != 0) {
            return;
        }
        stokePath.addArc(rectF, 120, 300);
        boldPath.addArc(rectF, 120, 300);
        paint.getFillPath(stokePath, borderPath);
        borderPaint.setStrokeWidth(1);
        borderPaint.setStyle(Paint.Style.STROKE);
        boldPaint.setShader(mGradient);
        k++;

    }

   private static class MyHandler extends Handler {
        WeakReference<ArcSeekBarView> reference;

        private MyHandler(ArcSeekBarView view) {
            reference = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (reference.get().degrees <= reference.get().cRotate) {
                reference.get().invalidate();
                sendEmptyMessageDelayed(1, 16);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }
}
