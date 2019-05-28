package com.example.rico.customerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.example.rico.customerview.R;

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
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.flip_3);
    }


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
                time = 0;
                positionChange = false;
                break;
            case MotionEvent.ACTION_MOVE:
                isFling = Math.abs(event.getX() - downX) < Math.abs(event.getY() - downY) && Math.abs(event.getY() - downY) > touchSlop;
                if (isFling) {
                    float distance = downY - event.getY();
                    moveAngle = getSlopAngle(distance);
                    if (Math.abs(moveAngle) > 90) {
                        
                    }
                    changPosition();
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                reduction();
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    //    在滑动中是向同一方向 Position的值只变动一次，
    boolean positionChange;

    //    滑动过程中经过临界值的次数
    int time;

    //    滑动的角度
    float moveAngle;
    //    翻页的临界值
    float threshold = 90;
    //    当前绘制的图的下标
    int cPosition = 2;

    //    图片集合下标变换
    private void changPosition() {
        if (positionChange && time == 1) {
            return;
        }
        if (moveAngle > threshold) {
            if (cPosition < bitmapList.size() - 1) {
                cPosition++;
            }
            positionChange = true;
        } else if (moveAngle < -threshold) {
            if (cPosition > 0) {
                cPosition--;
            }
            positionChange = true;
        }
    }

    @Override
    public boolean performClick() {
        return !isFling && super.performClick();
    }

    //    获得当前滑动的角度
    private float getSlopAngle(float distance) {
        return (distance / (touchSlop * 15)) * 90;
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
        mAnimator.setDuration(200);
        mAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float upAngle = 0, downAngle = 0;
        if (bitmapList != null) {
            if (moveAngle >= 0 && moveAngle < 90) {
                upAngle = 0;
                downAngle = moveAngle;
            } else if (moveAngle >= 90 && moveAngle < 180) {
                upAngle = moveAngle - 180;
                downAngle = 0;
            } else if (moveAngle > -90 && moveAngle <= 0) {
                upAngle = moveAngle;
                downAngle = 0;
            } else if (moveAngle > -180 && moveAngle <= -90) {
                upAngle = 0;
                downAngle = 180 + moveAngle;
            } else if (Math.abs(moveAngle) >= 180) {
                drawTop(canvas, 0);
                drawBottom(canvas, 0);
                upAngle = 0;
                downAngle = 0;
            }
        }
        drawTop(canvas, upAngle);
        drawBottom(canvas, downAngle);
    }

    Bitmap bitmap;

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

    //    滑到下一页
    public void nextPage() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 180);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                moveAngle = (float) animation.getAnimatedValue();
                changPosition();
                invalidate();
            }
        });
        animator.setDuration(3000);
        animator.start();
    }
}
