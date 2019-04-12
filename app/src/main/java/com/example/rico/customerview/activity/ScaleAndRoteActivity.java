package com.example.rico.customerview.activity;

import android.view.View;

import com.example.rico.customerview.BaseActivity;
import com.example.rico.customerview.R;
import com.example.rico.customerview.view.ScrollTextView;

/**
 * Created by Tmp on 2018/12/18.
 * 缩放和旋转
 */
public class ScaleAndRoteActivity extends BaseActivity {
    ScrollTextView text;

    @Override
    public int bindLayout() {
        return R.layout.activity_scale_rote;
    }

    @Override
    public void doBusiness() {
        text = findViewById(R.id.text_view);
        text.setText("this is test god");
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.startAnimator();
            }
        });
    }
}
