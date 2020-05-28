package com.example.rico.customerview.activity;

import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

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
        RecyclerView.LayoutManager manager = new BannerLayoutManager(this);
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);
        for (int i = 0; i < 10; i++) {
            adapter.addItem("this is item " + i);
        }
        new LinearSnapHelper().attachToRecyclerView(rv);
    }

}
