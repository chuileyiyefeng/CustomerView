package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.rico.customerview.R;

/**
 * create by pan yi on 2021/3/18
 * desc :  带图片的textView
 */
public class ImageTextView extends BaseCustomerView {
    private TextPaint textPaint;
    private Paint.FontMetrics fontMetrics;
    private StaticLayout layout;// 可用于文字自动换行绘制
    private Bitmap bitmap;
    private String text = "先帝创业未半而中道崩殂，今天下三分，益州疲弊，此诚危急存亡之秋也。然侍卫之臣不懈于内，忠志之士忘身于外者，盖追先帝之殊遇，欲报之于陛下也。诚宜开张圣听，以光先帝遗德，恢弘志士之气，不宜妄自菲薄，引喻失义，以塞忠谏之路也。\n" +
            "\n" +
            "宫中府中，俱为一体，陟罚臧否，不宜异同。若有作奸犯科及为忠善者，宜付有司论其刑赏，以昭陛下平明之理，不宜偏私，使内外异法也。\n" +
            "\n" +
            "侍中、侍郎郭攸之、费祎、董允等，此皆良实，志虑忠纯，是以先帝简拔以遗陛下。愚以为宫中之事，事无大小，悉以咨之，然后施行，必能裨补阙漏，有所广益。\n" +
            "\n" +
            "将军向宠，性行淑均，晓畅军事，试用于昔日，先帝称之曰能，是以众议举宠为督。愚以为营中之事，悉以咨之，必能使行阵和睦，优劣得所。";

    // 图片的顶点位置
    private int imgTop, imgLeft;

    // 图片的宽高
    private int bitmapW, bitmapH;

    // 文字绘制y值
    public int startY = 50;

    public ImageTextView(Context context) {
        super(context);
    }

    public ImageTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context) {
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(dpToSp(20));
        fontMetrics = textPaint.getFontMetrics();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        StaticLayout.Builder builder = StaticLayout.Builder.obtain(text, 0, text.length(), textPaint, width);
//        layout = builder.build();
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.dog);
        bitmapW = bitmap.getWidth();
        bitmapH = bitmap.getHeight();
        setPadding(10, 10, 10, 10);
    }

    private int dpToSp(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        float px = scale * dp;
        return (int) (px + 0.5f);
    }

    public void setTextSize(int sp) {
        textPaint.setTextSize(dpToSp(sp));
    }

    public void setTextColor(int color) {
        textPaint.setColor(getResources().getColor(color));
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    private final String TAG = getClass().getSimpleName();

    float[] cutWitch = new float[]{1};

    @Override
    protected void onDraw(Canvas canvas) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        canvas.drawBitmap(bitmap, imgLeft, imgTop, textPaint);
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        startY += getPaddingTop();
        int index, oldIndex = 0;
        boolean firstChangeLine = true;
        for (int i = 0; i < text.length(); i++) {
            int startX = getPaddingLeft();
            int drawWidth;

            if (startY >= imgTop && startY <= imgTop + bitmapH) {
                startX = bitmapW;
                drawWidth = width - bitmapW;
            } else {
                if (firstChangeLine) {
                    startY = (int) (bitmapH + textPaint.getFontSpacing());
                    firstChangeLine = false;
                }
                drawWidth = width;
            }
            index = textPaint.breakText(text, oldIndex, text.length(), true, drawWidth, cutWitch);
            canvas.drawText(text, oldIndex, oldIndex + index, startX, startY, textPaint);
            oldIndex = oldIndex + index;
            Log.e(TAG, "drawText: " + startY + "  " + bitmapH + " " + index);
            if (oldIndex >= text.length()) {
                return;
            }
            startY += textPaint.getFontSpacing();
        }
    }
}
