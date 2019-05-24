package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.rico.customerview.R;

/**
 * Created by Tmp on 2019/4/1.
 * 翻页效果view
 * 原理看图 R.mipmap.page_turning
 */
public class PageTurningView extends View {
    private int width, height;
    private PointF a, b, c, d, e, f, g, h, i, j, k;
    private Paint paintA, paintB, paintC;
    //    path分为三个区域，当前页面A，卷曲来的页面的反面B，和底下的页面C
    private Path pathA, pathB, pathC;

    public PageTurningView(Context context) {
        super(context);
        init();
    }

    public PageTurningView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        a = new PointF();
        b = new PointF();
        c = new PointF();
        d = new PointF();
        e = new PointF();
        f = new PointF();
        g = new PointF();
        h = new PointF();
        i = new PointF();
        j = new PointF();
        k = new PointF();
        paintA = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintB = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintC = new Paint(Paint.ANTI_ALIAS_FLAG);

        paintA.setColor(getResources().getColor(R.color.colorLine));
        paintB.setColor(getResources().getColor(R.color.yellow));
        paintC.setColor(getResources().getColor(R.color.button_bg));
        pathA = new Path();
        pathB = new Path();
        pathC = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w - 50;
        height = h - 50;
//        width = w;
//        height = h;
        setBackgroundColor(paintA.getColor());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!(event.getX() > width / 2 && event.getY() > height / 2)) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                move(event);
                break;
            case MotionEvent.ACTION_UP:
                pathA.reset();
                pathB.reset();
                pathC.reset();
                invalidate();
                break;

        }
        return true;

    }

    //    a是按下的点坐标 f是角落的点坐标 g是af线段中点坐标 eh是ab线段的垂直平分线,e在底边，h在右边
    private void move(MotionEvent event) {
        float downX = event.getX();
        float downY = event.getY();

        a.set(downX, downY);
        f.set(width, height);
        g.set((width + downX) / 2, (height + downY) / 2);
        float gfLength = (g.x - f.x) * (g.x - f.x) + (g.y - f.y) * (g.y - f.y);
        gfLength = (float) Math.pow(gfLength, 0.5);
//        gf与x轴的夹角为
        double gfAngle = Math.toDegrees(Math.acos(((f.x - g.x) / gfLength)));
//        eg与x轴的夹角为
        double egAngle = 90 - gfAngle;
//        g点到底部的距离
        float gDistanceY = f.y - g.y;
//        ge的长度为
        float geLength = gDistanceY / (float) Math.sin(Math.toRadians(egAngle));
//        e点的x轴坐标为
        e.x = g.x - (float) Math.cos(Math.toRadians(egAngle)) * geLength;
        e.y = height;
//       hf与gf之间的夹角就等于eg与ef之间的夹角 hf的长度为
        float hfLength = gfLength / (float) Math.cos(Math.toRadians(egAngle));
        h.x = width;
        h.y = height - hfLength;

//        线段cj是ag的垂直平分线 c靠底边，j靠右
//        假设ag的中点为l;
        float lx = (a.x + g.x) / 2;
        float ly = (a.y + g.y) / 2;
        float lfLength = (lx - f.x) * (lx - f.x) + (ly - f.y) * (ly - f.y);
        lfLength = (float) Math.pow(lfLength, 0.5);
//        l点到底部的距离
        float lDistanceY = f.y - ly;
//        gh的长度为
        float leLength = lDistanceY / (float) Math.sin(Math.toRadians(egAngle));
//        e点的x轴坐标为
        c.x = lx - (float) Math.cos(Math.toRadians(egAngle)) * leLength;
        c.y = height;
//       hf与gf之间的夹角就等于eg与ef之间的夹角 hf的长度为
        float jfLength = lfLength / (float) Math.cos(Math.toRadians(egAngle));
        j.x = width;
        j.y = height - jfLength;
//        点b是ae和cj的交点，点k是ah和cj的交点

        getNodePoint(b, a, e, c, j);
        getNodePoint(k, a, h, c, j);
//        取cb线段的中点P,点d就是pe的中点
        d.x = ((c.x + b.x) / 2 + e.x) / 2;
        d.y = ((c.y + b.y) / 2 + e.y) / 2;

//        取kj的中点q,点i就是qh的中点
        i.x = ((k.x + j.x) / 2 + h.x) / 2;
        i.y = ((k.y + j.y) / 2 + h.y) / 2;

        getPathA();
        getPathB();
        getPathC();
        invalidate();

    }

    int radius = 10;

    //    获取两条线段的交点，并赋值给目标点
    private void getNodePoint(PointF targetPoint, PointF a, PointF b, PointF c, PointF d) {
//       直线方程是为y=kx+b;
        float k, p, j, l;
        k = (b.y - a.y) / (b.x - a.x);
        p = a.y - k * a.x;

        j = (d.y - c.y) / (d.x - c.x);
        l = d.y - j * d.x;

        float x, y;
//        求交点则 k*x+p=j*x+l;
//         k*x=j*x+l-p;
//        (k-j)*x=l-p;
        x = (l - p) / (k - j);
        y = (k * x + p);
        targetPoint.set(x, y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        drawLines(canvas);
        canvas.drawPath(pathA, paintA);
        canvas.drawPath(pathC, paintC);
        canvas.drawPath(pathB, paintB);
        drawShadow(canvas);
    }

    private void drawShadow(Canvas canvas) {
//        就是画矩形，用paint.setShader实现阴影渐变
    }

    private void getPathA() {
        pathA.reset();
        pathA.lineTo(0, height);
        pathA.lineTo(c.x, c.y);
        pathA.quadTo(e.x, e.y, b.x, b.y);
        pathA.lineTo(a.x, a.y);
        pathA.lineTo(k.x, k.y);
        pathA.quadTo(h.x, h.y, j.x, j.y);
        pathA.lineTo(width, 0);
    }

    //    pathB减去pathA和pathB的交集,就是pathB的最终路径
    private void getPathB() {
        pathB.reset();
//        连接点d、b、a、k、i
        pathB.moveTo(d.x, d.y);
        pathB.lineTo(b.x, b.y);
        pathB.lineTo(a.x, a.y);
        pathB.lineTo(k.x, k.y);
        pathB.lineTo(i.x, i.y);
        pathB.lineTo(d.x, d.y);

        interR.reset();
        interR.moveTo(d.x, d.y);
        interR.lineTo(b.x, b.y);
        interR.lineTo(a.x, a.y);
        interR.lineTo(k.x, k.y);
        interR.lineTo(i.x, i.y);
        interR.lineTo(d.x, d.y);
        //        两条路径的交集
        interR.op(pathA, Path.Op.INTERSECT);
        pathB.op(interR, Path.Op.DIFFERENCE);
    }

    private Path interR = new Path();

    private void getPathC() {
        pathC.reset();
        pathC.lineTo(0, height);
        pathC.lineTo(width, height);
        pathC.lineTo(width, 0);
        pathC.lineTo(0, 0);

        pathC.op(pathA, Path.Op.DIFFERENCE);
//        pathC.op(pathB,Path.Op.DIFFERENCE);
    }

    private void drawLines(Canvas canvas) {
        canvas.drawLine(a.x, a.y, f.x, f.y, paintB);
        canvas.drawLine(a.x, a.y, e.x, e.y, paintB);
        canvas.drawLine(a.x, a.y, h.x, h.y, paintB);
        canvas.drawLine(e.x, e.y, h.x, h.y, paintB);
        canvas.drawLine(c.x, c.y, j.x, j.y, paintB);
        paintB.setTextSize(50);
        canvas.drawText("a", a.x, a.y, paintB);
        canvas.drawText("b", b.x, b.y, paintB);
        canvas.drawText("c", c.x, c.y, paintB);
        canvas.drawText("d", d.x, d.y, paintB);
        canvas.drawText("e", e.x, e.y, paintB);
        canvas.drawText("f", f.x, f.y, paintB);
        canvas.drawText("g", g.x, g.y, paintB);
        canvas.drawText("h", h.x, h.y, paintB);
        canvas.drawText("i", i.x, i.y, paintB);
        canvas.drawText("j", j.x, j.y, paintB);
        canvas.drawText("k", k.x, k.y, paintB);
    }
}
