package com.example.rico.customerview.activity;

import android.widget.LinearLayout;

import com.example.rico.customerview.BaseActivity;
import com.example.rico.customerview.R;
import com.example.rico.customerview.view.HexagonPathView;

/**
 * Created by Tmp on 2019/1/3.
 */
public class RadarActivity extends BaseActivity {
    LinearLayout llALl;

    @Override
    public int bindLayout() {
        return R.layout.activity_all;
    }
    @Override
    public void doBusiness() {
        llALl = findViewById(R.id.ll_content);
        HexagonPathView pathView = new HexagonPathView(this);
        pathView.clearData();
        pathView.addData("第一",0.75);
        pathView.addData("第二",0.95);
        pathView.addData("第三",0.65);
        pathView.addData("第四",0.85);
        pathView.addData("第五",0.375);
        pathView.addData("第六",0.675);
        pathView.addData("第七",0.655);
        pathView.addData("第八",0.475);
        pathView.addData("第九",0.285);
        pathView.draw();
        llALl.addView(pathView);
        pathView.setLevelCount(4);
    }
}
