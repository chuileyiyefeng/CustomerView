package com.example.rico.customerview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Tmp on 2019/11/11.
 */
public class ZoomImageView extends android.support.v7.widget.AppCompatImageView {
    private String TAG = getClass().getSimpleName();

    public ZoomImageView(Context context) {
        super(context);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    // 图片是否拉到了边界
    boolean isBound;

    // 按下点的坐标值
    float downPoint1X, downPoint1Y, downPoint2X, downPoint2Y;

    // 当前移动的坐标值
    float movePoint1X, movePoint1Y, movePoint2X, movePoint2Y;


    //缩放中心点
    float scaleCenterX, scaleCenterY;

    //缩放大小
    float scaleX, scaleY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downPoint1X = event.getX(event.getActionIndex());
                downPoint1Y = event.getY(event.getActionIndex());
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                downPoint2X = event.getX(event.getActionIndex());
                downPoint2Y = event.getY(event.getActionIndex());
                break;
            case MotionEvent.ACTION_MOVE:
                move(event);
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_UP:
        }
        return true;
    }

    private void move(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            disallowParent();
            movePoint1X = event.getX(0);
            movePoint1Y = event.getY(0);
            movePoint2X = event.getX(1);
            movePoint2Y = event.getY(1);
            scaleCenterX = (movePoint1X + movePoint2X) / 2f;
            scaleCenterY = (movePoint1Y + movePoint2Y) / 2f;
            float downDistanceX = Math.abs(downPoint1X - downPoint2X);
            float downDistanceY = Math.abs(downPoint1Y - downPoint2Y);

            float moveDistanceX = Math.abs(movePoint1X - movePoint2X);
            float moveDistanceY = Math.abs(movePoint1Y - movePoint2Y);


            float distanceDown = (float) Math.sqrt(downDistanceX * downDistanceX + downDistanceY * downDistanceY);
            float distanceMove = (float) Math.sqrt(moveDistanceX * moveDistanceX + moveDistanceY * moveDistanceY);

            if (downDistanceX != 0 && moveDistanceX != 0) {
                scaleX = downDistanceX < moveDistanceX ? moveDistanceX / downDistanceX : downDistanceX / moveDistanceX;
            }
            if (downDistanceY != 0 && moveDistanceY != 0) {
                scaleY = downDistanceY < moveDistanceY ? moveDistanceY / downDistanceY : downDistanceY / moveDistanceY;
            }
            Log.e(TAG, "onTouchEvent: " + "distanceDown: " + distanceDown + "  distanceMove: " + distanceMove + " scaleX " + scaleX + " scaleY " + scaleY);
            scaleX = distanceMove / distanceDown;
            scaleY = distanceMove / distanceDown;
            if (scaleX != 0) {
                setScaleX(scaleX);
            }
            if (scaleY != 0) {
                setScaleY(scaleY);
            }
        }
    }

    //    是否禁用父类的拦截功能
    private void disallowParent() {
        getParent().requestDisallowInterceptTouchEvent(true);
    }
}
