package com.example.rico.customerview.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * @Description: 两端对齐的 区域点击的text view
 * @Author: pan yi
 * @Date: 2021/12/6
 */
public class SideTextView extends BaseCustomerView {
    private TextPaint textPaint;
    private float textBaseLineTop, textBaseLineBottom;


    // 文字绘制y值
    private int startY = 50;
    private ArrayList<TextInfo> textInfoList;
    private int allPaintSize = 20;
    private StringBuilder contentStringBuilder, drawStringBuilder;
    private TextInfo lastDrawTextInfo;
    private Region globalRegion;
    private float touchSlop;

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
        textBaseLineTop = textPaint.getFontMetrics().ascent;
        textBaseLineBottom = textPaint.getFontMetrics().descent;
        globalRegion = new Region();
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        startY = (int) Math.abs(textPaint.getFontMetrics().top);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w - getPaddingLeft() - getPaddingRight();
        globalRegion.set(0, 0, w, h);
        isReMeasure = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            return;
        }
        int heightMode = MeasureSpec.UNSPECIFIED;
        int realHeight = (int) (startY + textPaint.getFontMetrics().bottom);
        setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(realHeight, heightMode));
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
        textInfoList.clear();
        addColorText(text, textPaint.getColor());
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
        if (isCreate) {
            textInfoList.clear();
            isCreate = false;
        }
        if (TextUtils.isEmpty(text)) {
            return;
        }
        TextInfo info = new TextInfo(text, color, listener);
        textInfoList.add(info);
    }

    private boolean isCreate = false;

    public void create() {
        if (contentStringBuilder == null) {
            contentStringBuilder = new StringBuilder();
        } else {
            contentStringBuilder.delete(0, contentStringBuilder.length());
        }

        if (drawStringBuilder == null) {
            drawStringBuilder = new StringBuilder();
        } else {
            drawStringBuilder.delete(0, drawStringBuilder.length());
        }
        isReMeasure = false;
        isCreate = true;
        invalidate();
    }

    private final String TAG = getClass().getSimpleName();

    float[] cutWitch = new float[]{1};


    float downX, downY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float x = event.getX();
                float y = event.getY();
                if (Math.abs(x - downX) > touchSlop || Math.abs(y - downY) > touchSlop) {
                    return false;
                }
                for (TextInfo info : textInfoList) {
                    if (info.region.contains((int) x, (int) y)) {
                        if (info.listener != null) {
                            info.listener.click();
                        }
                        break;
                    }
                }
                break;
        }
        return true;
    }

    //是否要重新绘制大小
    private boolean isReMeasure;

    @Override
    protected void onDraw(Canvas canvas) {
        startY = (int) Math.abs(textPaint.getFontMetrics().top);
        drawColorText(canvas);

        for (TextInfo info : textInfoList) {
            info.paint.setStyle(Paint.Style.STROKE);
//            canvas.drawPath(info.path, info.paint);
            info.region.setEmpty();
            info.region.setPath(info.path, globalRegion);
        }
        if (!isReMeasure) {
            requestLayout();
            isReMeasure = true;
        }
    }

    // 画有颜色的字
    private void drawColorText(Canvas canvas) {
        if (contentStringBuilder == null) {
            return;
        }
        String content;
        contentStringBuilder.delete(0, contentStringBuilder.length());
        for (int i = 0; i < textInfoList.size(); i++) {
            contentStringBuilder.append(textInfoList.get(i).text);
        }
        content = contentStringBuilder.toString();
        startY += getPaddingTop();
        int index, oldIndex = 0;
        for (int i = 0; i < content.length(); i++) {
            int startX = getPaddingLeft();
            index = textPaint.breakText(content, oldIndex, content.length(), true, width, cutWitch);
            //最后一行，不均分
            // 画每个字的位置 和颜色
            boolean isLastRow = oldIndex + index >= content.length();
            drawColorEqualText(content, oldIndex, oldIndex + index, startX, startY, isLastRow, canvas);
            oldIndex = oldIndex + index;
            if (oldIndex >= content.length()) {
                return;
            }
            startY += textPaint.getFontSpacing();
        }
    }

    private void drawColorEqualText(String text, int start, int end, int x, int y, boolean isLastRow, Canvas canvas) {
        float count = end - start;
        String result = text.substring(start, end);
        char[] arrays = result.toCharArray();
        //测量每一个字的宽度
        float everyDistance;
        if (isLastRow) {
            everyDistance = 0;
        } else {
            everyDistance = (width - textPaint.measureText(result)) / count;
        }
        float startX = 0;
        drawStringBuilder.delete(0, drawStringBuilder.length());
        for (int i = 0; i < count; i++) {
            //判断当前这个字该是什么颜色
            String drawText = String.valueOf(arrays[i]);

            float currentTextWidth = textPaint.measureText(drawText);
            for (TextInfo info : textInfoList) {
                int index1 = drawStringBuilder.length();
                drawStringBuilder.append(info.text);
                int index2 = drawStringBuilder.length();

                if (i + start >= index1 && i + start <= index2) {
                    if (lastDrawTextInfo != info) {
                        textPaint.setColor(info.paint.getColor());
                    }
                    info.path.addRoundRect(x + startX, (float) y + textBaseLineTop, x + startX + everyDistance + currentTextWidth, (float) y + textBaseLineBottom, 0, 0, Path.Direction.CW);
                }
                lastDrawTextInfo = info;
            }
            drawStringBuilder.delete(0, drawStringBuilder.length());

            canvas.drawText(drawText, x + startX, y, textPaint);
            startX += (everyDistance + currentTextWidth);
        }
    }

    //储存文字画笔信息
    private class TextInfo {
        String text;
        Paint paint;
        TextClickListener listener;
        Path path;
        Region region;

        public TextInfo(String text, int color, TextClickListener listener) {
            this.text = text;
            paint = new Paint();
            paint.setTextSize(dpToSp(allPaintSize));
            paint.setColor((getResources().getColor(color)));
            this.listener = listener;
            path = new Path();
            region = new Region();
        }
    }

    public interface TextClickListener {
        void click();
    }
}

