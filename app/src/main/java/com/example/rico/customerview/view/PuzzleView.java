package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;

import com.example.rico.customerview.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Tmp on 2019/columnCount/8.
 * 拼图view
 */
public class PuzzleView extends BaseCustomerView {
    private Context context;

    public PuzzleView(Context context) {
        super(context);
    }

    public PuzzleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    //    图片距离顶部的距离
    int distance;
    //    行数、列数
    int rowCount = 3, columnCount = 5;
    //    大图片
    Bitmap bitmap;
    Paint paint;
    Path dividerPath;
    ArrayList<BitmapInfo> bitmapList;
    ArrayList<LeftAndTop> ltList;
    //    乱序的下标映射原有的下标、原有的下标映射乱序的下标
    SparseIntArray originArray, changeArray;

    //    每个小图片的宽高
    int minWidth, minHeight;

    @Override
    protected void init(Context context) {
        this.context = context;
        bitmapList = new ArrayList<>();
        ltList = new ArrayList<>();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(4);
        dividerPath = new Path();
        originArray = new SparseIntArray();
        changeArray = new SparseIntArray();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setBackgroundColor(getResources().getColor(R.color.button_bg));
        bitmap = getJigsaw(context);
        distance = (height - bitmap.getHeight()) / 2;
//        整个图片分为三行五列的小图片
        minWidth = bitmap.getWidth() / columnCount;
        minHeight = bitmap.getHeight() / rowCount;
        for (int i = 0; i < rowCount; i++) {
            int top = bitmap.getHeight() / rowCount * i;
            for (int k = 0; k < columnCount; k++) {
                int left = bitmap.getWidth() / columnCount * k;
                BitmapInfo info = new BitmapInfo();
                info.left = left;
                info.top = top;
                info.bitmap = Bitmap.createBitmap(bitmap, left, top, minWidth, minHeight);
                bitmapList.add(info);
                LeftAndTop lt = new LeftAndTop();
                lt.left = left;
                lt.top = top;
                ltList.add(lt);
            }
        }
//        打乱list的顺序,但是要保留原有的top、left数据
//        用两个array来互相映射原有的和原本的排列位置
        Collections.shuffle(ltList);
        for (int i = 0; i < ltList.size(); i++) {
            LeftAndTop lt = ltList.get(i);
            for (int k = 0; k < bitmapList.size(); k++) {
                BitmapInfo info = bitmapList.get(k);
                if (lt.top == info.top && lt.left == info.left) {
                    originArray.put(i, k);
                    changeArray.put(k, i);
                }
            }
        }
        setWhiteArea();

//        添加分割线路径
        for (int i = 1; i < columnCount; i++) {
            dividerPath.moveTo((float) width / columnCount * i, distance);
            dividerPath.lineTo((float) width / columnCount * i, bitmap.getHeight() + distance);
        }
        for (int i = 1; i < rowCount; i++) {
            dividerPath.moveTo(0, distance + (float) bitmap.getHeight() / rowCount * i);
            dividerPath.lineTo(width, distance + (float) bitmap.getHeight() / rowCount * i);
        }

    }

    //    随机设置空白块
    private void setWhiteArea() {
        Random random = new Random();
        int i = random.nextInt(ltList.size());
        LeftAndTop lf = ltList.get(i);
        lf.isWhite = true;
    }

    float downX, downY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                downArea();
                Log.e("DownType", "onTouchEvent: " + moveType);
                break;
            case MotionEvent.ACTION_MOVE:
                if (moveType != NO_MOVE) {
                    float moveX = event.getX() - downX;
                    float moveY = event.getY() - downY;
                    changeDirection = NO_MOVE;
                    switch (moveType) {
                        case LEFT:
                            if (moveX < 0) {
                                changeDirection = LEFT;
                            }
                            break;
                        case UP:
                            if (moveY < 0) {
                                changeDirection = UP;
                            }
                            break;
                        case RIGHT:
                            if (moveX > 0) {
                                changeDirection = RIGHT;
                            }
                            break;
                        case DOWN:
                            if (moveY > 0) {
                                changeDirection = DOWN;
                            }
                            break;

                    }
                }
                break;
            case MotionEvent.ACTION_UP:
//                选中的图片与白块互换位置
                if (changeDirection != NO_MOVE) {

                }
                break;
        }
        return true;
    }

    //                首先判断按下的这个方块位置
    //                判断按下的这个方块可以向哪个方向移动
    //                可以滑动的方向、变换方块的方向
    int moveType, changeDirection;
    final int NO_MOVE = 0, LEFT = 1, UP = 2, RIGHT = 3, DOWN = 4;

    private void downArea() {
        int downPosition = -1;
//        这里判断点击的位置要用图片原始的位置，而不是打乱顺序后的位置
//        便于判断位置
        for (int i = 0; i < ltList.size(); i++) {
            LeftAndTop lf = ltList.get(i);
            boolean isWidthArea = downX >= lf.left && downX <= lf.left + minWidth;
            boolean isHeightArea = downY >= distance + lf.top && downY <= distance + lf.top + minHeight;
            if (isWidthArea && isHeightArea) {
                if (lf.isWhite) {
                    moveType = NO_MOVE;
                    return;
                }
                downPosition = originArray.get(i);
                break;
            }
        }
//        点击范围不在图片上.
        if (downPosition == -1) {
            moveType = NO_MOVE;
            return;
        }
//        判断上下左右方块的情况,并记下两个要交换的坐标
        LeftAndTop lf = ltList.get(changeArray.get(downPosition));
//        能否向左滑动
        if (downPosition > 0) {
            LeftAndTop lastLf = ltList.get(changeArray.get(downPosition - 1));
            if (lastLf.isWhite && lastLf.top == lf.top) {
                moveType = LEFT;
                return;
            }
        }
//        能否向上滑动
        if (downPosition > columnCount) {
            LeftAndTop lastLf = ltList.get(changeArray.get(downPosition - columnCount));
            if (lastLf.isWhite) {
                moveType = UP;
                return;
            }
        }
//        能否向右滑动
        if (downPosition < ltList.size() - 1) {
            LeftAndTop lastLf = ltList.get(changeArray.get(downPosition + 1));
            if (lastLf.isWhite && lastLf.top == lf.top) {
                moveType = RIGHT;
                return;
            }
        }
//        能否向下滑动
        if (downPosition + columnCount < ltList.size()) {
            LeftAndTop lastLf = ltList.get(changeArray.get(downPosition + columnCount));
            if (lastLf.isWhite) {
                moveType = DOWN;
                return;
            }
        }
        moveType = NO_MOVE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < bitmapList.size(); i++) {
            BitmapInfo info = bitmapList.get(i);
            LeftAndTop lt = ltList.get(i);
            if (!lt.isWhite) {
                canvas.drawBitmap(info.bitmap, lt.left, lt.top + distance, paint);
            }
        }
        canvas.drawPath(dividerPath, paint);
    }

    //    获取填满屏幕宽度的图片
    public Bitmap getJigsaw(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.img);
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int screenWidth = width;
        float scale = 1.0f;
        if (screenWidth < bitmapWidth) {
            scale = (float) screenWidth / bitmapWidth;
        }
        bitmap = Bitmap.createScaledBitmap(bitmap, screenWidth, (int) (bitmapHeight * scale), false);
        return bitmap;
    }

    //    原图顺序的top、left信息以及包含的bitmap
    private class BitmapInfo {
        float top, left;
        Bitmap bitmap;
    }

    //    打乱顺序排列的top、left信息
    private class LeftAndTop {
        float top, left;
        //        是否为空白块
        boolean isWhite;
    }
}
