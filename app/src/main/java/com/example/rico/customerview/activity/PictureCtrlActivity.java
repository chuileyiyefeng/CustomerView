package com.example.rico.customerview.activity;

import android.view.View;
import android.widget.Toast;

import com.example.rico.customerview.R;
import com.example.rico.customerview.bean.PointData;
import com.example.rico.customerview.view.SignImageLayout;

import java.util.ArrayList;

public class PictureCtrlActivity extends BaseActivity implements SignImageLayout.PointClickListener, View.OnClickListener {
    @Override
    public int bindLayout() {
        return R.layout.activity_picture_ctrl;
    }

    SignImageLayout layout;
    ArrayList<PointData> list = new ArrayList<>();

    @Override
    public void doBusiness() {
        layout = findViewById(R.id.signLayout);

        PointData data = new PointData();
        data.setMessage("这是点击内容，下标为" + 1);
        data.setRadius(10);
        data.setX(0.5f);
        data.setY(0.5f);
        data.setPointColorStr("#199aff");
        data.setTextSize(18);
        data.setTextColor(R.color.silver);
        data.setRectColor(R.color.blue_y);
        list.add(data);
        PointData data2 = new PointData();
        data2.setMessage("这是点击内容，下标为" + 2);
        data2.setRadius(15);
        data2.setX(0.2f);
        data2.setY(0.2f);
        data2.setPointColorStr("#FF6347");
        data2.setTextSize(22);
        data2.setTextColor(R.color.white);
        data2.setRectColor(R.color.trans_black);
        list.add(data2);
        layout.addPointData(list);
        layout.setImage(R.mipmap.img1);
        layout.setPointClickListener(this);
        findViewById(R.id.tv).setOnClickListener(this);
    }

    @Override
    public void pointClick(int position, String message) {
        Toast.makeText(this, "点击了" + position + "  " + message, Toast.LENGTH_SHORT).show();
    }

    boolean isDefault = true;

    @Override
    public void onClick(View view) {
        if (isDefault) {
            layout.clearData();
            layout.addPointData(list);
            layout.setImage(R.mipmap.flip_2);
        } else {
            layout.clearData();
            layout.addPointData(list);
            layout.setImage(R.mipmap.img1);
        }
        isDefault = !isDefault;
    }
}
