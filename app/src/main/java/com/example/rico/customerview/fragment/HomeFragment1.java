package com.example.rico.customerview.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.rico.customerview.ItemInfo;
import com.example.rico.customerview.R;
import com.example.rico.customerview.activity.AllViewActivity;
import com.example.rico.customerview.activity.AnimatorSwitchActivity;
import com.example.rico.customerview.activity.BezierMoveActivity;
import com.example.rico.customerview.activity.CornerImageActivity;
import com.example.rico.customerview.activity.CurveActivity;
import com.example.rico.customerview.activity.DrawBitmapActivity;
import com.example.rico.customerview.activity.FlipBoardActivity;
import com.example.rico.customerview.activity.PuzzleActivity;
import com.example.rico.customerview.activity.RadarActivity;
import com.example.rico.customerview.activity.TextMoveActivity;
import com.example.rico.customerview.adapter.BaseAdapter;
import com.example.rico.customerview.adapter.FirstAdapter;
import com.example.rico.customerview.view.MyItemDecoration;

/**
 * Created by Tmp on 2019/6/27.
 */
public class HomeFragment1 extends BaseFragment implements BaseAdapter.ItemClick {
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
        adapter.addItem(new ItemInfo("圆角ImageView", new Intent(context, CornerImageActivity.class)));
        adapter.addItem(new ItemInfo("文字滚动", new Intent(context, TextMoveActivity.class)));
        adapter.addItem(new ItemInfo("drawBitmap动画", new Intent(context, DrawBitmapActivity.class)));
        adapter.addItem(new ItemInfo("雷达网图", new Intent(context, RadarActivity.class)));
        adapter.addItem(new ItemInfo("贝塞尔曲线", new Intent(context, BezierMoveActivity.class)));
        adapter.addItem(new ItemInfo("上滑翻页", new Intent(context, FlipBoardActivity.class)));
        adapter.addItem(new ItemInfo("switch动画", new Intent(context, AnimatorSwitchActivity.class)));
        adapter.addItem(new ItemInfo("拼图", new Intent(context, PuzzleActivity.class)));
        adapter.addItem(new ItemInfo("曲线统计图", new Intent(context, CurveActivity.class)));

        adapter.addItem(new ItemInfo("小鱼游泳(静止)", 1));
        adapter.addItem(new ItemInfo("path填充模式", 2));
        adapter.addItem(new ItemInfo("不同区域点击", 3));
        adapter.addItem(new ItemInfo("写字板", 4));
        adapter.addItem(new ItemInfo("圆弧SeekBar", 5));
        adapter.addItem(new ItemInfo("气泡波浪", 6));
        adapter.addItem(new ItemInfo("Evaluator(Value)", 7));
        adapter.addItem(new ItemInfo("Evaluator(Object)", 8));
        adapter.addItem(new ItemInfo("翻页", 9));
        adapter.addItem(new ItemInfo("网状view", 10));
        adapter.addItem(new ItemInfo("太阳动画", 11));
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
