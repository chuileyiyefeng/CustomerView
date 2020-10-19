package com.example.rico.customerview.fragment;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rico.customerview.BuildConfig;
import com.example.rico.customerview.R;
import com.example.rico.customerview.activity.AllViewActivity;
import com.example.rico.customerview.activity.BannerLayoutActivity;
import com.example.rico.customerview.activity.CardLayoutActivity;
import com.example.rico.customerview.activity.JumpLoadActivity;
import com.example.rico.customerview.activity.JumpLoadActivity2;
import com.example.rico.customerview.activity.JumpLoadActivity3;
import com.example.rico.customerview.activity.ListLinkageActivity;
import com.example.rico.customerview.activity.PictureCtrlActivity;
import com.example.rico.customerview.activity.PictureViewerActivity;
import com.example.rico.customerview.activity.RevealActivity;
import com.example.rico.customerview.activity.ScrollSignActivity;
import com.example.rico.customerview.activity.WaterfallsFlowActivity;
import com.example.rico.customerview.adapter.BaseAdapter;
import com.example.rico.customerview.adapter.FirstAdapter;
import com.example.rico.customerview.bean.ItemInfo;
import com.example.rico.customerview.view.MyItemDecoration;

/**
 * Created by Tmp on 2019/6/27.
 */
public class HomeFragment3 extends BaseFragment implements BaseAdapter.ItemClick {
    private RecyclerView rv;
    private FirstAdapter adapter;

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
        Context context = getActivity();
        adapter.addItem(new ItemInfo("揭示动画", new Intent(context, RevealActivity.class)));
        adapter.addItem(new ItemInfo("瀑布流", new Intent(context, WaterfallsFlowActivity.class)));
        adapter.addItem(new ItemInfo("Banner RecyclerView", new Intent(context, BannerLayoutActivity.class)));
        adapter.addItem(new ItemInfo("卡片RecyclerView", new Intent(context, CardLayoutActivity.class)));
        adapter.addItem(new ItemInfo("列表联动选择", new Intent(context, ListLinkageActivity.class)));
        adapter.addItem(new ItemInfo("图片浏览", new Intent(context, PictureViewerActivity.class)));
        adapter.addItem(new ItemInfo("图片操作", new Intent(context, PictureCtrlActivity.class)));
        adapter.addItem(new ItemInfo("滑动标签", new Intent(context, ScrollSignActivity.class)));
//        adapter.addItem(new ItemInfo("上下拉刷新", new Intent(context, JumpLoadActivity.class)));
        adapter.addItem(new ItemInfo("上下拉刷新 结合SwipeRefreshLayout", new Intent(context, JumpLoadActivity2.class)));
        adapter.addItem(new ItemInfo("刷新 空状态", new Intent(context, JumpLoadActivity3.class)));
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
