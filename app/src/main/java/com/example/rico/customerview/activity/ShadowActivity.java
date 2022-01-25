package com.example.rico.customerview.activity;

import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rico.customerview.R;
import com.example.rico.customerview.adapter.DeleteAdapter;
import com.example.rico.customerview.view.MyItemDecoration;
import com.example.rico.customerview.view.SideTextView;

/**
 * create by pan yi on 2020/12/30
 * desc : 阴影
 */
public class ShadowActivity extends BaseActivity {
    RecyclerView rvDelete;
    DeleteAdapter adapter;

    @Override
    public int bindLayout() {
        return R.layout.activity_shadow;
    }

    @Override
    public void doBusiness() {
        rvDelete = findViewById(R.id.rv);
        rvDelete.setLayoutManager(new LinearLayoutManager(this));
        rvDelete.addItemDecoration(new MyItemDecoration());
        adapter = new DeleteAdapter(this);
        for (int i = 0; i < 10; i++) {
            adapter.addItem("第 " + i + " 项");
        }
        rvDelete.setAdapter(adapter);
    }
}
