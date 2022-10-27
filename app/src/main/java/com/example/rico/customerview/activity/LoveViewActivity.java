package com.example.rico.customerview.activity;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rico.customerview.R;
import com.example.rico.customerview.view.DrawBitmapWaveView;
import com.example.rico.customerview.view.WaveDrawable;
import com.example.rico.customerview.view.WaveDrawable2;
import com.example.rico.customerview.view.WaveView;

/**
 * @Description:
 * @Author: pan yi
 * @Date: 2022/5/23
 */
public class LoveViewActivity extends BaseActivity {
    TextView tvStart, tvUp, tvDown;
    WaveView waveView;
    int progress = 50;

    ImageView imageView,imageView2;

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
        imageView2 = findViewById(R.id.iv_image2);

        tvUp.setOnClickListener(v -> {
            waveView.setProgress(progress);
            progress += 10;
        });
        tvDown.setOnClickListener(v -> {
            waveView.setProgress(progress);
            progress -= 10;
        });

        WaveDrawable2 waveDrawable = new WaveDrawable2(this, R.mipmap.love_progress);
        WaveDrawable2 waveDrawable2 = new WaveDrawable2(this, R.mipmap.love_progress);
        waveDrawable2.setBg(true);
        imageView.setImageDrawable(waveDrawable);
        imageView2.setImageDrawable(waveDrawable2);
        waveDrawable.setMax(1000);
        waveDrawable.setProgress(500f);

        waveDrawable2.setMax(1000);
        waveDrawable2.setProgress(500f);



        DrawBitmapWaveView wave = findViewById(R.id.wave_drawable);
        Bitmap bgBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.love_hollow);
        Bitmap drawBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.love_progress);
        wave.setDrawBitmap(bgBitmap, drawBitmap);
//        wave.setProgress(progress);
        wave.setSpeed(3f);
        wave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wave.setProgress(progress);
                progress += 10;
            }
        });

    }
}
