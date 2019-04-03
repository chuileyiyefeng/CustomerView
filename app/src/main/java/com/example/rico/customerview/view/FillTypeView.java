package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.rico.customerview.R;

/**
 * Created by Tmp on 2019/1/23.
 * 填充模式view，主要用到Path.op()这个方法
 */
public class FillTypeView extends View {
    public FillTypeView(Context context) {
        super(context);

    }

    public FillTypeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }


    int width, height, radius;
    RectF rectF;
    Point point;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        radius = Math.min(w, h) / 3;
        point = new Point(width / 2, height / 2);
        rectF = new RectF(point.x - radius, point.y - radius, point.x + radius, point.y + radius);
        initPath();
    }

    Path lCircle = new Path();
    Path rCircle = new Path();

    private void initPath() {

        lCircle.addArc(rectF, 90, 180);


        rCircle.addArc(rectF, -90, 180);

        int sRadius = radius / 2;
        Path tCircle = new Path();
        tCircle.addCircle(point.x, point.y - sRadius, sRadius, Path.Direction.CCW);

        Path bCircle = new Path();
        bCircle.addCircle(point.x, point.y + sRadius, sRadius, Path.Direction.CCW);

        lCircle.op(tCircle, Path.Op.UNION);
        lCircle.op(bCircle, Path.Op.DIFFERENCE);

        rCircle.op(bCircle, Path.Op.UNION);
        rCircle.op(tCircle, Path.Op.DIFFERENCE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setBackgroundColor(getResources().getColor(R.color.button_bg));
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.text_normal));
        int sRadius = radius / 2;
        canvas.drawPath(lCircle, paint);
        paint.setColor(getResources().getColor(R.color.white));
        canvas.drawCircle(point.x, point.y - sRadius, 20, paint);
        canvas.drawPath(rCircle, paint);
        paint.setColor(getResources().getColor(R.color.text_normal));
        canvas.drawCircle(point.x, point.y + sRadius, 20, paint);
    }
}
