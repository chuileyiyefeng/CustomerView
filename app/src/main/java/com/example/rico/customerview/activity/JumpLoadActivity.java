package com.example.rico.customerview.activity;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rico.customerview.R;
import com.example.rico.customerview.adapter.BaseAdapter;
import com.example.rico.customerview.adapter.FirstAdapter;
import com.example.rico.customerview.bean.ItemInfo;
import com.example.rico.customerview.view.JumpLoadView;
import com.example.rico.customerview.view.MyItemDecoration;

import java.lang.ref.WeakReference;

public class JumpLoadActivity extends BaseActivity {
    FirstAdapter adapter;
    RecyclerView rv;
    JumpLoadView jumpLoadView;
    MyHandler handler;

    @Override
    public int bindLayout() {
        return R.layout.activity_jump_load;
    }

    @Override
    public void doBusiness() {
        handler = new MyHandler(this);
        rv = findViewById(R.id.rv);
        jumpLoadView = findViewById(R.id.jump_load);
        jumpLoadView.connect(rv);
        adapter = new FirstAdapter(this);
        rv.addItemDecoration(new MyItemDecoration());
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        for (int i = 0; i <5; i++) {
            adapter.addItem(new ItemInfo("这是item" + i, null));
        }
        jumpLoadView.setLoadListener(new JumpLoadView.LoadListener() {
            @Override
            public void loadMore() {
                handler.sendEmptyMessageDelayed(1, 300);
            }

            @Override
            public void refresh() {
                handler.sendEmptyMessageDelayed(0, 300);
            }
        });
        adapter.addItemClick(position -> Toast.makeText(JumpLoadActivity.this,"点击了 "+position,Toast.LENGTH_SHORT).show());
    }

    static class MyHandler extends Handler {
        WeakReference<JumpLoadActivity> reference;

        MyHandler(JumpLoadActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    // 下拉刷新
                    reference.get().adapter.clearAllItem();

                    for (int i = 0; i < 5; i++) {
                        long time = System.currentTimeMillis();
                        reference.get().adapter.addItem(new ItemInfo("这是刷新item 时间：" + time, null));
                    }
                    break;
                case 1:
                    // 上拉更多
                    for (int i = 0; i < 5; i++) {
                        reference.get().adapter.addItem(new ItemInfo("这是上拉item" + i, null));
                    }
                    break;
            }
            reference.get().rv.post(() -> reference.get().jumpLoadView.reductionScroll());
        }
    }
}
