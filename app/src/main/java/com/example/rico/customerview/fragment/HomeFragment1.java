package com.example.rico.customerview.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rico.customerview.ItemInfo;
import com.example.rico.customerview.R;
import com.example.rico.customerview.activity.AllViewActivity;
import com.example.rico.customerview.activity.AnimatorSwitchActivity;
import com.example.rico.customerview.activity.BezierMoveActivity;
import com.example.rico.customerview.activity.CircleLayoutActivity;
import com.example.rico.customerview.activity.CornerImageActivity;
import com.example.rico.customerview.activity.DrawBitmapActivity;
import com.example.rico.customerview.activity.FishSwimActivity;
import com.example.rico.customerview.activity.FlipBoardActivity;
import com.example.rico.customerview.activity.FlowActivity;
import com.example.rico.customerview.activity.MatrixActivity;
import com.example.rico.customerview.activity.RadarActivity;
import com.example.rico.customerview.activity.RevealActivity;
import com.example.rico.customerview.activity.RotateActivity;
import com.example.rico.customerview.activity.SideDeleteActivity;
import com.example.rico.customerview.activity.TextMoveActivity;
import com.example.rico.customerview.adapter.BaseAdapter;
import com.example.rico.customerview.adapter.FirstAdapter;

/**
 * Created by Tmp on 2019/6/27.
 */
public class HomeFragment1 extends Fragment implements BaseAdapter.ItemClick {
    View view;
    RecyclerView rv;
    FirstAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_main, container, false);
        rv = view.findViewById(R.id.rv);
        adapter = new FirstAdapter(getActivity());
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(adapter);
        Context context = getActivity();
        adapter.addItem(new ItemInfo("圆角ImageView", new Intent(context, CornerImageActivity.class)));
        adapter.addItem(new ItemInfo("文字滚动", new Intent(context, TextMoveActivity.class)));
        adapter.addItem(new ItemInfo("drawBitmap动画", new Intent(context, DrawBitmapActivity.class)));
        adapter.addItem(new ItemInfo("雷达网图", new Intent(context, RadarActivity.class)));
        adapter.addItem(new ItemInfo("贝塞尔曲线", new Intent(context, BezierMoveActivity.class)));
        adapter.addItem(new ItemInfo("上滑翻页", new Intent(context, FlipBoardActivity.class)));
        adapter.addItem(new ItemInfo("switch动画", new Intent(context, AnimatorSwitchActivity.class)));


        adapter.addItem(new ItemInfo("小鱼游泳(静止)"));
        adapter.addItem(new ItemInfo("path填充模式"));
        adapter.addItem(new ItemInfo("不同区域点击"));
        adapter.addItem(new ItemInfo("写字板"));
        adapter.addItem(new ItemInfo("圆弧SeekBar"));
        adapter.addItem(new ItemInfo("气泡波浪"));
        adapter.addItem(new ItemInfo("Evaluator(Value)"));
        adapter.addItem(new ItemInfo("Evaluator(Object)"));
        adapter.addItem(new ItemInfo("翻页"));
        adapter.addItem(new ItemInfo("网状view"));
        adapter.addItem(new ItemInfo("拼图"));
        adapter.addItem(new ItemInfo("太阳动画"));
        adapter.addItemClick(this);
        return view;
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
