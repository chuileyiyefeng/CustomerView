package com.example.rico.customerview.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Tmp on 2019/4/8.
 */
public abstract class BaseCustomerView extends View {
   protected int width, height;
    public BaseCustomerView(Context context) {
        super(context);
        init(context);
    }

    public BaseCustomerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseCustomerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    protected abstract void init(Context context);
}
