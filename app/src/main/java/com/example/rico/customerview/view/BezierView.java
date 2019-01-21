package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.rico.customerview.R;

import java.lang.ref.WeakReference;

/**
 * Created by Tmp on 2019/1/8.
 * 贝塞尔曲线view ,用4个三阶贝塞尔曲线画一个圆
 */
public class BezierView extends View {
    Paint paint;
    Point startP, endP, c1, c2;
    Path path;
    int width, height;
    MyHandler handler;
    private int radius;


    public BezierView(Context context) {
        super(context);
        init();
    }

    public BezierView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.colorAccent));
        paint.setStrokeWidth(1);
        path = new Path();
        handler = new MyHandler(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        radius = Math.min(width, height) / 10;
        int centerX = radius / 2 * 3, centerY = radius / 2 * 3;
        int halfRadius = radius / 2;
        startP = new Point(centerX - radius, centerY);
        endP = new Point(centerX, centerY - radius);
        c1 = new Point(centerX - radius, centerY - halfRadius);
        c2 = new Point(centerX - halfRadius, centerY - radius);

//        绘画顺序从左上开始画,也就是第四象限

//        圆的4个下标点,第一个和最后一个是同一个，为了好画，加一个
        originStars[0] = new Point(centerX - radius, centerY);
        originStars[1] = new Point(centerX, centerY - radius);
        originStars[2] = new Point(centerX + radius, centerY);
        originStars[3] = new Point(centerX, centerY + radius);
        originStars[4] = new Point(centerX - radius, centerY);

        for (int i = 0; i < originStars.length; i++) {
            stars[i] = new Point(originStars[i].x, originStars[i].y);
        }
//        8个控制点
        originControl[0] = new Point(centerX - radius, centerY - halfRadius);
        originControl[1] = new Point(centerX - halfRadius, centerY - radius);
        originControl[2] = new Point(centerX + halfRadius, centerY - radius);
        originControl[3] = new Point(centerX + radius, centerY - halfRadius);
        originControl[4] = new Point(centerX + radius, centerY + halfRadius);
        originControl[5] = new Point(centerX + halfRadius, centerY + radius);
        originControl[6] = new Point(centerX - halfRadius, centerY + radius);
        originControl[7] = new Point(centerX - radius, centerY + halfRadius);

        for (int i = 0; i < originControl.length; i++) {
            control[i] = new Point(originControl[i].x, originControl[i].y);
        }
    }

    boolean isMoving = false;
    Point[] stars = new Point[5], control = new Point[8], originStars = new Point[5], originControl = new Point[8];
    float x = 0, y = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float upX = event.getX();
                float upY = event.getY();
                if (upX - x < 10 && upY - y < 10) {
                    startMove();
                }
                break;
        }
        return true;
    }

    private void startMove() {
        if (!isMoving) {
            resetPosition();
            handler.sendEmptyMessage(1);
        }
    }

    private void resetPosition() {
        for (int i = 0; i < originStars.length; i++) {
            stars[i].x = originStars[i].x;
        }
        for (int i = 0; i < originControl.length; i++) {
            control[i].x = originControl[i].x;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        用4个三阶贝塞尔曲线画一个圆
//   下标点0 1对应控制点0 1  1 2对应 2 3  2 3对应 4 5  3 4对应 6 7
        path.reset();
        path.moveTo(stars[0].x, stars[0].y);
        for (int i = 0; i < 4; i++) {
            int k = 2 * i;
            paint.setColor(getResources().getColor(R.color.colorAccent));
            canvas.drawLine(stars[i].x, stars[i].y, control[k].x, control[k].y, paint);
            canvas.drawLine(control[k].x, control[k].y, control[k + 1].x, control[k + 1].y, paint);
            canvas.drawLine(control[k + 1].x, control[k + 1].y, stars[i + 1].x, stars[i + 1].y, paint);
            paint.setColor(getResources().getColor(R.color.text_normal));
            path.cubicTo(control[k].x, control[k].y, control[k + 1].x, control[k + 1].y, stars[i + 1].x, stars[i + 1].y);

        }
        canvas.drawPath(path, paint);
    }

    private static class MyHandler extends Handler {
        WeakReference<BezierView> reference;
        int everyMove,toEndEveryMove, moveDistance, endX, toEndMoveCount;

        public MyHandler(BezierView view) {
            reference = new WeakReference<>(view);
            everyMove = 10;
            toEndEveryMove=10;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BezierView view = reference.get();
            view.isMoving = true;
            if (null == view) {
                return;
            }
            endX = view.radius * 9;
            int each = endX / 4;
            //                endX分为4段，做出一个平滑的效果,右边控制点每次移动的是固定距离
            if (view.stars[2].x <= endX) {
                view.stars[2].x = view.stars[2].x + everyMove;
                view.control[3].x = view.control[3].x + everyMove;
                view.control[4].x = view.control[3].x;
                for (int i = 0; i < view.originStars.length; i++) {
                    int startX = view.stars[i].x;
                    int thisMove = everyMove;
                    int originMove = startX + thisMove;
                    if (i != 2) {
                        if (originMove <= each) {
                            thisMove = (int) (everyMove * 0.8F + 0.5);
                        } else if (originMove <= each * 2 && originMove > each) {
                            thisMove = (int) (everyMove * 0.85F + 0.5);
                        } else if (originMove <= each * 3 && originMove > each * 2) {
                            thisMove = (int) (everyMove * 0.9F + 0.5);
                        } else if (originMove <= each * 4 && originMove > each * 3) {
                            thisMove = (int) (everyMove * 0.95F + 0.5);
                        }
                        view.stars[i].x = startX + thisMove;
                    }
                }
                for (int i = 0; i < view.originControl.length; i++) {
                    int startX = view.control[i].x;
                    int thisMove = everyMove;
                    int originMove = startX + thisMove;
                    if (i != 3 && i != 4) {
                        if (originMove <= each) {
                            thisMove = (int) (everyMove * 0.8F + 0.5);
                        } else if (originMove <= each * 2 && originMove > each) {
                            thisMove = (int) (everyMove * 0.85F + 0.5);
                        } else if (originMove <= each * 3 && originMove > each * 2) {
                            thisMove = (int) (everyMove * 0.9F + 0.5);
                        } else if (originMove <= each * 4 && originMove > each * 3) {
                            thisMove = (int) (everyMove * 0.95F + 0.5);
                        }
                        view.control[i].x = startX + thisMove;
                    }
                }
                view.invalidate();
                sendEmptyMessageDelayed(1, 10);
                moveDistance += everyMove;
            } else {
                //                右边控制点已经到终点，要缓慢滑动左边的点恢复图形原状

//                最右边的点减去最左边的点的距离就是还需滑动的距离
                int needMove = view.stars[2].x - view.stars[0].x;
                if (toEndEveryMove * toEndMoveCount <= needMove) {
                    for (int i = 0; i < view.originStars.length; i++) {
                        if (i != 2) {
                            view.stars[i].x = view.stars[i].x + toEndEveryMove;
                        }
                    }
                    for (int i = 0; i < view.originControl.length; i++) {
                        if (i != 3 && i != 4) {
                            view.control[i].x = view.control[i].x + toEndEveryMove;
                        }
                    }
                    toEndMoveCount++;
                    view.invalidate();
                    sendEmptyMessageDelayed(1, 10);
                } else {
                    //                最后完全还原
                    view.isMoving = false;
                    for (int i = 0; i < view.originStars.length; i++) {
                        view.stars[i].x = view.originStars[i].x + moveDistance;
                    }
                    for (int i = 0; i < view.originControl.length; i++) {
                        view.control[i].x = view.originControl[i].x + moveDistance;
                    }
                    moveDistance = 0;
                    toEndMoveCount=0;
                }
            }

        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }
}
