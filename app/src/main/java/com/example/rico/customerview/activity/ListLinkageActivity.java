package com.example.rico.customerview.activity;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

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
    WheelLayoutManager manager;
    WheelLayoutView wheelLL;

    @Override
    public int bindLayout() {
        return R.layout.activity_list_linkage;
    }

    int allState;
    private ArrayList<WheelData> wheelDataList;

    @Override
    public void doBusiness() {
        adapter = new ListLinkageAdapter(this);
        rv = findViewById(R.id.rv);
        manager = new WheelLayoutManager();
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);
        helper = new LinearSnapHelper();
        helper.attachToRecyclerView(rv);
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            strings.add(i + "嘻嘻");
        }
        adapter.addData(strings);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                allState += newState;
                View view = helper.findSnapView(manager);
                int position = -1;
                if (view != null) {
                    position = recyclerView.getChildLayoutPosition(view);
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    allState = 0;
                    Log.e("getText", "onScrollStateChanged: " + position);
                }
            }
        });
        addWheel();
    }

    private void addWheel() {
        wheelLL = findViewById(R.id.wheel_ll);
        wheelDataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            WheelData wheelData = new WheelData();
            wheelData.setData("父数据" + i);
            ArrayList<WheelChildData> childList = new ArrayList<>();
            for (int k = 0; k < 5; k++) {
                WheelChildData data = new WheelChildData();
                data.setData("中间数据" + k);
                ArrayList<String> strings = new ArrayList<>();
                for (int l = 0; l < 6; l++) {
                    strings.add("子数据" + l);
                }
                data.setStrings(strings);
                childList.add(data);
            }
            wheelData.setChildList(childList);
            wheelDataList.add(wheelData);
        }
        wheelLL.setData(wheelDataList);
    }

    private int realStopState() {
        return RecyclerView.SCROLL_STATE_SETTLING * 2 + RecyclerView.SCROLL_STATE_DRAGGING;
    }
}
