package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.example.rico.customerview.bean.PointData;

import java.util.ArrayList;

public class ToAddView extends View {
    public ToAddView(Context context) {
        this(context, null);
    }

    public ToAddView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToAddView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private int width, height;
    // 指示点区域突出的高度
    private float protrudingHeight = 20;

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
    ArrayList<Paint> paintList;
    ArrayList<PointData> dataList;
    Region globalRegion;
    float radius = dpToPx(20);

    private void init() {
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
                        clickPosition = i;
                        dataList.get(i).setLastRectPos(PointData.defaultPos);
                        invalidate();
                        if (listener != null) {
                            listener.click(i, dataList.get(i).getMessage());
                        }
                    }
                }
                break;
        }
        return isTouchPoint;
    }

    // 添加一个点 基于x、y轴的比例  颜色、半径为默认
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

    // 移动某个数据点
    public void movePoint(int i, float distanceX, float distanceY) {
        PointF point = pointList.get(i);
        point.x = point.x - distanceX;
        point.y = point.y - distanceY;
        Path path = pathList.get(i);
        path.reset();
        path.addCircle(point.x, point.y, dpToPx(dataList.get(i).getRadius()), Path.Direction.CW);
        Region region = regionList.get(i);
        region.setPath(path, globalRegion);
        invalidate();
    }

    // 移动所有数据点
    public void moveAllPoint(float distanceX, float distanceY) {
        for (int i = 0; i < pointList.size(); i++) {
            PointF point = pointList.get(i);
            point.x = point.x - distanceX;
            point.y = point.y - distanceY;
            Path path = pathList.get(i);
            path.reset();
            path.addCircle(point.x, point.y, dpToPx(dataList.get(i).getRadius()), Path.Direction.CW);
            Region region = regionList.get(i);
            region.setPath(path, globalRegion);
        }
        invalidate();
    }

    // 添加点数据
    public void addPointData(PointData data) {
        post(() -> {
            initList();
            addData(data);
            invalidate();
        });
    }

    // 添加子数据
    private void addData(PointData data) {
        PointF point = new PointF();
        point.x = data.getRealX();
        point.y = data.getRealY();
        pointList.add(point);
        Path path = new Path();
        path.addCircle(point.x, point.y, dpToPx(data.getRadius()), Path.Direction.CW);
        pathList.add(path);
        Region region = new Region();
        region.setPath(path, globalRegion);
        regionList.add(region);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(spToPx(data.getTextSize()));
        setColor(data, paint);
        paintList.add(paint);
        dataList.add(data);
    }

    private void setColor(PointData data, Paint paint) {
        try {
            if (data.getPointColor() == 0) {
                paint.setColor(Color.parseColor(data.getPointColorStr()));
            } else {
                paint.setColor(mContext.getResources().getColor(data.getPointColor()));
            }
        } catch (Exception e) {
            paint.setColor(Color.parseColor("#333333"));
        }
    }

    // 添加数据点集合
    public void addPointData(ArrayList<PointData> list) {
        post(() -> {
            for (int i = 0; i < list.size(); i++) {
                PointData data = list.get(i);
                initList();
                addData(data);
            }
            invalidate();
        });
    }

    private void initList() {
        if (pointList == null) {
            pointList = new ArrayList<>();
            regionList = new ArrayList<>();
            pathList = new ArrayList<>();
            paintList = new ArrayList<>();
            dataList = new ArrayList<>();
        }
    }

    public ArrayList<PointF> getPointS() {
        return pointList == null ? new ArrayList<>() : pointList;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (pointList == null) {
            return;
        }
        drawPoint(canvas);
        drawClickInfo(canvas);

    }

    // 画数据点
    private void drawPoint(Canvas canvas) {
        for (int i = 0; i < pointList.size(); i++) {
            PointF point = pointList.get(i);
            if (point.x != 0 && point.y != 0) {
                setColor(dataList.get(i), paintList.get(i));
                canvas.drawPath(pathList.get(i), paintList.get(i));
            }
        }
    }


    private final int noClick = -1;

    // 当前点击的下标
    private int clickPosition = noClick;


    // 画指点击信息
    private void drawClickInfo(Canvas canvas) {
        //  说明点到指示点区域了
        float textMargin = 20;
        float protrudingWidth = 20;
        if (clickPosition != noClick) {
            PointData data = dataList.get(clickPosition);
            PointF pointF = pointList.get(clickPosition);
            Path rectPath = new Path();
            int radius = dpToPx(data.getRadius() / 2 + 4);
            float x = pointF.x;
            float y = pointF.y - radius;

            String text = dataList.get(clickPosition).getMessage();
            if (text.isEmpty()) {
                return;
            }
            Paint paint = paintList.get(clickPosition);
            float textLength = paint.measureText(text);
            float rectWidth = textLength + textMargin * 2;
            //  框在左边
            float starX, startY;
            if (data.getLastRectPos() == 0) {
                // 左边
                if (x + rectWidth >= width) {
                    rectWidth = -rectWidth;
                    protrudingWidth = -protrudingWidth;
                    x = x - radius;
                    starX = x - textLength - textMargin;
                    data.setLastRectPos(PointData.left);
                } else {// 右边
                    x = x + radius;
                    starX = x + textMargin;
                    data.setLastRectPos(PointData.right);
                }
            } else if (data.getLastRectPos() == PointData.right) {
                x = x + radius;
                starX = x + textMargin;
            } else {
                rectWidth = -rectWidth;
                protrudingWidth = -protrudingWidth;
                x = x - radius;
                starX = x - textLength - textMargin;
            }

            rectPath.moveTo(x, y);
            Paint.FontMetrics detailMetrics = paint.getFontMetrics();
            float rectHeight = (detailMetrics.bottom - detailMetrics.top) * 2;
            rectPath.lineTo(x, y - rectHeight);
            rectPath.lineTo(x + rectWidth, y - rectHeight);
            rectPath.lineTo(x + rectWidth, y - protrudingHeight);
            rectPath.lineTo(x + protrudingWidth, y - protrudingHeight);
            try {
                if (data.getRectColor() == 0) {
                    paint.setColor(Color.parseColor(data.getRectColorStr()));
                } else {
                    paint.setColor(mContext.getResources().getColor(data.getRectColor()));
                }
            } catch (Exception e) {
                paint.setColor(Color.parseColor("#333333"));
            }
            canvas.drawPath(rectPath, paint);
            float baseLine = (y * 2 - protrudingHeight - rectHeight) / 2;
            startY = baseLine + (detailMetrics.bottom - detailMetrics.top) / 2 - detailMetrics.bottom;
            try {
                if (data.getTextColor() == 0) {
                    paint.setColor(Color.parseColor(data.getTextColorStr()));
                } else {
                    paint.setColor(mContext.getResources().getColor(data.getTextColor()));
                }
            } catch (Exception e) {
                paint.setColor(Color.parseColor("#ffffff"));
            }
            canvas.drawText(text, starX, startY, paint);
        }
    }

    private int dpToPx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    private float spToPx(float size) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, getResources().getDisplayMetrics());
    }

    // 清除所有点标识
    public void clear() {
        if (pointList != null) {
            pointList.clear();
            regionList.clear();
            pathList.clear();
            paintList.clear();
            dataList.clear();
            clickPosition = noClick;
        }
        invalidate();
    }

    public void setRadius(int dp) {
        radius = dpToPx(dp);
    }

    PointClickListener listener;

    public void setPointClickListener(PointClickListener listener) {
        this.listener = listener;
    }


    public interface PointClickListener {
        void click(int position, String message);
    }
}

