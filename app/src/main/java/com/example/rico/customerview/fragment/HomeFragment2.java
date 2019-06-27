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
public class HomeFragment2 extends Fragment implements BaseAdapter.ItemClick {
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
        adapter.addItem(new ItemInfo("无限循环view", new Intent(context, CircleLayoutActivity.class)));
        adapter.addItem(new ItemInfo("循环滚动", new Intent(context, RotateActivity.class)));
        adapter.addItem(new ItemInfo("标签流式布局", new Intent(context, FlowActivity.class)));
        adapter.addItem(new ItemInfo("小鱼游泳(移动)", new Intent(context, FishSwimActivity.class)));
        adapter.addItem(new ItemInfo("侧滑删除", new Intent(context, SideDeleteActivity.class)));

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
