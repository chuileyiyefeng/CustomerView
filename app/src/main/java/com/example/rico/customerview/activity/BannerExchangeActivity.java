package com.example.rico.customerview.activity;

import android.support.v7.widget.RecyclerView;

import com.example.rico.customerview.R;
import com.example.rico.customerview.adapter.ExchangeItemAdapter;
import com.example.rico.customerview.layoutManager.ExchangeLayoutManager;

/**
 * Created by Tmp on 2019/7/30.
 */
public class BannerExchangeActivity extends BaseActivity {
    RecyclerView rv;
    ExchangeItemAdapter adapter;
    @Override
    public int bindLayout() {
        return R.layout.activity_recycler;
    }

    @Override
    public void doBusiness() {
        rv = findViewById(R.id.rv);
        adapter=new ExchangeItemAdapter(this);
        rv.setLayoutManager(new ExchangeLayoutManager());
        rv.setAdapter(adapter);
        adapter.addItem(R.mipmap.flip_1);
        adapter.addItem(R.mipmap.flip_2);
        adapter.addItem(R.mipmap.flip_3);
    }
}
