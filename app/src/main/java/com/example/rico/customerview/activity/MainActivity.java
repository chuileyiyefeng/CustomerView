package com.example.rico.customerview.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.rico.customerview.adapter.BaseAdapter;
import com.example.rico.customerview.adapter.FirstAdapter;
import com.example.rico.customerview.ItemInfo;
import com.example.rico.customerview.R;
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
        adapter.addItem(new ItemInfo("标签布局", new Intent(context, FlowActivity.class)));
        adapter.addItem(new ItemInfo("小鱼游泳(移动)", new Intent(context, FishSwimActivity.class)));

        adapter.addItem(new ItemInfo("小鱼游泳(静止)", 1));
        adapter.addItem(new ItemInfo("path填充模式", 2));
        adapter.addItem(new ItemInfo("不同区域点击", 3));
        adapter.addItem(new ItemInfo("写字板",4));
        adapter.addItem(new ItemInfo("圆弧SeekBar", 5));
        adapter.addItem(new ItemInfo("气泡波浪",6));
        adapter.addItem(new ItemInfo("Evaluator(Value)", 7));
        adapter.addItem(new ItemInfo("Evaluator(Object)",8));
        adapter.addItem(new ItemInfo("翻页view", 9));
        adapter.addItem(new ItemInfo("网状view", 10));
        adapter.addItem(new ItemInfo("拼图view", 11));

        adapter.addItem(new ItemInfo("侧滑删除", new Intent(context, SideDeleteActivity.class)));
        adapter.addItem(new ItemInfo("转场动画", new Intent(context, RevealActivity.class)));
        adapter.addItemClick(this);
        rv.addItemDecoration(new ItemDecoration());
    }

    @Override
    public void itemClick(int position) {
        ItemInfo info = adapter.getItem(position);
        if (info.intent != null) {
            startActivity(info.intent);
        } else {
            startActivity(new Intent(this, AllViewActivity.class).putExtra("type", info.type));
        }
    }
}
