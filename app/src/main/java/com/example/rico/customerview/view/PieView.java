package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tmp on 2018/12/18.
 * 饼状图
 */
public class PieView extends View {
    Paint paint;
    List<Piedata> datas;
    int width, heigth, radius;
    Point cPoint;
    Context context;

    public PieView(Context context) {
        super(context);
        init(context);
    }

    public PieView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        heigth = h;
        radius = width >= heigth ? heigth / 2 : width / 2;
        cPoint.x = width / 2;
        cPoint.y = heigth / 2;
    }

    private void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        datas = new ArrayList<>();
        cPoint = new Point();
    }

    public void addData(@ColorRes int colorRes, int percent) {
        datas.add(new Piedata(colorRes, percent));
        invalidate();
    }

    public void clearData() {
        datas.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int startAngle = 0, swipeAngle;
        RectF rectF = new RectF(cPoint.x - radius, cPoint.y - radius, cPoint.x + radius, cPoint.y + radius);
        for (int i = 0; i < datas.size(); i++) {
            Piedata data = datas.get(i);
            paint.setColor(getResources().getColor(data.getColor()));
            swipeAngle = 360 * data.getPercent()/100;
            canvas.drawArc(rectF, startAngle, swipeAngle, true, paint);
            startAngle = startAngle + swipeAngle;
        }
    }

    private class Piedata {
        int color;
        int percent;

        public Piedata(int color, int percent) {
            this.color = color;
            this.percent = percent;
        }

        public int getColor() {
            return color;
        }

        public int getPercent() {
            return percent;
        }
    }
}
