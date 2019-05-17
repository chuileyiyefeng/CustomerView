package com.example.rico.customerview.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.rico.customerview.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Tmp on 2019/1/8.
 */
public class AllViewActivity extends BaseActivity {
    LinearLayout llALl;
    View view;

    @Override
    public int bindLayout() {
        return R.layout.activity_all;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void doBusiness() {
        llALl = findViewById(R.id.ll_content);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMainThread(View view) {
        if (view != null) {
            llALl.addView(view);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
