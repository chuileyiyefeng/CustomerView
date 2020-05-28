package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.rico.customerview.bean.PointData;

import java.util.ArrayList;

public class SignImageLayout extends FrameLayout implements ToAddView.PointClickListener {

    private JustScaleImageView iv;
    private ToAddView addView;

    public SignImageLayout(@NonNull Context context) {
        this(context, null);
    }

    public SignImageLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SignImageLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
    }


    private void init() {

    }

    // 添加一个数据点
    public void addProportion(float x, float y) {
        post(() -> {
            if (getImage() != null) {
                iv.addProportion(x, y);
            }
        });
    }

    // 添加单个数据
    public void addPointData(PointData data) {
        post(() -> {
            if (getImage() != null) {
                iv.addPointData(data);
            }
        });
    }

    // 添加数据集合
    public void addPointData(ArrayList<PointData> dataList) {
        post(() -> {
            if (getImage() != null) {
                iv.addPointData(dataList);
            }

        });
    }

    // 设置图片
    public void setImage(Bitmap bitmap) {
        post(() -> {
            if (getImage() == null) {
                return;
            }
            iv.setImageBitmap(bitmap);
        });
    }

    public void setImage(int res) {
        post(() ->
        {
            if (getImage() == null) {
                return;
            }

            iv.setImageResource(res);
        });
    }

    // 获取图片控件
    public ImageView getImage() {
        if (iv == null) {
            try {
                iv = (JustScaleImageView) getChildAt(0);
                addView = (ToAddView) getChildAt(1);
                addView.setPointClickListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return iv;
    }

    // 清楚数据点
    public void clearData() {
        if (getImage() != null) {
            addView.clear();
        }
    }

    PointClickListener listener;

    // 添加点击监听
    public void setPointClickListener(PointClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void click(int position, String message) {
        if (listener != null) {
            listener.pointClick(position, message);
        }
    }

    public interface PointClickListener {
        void pointClick(int position, String message);
    }
}
