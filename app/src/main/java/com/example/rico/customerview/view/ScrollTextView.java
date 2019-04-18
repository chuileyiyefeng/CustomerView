package com.example.rico.customerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Tmp on 2019/4/12.
 * 滚动的文字view
 */
public class ScrollTextView extends BaseCustomerView {
    //    要移出屏幕的字符画笔、要显示的字符画笔、做平移字符画笔
    private Paint lastPaint, newPaint, firstPaint;
    //    移出屏幕的字符、要显示的字符
    private String lastText, newText;
    private float lastTextLength, newTextLength;
    private float newHeight, lastHeight, firstHeight;
    private ValueAnimator animator;
    private Paint.FontMetrics metrics;
    private float everyHeightMove;
    private MyHandler handler;

    public ScrollTextView(Context context) {
        super(context);
    }

    public ScrollTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        float textSize = 100;
        lastPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lastPaint.setTextSize(textSize);
        lastPaint.setColor(Color.BLACK);

        newPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        newPaint.setTextSize(textSize);
        newPaint.setColor(Color.BLACK);

        firstPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        firstPaint.setTextSize(textSize);
        firstPaint.setColor(Color.BLACK);

        metrics = lastPaint.getFontMetrics();
        lastInfoList = new ArrayList<>();
        newInfoList = new ArrayList<>();
        lastRemove = new ArrayList<>();
        newRemove = new ArrayList<>();
        handler = new MyHandler(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightMode = MeasureSpec.EXACTLY;
        int realHeight = MeasureSpec.makeMeasureSpec((int) (metrics.bottom - metrics.top + 0.5f) * 2, heightMode);
        setMeasuredDimension(widthMeasureSpec, realHeight);
    }

    //    字符串滚动的次数
    int moveCount = 30;
    //    每次位移时画笔透明度变化值  上次移动的字符画笔透明度值 要显示的字符画笔透明度值
    int alphaValue = 8, lastAlpha, newAlpha;

    //    设置滚动次数
    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
        moveCount = moveCount > 255 ? 255 : moveCount;
        alphaValue = 255 / moveCount;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initBeginValue();
        everyHeightMove = (lastHeight + metrics.bottom) / moveCount;
    }

    //    初始化一些值
    private void initBeginValue() {
        lastHeight = height / 2 + metrics.bottom;
        newHeight = lastHeight * 2 + metrics.bottom;
        firstHeight = lastHeight;
        lastAlpha = 255;
        newAlpha = 255 - moveCount * alphaValue;
        lastPaint.setAlpha(lastAlpha);
        newPaint.setAlpha(newAlpha);
        isQuery = false;
    }

    //    设置字符串的相关信息
    private void setText() {
        if (strings.size() == 0) {
            return;
        } else if (strings.size() == 1) {
            lastP = thisP = 0;
        }
        lastText = strings.get(lastP);
        newText = strings.get(thisP);
        lastTextLength = lastPaint.measureText(lastText);
        newTextLength = lastPaint.measureText(newText);
        charWeight(lastText, newText);
        lastP = thisP;
        thisP++;
        if (thisP > strings.size() - 1) {
            thisP = 0;
        }
    }

    private void charWeight(String lastText, String newText) {
        lastInfoList.clear();
        newInfoList.clear();
        lastRemove.clear();
        float lastPosition = 0, newPosition = 0;
        for (int i = 0; i < lastText.length(); i++) {
            String lastC = lastText.substring(i, i + 1);
            CharInfo info = new CharInfo();
            info.aChar = lastC;
            info.position = lastPosition;
            lastInfoList.add(info);
            lastPosition += lastPaint.measureText(lastC);
        }
        for (int k = 0; k < newText.length(); k++) {
            String newC = newText.substring(k, k + 1);
            CharInfo charInfo = new CharInfo();
            charInfo.aChar = newC;
            charInfo.position = newPosition;
            newInfoList.add(charInfo);
            newPosition += lastPaint.measureText(newC);
        }
    }

    int lastP, thisP = 1;
    ArrayList<String> strings;

    //    添加字符内容
    public void addText(ArrayList<String> strings) {
        this.strings = strings;
        setText();
    }


    public void startAnimator() {
        if (strings == null || strings.size() < 2) {
            return;
        }
        if (animator == null) {
            animator = ValueAnimator.ofInt(width);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
//                    计算上下字符移动位置
                    if (lastHeight > -metrics.bottom) {
                        if (lastHeight - everyHeightMove < -metrics.bottom) {
                            float distance = lastHeight + metrics.bottom;
                            lastHeight -= distance;
                            newHeight -= distance;
                        } else {
                            lastHeight -= everyHeightMove;
                            newHeight -= everyHeightMove;
                        }
                        lastAlpha -= alphaValue;
                        newAlpha += alphaValue;
                        if (lastAlpha < 0) {
                            lastAlpha = 0;
                        }
                        if (newAlpha > 255) {
                            newAlpha = 255;
                        }
                        lastPaint.setAlpha(lastAlpha);
                        newPaint.setAlpha(newAlpha);
                        querySame();
                    } else {
                        animation.cancel();
                        initBeginValue();
                        setText();
                        handler.sendEmptyMessageDelayed(1, 500);
                    }
                }
            });
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setDuration(1000);
        }
        if (!animator.isRunning()) {
            animator.start();
        }
    }

    //    筛选出当前字符串和新的字符串相同的字符
    //    Boolean值控制只计算一次
    boolean isQuery;

    //    查找上下字段相同的字符
    private void querySame() {
        if (lastText.equals(newText)) {
            invalidate();
            return;
        }
        if (!isQuery) {
            lastRemove.clear();
            newRemove.clear();
            for (int i = 0; i < lastInfoList.size(); i++) {
                CharInfo info = lastInfoList.get(i);
                for (int k = 0; k < newInfoList.size(); k++) {
                    CharInfo newInfo = newInfoList.get(k);
                    if (newInfo.aChar.equals(info.aChar)) {
//                        新的字符可能会包含多个相同的字符，这里只添加一个，多的不管
//                        例如：lastString="this" newString="though" 只移动前两个字符，最后一个字符忽略
                        if (!lastRemove.contains(info) && !newRemove.contains(newInfo)) {
                            info.distance = (lastTextLength - newTextLength) / 2 + newInfo.position - info.position;
                            info.everyMove = info.distance / moveCount;
                            lastRemove.add(info);
                            newRemove.add(newInfo);
                        }
                    }
                }
            }
            lastInfoList.removeAll(lastRemove);
            newInfoList.removeAll(newRemove);
            isQuery = true;
        }
        movingX();
        invalidate();
    }

    //    计算x轴的平移距离
    private void movingX() {
        for (int i = 0; i < lastRemove.size(); i++) {
            CharInfo info = lastRemove.get(i);
            float moveX = info.moveX;
            float everyMove = info.everyMove;
            float distance = info.distance;
            moveX += everyMove;
            if (distance > 0) {
                if (moveX > distance) {
                    moveX = distance;
                }
            }
            if (distance < 0) {
                if (moveX < distance) {
                    moveX = distance;
                }
            }
            info.moveX = moveX;
        }
    }

    float lastTextStart, newTextStart;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (TextUtils.isEmpty(lastText) || TextUtils.isEmpty(newText)) {
            return;
        }
        lastTextStart = (width - lastTextLength) / 2;
        newTextStart = (width - newTextLength) / 2;


        for (int i = 0; i < lastInfoList.size(); i++) {
            canvas.drawText(lastInfoList.get(i).aChar, lastTextStart + lastInfoList.get(i).position, lastHeight, lastPaint);
        }
        for (int i = 0; i < newInfoList.size(); i++) {
            canvas.drawText(newInfoList.get(i).aChar, newTextStart + newInfoList.get(i).position, newHeight, newPaint);
        }
        //        这里画位移字符
        for (int i = 0; i < lastRemove.size(); i++) {
            canvas.drawText(lastRemove.get(i).aChar, lastTextStart + lastRemove.get(i).position + lastRemove.get(i).moveX, firstHeight, firstPaint);
        }
    }

    //    从窗口移除时，取消动画
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.cancel();
        }
        handler.removeCallbacksAndMessages(null);
    }

    ArrayList<CharInfo> lastInfoList, newInfoList, lastRemove, newRemove;

    //    字符类，包含当前单个字符的内容以及位置
    class CharInfo {
        String aChar;
        float position, distance, moveX, everyMove;
    }

    private static class MyHandler extends Handler {
        WeakReference<ScrollTextView> reference;

        public MyHandler(ScrollTextView scrollTextView) {
            reference = new WeakReference<>(scrollTextView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            reference.get().animator.start();
        }
    }
}
