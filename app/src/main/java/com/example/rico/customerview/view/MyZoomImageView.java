package com.example.rico.customerview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Tmp on 2019/11/13.
 */
public class MyZoomImageView extends AppCompatImageView {
    ScaleGestureDetector scaleDetector;
    GestureDetector simpleDetector;
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

    // 单次缩放值、缩放中心X、缩放中心Y
    float scaleCenterX, scaleCenterY;

    // 是否是同一次缩放、是否继续缩放
    boolean isSameTimeScaling, isContinuedScaling = true;

    private void initGesture() {
        picMatrix = new Matrix();
        scaleDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scale = detector.getScaleFactor();
                if (!isSameTimeScaling) {
                    scaleCenterX = detector.getFocusX();
                    scaleCenterY = detector.getFocusY();
                    isSameTimeScaling = true;
                }
                float needScale = getNeedScale();
                if (needScale < 1) {
                    onScaling(scale);
                } else {
                    if (needScale > minScale) {
                        reductionScale();
                    } else {
                        onScaling(scale);
                    }
                }
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                super.onScaleEnd(detector);
                upToScale();
            }
        });
        simpleDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                float left = currentRectF.left, top = currentRectF.top, right = currentRectF.right, bottom = currentRectF.bottom;
                float endLeft = left - distanceX, endTop = top - distanceY, endRight = right - distanceX, endBottom = bottom - distanceY;
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
                disallowParent();
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // fling后每秒移动的速度
                float distanceX = e1.getRawX() - e2.getRawX();
                float distanceY = e1.getRawY() - e2.getRawY();
                Log.e("distance", "onFling: " + distanceX + " " + distanceY);
                //1080p 1818 2482
                // 以宽为1080为标准
                float realMoveX = getRealMove(1080f / width * Math.abs(distanceX) / 1000 * pxToDp(velocityX));
                float realMoveY = getRealMove((height / width * 1080f) / height * Math.abs(distanceY) / 1000 * pxToDp(velocityY));
                startFlingAnimator(realMoveX, realMoveY);
                return super.onFling(e1, e2, velocityX, velocityY);

            }
        });
    }

    private float pxToDp(float px) {
        float scale = getResources().getDisplayMetrics().density;
        return px / scale;
    }

    // 限制拖拽的最小最大距离
    private float getRealMove(float value) {
        if (value > 5000) {
            value = 5000;
        } else if (value < -5000) {
            value = -5000;
        }
        if (value > 0 && value < 50) {
            value = 50;
        }
        if (value < 0 && value > -50) {
            value = -50;
        }
        return value;
    }


    @Override
    public boolean performClick() {
        return super.performClick();
    }

    boolean isDoubleDown;
    float minScale = 1 / 0.6f;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        disallowParent();
        int pointCount = event.getPointerCount();
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                isContinuedScaling = true;
                isDoubleDown = false;
                isSameTimeScaling = false;
                break;
        }
        // 两个按下的点、不在缩放动画中、在同一次缩放操作
        if (pointCount == 2 && !onZooming && isContinuedScaling) {
            isDoubleDown = true;
            setMatrixType();
            disallowParent();
            scaleDetector.onTouchEvent(event);
        } else {
            //进行缩放操作后，最后抬起时会视为滑动操作，需要添加判断条件
            if (currentRectF != null && !isDoubleDown) {
                simpleDetector.onTouchEvent(event);
            }
        }
        return true;
    }

    private void disallowParent() {
        getParent().requestDisallowInterceptTouchEvent(true);
    }

    float shouldMoveX, shouldMoveY;
    float lastMoveX, lastMoveY;
    ValueAnimator animator;

    // 手指缩放中
    private void onScaling(float scale) {
        picMatrix.postScale(scale, scale, scaleCenterX, scaleCenterY);
        setImageMatrix(picMatrix);
//        // 计算缩放后的图片边界
        currentRectF = getEndValueRectF(currentRectF, scale);
    }

    // 当前的矩形范围缩放后的值
    private RectF getEndValueRectF(RectF rectF, float scale) {
        float left = scaleCenterX - (scaleCenterX - currentRectF.left) * scale;
        float top = scaleCenterY - (scaleCenterY - currentRectF.top) * scale;
        float right = scaleCenterX + (currentRectF.right - scaleCenterX) * scale;
        float bottom = scaleCenterY + (currentRectF.bottom - scaleCenterY) * scale;
        rectF.set(left, top, right, bottom);
        return rectF;
    }


    // 手指离开后或者缩放到最小值时的缩放动画
    ValueAnimator scaleAnimator;

    // 还原缩放为1
    float lastScale = 1f;
    // 是否正在缩放还原中
    boolean onZooming;

    // 还原缩放时的偏差值
    float needMoveX, needMoveY;


    // 还原缩放为1
    private void reductionScale() {
        onZooming = true;
        float needScale = getNeedScale();
        RectF targetRectF = new RectF();
        targetRectF = getEndValueRectF(targetRectF, needScale);
        needMoveX = (originRectF.left - targetRectF.left) / (needScale - 1);
        needMoveY = (originRectF.top - targetRectF.top) / (needScale - 1);
        if (scaleAnimator == null) {
            scaleAnimator = ValueAnimator.ofFloat(1f, needScale);
            scaleAnimator.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                float thisMoveX = (value - lastScale) * needMoveX;
                float thisMoveY = (value - lastScale) * needMoveY;
                float realScale = value / lastScale;
                picMatrix.preTranslate(thisMoveX, thisMoveY);
                onScaling(realScale);
                lastScale = value;
            });
            scaleAnimator.setInterpolator(new DecelerateInterpolator());
            scaleAnimator.setDuration(300);
            scaleAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    lastScale = 1f;
                    // onScaling有给currentRect赋值，这里重新赋值为了精度
                    currentRectF.left = originRectF.left;
                    currentRectF.right = originRectF.right;
                    currentRectF.top = originRectF.top;
                    currentRectF.bottom = originRectF.bottom;
                    picMatrix.reset();
                    picMatrix.postTranslate((width - drawableWidth) / 2, (height - drawableHeight) / 2);
                    setImageMatrix(picMatrix);
                    onZooming = false;
                    // 此时没有抬起全部按压点，不能继续缩放
                    if (isSameTimeScaling) {
                        isContinuedScaling = false;
                    }
                }
            });
        } else {
            scaleAnimator.setFloatValues(1f, needScale);
        }
        scaleAnimator.start();
    }


    //此时的大小对比原大小需要缩放的scale
    private float getNeedScale() {
        float currentImageWidth = currentRectF.right - currentRectF.left;
        float currentImageHeight = currentRectF.right - currentRectF.left;
        float originImageWidth = originRectF.right - originRectF.left;
        float originImageHeight = originRectF.right - originRectF.left;
        if (currentImageHeight > originImageHeight && currentImageWidth > originImageWidth) {
            return Math.min(originImageHeight / currentImageHeight, originImageWidth / currentImageWidth);
        } else {
            return Math.max(originImageHeight / currentImageHeight, originImageWidth / currentImageWidth);
        }
    }


    // 拖拽动画
    ValueAnimator flingAnimator;
    // 最终拖拽的距离
    float finalDragX, finalDragY, finalMoveValue;
    float lastDragX, lastDragY;
    float dragScaleX, dragScaleY;

    private void startFlingAnimator(float moveX, float moveY) {
        // 这里moveX、moveY和scroll的数值正负是相反的
        float left = currentRectF.left, top = currentRectF.top, right = currentRectF.right, bottom = currentRectF.bottom;
        float currentWidth = right - left;
        float currentHeight = bottom - top;
        if (left + moveX > 0) {
            moveX = -left;
        }
        if (right + moveX < width) {
            moveX = width - right;
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
                if (finalDragX != 0 && value <= Math.abs(finalDragX)) {
                    if (finalDragX > 0) {
                        dragX = value * dragScaleX - lastDragX;
                    } else {
                        dragX = -value * dragScaleX + lastDragX;
                    }
                }
                if (finalDragY != 0 && value <= Math.abs(finalDragY)) {
                    if (finalDragY > 0) {
                        dragY = value * dragScaleY - lastDragY;
                    } else {
                        dragY = -value * dragScaleY + lastDragY;
                    }
                }
                lastDragX = value * dragScaleX;
                lastDragY = value * dragScaleY;
                picMatrix.postTranslate(dragX, dragY);
                Log.e("startFlingAnimator", "startFlingAnimator: " + dragX + " " + dragY + " " + currentRectF.toString());
                setImageMatrix(picMatrix);
            });
            flingAnimator.setDuration(300);
        } else {
            flingAnimator.setFloatValues(finalMoveValue);
        }
        flingAnimator.start();
    }

    // 缩放后
    private void upToScale() {
        if (getNeedScale() > 1f) {
            reductionScale();
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
            if (animator == null) {
                animator = ValueAnimator.ofFloat(allMove);
                animator.addUpdateListener(animation -> {
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
                animator.setDuration(200);
                animator.setInterpolator(new DecelerateInterpolator());
            } else {
                animator.setFloatValues(allMove);
            }
            animator.start();
        }
    }


    boolean isSetType;
    //view宽高 图片宽高
    int width, height, drawableWidth, drawableHeight;
    // 图片原始边界
    RectF originRectF, currentRectF, matrixRect;
    int centerX, centerY;

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
            float originScale = Math.min((float) width / drawableWidth, (float) height / drawableHeight);
            float left = (width - originScale * drawableWidth) / 2;
            float top = (height - originScale * drawableHeight) / 2;
            float right = left + drawableWidth * originScale;
            float bottom = top + drawableHeight * originScale;
            originRectF = new RectF(left, top, right, bottom);
            currentRectF = new RectF(left, top, right, bottom);
            matrixRect = new RectF(0, 0, drawableWidth, drawableHeight);
        }
    }
}
