package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.rico.customerview.R;

/**
 * Created by Tmp on 2019/1/23.
 */
public class FillTypeView extends View {
    public FillTypeView(Context context) {
        super(context);
        init();
    }

    public FillTypeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FillTypeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    int width, height,radius;
    RectF rectF;
    Point point;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        radius=Math.min(w,h)/3;
        point=new Point(width/2,height/2);
        rectF=new RectF(point.x-radius,point.y-radius,point.x+radius,point.y+radius);
    }

    private void init() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setBackgroundColor(getResources().getColor(R.color.gray_thumb));
        Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.text_normal));
        canvas.drawArc(rectF,90,180,false,paint);
        paint.setColor(getResources().getColor(R.color.white));
        canvas.drawArc(rectF,270,180,false,paint);
        int sRadius=radius/2;
        paint.setColor(getResources().getColor(R.color.text_normal));
        canvas.drawCircle(point.x,point.y-sRadius,sRadius,paint);
        paint.setColor(getResources().getColor(R.color.white));
        canvas.drawCircle(point.x,point.y+sRadius,sRadius,paint);

        paint.setColor(getResources().getColor(R.color.white));
        canvas.drawCircle(point.x,point.y-sRadius,20,paint);
        paint.setColor(getResources().getColor(R.color.text_normal));
        canvas.drawCircle(point.x,point.y+sRadius,20,paint);
    }
}
