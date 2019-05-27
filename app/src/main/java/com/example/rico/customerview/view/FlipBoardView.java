package com.example.rico.customerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tmp on 2019/5/25.
 */
public class FlipBoardView extends BaseCustomerView {
    public FlipBoardView(Context context) {
        super(context);
    }

    public FlipBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private Paint paint;
    private Matrix matrix;
    private Camera camera;

    float downX, downY, scale;

    //    是否是上下滑动
    boolean isFling;

    @Override
    protected void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        matrix = new Matrix();
        camera = new Camera();
        scale = context.getResources().getDisplayMetrics().density;
//        改变camera深度 Camera默认距离为-8
        camera.setLocation(0, 0, -scale * 10);
    }


    float downAngle, upAngle, lastMoveY;
    //    翻页的临界值
    float threshold = 90;


    //    当前绘制的图的下标
    int cPosition = 0;


    private ArrayList<Bitmap> bitmapList;


    public void setBitmapList(ArrayList<Bitmap> bitmapList) {
        this.bitmapList = bitmapList;
        invalidate();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                isFling = false;
                positionChange = false;
                break;
            case MotionEvent.ACTION_MOVE:
                isFling = Math.abs(event.getX() - downX) < Math.abs(event.getY() - downY);
                if (isFling) {
//                    向下翻页,角度为正数，绘制上半部分
                    float distance = downY - event.getY();
                    if (event.getY() > downY) {
                        if (upAngle != 0) {
                            if (upAngle - 1 > 0) {
                                upAngle -= 0.5;
                            } else {
                                upAngle = 0;
                            }
                        }
                        downAngle = (distance / (height / 2)) * 90;
                        if (Math.abs(downAngle) > threshold) {
                            type = turnDown;

                        } else {
                            type = backDown;
                        }
                    } else {
//                        向上翻页
                        if (downAngle != 0) {
                            if (downAngle + 1 < 0) {
                                downAngle += 0.5;
                            } else {
                                downAngle = 0;
                            }
                        }
                        upAngle = (distance / (height / 2)) * 90;
                        if (upAngle > threshold) {
//                            这里上滑大于90度了，position增加，type变为down，upAngle变为0
                            addPosition();
                            type=backDown;
                            downAngle=upAngle-180;
                            upAngle=0;
                            Log.e("move", "dispatchTouchEvent: "+downAngle );
                        } else {
                            type = backUp;
                        }
                    }
                }
                lastMoveY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                reduction();
                break;
        }
        return true;
    }

    //    在滑动过程中 Position的值只变动一次
    boolean positionChange;

    //    下标增加
    private void addPosition() {
        if (positionChange) {
            return;
        }
        if (cPosition < bitmapList.size() - 1) {
            cPosition++;
        }
        positionChange = true;
    }

    //      下标减少
    private void lessPosition() {
        if (positionChange) {
            return;
        }
        if (cPosition > 0) {
            cPosition--;
        }
        positionChange = true;
    }

    //    view变换状态 四种状态上下还原和上写翻页
    ValueAnimator mAnimator;
    int type;
    //    back前缀是还原，turn前缀是翻页,up是下一页，down是上一页
    final int turnUp = 1, turnDown = 2, backUp = 3, backDown = 4;

    private void reduction() {
        switch (type) {
            case turnUp:
                mAnimator = ValueAnimator.ofFloat(upAngle, 90);
                break;
            case backUp:
                mAnimator = ValueAnimator.ofFloat(upAngle, 0);
                break;
            case turnDown:
                mAnimator = ValueAnimator.ofFloat(downAngle, -90);
                break;
            case backDown:
                mAnimator = ValueAnimator.ofFloat(downAngle, 0);
                break;
        }
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                switch (type) {
                    case turnUp:
                    case backUp:
                        upAngle = value;
                        break;
                    case turnDown:
                    case backDown:
                        downAngle = value;
                        break;
                }
                invalidate();
            }
        });
        mAnimator.setDuration(300);
        mAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmapList != null) {
//            不涉及到翻页的绘制
            drawTop(canvas);
            drawBottom(canvas);
//            涉及到翻页的绘制
        }
    }

    private void drawTop(Canvas canvas) {
        canvas.save();
        canvas.clipRect(0, 0, width, height / 2);
        //        手指向下滑动，显示上一张图片
        if (downAngle != 0 && cPosition > 0) {
            canvas.drawBitmap(bitmapList.get(cPosition - 1), matrix, paint);
        }
        camera.save();
        camera.rotateX(downAngle);
        camera.getMatrix(matrix);
        camera.restore();

        matrix.postScale(1.0f, (90 - Math.abs(downAngle)) / 90f);
        matrix.postTranslate(width / 2, height / 2);
        matrix.preTranslate(-width / 2, -height / 2);

        canvas.concat(matrix);
        canvas.drawBitmap(bitmapList.get(cPosition), matrix, paint);
        canvas.restore();
    }

    private void drawBottom(Canvas canvas) {
        canvas.save();
        canvas.clipRect(0, height / 2, width, height);
        //        手指向下滑动，显示下一张图片
        if (upAngle != 0 && cPosition < bitmapList.size() - 1) {
            canvas.drawBitmap(bitmapList.get(cPosition + 1), matrix, paint);
        }
        camera.save();
        camera.rotateX(upAngle);
        camera.getMatrix(matrix);
        camera.restore();

        matrix.postScale(1.0f, (90 - upAngle) / 90f);
        matrix.postTranslate(width / 2, height / 2);
        matrix.preTranslate(-width / 2, -height / 2);

        canvas.concat(matrix);
        canvas.drawBitmap(bitmapList.get(cPosition), matrix, paint);
        canvas.restore();
    }
}
