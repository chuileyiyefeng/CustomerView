package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * @Description: 两端对齐的text view
 * @Author: pan yi
 * @Date: 2021/12/6
 */
public class SideTextView extends BaseCustomerView {
    private TextPaint textPaint;
    private String text = "先帝创业未半而中道崩殂，今天下三分，益州疲弊，此诚危急存亡之秋也。" +
            "然侍卫之臣不懈于内，忠志之士忘身于外者，盖追先帝之殊遇，欲报之于陛下也。诚宜开张圣听，" +
            "以光先帝遗德，恢弘志士之气，不宜妄自菲薄，引喻失义，以塞忠谏之路也。" +
            "宫中府中，俱为一体，陟罚臧否，不宜异同。若有作奸犯科及为忠善者，" +
            "宜付有司论其刑赏，以昭陛下平明之理，不宜偏私，使内外异法也。侍中、侍郎郭攸之、费祎、董允等，" +
            "此皆良实，志虑忠纯，是以先帝简拔以遗陛下。愚以为宫中之事，事无大小，悉以咨之，然后施行，" +
            "必能裨补阙漏，有所广益。将军向宠，性行淑均，晓畅军事，试用于昔日，先帝称之曰能，是以众议举宠为督。" +
            "愚以为营中之事，悉以咨之，必能使行阵和睦，优劣得所。";

//    private String text="私，使内外异法也。侍中、侍郎郭攸之、费";

    // 文字绘制y值
    private int startY = 50;
    private ArrayList<TextInfo> textInfoList, currentDrawList;
    private int allPaintSize = 20;
    private StringBuilder stringBuilder;
    private int startIndex = 0, endIndex = 0;


    public SideTextView(Context context) {
        super(context);
    }

    public SideTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SideTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context) {
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(dpToSp(20));
        textInfoList = new ArrayList<>();
        currentDrawList = new ArrayList<>();
    }


    private int dpToSp(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        float px = scale * dp;
        return (int) (px + 0.5f);
    }

    public void setTextSize(int sp) {
        allPaintSize = dpToSp(sp);
        textPaint.setTextSize(allPaintSize);
        invalidate();
    }

    public void setTextColor(int color) {
        textPaint.setColor(getResources().getColor(color));
    }

    public void setText(String text) {
        this.text = text;
        textInfoList.clear();
        invalidate();
    }

    // 添加文字
    public SideTextView addColorText(String text, int color) {
        addData(text, color, null);
        return this;
    }


    //添加文字 带点击监听
    public SideTextView addColorText(String text, int color, TextClickListener listener) {
        addData(text, color, listener);
        return this;
    }

    private void addData(String text, int color, TextClickListener listener) {
        TextInfo info = new TextInfo(text, color, listener);
        info.startIndex = endIndex;
        info.endIndex = endIndex + text.length();
        startIndex = info.startIndex;
        endIndex = info.endIndex;
        textInfoList.add(info);
    }

    public void create() {
        if (stringBuilder == null) {
            stringBuilder = new StringBuilder();
        } else {
            stringBuilder.delete(0, stringBuilder.length());
        }
        invalidate();
    }

    private final String TAG = getClass().getSimpleName();

    float[] cutWitch = new float[]{1};

    @Override
    protected void onDraw(Canvas canvas) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        startY = 50;
//        drawText(canvas);
        drawColorText(canvas);
    }


    private void drawText(Canvas canvas) {

        startY += getPaddingTop();
        int index, oldIndex = 0;
        for (int i = 0; i < text.length(); i++) {
            int startX = getPaddingLeft();
            index = textPaint.breakText(text, oldIndex, text.length(), true, width, cutWitch);
            if (oldIndex + index >= text.length()) {//最后一行，不均分
                canvas.drawText(text, oldIndex, oldIndex + index, startX, startY, textPaint);
            } else {
                drawEqualText(text, oldIndex, oldIndex + index, startX, startY, textPaint, canvas);
            }
            oldIndex = oldIndex + index;
            if (oldIndex >= text.length()) {
                return;
            }
            startY += textPaint.getFontSpacing();
        }
    }

    // 画左右相等text
    private void drawEqualText(String text, int start, int end, int x, int y, Paint paint, Canvas canvas) {
        float count = end - start;
        String result = text.substring(start, end);
        char[] arrays = result.toCharArray();
        float everyDistance = width / count;
        float startX = 0;
        for (int i = 0; i < count; i++) {
            canvas.drawText(String.valueOf(arrays[i]), x + startX, y, paint);
            startX += everyDistance;
        }
    }

    // 画有颜色的字
    private void drawColorText(Canvas canvas) {
        if (stringBuilder==null) {
            return;
        }
        String content;
        for (int i = 0; i < textInfoList.size(); i++) {
            stringBuilder.append(textInfoList.get(i).text);
        }
        content = stringBuilder.toString();
        startY += getPaddingTop();
        int index, oldIndex = 0;
        int textContentPosition = 0;//当前取值的下标
        for (int i = 0; i < content.length(); i++) {
            int startX = getPaddingLeft();
            index = textPaint.breakText(content, oldIndex, content.length(), true, width, cutWitch);
            if (oldIndex + index >= content.length()) {//最后一行，不均分
                canvas.drawText(content, oldIndex, oldIndex + index, startX, startY, textPaint);
            } else {
                // 计算每个字的位置
//                drawEqualText(content, oldIndex, oldIndex + index, startX, startY, textPaint, canvas);

                // 计算每个字的位置 和颜色

                setEveryTextPosition(content, oldIndex, oldIndex + index, startX, startY, canvas);
            }
            oldIndex = oldIndex + index;
            if (oldIndex >= content.length()) {
                return;
            }
            startY += textPaint.getFontSpacing();
        }
    }

    private void setEveryTextPosition(String text, int start, int end, int x, int y, Canvas canvas) {
        float count = end - start;
        String result = text.substring(start, end);
        char[] arrays = result.toCharArray();
        float everyDistance = width / count;
        float startX = 0;
        //判断index 对应的paint
        currentDrawList.clear();
        getCurrentPosition(start, end);

        if (currentDrawList.size()==0) {
            return;
        }
        int currentIndex=0;
        for (int i = 0; i < currentDrawList.size(); i++) {
            Log.e("setEveryTextPosition",currentIndex+" "+currentDrawList.get(i).text);
        }

//        for (int i = 0; i < count; i++) {//循环画每一个字
//            TextInfo info;
//            info=currentDrawList.get(currentIndex);
//            boolean b=start+i<=info.endIndex&&start+i>=info.startIndex;
//            while (!b){
//                currentIndex++;
//                info=currentDrawList.get(currentIndex);
//                b=start+i<=info.endIndex&&start+i>=info.startIndex;
//            }
//            Log.e("setEveryTextPosition",currentIndex+" "+ arrays[i]);
//            canvas.drawText(String.valueOf(arrays[i]), x + startX, y, info.paint);
//            startX += everyDistance;
//        }
    }

    private int currentLength;//当前的文字length
    private int currentIndex;//当前取的数据类下标

    // 当前是在哪一个数据类 可能会有多个
    private void getCurrentPosition(int startIndex, int endIndex) {

        // 比如现在是100-150字符 开始为startIndex
        // startIndex 应该再当前类获得的start-end
        int index = currentIndex;
        for (int i = currentIndex; i < textInfoList.size(); i++) {
            int startX = textInfoList.get(i).startIndex;
            int endX = textInfoList.get(i).endIndex;
            //分为多种情况
            //必能裨补阙漏，有所广益。将军向宠，性行淑均，晓畅军事，试用于昔日，先帝称之曰能，是以众议举宠为督
            //愚以为 (营中之事) ，悉以咨之，必能使(行阵和睦)，优劣得所 带括号的为变色 这时候——
            if (startIndex >= startX && startIndex <= endX) {//第一种情况startIndex在中间
                currentDrawList.add(textInfoList.get(i));
                index++;
            }
            if (startX >= startIndex && endX <= endIndex) {//第二种情况 startIndex包含这两个
                currentDrawList.add(textInfoList.get(i));
                index++;
            }
            if (startX > endIndex) {
                currentIndex = index;
                break;
            }

        }
    }

    //储存文字画笔信息
    private class TextInfo {
        String text;
        Paint paint;
        TextClickListener listener;
        int startIndex, endIndex;//文字开始的下标 结束的下标

        public TextInfo(String text, int color, TextClickListener listener) {
            this.text = text;
            paint = new Paint();
            paint.setTextSize(dpToSp(allPaintSize));
            paint.setColor((getResources().getColor(color)));
            this.listener = listener;
        }
    }

    public interface TextClickListener {
        void click();
    }
}

