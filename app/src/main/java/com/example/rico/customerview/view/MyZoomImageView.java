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
    float scale, scaleCenterX, scaleCenterY;

    // 是否是同一次缩放
    boolean isSameTimeScaling;

    private void initGesture() {
        picMatrix = new Matrix();
        scaleDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scale = detector.getScaleFactor();
                if (!isSameTimeScaling) {
                    scaleCenterX = detector.getFocusX();
                    scaleCenterY = detector.getFocusY();
                    isSameTimeScaling = true;
                }
                float needScale = getNeedScale();
                if (needScale < 1) {
                    onScaling(scale);
                } else {
                    if (needScale > 1 / 0.6f) {
                        reductionScale();
                        return false;
                    } else {
                        onScaling(scale);
                    }
                }
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                super.onScaleEnd(detector);
                //缩放完成后
                upToScale();
            }

        });
        simpleDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (currentRectF != null) {
                    float left = currentRectF.left, top = currentRectF.top, right = currentRectF.right, bottom = currentRectF.bottom;
                    currentRectF.set(left - distanceX, top - distanceY, right - distanceX, bottom - distanceY);
                    picMatrix.postTranslate(-distanceX, -distanceY);
                    setImageMatrix(picMatrix);
                    disallowParent();
                }
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return super.onFling(e1, e2, velocityX, velocityY);
            }

        });
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        int pointCount = event.getPointerCount();
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                onReductioning = false;
                isSameTimeScaling = false;
                break;
        }
        if (pointCount == 2 && !onReductioning) {
            setMatrixType();
            disallowParent();
            scaleDetector.onTouchEvent(event);
        } else {
            //进行缩放操作后，最后抬起时会视为滑动操作，需要添加判断条件
//            simpleDetector.onTouchEvent(event);
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
    boolean onReductioning;

    // 还原缩放时的偏差值
    float needMoveX, needMoveY;

    float allNeedMoveX, allNeedMoveY;

    // 还原缩放为1
    private void reductionScale() {
        float needScale = getNeedScale();
        RectF targetRectF = new RectF();
        targetRectF = getEndValueRectF(targetRectF, needScale);
        float[] currentValue = new float[9];
        picMatrix.getValues(currentValue);
        needMoveX = (originRectF.left - targetRectF.left) / (needScale - 1);
        needMoveY = (originRectF.top - targetRectF.top) / (needScale - 1);
        if (scaleAnimator == null) {
            scaleAnimator = ValueAnimator.ofFloat(1f, needScale);
            scaleAnimator.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                float thisMoveX = (value - lastScale) * needMoveX;
                float thisMoveY = (value - lastScale) * needMoveY;
                allNeedMoveX += thisMoveX;
                allNeedMoveY += thisMoveY;
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
                }
            });
        } else {
            scaleAnimator.setFloatValues(1f, needScale);
        }
        scaleAnimator.start();
        onReductioning = true;
    }


    //此时的大小对比原大小需要缩放的scale
    private float getNeedScale() {
        float currentImageWidth = currentRectF.right - currentRectF.left;
        float currentImageHeight = currentRectF.right - currentRectF.left;
        float originImageWidth = originRectF.right - originRectF.left;
        float originImageHeight = originRectF.right - originRectF.left;
        if (currentImageHeight > originImageHeight && currentImageWidth > originImageWidth) {
            //如果是放大的状态，要缩小，不处理
            return 1f;
        } else {
            return Math.max(originImageHeight / currentImageHeight, originImageWidth / currentImageWidth);
        }

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
    RectF originRectF, currentRectF;
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
        }
    }
}
