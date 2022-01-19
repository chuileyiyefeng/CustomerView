package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;

import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.rico.customerview.R;

/**
 * Created by Tmp on 2019/1/24.
 * 区域点击view
 */
public class RegionClickView extends View {
    public RegionClickView(Context context) {
        super(context);
        init();
    }

    public RegionClickView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RegionClickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    Paint paint;

    int width, height, radius;
    Point centerPoint;
    //    四个方向的Region  中间的
    Region rgL, rgT, rgR, rgB, rgC;
    //    四个方向的path 中间的 点击的path
    Path pL, pT, pR, pB, pC, pS;

    Region globalRegion;

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        pL = new Path();
        pT = new Path();
        pR = new Path();
        pB = new Path();
        pC = new Path();
        pS = new Path();

        rgL = new Region();
        rgT = new Region();
        rgR = new Region();
        rgB = new Region();
        rgC = new Region();
        globalRegion = new Region();
    }

    float touchX, touchY;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchX = event.getX();
                touchY = event.getY();
//                这里有点击效果，背景应该改变
                pS.reset();
                if (rgL.contains((int) touchX, (int) touchY)) {
                    pS.set(pL);
                } else if (rgT.contains((int) touchX, (int) touchY)) {
                    pS.set(pT);
                } else if (rgR.contains((int) touchX, (int) touchY)) {
                    pS.set(pR);
                } else if (rgB.contains((int) touchX, (int) touchY)) {
                    pS.set(pB);
                } else if (rgC.contains((int) touchX, (int) touchY)) {
                    pS.set(pC);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                pS.reset();
                invalidate();
                break;

        }
        return true;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        radius = Math.min(width, height) / 3;
        centerPoint = new Point(width / 2, height / 2);
        int smallRadius = radius / 4;

        int inWidth = smallRadius * 2;

        globalRegion.set(0, 0, w, h);

        pC.addCircle(centerPoint.x, centerPoint.y, smallRadius, Path.Direction.CW);
        rgC.setPath(pC, globalRegion);

        RectF rSmall = new RectF(centerPoint.x - inWidth, centerPoint.y - inWidth, centerPoint.x + inWidth, centerPoint.y + inWidth);
        RectF rBig = new RectF(centerPoint.x - radius, centerPoint.y - radius, centerPoint.x + radius, centerPoint.y + radius);

//        添加路径 从270-40°C开始,大小圆弧要反着来

        int bigAngle = 80;
        int spaceAngle = 10;
        int disAngleLength = (int) (spaceAngle * Math.PI * (radius - inWidth) / 180 + 0.5);
        int disAngle = (int) (disAngleLength / Math.PI * inWidth / 180 + 0.5);
//        里面的角度应当小的距离
        int lessAngle = (int) (disAngle / 2 + 0.5f);

//        上
        pT.addArc(rSmall, 270 - bigAngle / 2f + lessAngle, bigAngle - lessAngle);
        pT.arcTo(rBig, 310, -bigAngle);
        pT.close();
        rgT.setPath(pT, globalRegion);
//         右
        pR.addArc(rSmall, 0 - bigAngle / 2f + lessAngle, bigAngle - lessAngle);
        pR.arcTo(rBig, 40, -bigAngle);
        pR.close();
        rgR.setPath(pR, globalRegion);
//        下
        pB.addArc(rSmall, 90 - bigAngle / 2f + lessAngle, bigAngle - lessAngle);
        pB.arcTo(rBig, 130, -bigAngle);
        pB.close();
        rgB.setPath(pB, globalRegion);
//        左
        pL.addArc(rSmall, 180 - bigAngle / 2f + lessAngle, bigAngle - lessAngle);
        pL.arcTo(rBig, 220, -bigAngle);
        pL.close();
        rgL.setPath(pL, globalRegion);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(getResources().getColor(R.color.gray_thumb));
        canvas.drawPath(pC, paint);
        canvas.drawPath(pT, paint);
        canvas.drawPath(pR, paint);
        canvas.drawPath(pB, paint);
        canvas.drawPath(pL, paint);

        paint.setColor(getResources().getColor(R.color.gray_light));
        canvas.drawPath(pS, paint);
    }
}
