package com.example.rico.customerview.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.rico.customerview.ItemInfo;
import com.example.rico.customerview.R;
import com.example.rico.customerview.activity.AllViewActivity;
import com.example.rico.customerview.activity.BannerExchangeActivity;
import com.example.rico.customerview.activity.BannerLayoutActivity;
import com.example.rico.customerview.activity.CardLayoutActivity;
import com.example.rico.customerview.activity.ListLinkageActivity;
import com.example.rico.customerview.activity.RevealActivity;
import com.example.rico.customerview.activity.WaterfallsFlowActivity;
import com.example.rico.customerview.adapter.BaseAdapter;
import com.example.rico.customerview.adapter.FirstAdapter;
import com.example.rico.customerview.view.MyItemDecoration;

/**
 * Created by Tmp on 2019/6/27.
 */
public class HomeFragment3 extends BaseFragment implements BaseAdapter.ItemClick {
    RecyclerView rv;
    FirstAdapter adapter;

    @Override
    int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        rv = (RecyclerView) findViewById(R.id.rv);
        adapter = new FirstAdapter(getActivity());
        rv.addItemDecoration(new MyItemDecoration());
        rv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rv.setAdapter(adapter);
        Context context = getActivity();
        adapter.addItem(new ItemInfo("揭示动画", new Intent(context, RevealActivity.class)));
        adapter.addItem(new ItemInfo("瀑布流", new Intent(context, WaterfallsFlowActivity.class)));
        adapter.addItem(new ItemInfo("Banner RecyclerView", new Intent(context, BannerLayoutActivity.class)));
        adapter.addItem(new ItemInfo("卡片RecyclerView", new Intent(context, CardLayoutActivity.class)));
        adapter.addItem(new ItemInfo("Banner花式RecyclerView", new Intent(context, BannerExchangeActivity.class)));
        adapter.addItem(new ItemInfo("列表联动选择", new Intent(context, ListLinkageActivity.class)));
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
