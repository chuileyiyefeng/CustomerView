package com.example.rico.customerview.fragment;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rico.customerview.activity.BannerExchangeActivity;
import com.example.rico.customerview.bean.ItemInfo;
import com.example.rico.customerview.R;
import com.example.rico.customerview.activity.AllViewActivity;
import com.example.rico.customerview.activity.CircleLayoutActivity;
import com.example.rico.customerview.activity.FishSwimActivity;
import com.example.rico.customerview.activity.FlowActivity;
import com.example.rico.customerview.activity.RotateActivity;
import com.example.rico.customerview.activity.SideDeleteActivity;
import com.example.rico.customerview.adapter.BaseAdapter;
import com.example.rico.customerview.adapter.FirstAdapter;
import com.example.rico.customerview.view.MyItemDecoration;

/**
 * Created by Tmp on 2019/6/27.
 */
public class HomeFragment2 extends BaseFragment implements BaseAdapter.ItemClick {
    RecyclerView rv;
    FirstAdapter adapter;

    @Override
    protected int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        rv = (RecyclerView) findViewById(R.id.rv);
        adapter = new FirstAdapter(getActivity());
        rv.addItemDecoration(new MyItemDecoration());
        rv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rv.setAdapter(adapter);
        addItem("无限循环view", CircleLayoutActivity.class);
        addItem("循环滚动", RotateActivity.class);
        addItem("标签流式布局", FlowActivity.class);
        addItem("小鱼游泳(移动)", FishSwimActivity.class);
        addItem("侧滑删除", SideDeleteActivity.class);
        addItem("标签切换", BannerExchangeActivity.class);
        adapter.addItemClick(this);
    }

    public void addItem(String name, Class<?> cls) {
        adapter.addItem(new ItemInfo(name, new Intent(getActivity(), cls)));
    }

    @Override
    public void itemClick(int position) {
        ItemInfo info = adapter.getItem(position);
        if (info.intent != null) {
            startActivity(info.intent);
        } else {
            startActivity(new Intent(getActivity(), AllViewActivity.class).putExtra("type", info.type));
        }
    }
}
