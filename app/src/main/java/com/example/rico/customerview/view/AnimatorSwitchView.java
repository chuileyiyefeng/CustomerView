package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.example.rico.customerview.R;

/**
 * Created by Tmp on 2019/6/17.
 * 带动画的switch
 */
public class AnimatorSwitchView extends BaseCustomerView {
    public AnimatorSwitchView(Context context) {
        super(context);
    }

    public AnimatorSwitchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private Paint textPaint, borderPaint;
    private RectF rectF;

    @Override
    protected void init(Context context) {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        textPaint.setColor(getResources().getColor(R.color.ivory));
        borderPaint.setColor(getResources().getColor(R.color.ivory));

        borderPaint.setStrokeWidth(20);
        borderPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rectF = new RectF(0, 0, w, h);
    }

    private String leftStr, rightStr;

    public void setLeftStr(String leftStr, String rightStr) {
        this.leftStr = leftStr;
        this.rightStr = rightStr;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(rectF, width / 4, height / 2, borderPaint);
    }
}
