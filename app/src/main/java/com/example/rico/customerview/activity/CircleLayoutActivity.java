package com.example.rico.customerview.activity;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rico.customerview.R;
import com.example.rico.customerview.view.CircleLayoutView;

/**
 * Created by Tmp on 2019/2/21.
 */
public class CircleLayoutActivity extends BaseActivity implements View.OnClickListener {
    TextView lastTv, nextTv, oneTv;
    CircleLayoutView circleView;

    @Override
    public int bindLayout() {
        return R.layout.activity_circle;
    }

    @Override
    public void doBusiness() {
        lastTv = findViewById(R.id.tv_last);
        nextTv = findViewById(R.id.tv_next);
        oneTv = findViewById(R.id.tv_one);
        circleView = findViewById(R.id.circle_view);
        lastTv.setOnClickListener(this);
        nextTv.setOnClickListener(this);
        oneTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_last:
                circleView.moveLast();
                break;
            case R.id.tv_next:
                circleView.moveNext();
                break;
            case R.id.tv_one:
                Toast.makeText(CircleLayoutActivity.this, "子view点击了", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
