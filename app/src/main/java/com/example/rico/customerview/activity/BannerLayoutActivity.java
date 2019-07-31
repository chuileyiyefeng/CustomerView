package com.example.rico.customerview.activity;

import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;

import com.example.rico.customerview.R;
import com.example.rico.customerview.adapter.BannerAdapter;
import com.example.rico.customerview.layoutManager.BannerLayoutManager;

/**
 * Created by Tmp on 2019/7/23.
 */
public class BannerLayoutActivity extends BaseActivity {
    RecyclerView rv;
    BannerAdapter adapter;

    @Override
    public int bindLayout() {
        return R.layout.activity_recycler;
    }

    @Override
    public void doBusiness() {
        rv = findViewById(R.id.rv);
        adapter = new BannerAdapter(this);
        RecyclerView.LayoutManager manager = new BannerLayoutManager();
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);
        for (int i = 0; i < 10; i++) {
            adapter.addItem("this is item " + i);
        }
        new LinearSnapHelper().attachToRecyclerView(rv);
    }

}
