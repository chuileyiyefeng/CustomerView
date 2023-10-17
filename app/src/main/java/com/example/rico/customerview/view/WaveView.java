package com.example.rico.customerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

/**
 * @Description: 爱心动画控件
 * @Author: pan yi
 * @Date: 2022/5/20
 */
public class WaveView extends View {
    private Paint paint, borderPaint;
    private Path wavePath, lovePath;
    private int baseLine, underLine;

    private final float[] mData = new float[8];               // 顺时针记录绘制圆形的四个数据点
    private final float[] mCtrl = new float[16];              // 顺时针记录绘制圆形的八个控制点

    private float everyMoveX = 15;

    private final int UP = 1;
    private final int DOWN = 2;
    private int currentType = UP;


    private float progressEnd;
    private float progressDistance;


    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    int[] arcColors = new int[]{
            Color.parseColor("#FF4C90"),
            Color.parseColor("#D390FF"),

    };

    private void init() {
        lovePath = new Path();
        wavePath = new Path();


        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#ffffff"));
        paint.setStyle(Paint.Style.FILL);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(Color.parseColor("#FFA5C7"));
        borderPaint.setStrokeWidth(dpToPx(1));
        borderPaint.setStyle(Paint.Style.STROKE);
    }

    int width, height, offsetX;

    private int dpToPx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    public void setBorderWidth(int dp) {
        borderPaint.setStrokeWidth(dpToPx(dp));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;


        // 圆的半径
        float mCircleRadius = w / 2f;
        waveHeight = mCircleRadius / 6f;
        float borderWidth = borderPaint.getStrokeWidth() / 2f;



        baseLine = (int) (-height / 2-waveHeight/4);
        underLine = baseLine;

        LinearGradient gradient = new LinearGradient(0, baseLine, 0, height, arcColors, null, Shader.TileMode.CLAMP);
        paint.setShader(gradient);
        borderPaint.setShader(gradient);
        everyMoveX = mCircleRadius / 10f;
        progressEnd = height / 2f- waveHeight*1.2f ;
        lastProgressEnd=underLine;
        progressDistance = progressEnd - underLine;
        setPath();

        // 初始化数据点

        mData[0] = 0;
        mData[1] = mCircleRadius - borderWidth;

        mData[2] = mCircleRadius - borderWidth;
        mData[3] = 0;

        mData[4] = 0;
        mData[5] = -mCircleRadius + borderWidth;

        mData[6] = -mCircleRadius + borderWidth;
        mData[7] = 0;

        // 圆形的控制点与数据点的差值
        // 一个常量，用来计算绘制圆形贝塞尔曲线控制点的位置
        float c = 0.551915024494f;
        float mDifference = mCircleRadius * c;
        // 初始化控制点

        mCtrl[0] = mData[0] + mDifference;
        mCtrl[1] = mData[1];

        mCtrl[2] = mData[2];
        mCtrl[3] = mData[3] + mDifference;

        mCtrl[4] = mData[2];
        mCtrl[5] = mData[3] - mDifference;

        mCtrl[6] = mData[4] + mDifference;
        mCtrl[7] = mData[5];

        mCtrl[8] = mData[4] - mDifference;
        mCtrl[9] = mData[5];

        mCtrl[10] = mData[6];
        mCtrl[11] = mData[7] - mDifference;

        mCtrl[12] = mData[6];
        mCtrl[13] = mData[7] + mDifference;

        mCtrl[14] = mData[0] - mDifference;
        mCtrl[15] = mData[1];

        mData[1] -= mCircleRadius * 0.6f;
        mCtrl[7] += mCircleRadius * 0.4f;
        mCtrl[9] += mCircleRadius * 0.4f;

        mCtrl[4] -= mCircleRadius * 0.1f;
        mCtrl[10] += mCircleRadius * 0.1f;


        Path path = new Path();
        path.moveTo(mData[0], mData[1]);

        path.cubicTo(mCtrl[0], mCtrl[1], mCtrl[2], mCtrl[3], mData[2], mData[3]);
        path.cubicTo(mCtrl[4], mCtrl[5], mCtrl[6], mCtrl[7], mData[4], mData[5]);
        path.cubicTo(mCtrl[8], mCtrl[9], mCtrl[10], mCtrl[11], mData[6], mData[7]);
        path.cubicTo(mCtrl[12], mCtrl[13], mCtrl[14], mCtrl[15], mData[0], mData[1]);

        lovePath = path;
//        lovePath.addRect(new RectF(-width/2f,-height/2f,width/2f,height/2f), Path.Direction.CCW);
    }


    ValueAnimator animator;

    public void startAnimator() {
        if (null != animator && animator.isRunning()) {
            animator.cancel();
//            return;
        }
        animator = ValueAnimator.ofFloat(0, width);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            offsetX += everyMoveX;
            if (offsetX >= width) {
                offsetX = 0;
            }
            if (currentType == UP) {
                baseLine++;
            } else {
                baseLine--;
            }
            setPath();
            //     画波浪线的path与矩形path相交的值为波浪线的path
            //    原波浪线的path包含的内容太多，影响性能
            wavePath.op(lovePath, Path.Op.INTERSECT);
            invalidate();
        });
        animator.setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }

    private void setPath() {
        if (currentType == UP) {
            if (baseLine > progressEnd) {
                animator.cancel();
                return;
            }
        } else {
            if (baseLine < progressEnd) {
                animator.cancel();
                return;
            }
        }

        wavePath.reset();
        wavePath.moveTo(-width - width / 2f, baseLine);
        for (int i = -3; i < 2; i++) {
            int start = i * width / 2 + offsetX;
            wavePath.quadTo(start + width / 4f - width / 2f, getY(i), start, baseLine);
        }
        wavePath.lineTo(width / 2f, underLine);
        wavePath.lineTo(-width / 2f, underLine);
    }

    private float waveHeight;

    private float getY(int position) {
        if (position % 2 == 0) {
            return baseLine - waveHeight;
        } else {
            return baseLine + waveHeight;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(width / 2f, height / 2f); // 将坐标系移动到画布中央
        canvas.scale(1, -1);                 // 翻转Y轴
        canvas.drawPath(lovePath, borderPaint);

        canvas.clipPath(lovePath);
        canvas.drawPath(wavePath, paint);
    }


    private int lastProgress = 0;
    private float lastProgressEnd=0;

    public void setProgress(int progress) {
        Log.e("wave progress",progress+"");
        if (progress > 100) {
            progress = 100;
        }
        if (progress < 0) {
            progress = 0;
        }
        //修正底部和顶部波浪效果不明显
        if(progress<=10&&progress>0){
            progress=15;
        }
        if (progress>=90&&progress<100) {
            progress=85;
        }
        if (progress==lastProgress) {
            return;
        }

        progressEnd =lastProgressEnd+ (progress-lastProgress)*progressDistance/100f;
        if (progress > lastProgress) {
            currentType = UP;
        } else {
            currentType = DOWN;
        }
        lastProgress = progress;
        lastProgressEnd=progressEnd;
        startAnimator();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != animator) {
            animator.cancel();
        }
    }
}
