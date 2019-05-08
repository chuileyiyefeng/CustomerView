package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import com.example.rico.customerview.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Tmp on 2019/5/8.
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

    int distance;
    Bitmap bitmap;
    Paint paint;
    Rect src, dst;
    Path dividerPath;
    ArrayList<BitmapInfo> bitmapList;
    ArrayList<LeftAndTop> ltList;

    @Override
    protected void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bitmapList = new ArrayList<>();
        ltList = new ArrayList<>();
        this.context = context;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setBackgroundColor(getResources().getColor(R.color.text_bg));
        bitmap = getJigsaw(context);
        distance = (height - bitmap.getHeight()) / 2;
        src = new Rect(0, 0, width, bitmap.getHeight());
        dst = new Rect(0, distance, width, distance + bitmap.getHeight());
//        整个图片分为三行五列的小图片
        for (int i = 0; i < 3; i++) {
            int top = bitmap.getHeight() / 3 * i;
            for (int k = 0; k < 5; k++) {
                int left = bitmap.getWidth() / 5 * k;
                BitmapInfo info = new BitmapInfo();
                info.left = left;
                info.top = top;
                info.bitmap = Bitmap.createBitmap(bitmap, left, top, bitmap.getWidth() / 5, bitmap.getHeight() / 3);
                bitmapList.add(info);

                LeftAndTop lt = new LeftAndTop();
                lt.left = left;
                lt.top = top;
                ltList.add(lt);
            }
        }
//        打乱list的顺序
        Collections.shuffle(ltList);
        setWhiteArea();
    }

    //    随机设置空白块
    private void setWhiteArea() {
        Random random = new Random();
        int i = random.nextInt(bitmapList.size());
        BitmapInfo info = bitmapList.get(i);
        info.isWhite = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < bitmapList.size(); i++) {
            BitmapInfo info = bitmapList.get(i);
            LeftAndTop lt = ltList.get(i);
            if (!info.isWhite) {
                canvas.drawBitmap(info.bitmap, lt.left, lt.top + distance, paint);
            }
        }
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

    private class BitmapInfo {
        float top, left;
        Bitmap bitmap;
        //        是否为空白块
        boolean isWhite;
    }

    private class LeftAndTop {
        float top, left;
    }
}
