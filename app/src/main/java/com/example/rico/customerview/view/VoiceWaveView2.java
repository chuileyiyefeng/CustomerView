package com.example.rico.customerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * @Description:
 * @Author: pan yi
 * @Date: 2023/7/5
 */
public class VoiceWaveView2 extends View {
    private Context context;

    public VoiceWaveView2(Context context) {
        super(context);
        init(context);
    }

    public VoiceWaveView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VoiceWaveView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public VoiceWaveView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        paint = new Paint();
        paint.setColor(Color.parseColor(defaultColor));
    }


    private String defaultColor = "#000000";

    public void setDefaultColor(String defaultColor) {
        this.defaultColor = defaultColor == null ? "" : defaultColor;
    }

    private int lineSpace;
    private int lineWidth;

    public void setLineSpace(int lineSpace) {
        this.lineSpace = lineSpace;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        lineSpace = dpToPx(context, 3);
        lineWidth = dpToPx(context, 2);
        paint.setStrokeWidth(lineWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    private ArrayList<LineInfo> lineList;
    private int maxPercent = 0;
    private int minPercent = 0;

    public void addLinePercent(int linePercent1, int linePercent2, int linePercent3) {
        int max = Math.max(linePercent1, Math.max(linePercent2, linePercent3));
        int min = Math.min(linePercent1, Math.min(linePercent2, linePercent3));
        if (lineList == null) {
            lineList = new ArrayList<>();
        } else {
            lineList.clear();
        }
        lineList.add(new LineInfo(linePercent1, linePercent1 < max));
        lineList.add(new LineInfo(linePercent2, linePercent2 < max));
        lineList.add(new LineInfo(linePercent3, linePercent3 < max));
        maxPercent = max;
        minPercent = min;
    }

    private Paint paint;
    private ValueAnimator animator;

    public void start() {
        if (animator == null) {
            animator = ValueAnimator.ofFloat(0, 1f);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    invalidate();
                }
            });
            animator.setRepeatCount(ValueAnimator.INFINITE);
        }
        animator.start();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == GONE) {
            if (animator != null) {
                animator.cancel();
            }
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.cancel();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < lineList.size(); i++) {
            LineInfo lineInfo = lineList.get(i);
            if (lineInfo.isAddMode) {
                lineInfo.initPercent++;
            } else {
                lineInfo.initPercent--;
            }
        }
    }

    public static int dpToPx(Context context, int dip) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
                r.getDisplayMetrics());
        return (int) px;
    }

    private static class LineInfo {
        public LineInfo(int initPercent, boolean isAddMode) {
            this.initPercent = initPercent;
            this.isAddMode = isAddMode;
        }

        public int initPercent;
        public boolean isAddMode;
    }
}
