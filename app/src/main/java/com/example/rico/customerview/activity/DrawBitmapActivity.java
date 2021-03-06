package com.example.rico.customerview.activity;

import android.graphics.BitmapFactory;
import android.view.View;

import com.example.rico.customerview.R;
import com.example.rico.customerview.view.DrawBitmapView;

/**
 * Created by Tmp on 2018/12/18.
 */
public class DrawBitmapActivity extends BaseActivity implements View.OnClickListener {
    DrawBitmapView drawV;

    @Override
    public int bindLayout() {
        return R.layout.activity_draw_bitmap;
    }

    @Override
    public void doBusiness() {
        drawV = findViewById(R.id.drawV);
        drawV.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        drawV.setBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_complete));
    }
}
