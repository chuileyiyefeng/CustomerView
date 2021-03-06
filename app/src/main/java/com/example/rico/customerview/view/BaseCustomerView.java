package com.example.rico.customerview.view;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Tmp on 2019/4/8.
 */
public abstract class BaseCustomerView extends View {
   protected int width, height;
    public BaseCustomerView(Context context) {
        this(context,null);
    }

    public BaseCustomerView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
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
