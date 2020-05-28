package com.example.rico.customerview.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rico.customerview.R;
import com.example.rico.customerview.adapter.FirstAdapter;
import com.example.rico.customerview.bean.ItemInfo;
import com.example.rico.customerview.view.JumpLoadView;
import com.example.rico.customerview.view.MyItemDecoration;

public class JumpLoadActivity extends BaseActivity {
    FirstAdapter adapter;
    RecyclerView rv;
    JumpLoadView jumpLoadView;

    @Override
    public int bindLayout() {
        return R.layout.activity_jump_load;
    }

    @Override
    public void doBusiness() {
        rv = findViewById(R.id.rv);
        jumpLoadView = findViewById(R.id.jump_load);
        jumpLoadView.connect(rv);
        adapter = new FirstAdapter(this);
        rv.addItemDecoration(new MyItemDecoration());
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        for (int i = 0; i < 15; i++) {
            adapter.addItem(new ItemInfo("这是item" + i, null));
        }
        jumpLoadView.setLoadListener(new JumpLoadView.LoadListener() {
            @Override
            public void loadMore() {

            }

            @Override
            public void refresh() {

            }
        });
    }
}
