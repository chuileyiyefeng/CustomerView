package com.example.rico.customerview.activity;

import com.bumptech.glide.Glide;
import com.example.rico.customerview.R;
import com.example.rico.customerview.view.CornerImageView;

/**
 * Created by Tmp on 2018/12/18.
 */
public class CornerImageActivity extends BaseActivity {
    CornerImageView pieView;

    @Override
    public int bindLayout() {
        return R.layout.activity_pie;
    }

    @Override
    public void doBusiness() {
        pieView = findViewById(R.id.pie);
        Glide.with(this).load(R.mipmap.timg).into(pieView);
    }
}
