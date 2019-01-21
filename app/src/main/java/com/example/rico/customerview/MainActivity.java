package com.example.rico.customerview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.rico.customerview.activity.AllViewActivity;
import com.example.rico.customerview.activity.DrawBitmapActivity;
import com.example.rico.customerview.activity.PieActivity;
import com.example.rico.customerview.activity.RadarActivity;
import com.example.rico.customerview.activity.ScalAndRoteActivity;

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
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        adapter.addItem("饼状图");
        adapter.addItem("缩放旋转");
        adapter.addItem("drawBitmap 动画");
        adapter.addItem("雷达网图");
        adapter.addItem("贝塞尔曲线");
        adapter.addItemClick(this);
    }

    @Override
    public void click(int position) {
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
            default:
                intent = new Intent(this, AllViewActivity.class).putExtra("type", position);
                break;
        }
        startActivity(intent);
    }

}
