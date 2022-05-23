package com.example.rico.customerview.activity;


import android.view.View;
import android.widget.TextView;

import com.example.rico.customerview.R;
import com.example.rico.customerview.view.WaveView;

/**
 * @Description:
 * @Author: pan yi
 * @Date: 2022/5/23
 */
public class LoveViewActivity extends BaseActivity {
    TextView tvStart, tvUp, tvDown;
    WaveView waveView;

    @Override
    public int bindLayout() {
        return R.layout.activity_love;
    }

    @Override
    public void doBusiness() {
        tvStart = findViewById(R.id.tv_start);
        tvUp = findViewById(R.id.tv_up);
        tvDown = findViewById(R.id.tv_down);
        waveView = findViewById(R.id.wave_view);
        tvStart.setOnClickListener(v -> {
            waveView.startAnimator();
        });
        tvUp.setOnClickListener(v -> {

        });
        tvDown.setOnClickListener(v -> {

        });
    }
}
