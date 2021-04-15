package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.example.rico.customerview.R;

/**
 * create by pan yi on 2021/3/24
 * desc : 自定object属性的view
 */
public class ObjectAnimView extends BaseCustomerView {
    private int radius=dpToPx(10);
    private Paint paint;

    public ObjectAnimView(Context context) {
        super(context);
    }

    public ObjectAnimView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ObjectAnimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        invalidate();
    }

    private int dpToPx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }
    @Override
    protected void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(R.color.blue_y));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(width / 2, height / 2, dpToPx(radius), paint);
    }
}
