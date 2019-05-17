package com.example.rico.customerview.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.rico.customerview.BaseAdapter;
import com.example.rico.customerview.FirstAdapter;
import com.example.rico.customerview.ItemInfo;
import com.example.rico.customerview.R;
import com.example.rico.customerview.activity.AllViewActivity;
import com.example.rico.customerview.activity.BezierMoveActivity;
import com.example.rico.customerview.activity.CircleLayoutActivity;
import com.example.rico.customerview.activity.DrawBitmapActivity;
import com.example.rico.customerview.activity.FishSwimActivity;
import com.example.rico.customerview.activity.FlowActivity;
import com.example.rico.customerview.activity.CornerImageActivity;
import com.example.rico.customerview.activity.MatrixActivity;
import com.example.rico.customerview.activity.RadarActivity;
import com.example.rico.customerview.activity.RotateActivity;
import com.example.rico.customerview.activity.TextMoveActivity;
import com.example.rico.customerview.view.ArcSeekBarView;
import com.example.rico.customerview.view.EvaluatorAttrView;
import com.example.rico.customerview.view.EvaluatorMoveView;
import com.example.rico.customerview.view.FillTypeView;
import com.example.rico.customerview.view.FishSwimView;
import com.example.rico.customerview.view.HandWritingView;
import com.example.rico.customerview.view.ItemDecoration;
import com.example.rico.customerview.view.NetColorView;
import com.example.rico.customerview.view.PageTurningView;
import com.example.rico.customerview.view.PuzzleView;
import com.example.rico.customerview.view.RegionClickView;
import com.example.rico.customerview.view.WaveBubbleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements BaseAdapter.ItemClick {
    RecyclerView rv;
    FirstAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    private void initView() {
        rv = findViewById(R.id.rv);
        adapter = new FirstAdapter(this);
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        rv.setAdapter(adapter);
        Context context = this.getApplicationContext();
        adapter.addItem(new ItemInfo("圆角ImageView", new Intent(context, CornerImageActivity.class)));
        adapter.addItem(new ItemInfo("文字滚动view", new Intent(context, TextMoveActivity.class)));
        adapter.addItem(new ItemInfo("drawBitmap动画", new Intent(context, DrawBitmapActivity.class)));
        adapter.addItem(new ItemInfo("雷达网图", new Intent(context, RadarActivity.class)));
        adapter.addItem(new ItemInfo("贝塞尔曲线", new Intent(context, BezierMoveActivity.class)));
        adapter.addItem(new ItemInfo("无限循环view", new Intent(context, CircleLayoutActivity.class)));
        adapter.addItem(new ItemInfo("循环滚动", new Intent(context, RotateActivity.class)));
        adapter.addItem(new ItemInfo("MatrixSetPoly", new Intent(context, MatrixActivity.class)));
        adapter.addItem(new ItemInfo("流式布局", new Intent(context, FlowActivity.class)));
        adapter.addItem(new ItemInfo("小鱼游泳(移动)", new Intent(context, FishSwimActivity.class)));

        adapter.addItem(new ItemInfo("小鱼游泳(静止)", new FishSwimView(context)));
        adapter.addItem(new ItemInfo("path填充模式", new FillTypeView(context)));
        adapter.addItem(new ItemInfo("不同区域点击", new RegionClickView(context)));
        adapter.addItem(new ItemInfo("写字板", new HandWritingView(context)));
        adapter.addItem(new ItemInfo("圆弧SeekBar", new ArcSeekBarView(context)));
        adapter.addItem(new ItemInfo("气泡波浪", new WaveBubbleView(context)));
        adapter.addItem(new ItemInfo("Evaluator(Value)", new EvaluatorMoveView(context)));
        adapter.addItem(new ItemInfo("Evaluator(Object)", new EvaluatorAttrView(context)));
        adapter.addItem(new ItemInfo("翻页view", new PageTurningView(context)));
        adapter.addItem(new ItemInfo("网状view", new NetColorView(context)));
        adapter.addItem(new ItemInfo("拼图view", new PuzzleView(context)));
        adapter.addItem(new ItemInfo("侧滑删除", new Intent(context, SideDeleteActivity.class)));
        adapter.addItemClick(this);
        rv.addItemDecoration(new ItemDecoration());
    }

    @Override
    public void itemClick(int position) {
        ItemInfo info = adapter.getItem(position);
        if (info.intent != null) {
            startActivity(info.intent);
        } else if (info.view != null) {
            EventBus.getDefault().postSticky(info.view);
            startActivity(new Intent(this, AllViewActivity.class));
        }
    }
}
