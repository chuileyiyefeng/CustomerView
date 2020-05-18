package com.example.rico.customerview.activity;

import android.view.View;

import com.example.rico.customerview.R;
import com.example.rico.customerview.bean.SignData;
import com.example.rico.customerview.view.ScrollSignView;

import java.util.ArrayList;

public class ScrollSignActivity extends BaseActivity {
    ScrollSignView signView;

    @Override
    public int bindLayout() {
        return R.layout.activity_scroll_sign;
    }

    @Override
    public void doBusiness() {
        signView = findViewById(R.id.sign_view);
        setData();
        findViewById(R.id.tv_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<SignData> dataList = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    SignData data = new SignData();
                    data.setMessage("# 这是一条重设的信息" + i);
                    data.setTitle("这是标题" + i);
                    dataList.add(data);
                }
                signView.reSetSignDataList(dataList);
            }
        });
    }

    private void setData() {
        ArrayList<SignData> dataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            SignData data = new SignData();
            data.setMessage("# 这是一条信息长长长" + i);
            data.setTitle("这是标题" + i);
            dataList.add(data);
        }
        signView.setSignDataList(dataList);
    }
}
