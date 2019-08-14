package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.rico.customerview.CurveData;

import java.util.List;

/**
 * Created by Tmp on 2019/8/14.
 */
public class CurveLinearLayout extends LinearLayout {
    public CurveLinearLayout(Context context) {
        super(context);
    }

    public CurveLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CurveLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setData(List<CurveData> data) {
        CurveMarkView markView = (CurveMarkView) getChildAt(0);
        markView.setData(data);
        HorizontalScrollView scrollView = (HorizontalScrollView) getChildAt(1);
        CurveView curveView = (CurveView) scrollView.getChildAt(0);
        curveView.setData(data);
    }
}
