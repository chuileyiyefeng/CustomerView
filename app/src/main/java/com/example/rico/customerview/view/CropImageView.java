package com.example.rico.customerview.view;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * @Description: 缩放移动裁剪imageView
 * @Author: pan yi
 * @Date: 2021/12/23
 */
public class CropImageView extends AppCompatImageView {
    ScaleGestureDetector scaleDetector;
    GestureDetector simpleDetector;
    Matrix picMatrix;
    float touchSlop;

    public CropImageView(Context context) {
        this(context, null);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyleAttr) {
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
    //view 要显示的宽高
    int showWidth, showHeight;
    // 图片原始边界
    RectF originRectF, currentRectF;
    // 显示区域的rect 图片显示真正的rectF 图片原始rectF
    RectF showRectF, picRealRectF, picOriginRealRectF;
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
        showWidth = w;
        showHeight = h;
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
                    return false;
                } else {
                    onScaling(scale);
                }
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                super.onScaleEnd(detector);
                scaleCenterX = detector.getFocusX();
                scaleCenterY = detector.getFocusY();
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
//                // 图片高度大于view高度可以上下滑动
//                // distanceX右滑是负数，左滑是正数
//                // 先执行滑动，滑动到边界在回去

                if (picRealRectF != null && showRectF != null) {

                    if (picRealRectF.left - distanceX > showRectF.left) {
                        //手指向右 x为负数
                        distanceX = picRealRectF.left - showRectF.left;
                    }
                    if (picRealRectF.top - distanceY > showRectF.top) {
                        distanceY = picRealRectF.top - showRectF.top;
                    }
                    if (picRealRectF.right - distanceX < showRectF.right) {
                        distanceX = picRealRectF.right - showRectF.right;
                    }
                    if (picRealRectF.bottom - distanceY < showRectF.bottom) {
                        distanceY = picRealRectF.bottom - showRectF.bottom;
                    }
                }
                endLeft = endLeft - distanceX;
                endTop = endTop - distanceY;
                endRight = endRight - distanceX;
                endBottom = endBottom - distanceY;
                picMatrix.postTranslate(-distanceX, -distanceY);
                setImageMatrix(picMatrix);
                currentRectF.set(endLeft, endTop, endRight, endBottom);
                float picRealRectLeft = picRealRectF.left - distanceX;
                float picRealRectTop = picRealRectF.top - distanceY;
                float picRealRectRight = picRealRectF.right - distanceX;
                float picRealRectBottom = picRealRectF.bottom - distanceY;
                picRealRectF.set(picRealRectLeft, picRealRectTop, picRealRectRight, picRealRectBottom);
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                disallowParent(false);
                if (onClickListener != null) {
                    onClickListener.onClick(CropImageView.this);
                }
                return false;
            }
        });
    }


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
            picMatrix.postTranslate((width - drawableWidth) / 2f, (height - drawableHeight) / 2f);
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
    
    // 是否抬起了手指,可以拖拽view的Boolean变了，要完全抬起手指，有一个新的滑动事件才能拖拽
    boolean isNewTouch;

    int touchType;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        disallowParent(true);
        setMatrixType();
        int pointCount = event.getPointerCount();
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
                float distanceX = event.getX() - lastDownX;
                boolean smallPic = currentRectF.width() <= originRectF.width();
                boolean similarPic = currentRectF.equals(originRectF);
                boolean isEndL = distanceX > 0 && lastScrollLeft;
                boolean isEndR = distanceX < 0 && lastScrollRight;
                if (smallPic || similarPic || isEndL || isEndR || isScaleToParentWidth) {
                    Log.e("disallow", "onTouchEvent: ");
                    disallowParent(false);
                }
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
//                            if (layoutChangeListener != null) {
//                                layoutChangeListener.change(distanceRowX, distanceRowY, (int) event.getRawY());
//                            }
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
//                if (startChangePos && layoutChangeListener != null) {
//                    layoutChangeListener.release();
//                }
                break;
        }
        if (pointCount == 2) {
            isDoubleDown = true;
        }
        scaleDetector.onTouchEvent(event);
        simpleDetector.onTouchEvent(event);
        return true;
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
        getEndValueRectF(currentRectF, scale);
        getEndValueRectF(picRealRectF, scale);
    }

    // 当前的图形范围缩放后的值
    private void getEndValueRectF(RectF rectF, float scale) {
        float left = scaleCenterX - (scaleCenterX - rectF.left) * scale;
        float top = scaleCenterY - (scaleCenterY - rectF.top) * scale;
        float right = scaleCenterX + (rectF.right - scaleCenterX) * scale;
        float bottom = scaleCenterY + (rectF.bottom - scaleCenterY) * scale;
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


    float testX, testY;

    // 从小到大还原缩放为原图大小
    private void reductionScale(float needScale) {
        Log.e("inMess", "onNeedScale: " + minScale + " " + needScale);
        if (isInMess()) {
            return;
        }
        //还原缩放后，必须全部按压抬起才能继续下一次缩放
        isOnZooming = true;
        RectF targetRectF = new RectF();
        targetRectF.set(picRealRectF);
        getEndValueRectF(targetRectF, needScale);
        needMoveX = picOriginRealRectF.left - targetRectF.left;
        needMoveY = picOriginRealRectF.top - targetRectF.top;
        Log.e("reductionScale", "scaling value " + targetRectF.toString() + " " + needMoveX + " " + needMoveY);
        testX = 0;
        testY = 0;
        needMoveX = needMoveX / (needScale - 1f);
        needMoveY = needMoveY / (needScale - 1f);
        if (minScaleAnimator == null) {
            minScaleAnimator = ValueAnimator.ofFloat(1f, needScale);
            minScaleAnimator.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                float thisMoveX = (value - lastScale) * needMoveX;
                float thisMoveY = (value - lastScale) * needMoveY;
                float realScale = value / lastScale;
                picMatrix.postTranslate(thisMoveX, thisMoveY);
                scaleCenterX += thisMoveX;
                scaleCenterY += thisMoveY;
                onScaling(realScale);
                testX += thisMoveX;
                testY += thisMoveY;
//                Log.e("reductionScale", "scaling inside" + testX + " " + testY + " " + needScale + " " + value);
                lastScale = value;
            });
            minScaleAnimator.setInterpolator(new DecelerateInterpolator());
            minScaleAnimator.setDuration(200);
            minScaleAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    Log.e("reductionScale end", "end " + testX + " " + testY + " " + picRealRectF.toString() + " " + picOriginRealRectF.toString());
                    isOnZooming = false;
                    resetMatrix();
                    picMatrix.postTranslate((width - drawableWidth) / 2f, (height - drawableHeight) / 2f);
                    picMatrix.postScale(originScale, originScale, (float) width / 2, (float) height / 2);
                    picRealRectF.set(picOriginRealRectF);
                    currentRectF.set(originRectF);
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
            currentRectF.set(originRectF);
        }
        picMatrix.reset();
    }

    //此时的大小对比原大小需要缩放的scale
    private float getNeedScale() {
        float currentImageWidth = picRealRectF.right - picRealRectF.left;
        float currentImageHeight = picRealRectF.bottom - picRealRectF.top;
        float originImageWidth = picOriginRealRectF.right - picOriginRealRectF.left;
        float originImageHeight = picOriginRealRectF.bottom - picOriginRealRectF.top;
        if (currentImageHeight > originImageHeight && currentImageWidth > originImageWidth) {
            return Math.min(originImageHeight / currentImageHeight, originImageWidth / currentImageWidth);
        } else {
            return Math.max(originImageHeight / currentImageHeight, originImageWidth / currentImageWidth);
        }
    }


    // 获取图片的缩放值
    private float getPicCurrentScale() {
        float currentImageWidth = picRealRectF.width();
        float originImageWidth = getDrawable().getIntrinsicWidth();
        return currentImageWidth / originImageWidth;
    }

    // 拖拽动画
    ValueAnimator flingAnimator;


    //是否在滑动动画中
    boolean isOnFling;


    // 缩放后
    private void upToScale() {
        float needScale = getNeedScale();
        if (needScale > 1f) {
            reductionScale(needScale);
        } else {
            // 需要向各个方向移动的距离
            shouldMoveX = 0;
            shouldMoveY = 0;
            if (picRealRectF.left > showRectF.left) {
                shouldMoveX = showRectF.left - picRealRectF.left;
            } else if (picRealRectF.right < showRectF.right) {
                shouldMoveX = showRectF.right - picRealRectF.right;
            }
            if (picRealRectF.top > showRectF.top) {
                shouldMoveY = showRectF.top - picRealRectF.top;
            } else if (picRealRectF.bottom < showRectF.bottom) {
                shouldMoveY = showRectF.bottom - picRealRectF.bottom;
            }
            if (shouldMoveX == 0 && shouldMoveY == 0) {
                return;
            }
            float currentLeft = currentRectF.left + shouldMoveX;
            float currentTop = currentRectF.top + shouldMoveY;
            float currentRight = currentRectF.right + shouldMoveX;
            float currentBottom = currentRectF.bottom + shouldMoveY;
            currentRectF.set(currentLeft, currentTop, currentRight, currentBottom);

            float picRealLeft = picRealRectF.left + shouldMoveX;
            float picRealTop = picRealRectF.top + shouldMoveY;
            float picRealRight = picRealRectF.right + shouldMoveX;
            float picRealBottom = picRealRectF.bottom + shouldMoveY;
            picRealRectF.set(picRealLeft, picRealTop, picRealRight, picRealBottom);

            lastMoveX = 0;
            lastMoveY = 0;
            // 拿最大的移动距离为动画变动的值
            float allMove = Math.abs(Math.max(Math.abs(shouldMoveX), Math.abs(shouldMoveY)));
            float xScale = shouldMoveX / allMove;
            float ySCale = shouldMoveY / allMove;// 负数/正数 = 负数
            if (upAnimator == null) {
                upAnimator = ValueAnimator.ofFloat(allMove);
                upAnimator.addUpdateListener(animation -> {
                    float value = (float) animation.getAnimatedValue();
                    float dx = (value - lastMoveX) * xScale;
                    float dy = (value - lastMoveY)
                            * ySCale;// 正数*负数=负数

                    if (shouldMoveX < 0) {
                        dx = -Math.abs(dx);
                    } else {
                        dx = Math.abs(dx);
                    }
                    if (shouldMoveY < 0) {
                        dy = -Math.abs(dy);
                    } else {
                        dy = Math.abs(dy);
                    }
                    picMatrix.postTranslate(dx, dy);
                    setImageMatrix(picMatrix);
                    lastMoveX = value;
                    lastMoveY = value;
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
    // 双击放大的次数 因为缩放会有误差，所以加上次数判断
    int doubleScaleCount;
    // 是否是双击屏幕缩放、是否是缩放到父view宽度
    boolean isDoubleScale, isScaleToParentWidth;


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
            //缩放
        }
    }

    public void setCropWandH(int width, int height) {
        showWidth = width;
        showHeight = height;
        setMatrixChangeType();
    }

    private void setMatrixChangeType() {
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
        picMatrix.postTranslate((width - drawableWidth) / 2f, (height - drawableHeight) / 2f);
        originScale = Math.max((float) showWidth / drawableWidth, (float) showHeight / drawableHeight);
        if (originScale > 1) {
            minScale = originScale / 0.8f;
        } else if (originScale < 1) {
            minScale = 1 / (originScale * 0.8f);
        } else {
            minScale = 1f;
        }
        picMatrix.postScale(originScale, originScale, (float) width / 2, (float) height / 2);
        float left = (width - originScale * drawableWidth) / 2;
        float top = (height - originScale * drawableHeight) / 2;
        float right = left + drawableWidth * originScale;
        float bottom = top + drawableHeight * originScale;
        originRectF = new RectF(left, top, right, bottom);
        currentRectF = new RectF(left, top, right, bottom);
        showRectF = new RectF((width - showWidth) / 2f, (height - showHeight) / 2f, (width - showWidth) / 2f + showWidth, (height - showHeight) / 2f + showHeight);
        picRealRectF = new RectF((width - drawableWidth * originScale) / 2f, (height - drawableHeight * originScale) / 2f, (width - drawableWidth * originScale) / 2f + drawableWidth * originScale, (height - drawableHeight * originScale) / 2f + drawableHeight * originScale);
        picOriginRealRectF = new RectF((width - drawableWidth * originScale) / 2f, (height - drawableHeight * originScale) / 2f, (width - drawableWidth * originScale) / 2f + drawableWidth * originScale, (height - drawableHeight * originScale) / 2f + drawableHeight * originScale);
        setImageMatrix(picMatrix);
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

    // 截取显示框里的图片
    public Bitmap getCropBitmap() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap = drawableToBitmap(drawable);
        if (showRectF == null) {
            return bitmap;
        }
        float picCurrentScale = getPicCurrentScale();
        float cropWidth = showRectF.width() / picCurrentScale;
        float cropHeight = showRectF.height() / picCurrentScale;
        float leftDistance = showRectF.left - picRealRectF.left;
        float topDistance = showRectF.top - picRealRectF.top;
        float startX = leftDistance / picCurrentScale;
        float startY = topDistance / picCurrentScale;
        //修复精度丢失问题
        if (startX + cropWidth > bitmap.getWidth()) {
            cropWidth = bitmap.getWidth() - startX;
            if (cropWidth <= 0) {
                return bitmap;
            }
        }
        if (startY + cropHeight > bitmap.getHeight()) {
            cropHeight = bitmap.getHeight() - startY;
            if (cropHeight <= 0) {
                return bitmap;
            }
        }
        // 0 0  820  1084  1080 1084
        Log.e("getCropBitmap", startX + " " + startY + " " + cropWidth + " " + cropHeight + " " + bitmap.getWidth() + " " + bitmap.getHeight());
        return Bitmap.createBitmap(bitmap, (int) startX, (int) startY, (int) cropWidth, (int) cropHeight);
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }


}

