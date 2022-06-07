package com.example.rico.customerview.activity;


import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rico.customerview.R;
import com.example.rico.customerview.view.DrawBitmapWaveView;
import com.example.rico.customerview.view.WaveDrawable;
import com.example.rico.customerview.view.WaveView;

/**
 * @Description:
 * @Author: pan yi
 * @Date: 2022/5/23
 */
public class LoveViewActivity extends BaseActivity {
    TextView tvStart, tvUp, tvDown;
    WaveView waveView;
    int progress = 10;

    ImageView imageView;

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
        imageView = findViewById(R.id.iv_image);

        tvUp.setOnClickListener(v -> {
            waveView.setProgress(progress);
            progress += 10;
        });
        tvDown.setOnClickListener(v -> {
            waveView.setProgress(progress);
            progress -= 10;
        });

        WaveDrawable waveDrawable = new WaveDrawable(this, R.mipmap.chrome_logo);
        imageView.setImageDrawable(waveDrawable);


        waveDrawable.setLevel(800);
        waveDrawable.setWaveAmplitude(10);
        waveDrawable.setWaveLength(50);
        waveDrawable.setWaveSpeed(50);

        DrawBitmapWaveView wave = findViewById(R.id.wave_drawable);
        wave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wave.setBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.love_full));
            }
        });

    }
}
