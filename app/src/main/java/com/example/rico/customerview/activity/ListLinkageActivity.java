package com.example.rico.customerview.activity;

import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.rico.customerview.R;
import com.example.rico.customerview.WheelChildData;
import com.example.rico.customerview.WheelData;
import com.example.rico.customerview.adapter.ListLinkageAdapter;
import com.example.rico.customerview.layoutManager.WheelLayoutManager;
import com.example.rico.customerview.view.WheelLayoutView;

import java.util.ArrayList;

/**
 * Created by Tmp on 2019/8/16.
 */
public class ListLinkageActivity extends BaseActivity {
    RecyclerView rv;
    ListLinkageAdapter adapter;
    LinearSnapHelper helper;
    RecyclerView.LayoutManager manager;
    WheelLayoutView wheelLL;

    @Override
    public int bindLayout() {
        return R.layout.activity_list_linkage;
    }

    @Override
    public void doBusiness() {
        adapter = new ListLinkageAdapter(this);
        rv = findViewById(R.id.rv);
        manager = new WheelLayoutManager(this);
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);
        helper = new LinearSnapHelper();
        helper.attachToRecyclerView(rv);

        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            strings.add("哈哈"+i);
        }
        adapter.addData(strings);
        addWheel();
    }


    private void addWheel() {
        wheelLL = findViewById(R.id.wheel_ll);
        ArrayList<WheelData> wheelDataList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            WheelData wheelData = new WheelData();
            wheelData.setData("父数据" + i);
            ArrayList<WheelChildData> childList = new ArrayList<>();
            for (int k = 0; k < 10; k++) {
                WheelChildData data = new WheelChildData();
                data.setData("中数据" + k);
                ArrayList<String> strings = new ArrayList<>();
                for (int l = 0; l < 20; l++) {
                    strings.add("子数据" + l);
                }
                data.setStrings(strings);
                childList.add(data);
            }
            wheelData.setChildList(childList);
            wheelDataList.add(wheelData);
        }
        wheelLL.setData(wheelDataList);
        wheelLL.setListener(new WheelLayoutView.SelectionListener() {
            @Override
            public void selected(String text, int position) {
                Log.e("position", "selected: "+text );
            }
        });
    }
}
