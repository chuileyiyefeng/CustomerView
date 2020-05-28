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
    protected  int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        rv = (RecyclerView) findViewById(R.id.rv);
        adapter = new FirstAdapter(getActivity());
        rv.addItemDecoration(new MyItemDecoration());
        rv.setLayoutManager(new GridLayoutManager(getActivity(),2));
        rv.setAdapter(adapter);
        Context context = getActivity();
        adapter.addItem(new ItemInfo("无限循环view", new Intent(context, CircleLayoutActivity.class)));
        adapter.addItem(new ItemInfo("循环滚动", new Intent(context, RotateActivity.class)));
        adapter.addItem(new ItemInfo("标签流式布局", new Intent(context, FlowActivity.class)));
        adapter.addItem(new ItemInfo("小鱼游泳(移动)", new Intent(context, FishSwimActivity.class)));
        adapter.addItem(new ItemInfo("侧滑删除", new Intent(context, SideDeleteActivity.class)));
        adapter.addItem(new ItemInfo("标签切换",  new Intent(context, BannerExchangeActivity.class)));
        adapter.addItemClick(this);
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
