package com.example.rico.customerview.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Tmp on 2019/3/5.
 */
public class ItemDecoration extends RecyclerView.ItemDecoration {
    private Paint mPaint;
    private int mDividerHeight = 2;

    public ItemDecoration() {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#BBBBBB"));
        // 画笔颜色设置为黄色
    }

    public ItemDecoration(int dividerHeight, int color) {
        mDividerHeight = dividerHeight;
        mPaint = new Paint();
        mPaint.setColor(color);
    }

    //设置ItemView的内嵌偏移长度（inset）
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
    }
    //    绘制在子view的下层
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }


    //    绘制在子view的上层
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int childCount = parent.getChildCount();
        // 画水平线
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int left = child.getLeft();
            int top = child.getBottom();
            int right = child.getRight();
            int bottom = top + mDividerHeight;
            c.drawRect(left, top, right, bottom, mPaint);
        }
        //        画竖直线
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int left = child.getRight();
            int top = child.getTop();
            int right = child.getRight() + mDividerHeight;
            int bottom = child.getBottom();
            c.drawRect(left, top, right, bottom, mPaint);
        }
    }
}


