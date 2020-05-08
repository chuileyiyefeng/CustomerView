package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.rico.customerview.R;

import java.util.ArrayList;

public class TouchToAddView extends View {
    public TouchToAddView(Context context) {
        this(context, null);
    }

    public TouchToAddView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchToAddView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    int width, height;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        globalRegion = new Region();
        globalRegion.set(0, 0, w, h);
    }

    Context mContext;
    ArrayList<PointF> pointList;
    ArrayList<Region> regionList;
    ArrayList<Path> pathList;
    Paint paint;
    Region globalRegion;
    float radius;

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(mContext.getResources().getColor(R.color.deepskyblue));
        radius = 50f;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    float touchX, touchY;
    boolean isTouchPoint;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouchPoint = false;
                touchX = event.getX();
                touchY = event.getY();
                for (int i = 0; i < regionList.size(); i++) {
                    if (regionList.get(i).contains((int) touchX, (int) touchY)) {
                        isTouchPoint = true;
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                touchX = event.getX();
                touchY = event.getY();
                for (int i = 0; i < regionList.size(); i++) {
                    if (regionList.get(i).contains((int) touchX, (int) touchY)) {
                        Log.e("TouchToAdd", "onTouchEvent: " + "click" + i);
                        if (listener != null) {
                            listener.click(i);
                        }
                    }
                }
                break;
        }
        return isTouchPoint;
    }

    public void addPoint(float x, float y) {
        post(() -> {
            if (pointList == null) {
                pointList = new ArrayList<>();
                regionList = new ArrayList<>();
                pathList = new ArrayList<>();
            }
            PointF point = new PointF();
            point.x = x;
            point.y = y;
            pointList.add(point);
            Path path = new Path();
            path.addCircle(point.x, point.y, radius, Path.Direction.CW);
            pathList.add(path);
            Region region = new Region();
            region.setPath(path, globalRegion);
            regionList.add(region);
            invalidate();
        });
    }

    public void movePoint(int i, float distanceX, float distanceY) {
        PointF point = pointList.get(i);
        point.x = point.x - distanceX;
        point.y = point.y - distanceY;
        Path path = pathList.get(i);
        path.reset();
        path.addCircle(point.x, point.y, radius, Path.Direction.CW);
        Region region = regionList.get(i);
        region.setPath(path, globalRegion);
        invalidate();
    }

    public void moveAllPoint(float distanceX, float distanceY) {
        for (int i = 0; i < pointList.size(); i++) {
            PointF point = pointList.get(i);
            point.x = point.x - distanceX;
            point.y = point.y - distanceY;
            Path path = pathList.get(i);
            path.reset();
            path.addCircle(point.x, point.y, radius, Path.Direction.CW);
            Region region = regionList.get(i);
            region.setPath(path, globalRegion);
        }
        invalidate();
    }

    public ArrayList<PointF> getPointS() {
        return pointList == null ? new ArrayList<>() : pointList;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (pointList == null) {
            return;
        }
        for (int i = 0; i < pointList.size(); i++) {
            PointF point = pointList.get(i);
            if (point.x != 0 && point.y != 0) {
                canvas.drawPath(pathList.get(i), paint);
            }
        }

    }

    private int dpToPx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    public void setRadius(int dp) {
        radius = dpToPx(dp);
    }

    PointClickListener listener;

    public void setPointClickListener(PointClickListener listener) {
        this.listener = listener;
    }

    public interface PointClickListener {
        void click(int position);
    }
}

