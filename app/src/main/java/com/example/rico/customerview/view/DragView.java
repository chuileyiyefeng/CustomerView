package com.example.rico.customerview.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;


/**
 * 随意拖动的view
 */

@SuppressLint("AppCompatCustomView")
public class DragView extends ImageView {

    private int width;
    private int height;
    private int screenWidth = 1080;
    private int screenHeight = 1920;
    private Context context;

    //是否拖动
    private boolean isDrag = false;

    public boolean isDrag() {
        return isDrag;
    }

    public DragView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        left = getLeft();
        top = getTop();
        right = getRight();
        bottom = getBottom();
    }

    public int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        return getResources().getDimensionPixelSize(resourceId);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private float downX;
    private float downY;

    int left, top, right, bottom;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (this.isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isDrag = false;
                    downX = event.getX();
                    downY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.e("kid", "ACTION_MOVE");
                    final float xDistance = event.getX() - downX;
                    final float yDistance = event.getY() - downY;
                    int l, r, t, b;
                    //当水平或者垂直滑动距离大于10,才算拖动事件
                    if (Math.abs(xDistance) > 10 || Math.abs(yDistance) > 10) {
                        Log.e("kid", "Drag");
                        isDrag = true;
                        l = (int) (left + xDistance);
                        r = l + width;
                        t = (int) (top + yDistance);
                        b = t + height;
                        if (l < 0) {
                            l = 0;
                            r = l + width;
                        } else if (r > screenWidth) {
                            r = screenWidth;
                            l = r - width;
                        }
                        if (t < 0) {
                            t = 0;
                            b = t + height;
                        } else if (b > screenHeight) {
                            b = screenHeight;
                            t = b - height;
                        }
                        left = l;
                        top = t;
                        right = r;
                        bottom = b;
                        layout(l, t, r, b);
                    }
                    break;
                case MotionEvent.ACTION_UP:
//                    setPressed(false);

                    break;
                case MotionEvent.ACTION_CANCEL:
//                    setPressed(false);
                    break;
            }
            return true;
        }
        return false;
    }

}
