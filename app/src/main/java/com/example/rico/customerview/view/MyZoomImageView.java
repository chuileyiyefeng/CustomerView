package com.example.rico.customerview.view;

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
                float scaleCenterX = detector.getFocusX();
                float scaleCenterY = detector.getFocusY();
                picMatrix.postScale(scale, scale, scaleCenterX, scaleCenterY);
                setImageMatrix(picMatrix);
                // 计算缩放后的图片边界
                float left = scaleCenterX - (scaleCenterX - originRectF.left) * scale;
                float top = scaleCenterY - (scaleCenterY - originRectF.top) * scale;
                float right = scaleCenterX + (originRectF.right - scaleCenterX) * scale;
                float bottom = scaleCenterY + (originRectF.bottom - scaleCenterY) * scale;
                currentRectF.set(left, top, right, bottom);
                originRectF.set(left, top, right, bottom);
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
                picMatrix.postTranslate(-distanceX, -distanceY);
                setImageMatrix(picMatrix);
                disallParent();
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointCount = event.getPointerCount();
        if (pointCount == 2) {
            setMatrixType();
            disallParent();
            scaleDetector.onTouchEvent(event);
        } else {
//            simpleDetector.onTouchEvent(event);
        }
        return true;
    }

    private void disallParent() {
        getParent().requestDisallowInterceptTouchEvent(true);
    }

    float shouldMoveX, shouldMoveY;
    float lastMoveX, lastMoveY;
    ValueAnimator animator;

    private void upToScale() {
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
        originRectF.set(left, top, right, bottom);

        lastMoveX = 0;
        lastMoveY = 0;
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
                Log.e("allMove", "upToScale: " + dx + " " + shouldMoveX + " " + dy + " " + shouldMoveY);
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
            currentRectF = new RectF();
        }
    }
}
