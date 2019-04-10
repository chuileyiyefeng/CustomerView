package com.example.rico.customerview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

import java.lang.reflect.Field;

/**
 * Created by Tmp on 2019/4/9.
 * 圆角imageView，支持gif加载
 * 父控件是图片背景时，会有一些问题
 */
public class CornerImageView extends AppCompatImageView {
    Context context;

    public CornerImageView(Context context) {
        super(context);
        this.context = context;
    }

    public CornerImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    int width;
    int height;
    int radiusX = 50, radiusY = 50;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        getRadius();
        init();
    }

    //    设置圆角大小
    private void getRadius() {
        radii = new float[]{radiusX, radiusY,
                radiusX, radiusY,
                radiusX, radiusY,
                radiusX, radiusY};
    }

    private Paint paint;
    private Path path;
    private RectF rectFAll, rectFCrop;
    float[] radii;

    protected void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        如果父控件没有背景，就使用theme的背景色
        TypedArray array = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.colorBackground,
                android.R.attr.textColorPrimary,
        });
        int backgroundColor = array.getColor(0, 0xFF00FF);
        View parentView = (View) getParent();
        Drawable drawable = parentView.getBackground();
        if (drawable != null) {
            if (drawable != null) {
                Class<Drawable> mDrawable_class = (Class<Drawable>) drawable.getClass();
                try {
                    Field mField = mDrawable_class.getDeclaredField("mColorState");
                    mField.setAccessible(true);
                    Object mColorState = mField.get(drawable);
                    Class mColorState_class = mColorState.getClass();
                    Field mColorState_field = mColorState_class.getDeclaredField("mUseColor");
                    mColorState_field.setAccessible(true);
                    int color = (int) mColorState_field.get(mColorState);
                    if (color != Color.TRANSPARENT) {
                        paint.setColor(backgroundColor);
                    } else {
                        paint.setColor(color);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            paint.setColor(backgroundColor);
        }
        path = new Path();
        rectFAll = new RectF(0, 0, width, height);
        rectFCrop = new RectF(getPaddingLeft(), getPaddingTop(), width - getPaddingRight(), height - getPaddingBottom());
        path.setFillType(Path.FillType.EVEN_ODD);
        path.addRect(rectFAll, Path.Direction.CW);
        path.addRoundRect(rectFCrop, radii, Path.Direction.CW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }
}
