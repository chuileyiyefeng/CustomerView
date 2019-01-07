package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.rico.customerview.R;

/**
 * Created by Tmp on 2018/12/18.
 */
public class ScaleView extends View {
    public ScaleView(Context context) {
        super(context);
    }

    public ScaleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    int width, height, radius;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        radius =(w >= h ? h / 2 : w / 2)-20;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        缩放画圆
        canvas.save();
        canvas.translate(width / 2, height / 2);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(getResources().getColor(R.color.colorAccent));
        canvas.drawCircle(0, 0, radius, paint);
        canvas.scale(0.8f, 0.8f);
        canvas.drawCircle(0, 0, radius, paint);


        canvas.restore();
        canvas.save();
        canvas.translate(width / 2, height / 2);
        for (int i = 0; i <= 360; i = i + 10) {
            canvas.drawLine(radius * 0.8f, 0, radius, 0, paint);
            canvas.rotate(10);
        }
        canvas.restore();
        canvas.translate(width / 2, height / 2);
        paint.setColor(getResources().getColor(R.color.text_normal));
        RectF rectF = new RectF(0, 0, radius / 1.2F, radius / 1.2F);
        canvas.drawRect(rectF,paint);
        canvas.scale(-1,-1); //绕原点旋转180
        paint.setColor(getResources().getColor(R.color.saffon_yellow));
        canvas.drawRect(rectF,paint);

    }
}
