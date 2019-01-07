package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.rico.customerview.R;

import java.lang.ref.WeakReference;

/**
 * Created by Tmp on 2018/12/18.
 * 从左到右动态绘制一张图片
 */
public class DrawBitmapView extends View {
    Paint paint;
    RectF dst;

    public DrawBitmapView(Context context) {
        super(context);
        initView();
    }


    public DrawBitmapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    Bitmap bitmap;

    public void setBitmap(Bitmap bitmap) {
        if (isDraw || bitmap == null) {
            return;
        }
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        float widthdScale = (float) radius * 2 / bitmapWidth;
        float heightScale = (float) radius * 2 / bitmapHeight;
        float scale = widthdScale > heightScale ? heightScale : widthdScale;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        this.bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
        invalidate();
    }

    public void clearBitmap() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        invalidate();
    }

    private void initView() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(R.color.bottom_bg));
        paint.setStyle(Paint.Style.FILL);
        handler = new MyHandler(this);

    }

    int width, height, radius;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        radius = (w > h ? h / 2 : w / 2);
    }

    MyHandler handler;
    boolean isDraw;
    int rightX;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(width / 2, height / 2);
        canvas.drawCircle(0, 0, radius, paint);
        if (bitmap != null) {
            rightX = rightX + bitmap.getWidth() / 10;
            isDraw = true;
            Rect src = new Rect(0, 0, rightX, bitmap.getHeight());
            dst = new RectF(-bitmap.getWidth() / 2, -bitmap.getHeight() / 2, -bitmap.getWidth() / 2 + rightX, bitmap.getHeight() / 2);
            canvas.drawBitmap(bitmap, src, dst, paint);
            if (rightX < bitmap.getWidth()) {
                handler.sendEmptyMessageDelayed(1, 6);
            } else {
                isDraw = false;
            }
        } else {
            rightX = 0;
        }
    }

    private static class MyHandler extends Handler {
        WeakReference<DrawBitmapView> weakReference;

        public MyHandler(DrawBitmapView drawBitmapView) {
            weakReference = new WeakReference<>(drawBitmapView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DrawBitmapView view = weakReference.get();
            view.invalidate();
        }
    }
}
