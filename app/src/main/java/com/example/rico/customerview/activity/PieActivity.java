package com.example.rico.customerview.activity;

import com.example.rico.customerview.BaseActivity;
import com.example.rico.customerview.R;
import com.example.rico.customerview.view.PieView;

/**
 * Created by Tmp on 2018/12/18.
 */
public class PieActivity extends BaseActivity {
    PieView pieView;

    @Override
    public int bindLayout() {
        return R.layout.activity_pie;
    }

    @Override
    public void doBusiness() {
        pieView = findViewById(R.id.pie);
        pieView.addData(R.color.colorAccent, 50);
        pieView.addData(R.color.colorPrimaryDark, 50);
        pieView.addData(R.color.saffon_yellow, 10);
    }

}
