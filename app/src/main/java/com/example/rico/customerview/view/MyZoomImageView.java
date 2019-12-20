package com.example.rico.customerview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.example.rico.customerview.bean.LayoutChangeListener;

/**
 * Created by Tmp on 2019/11/13.
 */
public class MyZoomImageView extends AppCompatImageView {
    ScaleGestureDetector scaleDetector;
    GestureDetector simpleDetector;
    Matrix picMatrix;
    float touchSlop;

    public MyZoomImageView(Context context) {
        this(context, null);
    }

    public MyZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
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
    float minScale = 1 / 0.6f;

    // 上次滑动是否到了左边界、右边界
    boolean lastScrollLeft, lastScrollRight;

    // 是否在滑动中
    boolean isOnScrolling;


    float lastDownX, lastDownY;
    float lastDownRowX, lastDownRowY;
    float currentLeft, currentTop;
    // 能否滑动view的位置
    boolean canChangeViewPosition = true;

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
                if (currentRectF == null || currentRectF.equals(originRectF)) {
                    disallowParent(false);
                    return false;
                }
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
                setImageMatrix(picMatrix);
                currentRectF.set(endLeft, endTop, endRight, endBottom);
                Log.e("inMess", "onScroll:");
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                disallowParent(false);
                if (onClickListener != null) {
                    onClickListener.onClick(MyZoomImageView.this);
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
                float realMoveY = getRealMove((height / width * 1080f) / height * Math.abs(distanceY) / 100 * pxToDp(velocityY));
                startFlingAnimator(realMoveX, realMoveY);
                Log.e("inMess", "onFling:");
                return super.onFling(e1, e2, velocityX, velocityY);

            }
        });
    }


    private void setMatrixType() {
        if (!isSetType) {
            isSetType = true;
            setScaleType(ScaleType.MATRIX);
            Drawable drawable = getDrawable();
            if (drawable == null) {
                isSetType = false;
                return;
            }
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


    public void setCanChangeViewPosition(boolean canChangeViewPosition) {
        this.canChangeViewPosition = canChangeViewPosition;
    }

    boolean startChangePos;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        disallowParent(true);
        setMatrixType();
        int pointCount = event.getPointerCount();
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
                float distanceX = event.getX() - lastDownX;
                float distanceY = event.getX() - lastDownY;
                boolean smallPic = currentRectF.width() <= originRectF.width();
                boolean similarPic = currentRectF.equals(originRectF);
                boolean isEndL = distanceX > 0 && lastScrollLeft && !isOnScrolling;
                boolean isEndR = distanceX < 0 && lastScrollRight && !isOnScrolling;
                if (smallPic || similarPic || isEndL || isEndR || isScaleToParentWidth) {
                    disallowParent(false);
                }
                if (canChangeViewPosition && currentRectF == null || currentRectF.equals(originRectF)) {
                    float distanceRowX = event.getRawX() - lastDownRowX;
                    float distanceRowY = event.getRawY() - lastDownRowY;
                    float realX = Math.abs(distanceRowX);
                    float realY = Math.abs(distanceRowY);
                    if ((realY > touchSlop && realY > realX) || startChangePos) {
                        startChangePos = true;
                        currentLeft = getLeft() + distanceRowX;
                        currentTop = getTop() + distanceRowY;
                        layout((int) currentLeft, (int) currentTop, (int) currentLeft + width, (int) currentTop + height);
                        if (layoutChangeListener != null) {
                            layoutChangeListener.change(distanceRowX, distanceRowY, (int) originRectF.top);
                        }
                    }
                    lastDownRowX = event.getRawX();
                    lastDownRowY = event.getRawY();
                }
                break;
            case MotionEvent.ACTION_UP:
                isOnScrolling = false;
                isOnScrolling = false;
                isOnZooming = false;
                isDoubleDown = false;
                isSameTimeScaling = false;
                if (startChangePos && layoutChangeListener != null) {
                    layoutChangeListener.release();
                }
                // 还原position
                break;
        }
        if (pointCount == 2) {
            isDoubleDown = true;
        }
        scaleDetector.onTouchEvent(event);
        simpleDetector.onTouchEvent(event);
        return true;
    }


    // 限制fling的最小最大距离
    private float getRealMove(float value) {
        if (value > 5000) {
            value = 5000;
        } else if (value < -5000) {
            value = -5000;
        }
        if (value > 0) {
            if (value > 20 && value < 50) {
                value = 50;
            } else if (value < 20) {
                value = 0;
            }
        }
        if (value < 0) {
            if (value < -20 && value > -50) {
                value = -50;
            } else if (value > -20) {
                value = 0;
            }
        }
        return value;
    }


    private void disallowParent(boolean b) {
        getParent().requestDisallowInterceptTouchEvent(b);
    }

    float shouldMoveX, shouldMoveY;
    float lastMoveX, lastMoveY;
    ValueAnimator upAnimator;

    // 手指缩放中
    private void onScaling(float scale) {
        picMatrix.postScale(scale, scale, scaleCenterX, scaleCenterY);
        setImageMatrix(picMatrix);
//        // 计算缩放后的图片边界
        currentRectF = getEndValueRectF(currentRectF, scale);
    }

    // 当前的图形范围缩放后的值
    private RectF getEndValueRectF(RectF rectF, float scale) {
        float left = scaleCenterX - (scaleCenterX - currentRectF.left) * scale;
        float top = scaleCenterY - (scaleCenterY - currentRectF.top) * scale;
        float right = scaleCenterX + (currentRectF.right - scaleCenterX) * scale;
        float bottom = scaleCenterY + (currentRectF.bottom - scaleCenterY) * scale;
        rectF.set(left, top, right, bottom);
        return rectF;
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
        targetRectF = getEndValueRectF(targetRectF, needScale);
        needMoveX = (originRectF.left - targetRectF.left) / (needScale - 1);
        needMoveY = (originRectF.top - targetRectF.top) / (needScale - 1);
        if (minScaleAnimator == null) {
            minScaleAnimator = ValueAnimator.ofFloat(1f, needScale);
            minScaleAnimator.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                float thisMoveX = (value - lastScale) * needMoveX;
                float thisMoveY = (value - lastScale) * needMoveY;
                float realScale = value / lastScale;
                picMatrix.preTranslate(thisMoveX, thisMoveY);
                onScaling(realScale);
                lastScale = value;
            });
            minScaleAnimator.setInterpolator(new DecelerateInterpolator());
            minScaleAnimator.setDuration(200);
            minScaleAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    doubleScaleCount = 0;
                    lastScrollLeft = true;
                    lastScrollRight = true;
                    lastScale = 1f;
                    // onScaling有给currentRect赋值，这里重新赋值为了精度
                    currentRectF.left = originRectF.left;
                    currentRectF.right = originRectF.right;
                    currentRectF.top = originRectF.top;
                    currentRectF.bottom = originRectF.bottom;
                    picMatrix.reset();
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
                    setImageMatrix(picMatrix);
                });
                upAnimator.setDuration(200);
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
                        if (isScaleToParentWidth) {
                            //精度可能会有误差，所以要设置一下最终值
                            currentRectF.left = 0;
                            currentRectF.right = width;
                        }
                    }
                });
                targetAnimator.setDuration(200);
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
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimator(flingAnimator, minScaleAnimator, targetAnimator, upAnimator);
    }

    private void stopAnimator(Animator... animators) {
        for (Animator animator : animators) {
            if (animator != null) {
                animator.cancel();
            }
        }
    }

    LayoutChangeListener layoutChangeListener;

    public void setLayoutChangeListener(LayoutChangeListener layoutChangeListener) {
        this.layoutChangeListener = layoutChangeListener;
    }
}
