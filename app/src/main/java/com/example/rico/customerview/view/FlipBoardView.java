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

    float downX, downY, scale, touchSlop;


    @Override
    protected void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        matrix = new Matrix();
        camera = new Camera();
        bitmapList = new ArrayList<>();
        scale = context.getResources().getDisplayMetrics().density;
//        改变camera深度 Camera默认距离为-8
        camera.setLocation(0, 0, -scale * 10);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }


    private ArrayList<Bitmap> bitmapList;


    public void setBitmapList(ArrayList<Bitmap> bitmapList) {
        this.bitmapList = bitmapList;
        invalidate();
    }

    @Override
    public boolean performClick() {
        return moveAngle == 0 && super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                pChange = false;
                tpChange = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float distance = downY - event.getY();
                moveAngle = getSlopAngle(distance);
//                    当前为最后一张图，不能上滑
//                    当前为第一张图，不能下滑
                if (moveAngle < 0 && cPosition == 0 && !pChange) {
                    moveAngle = 0;
                    return false;
                } else if (moveAngle > 0 && cPosition == bitmapList.size() - 1 && !pChange) {
                    moveAngle = 0;
                    return false;
                }
                changPosition();
                invalidate();

                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(event.getY() - downY) < touchSlop && Math.abs(event.getX() - downX) < touchSlop) {
                    performClick();
                }
                reduction();
                break;
        }
        return true;
    }

    //    在滑动中是向同一方向 Position的值只变动一次，不同方向tpChange
    boolean pChange, tpChange;


    //    滑动的角度
    float moveAngle;
    //    翻页的临界值
    float threshold = 90;
    //    当前绘制的图的下标
    int cPosition = 0;

    //    图片集合下标变换
    private void changPosition() {
//        例如上滑超过临界值，然后又下滑小于临界值的情况
        if (moveAngle > 0 && pChange) {
            if (moveAngle <= threshold) {
                if (!tpChange) {
                    if (cPosition > 0) {
                        cPosition--;
                    }
                    tpChange = true;
                    pChange = !tpChange;
                }
                return;
            }
        } else if (moveAngle < 0 && pChange) {
            if (Math.abs(moveAngle) <= threshold) {
                if (!tpChange) {
                    if (cPosition < bitmapList.size() - 1) {
                        cPosition++;
                    }
                    tpChange = true;
                    pChange = false;
                }
                return;
            }
        }
//        普通的单方向的滑动情况
        if (pChange) {
            return;
        }
        if (moveAngle > threshold) {
            if (cPosition < bitmapList.size() - 1) {
                cPosition++;
            }
            pChange = true;
            tpChange = false;
        } else if (moveAngle < -threshold) {
            if (cPosition > 0) {
                cPosition--;
            }
            pChange = true;
            tpChange = false;
        }
    }

    //    获得当前滑动的角度
    private float getSlopAngle(float distance) {
//        角度越接近90度，角度变化越小，视觉效果不好
//        修改系数，使变化平滑
//        使用正弦函数，sin0=0,sin90=1 sin180=0;
        float angle = (distance / (touchSlop * 15)) * 90;
        double cos;
        if (angle >= 0) {
            cos = Math.sin(Math.toRadians(angle));
            angle = (float) (angle + scale * 10 * cos);
        } else {
            cos = Math.sin(Math.toRadians(-angle));
            angle = (float) (angle - scale * 10 * cos);
        }
        return angle;
    }


    //    滑动放手之后的变化
    ValueAnimator mAnimator;

    private void reduction() {
        if (Math.abs(moveAngle) > 180) {
            return;
        }
        if (moveAngle > threshold) {
            moveAngle -= 180;
        } else if (moveAngle < -threshold) {
            moveAngle += 180;
        }
        mAnimator = ValueAnimator.ofFloat(moveAngle, 0);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                moveAngle = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnimator.setDuration(160);
        mAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float upAngle = 0, downAngle = 0;
        if (bitmapList != null && bitmapList.size() > 0) {
            if (moveAngle >= 0 && moveAngle < threshold) {
                upAngle = 0;
                downAngle = moveAngle;
            } else if (moveAngle >= threshold && moveAngle < 180) {
                upAngle = moveAngle - 180;
                downAngle = 0;
            } else if (moveAngle > -threshold && moveAngle <= 0) {
                upAngle = moveAngle;
                downAngle = 0;
            } else if (moveAngle > -180 && moveAngle <= -threshold) {
                upAngle = 0;
                downAngle = 180 + moveAngle;
            } else if (Math.abs(moveAngle) >= 180) {
                drawTop(canvas, 0);
                drawBottom(canvas, 0);
                upAngle = 0;
                downAngle = 0;
            }
            drawTop(canvas, upAngle);
            drawBottom(canvas, downAngle);
        }

    }

    //    1
    private void drawTop(Canvas canvas, float mAngle) {
        matrix.reset();
        canvas.save();
        canvas.clipRect(0, 0, width, height / 2);
        if (cPosition > 0) {
            canvas.drawBitmap(bitmapList.get(cPosition - 1), matrix, paint);
        }
        camera.save();
        camera.rotateX(mAngle);
        camera.getMatrix(matrix);
        camera.restore();

        matrix.postScale(1.0f, (90 - Math.abs(mAngle)) / 90);
        matrix.postTranslate(width / 2, height / 2);
        matrix.preTranslate(-width / 2, -height / 2);
        canvas.concat(matrix);
        canvas.drawBitmap(bitmapList.get(cPosition), matrix, paint);
        canvas.restore();
    }

    private void drawBottom(Canvas canvas, float mAngle) {
        matrix.reset();
        canvas.save();
        canvas.clipRect(0, height / 2, width, height);
        if (cPosition < bitmapList.size() - 1) {
            canvas.drawBitmap(bitmapList.get(cPosition + 1), matrix, paint);
        }
        camera.save();
        camera.rotateX(mAngle);
        camera.getMatrix(matrix);
        camera.restore();
        matrix.postScale(1.0f, (90 - mAngle) / 90);
        matrix.postTranslate(width / 2, height / 2);
        matrix.preTranslate(-width / 2, -height / 2);
        canvas.concat(matrix);
        canvas.drawBitmap(bitmapList.get(cPosition), matrix, paint);
        canvas.restore();
    }
}
