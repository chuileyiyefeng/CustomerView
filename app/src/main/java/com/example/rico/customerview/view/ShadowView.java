package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

/**
 * create by pan yi on 2020/12/29
 * desc : 阴影控件 此方式会有兼容问题
 */
public class ShadowView extends BaseCustomerView {

    public ShadowView(Context context) {
        super(context);
    }

    public ShadowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ShadowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Paint paint;
    private RectF rectF;

    @Override
    protected void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        rectF = new RectF();
    }

    public  int shadowDistance=20;
    private int dpToPx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float rd =dpToPx(shadowDistance);

        paint.setShadowLayer(width/10f,0,0,  Color.parseColor("#90000000"));//绘制阴影的主要方法
        rectF.set(rd, rd, width-rd, height-rd);
        canvas.drawRoundRect(rectF, 100, 100, paint);
//        canvas.drawCircle(width/2f, height/2f, width/2f-rd, paint);
        canvas.save();
    }
}
