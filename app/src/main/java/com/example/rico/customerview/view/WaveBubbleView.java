package com.example.rico.customerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Tmp on 2019/3/5.
 * 气泡波浪view
 * 原理在图 R.mipmap.wave_wave_explanation
 */
public class WaveBubbleView extends View {
    private Paint paint, bubblePaint, rectFPaint;
    private Path path, rectPath;
    private int baseLine;
    private int waveHeight = 60;
    private Random random;

    public WaveBubbleView(Context context) {
        super(context);
        init();
    }

    public WaveBubbleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveBubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    int[] arcColors = new int[]{
            Color.parseColor("#08E7E7"),
            Color.parseColor("#08D6D6"),
            Color.parseColor("#07C6C6"),
            Color.parseColor("#059494"),

    };

    private void init() {
        rectPath = new Path();
        path = new Path();

        rectFPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectFPaint.setColor(Color.parseColor("#2608E7E7"));
        rectFPaint.setStyle(Paint.Style.FILL);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#5CACEE"));
        paint.setStyle(Paint.Style.FILL);

        bubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bubblePaint.setColor(Color.parseColor("#EBEBEB"));
        bubblePaint.setStyle(Paint.Style.FILL);

        random = new Random();
    }

    int width, height, offsetX;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        baseLine = height / 2;
        LinearGradient gradient = new LinearGradient(0, baseLine, 0, height, arcColors, null, Shader.TileMode.CLAMP);
        paint.setShader(gradient);
        produceBubble();
        resetPath();
        rectPath.addRect(50, 50, width - 50, height - 50, Path.Direction.CW);
    }

    List<Bubble> bubbleList;

    //    随机产生气泡
    private void produceBubble() {
        bubbleList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            bubbleList.add(createNewBubble());
        }
    }


    long drawTime;


    //    生成一个新的气泡
    private Bubble createNewBubble() {

        Bubble bubble = new Bubble();

        int radius = random.nextInt(50) + 10;
        int x = random.nextInt(width - 2 * radius) + bubble.getRadius() / 2;
        int y = random.nextInt(height - baseLine - waveHeight) + baseLine + waveHeight;

        int speedX = random.nextInt(5) + 1;
        if (random.nextInt() % 2 == 0) {
            speedX = -speedX;
        }

        int speedY = random.nextInt(5) + 1;
        bubble.setRadius(radius);
        bubble.setSpeedX(speedX);
        bubble.setSpeedY(speedY);
        bubble.setPointX(x);
        bubble.setPointY(y);
        return bubble;
    }

    //    根据旧的值生成一个新的值
    private Bubble getNewBubble(Bubble oldBubble) {

        int x = oldBubble.getPointX();
        int y = oldBubble.getPointY();
        int speedY = oldBubble.getSpeedY();
        int speedX = oldBubble.getSpeedX();
        int radius = oldBubble.getRadius();
        if (x + speedX > width - radius || x + speedX < radius || y - speedY - radius < baseLine + waveHeight) {
            int index = bubbleList.indexOf(oldBubble);
            bubbleList.remove(oldBubble);
            bubbleList.add(index, createNewBubble());
            return bubbleList.get(index);
        } else {
            oldBubble.setPointX(x + speedX);
            oldBubble.setPointY(y - speedY);
        }
        return oldBubble;
    }

    ValueAnimator animator;

    public void startAnimator() {
        if (null != animator && animator.isRunning()) {
            return;
        }
        drawTime = System.currentTimeMillis();
//        ValueAnimator本身并不会作用与任何一个属性，本身也不会提供任何一种动画。
//         简单的来说，就是一个数值发生器，可以产生想要的各种数值
        animator = ValueAnimator.ofFloat(0, width);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offsetX += 15;
                if (offsetX >= width) {
                    offsetX = 0;
                }
                baseLine--;
                resetPath();
//                画波浪线的path与矩形path相交的值为波浪线的path
//                原波浪线的path包含的内容太多，影响性能
                path.op(rectPath,Path.Op.INTERSECT);
                invalidate();
            }
        });
        animator.setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }
    private void resetPath() {
        if (baseLine<=0) {
            animator.cancel();
            return;
        }
        path.reset();
        path.moveTo(-width, baseLine);
        for (int i = -3; i < 2; i++) {
            int start = i * width / 2 + offsetX;
            path.quadTo(start + width / 4, getY(i), start + width / 2, baseLine);
        }
        path.lineTo(width, height);
        path.lineTo(0, height);

    }

    float downX, downY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(event.getX() - downX) < 10 && Math.abs(event.getY() - downY) < 10) {
                    startAnimator();
                }
                break;
        }
        return true;
    }

    private int getY(int position) {
        if (position % 2 == 0) {
            return baseLine - waveHeight;
        } else {
            return baseLine + waveHeight;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
//        clipPath操作要在画图之前操作，clipPath不会影响到已经画好的图形
        canvas.clipPath(rectPath);
        canvas.drawPath(rectPath, rectFPaint);
        canvas.drawPath(path, paint);
//
        for (int i = 0; i < bubbleList.size(); i++) {
            Bubble newBubble = getNewBubble(bubbleList.get(i));
            canvas.drawCircle(newBubble.getPointX(), newBubble.getPointY(), newBubble.getRadius(), bubblePaint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != animator) {
            animator.cancel();
        }
    }

    private class Bubble {
        int pointX, pointY, speedX, speedY, radius;

        private int getSpeedX() {
            return speedX;
        }

        private void setSpeedX(int xSpeed) {
            this.speedX = xSpeed;
        }

        private int getSpeedY() {
            return speedY;
        }

        private void setSpeedY(int ySpeed) {
            this.speedY = ySpeed;
        }

        private int getPointX() {
            return pointX;
        }

        private void setPointX(int pointX) {
            this.pointX = pointX;
        }

        private int getPointY() {
            return pointY;
        }

        private void setPointY(int pointY) {
            this.pointY = pointY;
        }

        public int getRadius() {
            return radius;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }
    }
}
