package com.example.rico.customerview.activity;

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
        ArrayList<SignData> dataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            SignData data = new SignData();
            data.setMessage("这是一条信息" + i);
//            int k = (int) (Math.random() * 10);
//            int left = (int) ((Math.random() * 10) * (Math.random() * 10));
//            int top = (int) ((Math.random() * 15) * (Math.random() * 15));
//            k = 100;
//            int left = i + 1;
//            int top = i + 1;
//            data.setLeft(k * left);
//            data.setTop(k * top);
            dataList.add(data);
        }
        signView.setSignDataList(dataList);
    }
}
