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
        pathView.addData("第一",0.45);
        pathView.addData("埃里克结果",0.565);
        pathView.addData("安抚",0.85);
        pathView.addData("阿斯蒂芬",0.75);
        pathView.addData("爱上广告公司",0.25);
        pathView.addData("改为耳听为我",0.65);
        pathView.draw();

        pathView.clearData();
        pathView.addData("第一",0.75);
        pathView.addData("第二",0.25);
        pathView.addData("第三",0.65);
        pathView.addData("第四",0.85);
        pathView.addData("第五",0.375);
        pathView.addData("第六",0.675);
        pathView.addData("第七",0.675);
        pathView.addData("第八",0.675);
        pathView.addData("第九",0.675);
        pathView.draw();
        llALl.addView(pathView);
    }
}
