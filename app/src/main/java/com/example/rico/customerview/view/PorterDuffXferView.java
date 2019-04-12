package com.example.rico.customerview.view;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by Tmp on 2019/4/10.
 */
public class PorterDuffXferView extends BaseCustomerView {
    public PorterDuffXferView(Context context) {
        super(context);
    }

    public PorterDuffXferView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        rootView.setDrawingCacheEnabled(true);
        rootView = (ViewGroup) getActivityFromContext(context).getWindow().getDecorView();
        rootDrawable = rootView.getDrawingCache();
        rootView.setDrawingCacheEnabled(false);
    }

    private ViewGroup rootView;
    private Bitmap rootDrawable;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    private Activity getActivityFromContext(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        throw new RuntimeException("Activity not found!");
    }
}
