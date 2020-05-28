package com.example.rico.customerview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.example.rico.customerview.bean.PointData;

import java.util.ArrayList;

public class JustScaleImageView extends AppCompatImageView {
    ScaleGestureDetector scaleDetector;
    GestureDetector simpleDetector;
    Matrix picMatrix;
    float touchSlop;
    ToAddView addView;
    int duration = 200;

    public JustScaleImageView(Context context) {
        this(context, null);
    }

    public JustScaleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JustScaleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setScaleType(ScaleType.FIT_CENTER);
        initGesture();
    }

    // 单次缩放值、缩放中心X、缩放中心Y
    float scaleCenterX, scaleCenterY;

    // 是否是同一次缩放、是否继续缩放(在缩放还原之后，不抬起全部手指不能继续缩放)
    boolean isSameTimeScaling;

    // 是否进入了缩放操作
    boolean isEnterScale;

    // 是否设置图片的显示type
    boolean isSetType;
    //view宽高 图片宽高
    int width, height, drawableWidth, drawableHeight;
    // 图片原始边界
    RectF originRectF, currentRectF;
    // matrix初始缩放大小
    float originScale;
    // view的中心点
    int centerX, centerY;

    // 是否是多指事件
    boolean isDoubleDown;

    // 最小的缩放值
    float minScale = 1 / 0.4f;

    // 上次滑动是否到了左边界、右边界
    boolean lastScrollLeft, lastScrollRight;

    // 是否在滑动中
    boolean isOnScrolling;


    float lastDownX, lastDownY;
    float lastDownRowX, lastDownRowY;
    float currentLeft, currentTop;

    //view的原始顶点
    int originTop, originLeft;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        originLeft = getLeft();
        originTop = getTop();
    }

    private void initGesture() {
        picMatrix = new Matrix();
        scaleDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                isEnterScale = true;
                if (isOnZooming) {
                    return false;
                }
                float scale = detector.getScaleFactor();
                if (!isSameTimeScaling) {
                    scaleCenterX = detector.getFocusX();
                    scaleCenterY = detector.getFocusY();
                    isSameTimeScaling = true;
                }
                float needScale = getNeedScale();
                if (needScale > minScale) {
                    reductionScale(needScale);
                } else {
                    onScaling(scale);
                }
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                super.onScaleEnd(detector);
                Log.e("inMess", "onScaleEnd: ");
                upToScale();
            }
        });
        simpleDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                isOnScrolling = true;
                lastScrollLeft = false;
                lastScrollRight = false;
                if (isInMess() || isDoubleDown) {
                    return false;
                }
                if (flingAnimator != null) {
                    flingAnimator.cancel();
                }
                float left = currentRectF.left, top = currentRectF.top, right = currentRectF.right, bottom = currentRectF.bottom;
                float endLeft = left - distanceX, endTop = top - distanceY, endRight = right - distanceX, endBottom = bottom - distanceY;
                if (left - distanceX > 0 && distanceX < 0) {
                    lastScrollLeft = true;
                }
                if (right - distanceX < width && distanceX > 0) {
                    lastScrollRight = true;
                }
                float currentWidth = right - left;
                float currentHeight = bottom - top;

                picMatrix.postTranslate(-distanceX, -distanceY);

                moveAllPoint(distanceX, distanceY);
//                // 图片高度大于view高度可以上下滑动
//                // distanceX右滑是负数，左滑是正数
//                // 先执行滑动，滑动到边界在回去
                if (currentWidth > width) {
                    distanceX = 0;
                    if (endLeft > 0) {
                        distanceX = endLeft;
                    }
                    if (endRight < width) {
                        distanceX = endRight - width;
                    }
                } else {
                    distanceX = -distanceX;
                }
                if (currentHeight > height) {
                    distanceY = 0;
                    if (endTop > 0) {
                        distanceY = endTop;
                    }
                    if (endBottom < height) {
                        distanceY = endBottom - height;
                    }
                } else {
                    distanceY = -distanceY;
                }
                endLeft = endLeft - distanceX;
                endTop = endTop - distanceY;
                endRight = endRight - distanceX;
                endBottom = endBottom - distanceY;
                picMatrix.postTranslate(-distanceX, -distanceY);
                moveAllPoint(distanceX, distanceY);
                setImageMatrix(picMatrix);
                currentRectF.set(endLeft, endTop, endRight, endBottom);
                Log.e("inMess", "onScroll:");
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (onClickListener != null) {
                    onClickListener.onClick(JustScaleImageView.this);
                }
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                scaleCenterX = e.getRawX();
                scaleCenterY = e.getRawY();
                toDoubleClickScale();
                Log.e("inMess", "onDouble:" + e.getRawX() + " " + e.getRawY());
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // 进入了缩放操作就不处理fling
                if (isEnterScale) {
                    return false;
                }
                // velocityX、velocityY为fling后每秒移动的像素
                float distanceX = e1.getRawX() - e2.getRawX();
                float distanceY = e1.getRawY() - e2.getRawY();
                // 以宽为1080为标准
                float realMoveX = getRealMove(1080f / width * Math.abs(distanceX) / 100 * pxToDp(velocityX));
                float realMoveY = getRealMove(((float) height / width * 1080f) / height * Math.abs(distanceY) / 100 * pxToDp(velocityY));
                startFlingAnimator(realMoveX, realMoveY);
                Log.e("inMess", "onFling:");
                return super.onFling(e1, e2, velocityX, velocityY);

            }

        });
    }

    // 添加一个指示点,基于x,y轴比例
    private void addNewProportion(float x, float y) {
        post(() -> {
            Rect picRect = getRect();
            if (picRect == null) return;
            addView.addPoint(picRect.left + picRect.width() * x, picRect.top + picRect.height() * y);
        });
    }

    // 添加一个指示点,基于x,y轴比例
    private void addNewProportion(PointData data) {
        post(() -> {
            Rect picRect = getRect();
            if (picRect == null) return;
            if (data.getX() > 1f) {
                data.setX(1f);
            }
            if (data.getY() > 1f) {
                data.setY(1f);
            }
            data.setRealX(picRect.left + picRect.width() * data.getX());
            data.setRealY(picRect.top + picRect.height() * data.getY());
            addView.addPointData(data);
        });
    }

    // 添加一个指示点,基于x,y轴比例
    private void addNewProportion(ArrayList<PointData> list) {
        post(() -> {
            Rect picRect = getRect();
            if (picRect == null) return;
            for (int i = 0; i < list.size(); i++) {
                PointData data = list.get(i);
                if (data.getX() > 1f) {
                    data.setX(1f);
                }
                if (data.getY() > 1f) {
                    data.setY(1f);
                }
                data.setRealX(picRect.left + picRect.width() * data.getX());
                data.setRealY(picRect.top + picRect.height() * data.getY());
            }
            addView.addPointData(list);
        });
    }

    @Nullable
    private Rect getRect() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return null;
        }
        drawableWidth = drawable.getIntrinsicWidth();
        drawableHeight = drawable.getIntrinsicHeight();
        float picScale;
        Rect picRect = new Rect();
        if (drawableWidth > drawableHeight) {
            if (getMeasuredWidth() == 0) {
                return null;
            }
            picScale = (float) drawableWidth / getMeasuredWidth();
            picRect.set(0, (int) (getMeasuredHeight() - (drawableHeight / picScale)) / 2, (int) (drawableWidth / picScale), (int) (getMeasuredHeight() + (drawableHeight / picScale)) / 2);
        } else {
            if (getMeasuredHeight() == 0) {
                return null;
            }
            picScale = (float) drawableHeight / getMeasuredHeight();
            picRect.set((int) (getMeasuredWidth() - (drawableWidth / picScale)) / 2, 0, (int) (getMeasuredWidth() + (drawableWidth / picScale)) / 2, (int) (drawableHeight / picScale));

        }
        return picRect;
    }

    // 移动单个指示点
    private void movePoint(int position, float distanceX, float distanceY) {
        if (addView != null) {
            addView.movePoint(position, distanceX, distanceY);
        }
    }

    // 移动指示点
    private void moveAllPoint(float distanceX, float distanceY) {
        if (addView != null) {
            addView.moveAllPoint(distanceX, distanceY);
        }
    }

    //  缩放时移动指示点
    private void scaleMovePoint(float scale) {
        if (addView != null) {
            for (int i = 0; i < addView.getPointS().size(); i++) {
                PointF point = addView.getPointS().get(i);
                float pointX = point.x;
                float pointY = point.y;
                float distanceX = pointX - scaleCenterX;
                float distanceY = pointY - scaleCenterY;
                movePoint(i, distanceX * (1 - scale), distanceY * (1 - scale));
            }

        }
    }

    // 设置图片加载type为Matrix
    private void setMatrixType() {
        if (!isSetType) {
            isSetType = true;
            Drawable drawable = getDrawable();
            if (drawable == null) {
                isSetType = false;
                return;
            }
            setScaleType(ScaleType.MATRIX);
            width = getWidth();
            height = getHeight();
            centerX = width / 2;
            centerY = height / 2;
            drawableWidth = drawable.getIntrinsicWidth();
            drawableHeight = drawable.getIntrinsicHeight();
            picMatrix.postTranslate((width - drawableWidth) / 2, (height - drawableHeight) / 2);
            originScale = Math.min((float) width / drawableWidth, (float) height / drawableHeight);
            picMatrix.postScale(originScale, originScale, (float) width / 2, (float) height / 2);
            float left = (width - originScale * drawableWidth) / 2;
            float top = (height - originScale * drawableHeight) / 2;
            float right = left + drawableWidth * originScale;
            float bottom = top + drawableHeight * originScale;
            originRectF = new RectF(left, top, right, bottom);
            currentRectF = new RectF(left, top, right, bottom);
        }
    }

    private float pxToDp(float px) {
        float scale = getResources().getDisplayMetrics().density;
        return px / scale;
    }


    @Override
    public boolean performClick() {
        return super.performClick();
    }

    OnClickListener onClickListener;

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        onClickListener = l;
    }


    boolean startChangePos;

    //是否可以拖拽view
    boolean canLayoutChange = true;
    // 是否抬起了手指,可以拖拽view的Boolean变了，要完全抬起手指，有一个新的滑动事件才能拖拽
    boolean isNewTouch;

    int touchType;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        setMatrixType();
        int pointCount = event.getPointerCount();
        if (currentRectF == null || !canLayoutChange) {
            return true;
        }
        touchType = event.getAction();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 按下时不知道是缩放还是滑动，重置值
                isEnterScale = false;
                lastDownX = event.getX();
                lastDownY = event.getY();
                lastDownRowX = event.getRawX();
                lastDownRowY = event.getRawY();
                startChangePos = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (currentRectF == null || currentRectF.equals(originRectF)) {
                    //要完全抬起手指，能够拖拽并且是一个新的滑动事件才能拖拽
                    if (!isDoubleDown && isNewTouch) {
                        Log.e("drag", "onTouchEvent: ");
                        float distanceRowX = event.getRawX() - lastDownRowX;
                        float distanceRowY = event.getRawY() - lastDownRowY;
                        float realX = Math.abs(distanceRowX);
                        float realY = Math.abs(distanceRowY);
                        if ((realY > touchSlop && realY > realX) || startChangePos) {
                            startChangePos = true;
                            currentLeft = getLeft() + distanceRowX;
                            currentTop = getTop() + distanceRowY;
                        }
                    }
                    lastDownRowX = event.getRawX();
                    lastDownRowY = event.getRawY();
                }
                break;
            case MotionEvent.ACTION_UP:
                isNewTouch = true;
                isOnScrolling = false;
                isOnZooming = false;
                isDoubleDown = false;
                isSameTimeScaling = false;

                break;
        }
        if (pointCount == 2) {
            isDoubleDown = true;
        }
        scaleDetector.onTouchEvent(event);
        simpleDetector.onTouchEvent(event);
        return true;
    }

    // fling最大速度、最小速度
    int maxSpeed = 8000, minSpeed = 20;

    // 限制fling的最小最大距离
    private float getRealMove(float value) {
        if (value > maxSpeed) {
            value = maxSpeed;
        } else if (value < -maxSpeed) {
            value = -maxSpeed;
        }
        if (value > 0) {
            if (value > minSpeed && value < 50) {
                value = 50;
            } else if (value < minSpeed) {
                value = 0;
            }
        }
        if (value < 0) {
            if (value < -minSpeed && value > -50) {
                value = -50;
            } else if (value > -minSpeed) {
                value = 0;
            }
        }
        return value;
    }


    float shouldMoveX, shouldMoveY;
    float lastMoveX, lastMoveY;
    ValueAnimator upAnimator;

    // 手指缩放中
    private void onScaling(float scale) {
        picMatrix.postScale(scale, scale, scaleCenterX, scaleCenterY);
        scaleMovePoint(scale);
        setImageMatrix(picMatrix);
//        // 计算缩放后的图片边界
        getEndValueRectF(currentRectF, scale);
    }

    // 手指缩放中
    private void onScalingNoMove(float scale) {
        picMatrix.postScale(scale, scale, scaleCenterX, scaleCenterY);
        setImageMatrix(picMatrix);
//        // 计算缩放后的图片边界
        getEndValueRectF(currentRectF, scale);
    }

    // 当前的图形范围缩放后的值
    private void getEndValueRectF(RectF rectF, float scale) {
        float left = scaleCenterX - (scaleCenterX - currentRectF.left) * scale;
        float top = scaleCenterY - (scaleCenterY - currentRectF.top) * scale;
        float right = scaleCenterX + (currentRectF.right - scaleCenterX) * scale;
        float bottom = scaleCenterY + (currentRectF.bottom - scaleCenterY) * scale;
        rectF.set(left, top, right, bottom);
    }


    // 手指离开后或者缩放到最小值时的缩放动画
    ValueAnimator minScaleAnimator;

    // 还原缩放为1
    float lastScale = 1f;
    // 是否正在缩放还原中
    boolean isOnZooming;

    // 还原缩放时的偏差值
    float needMoveX, needMoveY;

    // 从小到大还原缩放为1
    private void reductionScale(float needScale) {
        if (isInMess()) {
            return;
        }
        //还原缩放后，必须全部按压抬起才能继续下一次缩放
        isOnZooming = true;
        RectF targetRectF = new RectF();
        getEndValueRectF(targetRectF, needScale);

        needMoveX = (originRectF.left - targetRectF.left) / (needScale - 1);
        needMoveY = (originRectF.top - targetRectF.top) / (needScale - 1);

        // 需要还原标记点
        if (addView != null) {
            for (int i = 0; i < addView.getPointS().size(); i++) {
                final float[] lastValue = {0};
                PointF point = addView.getPointS().get(i);
                float pointX = point.x;
                float pointY = point.y;
                float distanceX = pointX - scaleCenterX;
                float distanceY = pointY - scaleCenterY;
                distanceX = -(originRectF.left - targetRectF.left) + distanceX * (1 - needScale);
                distanceY = -(originRectF.top - targetRectF.top) + distanceY * (1 - needScale);
                float moveScale = 0;
                if (distanceY != 0) {
                    moveScale = distanceX / distanceY;
                }
                ValueAnimator movePointAnim = ValueAnimator.ofFloat(distanceX);
                Log.e("pointMove", "reductionScale: " + "pointMove " + distanceX);
                float finalMoveScale = moveScale;
                int finalI = i;
                movePointAnim.addUpdateListener(animator -> {
                    float value = (float) animator.getAnimatedValue();
                    movePoint(finalI, value - lastValue[0], (value - lastValue[0]) / finalMoveScale);
                    lastValue[0] = value;
                });
                movePointAnim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        lastValue[0] = 0;
                    }
                });
                movePointAnim.setInterpolator(new DecelerateInterpolator());
                movePointAnim.setDuration(duration);
                movePointAnim.start();
            }
        }
        if (minScaleAnimator == null) {
            minScaleAnimator = ValueAnimator.ofFloat(1f, needScale);
            minScaleAnimator.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                float thisMoveX = (value - lastScale) * needMoveX;
                float thisMoveY = (value - lastScale) * needMoveY;
                float realScale = value / lastScale;
                picMatrix.preTranslate(thisMoveX, thisMoveY);
                onScalingNoMove(realScale);
                lastScale = value;
            });
            minScaleAnimator.setInterpolator(new DecelerateInterpolator());
            minScaleAnimator.setDuration(duration);
            minScaleAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    isOnZooming = false;
                    resetMatrix();
                    picMatrix.postTranslate((width - drawableWidth) / 2, (height - drawableHeight) / 2);
                    picMatrix.postScale(originScale, originScale, (float) width / 2, (float) height / 2);
                    setImageMatrix(picMatrix);
                }
            });
        } else {
            minScaleAnimator.setFloatValues(1f, needScale);
        }
        minScaleAnimator.start();
    }

    private void resetMatrix() {
        doubleScaleCount = 0;
        lastScrollLeft = true;
        lastScrollRight = true;
        lastScale = 1f;
        // onScaling有给currentRect赋值，这里重新赋值为了精度
        // 有可能有加载缩略图和原图的情况，需要重置rect
        if (currentRectF != null && originRectF != null) {
            currentRectF.left = originRectF.left;
            currentRectF.right = originRectF.right;
            currentRectF.top = originRectF.top;
            currentRectF.bottom = originRectF.bottom;
        }
        picMatrix.reset();
    }

    //此时的大小对比原大小需要缩放的scale
    private float getNeedScale() {
        float currentImageWidth = currentRectF.right - currentRectF.left;
        float currentImageHeight = currentRectF.bottom - currentRectF.top;
        float originImageWidth = originRectF.right - originRectF.left;
        float originImageHeight = originRectF.bottom - originRectF.top;
        if (currentImageHeight > originImageHeight && currentImageWidth > originImageWidth) {
            return Math.min(originImageHeight / currentImageHeight, originImageWidth / currentImageWidth);
        } else {
            return Math.max(originImageHeight / currentImageHeight, originImageWidth / currentImageWidth);
        }
    }

    // 获取当前的缩放值
    private float getCurrentScale() {
        float currentImageWidth = currentRectF.right - currentRectF.left;
        float originImageWidth = originRectF.right - originRectF.left;
        return currentImageWidth / originImageWidth;
    }


    // 拖拽动画
    ValueAnimator flingAnimator;
    // 最终拖拽的距离
    float finalDragX, finalDragY, finalMoveValue;
    float lastDragX, lastDragY;
    float dragScaleX, dragScaleY;

    //是否在滑动动画中
    boolean isOnFling;
    float allDragX, allDragY;

    private void startFlingAnimator(float moveX, float moveY) {
        if (isInMess()) {
            return;
        }
        allDragX = 0;
        allDragY = 0;
        // 这里moveX、moveY和scroll的数值正负是相反的
        float left = currentRectF.left, top = currentRectF.top, right = currentRectF.right, bottom = currentRectF.bottom;
        float currentWidth = right - left;
        float currentHeight = bottom - top;
        if (left + moveX > 0) {
            moveX = -left;
            lastScrollLeft = true;
        }
        if (right + moveX < width) {
            moveX = width - right;
            lastScrollRight = true;
        }
        if (top + moveY > 0) {
            moveY = -top;
        }
        if (bottom + moveY < height) {
            moveY = height - bottom;
        }
        if (currentWidth <= width) {
            moveX = 0;
        }
        if (currentHeight <= height) {
            moveY = 0;
        }
        finalDragX = moveX;
        finalDragY = moveY;
        lastDragX = 0;
        lastDragY = 0;
        if (finalDragX == 0 && finalDragY == 0) {
            return;
        }
        isOnFling = true;
        float endLeft = left + moveX, endTop = top + moveY, endRight = right + moveX, endBottom = bottom + moveY;
        currentRectF.set(endLeft, endTop, endRight, endBottom);

        finalMoveValue = Math.max(Math.abs(moveX), Math.abs(moveY));
        dragScaleX = Math.abs(moveX) / finalMoveValue;
        dragScaleY = Math.abs(moveY) / finalMoveValue;
        if (flingAnimator == null) {
            flingAnimator = ValueAnimator.ofFloat(finalMoveValue);
            flingAnimator.setInterpolator(new DecelerateInterpolator());
            flingAnimator.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                float dragX = 0, dragY = 0;
                if (finalDragX != 0 && value * dragScaleX <= Math.abs(finalDragX)) {
                    if (finalDragX > 0) {
                        dragX = value * dragScaleX - lastDragX;
                    } else {
                        dragX = -value * dragScaleX + lastDragX;
                    }
                }
                if (finalDragY != 0 && value * dragScaleY <= Math.abs(finalDragY)) {
                    if (finalDragY > 0) {
                        dragY = value * dragScaleY - lastDragY;
                    } else {
                        dragY = -value * dragScaleY + lastDragY;
                    }
                }
                lastDragX = value * dragScaleX;
                lastDragY = value * dragScaleY;
                allDragX += dragX;
                allDragY += dragY;
                moveAllPoint(-dragX, -dragY);
                picMatrix.postTranslate(dragX, dragY);
                setImageMatrix(picMatrix);
            });
            flingAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    isOnFling = false;
                }
            });
        } else {
            flingAnimator.cancel();
            flingAnimator.setFloatValues(finalMoveValue);
        }
        long duration;
        duration = (long) (finalMoveValue / 4);
        if (duration <= 300) {
            duration = 300;
        }
        flingAnimator.setDuration(duration);
        flingAnimator.start();
    }

    // 缩放后
    private void upToScale() {
        float needScale = getNeedScale();
        if (needScale > 1f) {
            reductionScale(needScale);
        } else {
            // 需要向各个方向移动的距离
            if (currentRectF.left > 0) {
                if (currentRectF.right < width) {
                    shouldMoveX = centerX - (currentRectF.right + currentRectF.left) / 2;
                } else {
                    if (currentRectF.right - currentRectF.left > width) {
                        shouldMoveX = -currentRectF.left;
                    } else {
                        shouldMoveX = centerX - (currentRectF.right + currentRectF.left) / 2;
                    }
                }
            } else {
                if (currentRectF.right < width) {
                    if (width - currentRectF.right > Math.abs(currentRectF.left)) {
                        shouldMoveX = centerX - (currentRectF.right + currentRectF.left) / 2;
                    } else {
                        shouldMoveX = width - currentRectF.right;
                    }
                }
            }
            if (currentRectF.left < 0 && currentRectF.right > width) {
                shouldMoveX = 0;
            }
            if (currentRectF.top > 0) {
                if (currentRectF.bottom < height) {
                    shouldMoveY = centerY - (currentRectF.bottom + currentRectF.top) / 2;
                } else {
                    if (currentRectF.bottom - currentRectF.top > height) {
                        shouldMoveY = -currentRectF.top;
                    } else {
                        shouldMoveY = centerY - (currentRectF.bottom + currentRectF.top) / 2;
                    }
                }
            } else {
                if (currentRectF.bottom < height) {
                    if (height - currentRectF.bottom > Math.abs(currentRectF.top)) {
                        shouldMoveY = centerY - (currentRectF.bottom + currentRectF.top) / 2;
                    } else {
                        shouldMoveY = height - currentRectF.bottom;
                    }
                }
            }
            if (currentRectF.top < 0 && currentRectF.bottom > height) {
                shouldMoveY = 0;
            }
            float left = currentRectF.left + shouldMoveX;
            float top = currentRectF.top + shouldMoveY;
            float right = currentRectF.right + shouldMoveX;
            float bottom = currentRectF.bottom + shouldMoveY;
            currentRectF.set(left, top, right, bottom);

            lastMoveX = 0;
            lastMoveY = 0;
            // 拿最大的移动距离为动画变动的值
            float allMove = Math.max(Math.abs(shouldMoveX), Math.abs(shouldMoveY));
            if (upAnimator == null) {
                upAnimator = ValueAnimator.ofFloat(allMove);
                upAnimator.addUpdateListener(animation -> {
                    float value = (float) animation.getAnimatedValue();
                    float dx, dy;
                    if (shouldMoveX > 0) {
                        dx = value - lastMoveX;
                    } else {
                        dx = -value + lastMoveX;
                    }
                    if (shouldMoveY > 0) {
                        dy = value - lastMoveY;
                    } else {
                        dy = -value + lastMoveY;
                    }
                    if (Math.abs(shouldMoveX) < value) {
                        dx = 0;
                    } else {
                        lastMoveX = value;
                    }
                    if (Math.abs(shouldMoveY) < value) {
                        dy = 0;
                    } else {
                        lastMoveY = value;
                    }
                    picMatrix.postTranslate(dx, dy);
                    moveAllPoint(-dx, -dy);
                    setImageMatrix(picMatrix);
                });
                upAnimator.setDuration(duration);
                upAnimator.setInterpolator(new DecelerateInterpolator());
            } else {
                upAnimator.setFloatValues(allMove);
            }
            upAnimator.start();
        }
    }


    // 双击缩放方法
    ValueAnimator targetAnimator;
    // 缩放次数为两次 singleBigScale*singleBigScale=maxScale;
    float maxScale = 2.56f, singleBigScale = 1.6f;
    // 双击放大的次数 因为缩放会有误差，所以加上次数判断
    int doubleScaleCount;
    float lastBingScale;
    // 是否是双击屏幕缩放、是否是缩放到父view宽度
    boolean isDoubleScale, isScaleToParentWidth;

    private void toDoubleClickScale() {
        if (isInMess()) {
            return;
        }
        //重新判断是否划到了边缘
        lastScrollLeft = false;
        lastScrollRight = false;
        float targetScale;
        float currentScale = getCurrentScale();
        float picWidth = currentRectF.right - currentRectF.left;
        float picHeight = currentRectF.bottom - currentRectF.top;
        lastBingScale = currentScale;
        // 这是属性是判断图片视图是否处于铺满宽度状态，这个状态左右滑动不拦截父类的监听事件
        isScaleToParentWidth = false;
        // 宽小于屏幕，高大于等于屏幕 缩放宽度和屏幕一样宽
        if (picWidth <= width) {
            scaleCenterX = width / 2;
        }
        if (picHeight <= height) {
            scaleCenterY = height / 2;
        }
        Log.e("currentScale", "toDoubleClickScale: " + currentScale);
        if (currentScale >= maxScale || doubleScaleCount == 2) {
            // 计算缩放中心,使缩放效果为向中心点缩放
            float topDistance = originRectF.top - currentRectF.top;
            float bottomDistance = currentRectF.bottom - originRectF.bottom;
            float leftDistance = originRectF.left - currentRectF.left;
            float rightDistance = currentRectF.right - originRectF.right;
            if (topDistance != 0 && bottomDistance != 0) {
                scaleCenterX = width / (leftDistance + rightDistance) * leftDistance;
                scaleCenterY = height / (topDistance + bottomDistance) * topDistance;
            } else {
                if (currentRectF.bottom > originRectF.bottom && topDistance == 0) {
                    scaleCenterY = 0;
                } else if (currentRectF.top < originRectF.top && bottomDistance == 0) {
                    scaleCenterY = height;
                }
                if (currentRectF.right > originRectF.right && leftDistance == 0) {
                    scaleCenterX = 0;
                } else if (currentRectF.left < originRectF.left && rightDistance == 0) {
                    scaleCenterX = width;
                }
            }

            reductionScale(1 / currentScale);
        } else {
            // 宽小于屏幕，高大于等于屏幕 缩放宽度和屏幕一样宽
            isDoubleScale = true;
            if (picWidth < width && picHeight >= height) {
                isScaleToParentWidth = true;
                targetScale = currentScale * (width / picWidth);
            } else {
                if (currentScale < singleBigScale) {
                    if (doubleScaleCount == 0) {
                        targetScale = singleBigScale;
                        doubleScaleCount++;
                    } else {
                        targetScale = maxScale;
                        doubleScaleCount = 2;
                    }
                } else {
                    doubleScaleCount++;
                    targetScale = maxScale;
                }
            }
            isOnZooming = true;
            if (targetAnimator == null) {
                targetAnimator = ValueAnimator.ofFloat(currentScale, targetScale);
                targetAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                targetAnimator.addUpdateListener(animation -> {
                    float value = (float) animation.getAnimatedValue();
                    float scale = value / lastBingScale;
                    onScaling(scale);
                    lastBingScale = value;
                });
                targetAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isDoubleScale = false;
                        isOnZooming = false;
                        if (isScaleToParentWidth) {
                            //精度可能会有误差，所以要设置一下最终值
                            currentRectF.left = 0;
                            currentRectF.right = width;
                        }
                    }
                });
                targetAnimator.setDuration(duration);
            } else {
                targetAnimator.setFloatValues(currentScale, targetScale);
            }
            targetAnimator.start();
        }
    }


    // 是否处于动画中
    private boolean isInMess() {
        return isDoubleScale || isOnZooming || isOnFling;
    }


    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        if (drawable != null && picMatrix != null) {
            setScaleType(ScaleType.FIT_CENTER);
            isSetType = false;
            resetMatrix();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimator(flingAnimator, minScaleAnimator, targetAnimator, upAnimator);
    }

    // 停止动画
    private void stopAnimator(Animator... animators) {
        for (Animator animator : animators) {
            if (animator != null) {
                animator.cancel();
            }
        }
    }


    public void addProportion(float x, float y) {
        if (getAddView() != null) {
            addNewProportion(x, y);
        }
    }

    private View getAddView() {
        if (addView == null) {
            try {
                FrameLayout group = (FrameLayout) getParent();
                addView = (ToAddView) group.getChildAt(1);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return addView;
    }


    public void addPointData(PointData data) {
        if (getAddView() != null) {
            addNewProportion(data);
        }
    }

    public void addPointData(ArrayList<PointData> list) {
        if (getAddView() != null) {
            addNewProportion(list);
        }
    }
}

