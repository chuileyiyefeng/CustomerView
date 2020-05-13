package com.example.rico.customerview.activity;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.rico.customerview.R;
import com.example.rico.customerview.adapter.ExchangeItemAdapter;
import com.example.rico.customerview.layoutManager.ExchangeLayoutManager;

/**
 * Created by Tmp on 2019/7/30.
 */
public class BannerExchangeActivity extends BaseActivity {
    @Override
    public int bindLayout() {
        return R.layout.activity_exchange_view;
    }

    @Override
    public void doBusiness() {
    }
}
