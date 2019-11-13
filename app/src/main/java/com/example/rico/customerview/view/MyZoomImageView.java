package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewTreeObserver;

/**
 * Created by Tmp on 2019/11/13.
 */
public class MyZoomImageView extends AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener {
    ScaleGestureDetector detector;
    Matrix picMatrix;

    public MyZoomImageView(Context context) {
        this(context, null);
    }

    public MyZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initGesture();
    }

    private void initGesture() {
        picMatrix = new Matrix();
        setScaleType(ScaleType.MATRIX);
        detector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scale = detector.getScaleFactor();
                float scaleCenterX = detector.getFocusX();
                float scaleCenterY = detector.getFocusY();
                picMatrix.postScale(scale, scale, scaleCenterX, scaleCenterY);
                setImageMatrix(picMatrix);
                return true;
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointCount = event.getPointerCount();
        if (pointCount == 2) {
            getParent().requestDisallowInterceptTouchEvent(true);
            detector.onTouchEvent(event);
        }
        return true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
    }

    //视图变化监听,会调用多次，只适配一次 图片加载完成后，适配缩放大小
    boolean isGlobalLayout;
    @Override
    public void onGlobalLayout() {
        if (isGlobalLayout) {
            return;
        }
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        //view宽高
        int width = getWidth();
        int height = getHeight();
        // 图片宽高
        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();
        float scaleX,scaleY,realScale =1.0f;
        scaleX=(float) width/drawableWidth;
        scaleY=(float)height/drawableHeight;
        realScale=Math.min(scaleX,scaleY);
        // 图片居中显示
        picMatrix.postTranslate((width - drawableWidth) / 2, (height - drawableHeight) / 2);
        picMatrix.postScale(realScale, realScale, width / 2, height / 2);
        setImageMatrix(picMatrix);
        Log.e("onGlobalLayout", "onGlobalLayout: "+"change" );
        isGlobalLayout=true;
    }
}
