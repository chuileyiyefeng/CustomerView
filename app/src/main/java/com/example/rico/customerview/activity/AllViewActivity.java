package com.example.rico.customerview.activity;

import android.view.View;
import android.widget.LinearLayout;

import com.example.rico.customerview.BaseActivity;
import com.example.rico.customerview.R;
import com.example.rico.customerview.view.BezierView;

/**
 * Created by Tmp on 2019/1/8.
 */
public class AllViewActivity extends BaseActivity {
    LinearLayout llALl;
    int type;

    @Override
    public int bindLayout() {
        return R.layout.activity_all;
    }

    @Override
    public void doBusiness() {
        llALl = findViewById(R.id.ll_content);
        type = getIntent().getIntExtra("type", 1);
        View view;
        switch (type) {
            case 4:
                view = new BezierView(this);
                break;
            default:
                view = new View(this);
                break;
        }
        llALl.addView(view);
    }
}
