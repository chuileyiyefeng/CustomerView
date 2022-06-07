package com.example.rico.customerview.fragment;

import android.content.Intent;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rico.customerview.R;
import com.example.rico.customerview.activity.AllViewActivity;
import com.example.rico.customerview.activity.BadgeDrawableTestActivity;
import com.example.rico.customerview.activity.BannerLayoutActivity;
import com.example.rico.customerview.activity.CardLayoutActivity;
import com.example.rico.customerview.activity.ChangeChildViewActivity;
import com.example.rico.customerview.activity.CropImageActivity;
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
    private FirstAdapter adapter;

    @Override
    protected int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        adapter = new FirstAdapter(getActivity());
        rv.addItemDecoration(new MyItemDecoration());
        rv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rv.setAdapter(adapter);
        addItem("揭示动画",RevealActivity.class);
        addItem("瀑布流",  WaterfallsFlowActivity.class);
        addItem("Banner RecyclerView",  BannerLayoutActivity.class);
        addItem("卡片RecyclerView",  CardLayoutActivity.class);
        addItem("列表联动选择",  ListLinkageActivity.class);
        addItem("图片浏览",  PictureViewerActivity.class);
        addItem("图片操作",  PictureCtrlActivity.class);
        addItem("滑动标签",  ScrollSignActivity.class);
//        addItem("上下拉刷新",  JumpLoadActivity.class);
        addItem("上下拉刷新 结合SwipeRefreshLayout",  JumpLoadActivity2.class);
        addItem("刷新 空状态",  JumpLoadActivity3.class);
        addItem("viewPager 切换效果",  ChangeChildViewActivity.class);
        addItem("裁剪框",  CropImageActivity.class);
        addItem("红点测试",  BadgeDrawableTestActivity.class);
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
