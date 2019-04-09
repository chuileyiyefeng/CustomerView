package com.example.rico.customerview;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.example.rico.customerview.activity.AllViewActivity;
import com.example.rico.customerview.activity.CircleLayoutActivity;
import com.example.rico.customerview.activity.DrawBitmapActivity;
import com.example.rico.customerview.activity.FishSwimActivity;
import com.example.rico.customerview.activity.FlowActivity;
import com.example.rico.customerview.activity.PieActivity;
import com.example.rico.customerview.activity.RadarActivity;
import com.example.rico.customerview.activity.ScalAndRoteActivity;
import com.example.rico.customerview.view.ItemDecoration;

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
        adapter.addItem("1.饼状图");
        adapter.addItem("2.缩放旋转");
        adapter.addItem("3.drawBitmap 动画");
        adapter.addItem("4.雷达网图");
        adapter.addItem("5.贝塞尔曲线");
        adapter.addItem("6.path填充模式");
        adapter.addItem("7.不同区域点击");
        adapter.addItem("8.写字板");
        adapter.addItem("9.圆弧SeekBar");
        adapter.addItem("10.layout改变测试");
        adapter.addItem("11.流式布局");
        adapter.addItem("12.循环滚动view");
        adapter.addItem("13.气泡波浪");
        adapter.addItem("14.小鱼游泳(静止)");
        adapter.addItem("15.小鱼游泳(移动)");
        adapter.addItem("16.Evaluator(ValueAnimator)");
        adapter.addItem("17.Evaluator(ObjectAnimator)");
        adapter.addItem("18.翻页view");
        adapter.addItem("19.网状view");
        adapter.addItemClick(this);
        rv.addItemDecoration(new ItemDecoration());
    }

    @Override
    public void itemClick(int position) {
        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent(this, PieActivity.class);
                break;
            case 1:
                intent = new Intent(this, ScalAndRoteActivity.class);
                break;
            case 2:
                intent = new Intent(this, DrawBitmapActivity.class);
                break;
            case 3:
                intent = new Intent(this, RadarActivity.class);
                break;
            case 10:
                intent = new Intent(this, FlowActivity.class);
                break;
            case 11:
                intent = new Intent(this, CircleLayoutActivity.class);
                break;
            case 14:
                intent = new Intent(this, FishSwimActivity.class);
                break;
            default:
                intent = new Intent(this, AllViewActivity.class).putExtra("type", position);
                break;
        }
        startActivity(intent);
    }

}
