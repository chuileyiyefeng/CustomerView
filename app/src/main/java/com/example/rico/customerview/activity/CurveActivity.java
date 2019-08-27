package com.example.rico.customerview.activity;

import com.example.rico.customerview.bean.CurveData;
import com.example.rico.customerview.R;
import com.example.rico.customerview.view.CurveLinearLayout;

import java.util.ArrayList;

/**
 * Created by Tmp on 2019/8/5.
 */
public class CurveActivity extends BaseActivity {
    CurveLinearLayout curveLinear;
    ArrayList<CurveData> list;

    @Override
    public int bindLayout() {
        return R.layout.activity_curve;
    }

    @Override
    public void doBusiness() {
        curveLinear = findViewById(R.id.ll_curve);
        list=new ArrayList<>();
        list.add(new CurveData(300, "一月"));
        list.add(new CurveData(950, "二月"));
        list.add(new CurveData(200, "三月"));
        list.add(new CurveData(600, "四月"));
        list.add(new CurveData(200, "五月"));
        list.add(new CurveData(280, "六月"));
        list.add(new CurveData(700, "七月"));
        list.add(new CurveData(320, "八月"));
        list.add(new CurveData(150, "九月"));
        list.add(new CurveData(300, "十月"));
        list.add(new CurveData(121, "十一月"));
        list.add(new CurveData(221, "十二月"));
        curveLinear.setData(list);
    }
}
