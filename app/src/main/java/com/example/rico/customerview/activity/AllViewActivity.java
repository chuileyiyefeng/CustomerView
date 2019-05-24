package com.example.rico.customerview.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.example.rico.customerview.R;
import com.example.rico.customerview.view.ArcSeekBarView;
import com.example.rico.customerview.view.EvaluatorAttrView;
import com.example.rico.customerview.view.EvaluatorMoveView;
import com.example.rico.customerview.view.FillTypeView;
import com.example.rico.customerview.view.FishSwimView;
import com.example.rico.customerview.view.HandWritingView;
import com.example.rico.customerview.view.NetColorView;
import com.example.rico.customerview.view.PageTurningView;
import com.example.rico.customerview.view.PuzzleView;
import com.example.rico.customerview.view.RegionClickView;
import com.example.rico.customerview.view.WaveBubbleView;

/**
 * Created by Tmp on 2019/1/8.
 */
public class AllViewActivity extends BaseActivity {
    LinearLayout llALl;

    @Override
    public int bindLayout() {
        return R.layout.activity_all;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void doBusiness() {
        llALl = findViewById(R.id.ll_content);
        int type = getIntent().getIntExtra("type", -1);
        View view;
        Context context = AllViewActivity.this;
        switch (type) {
            case 1:
                view = new FishSwimView(context);
                break;
            case 2:
                view = new FillTypeView(context);
                break;
            case 3:
                view = new RegionClickView(context);
                break;
            case 4:
                view = new HandWritingView(context);
                break;
            case 5:
                view = new ArcSeekBarView(context);
                break;
            case 6:
                view = new WaveBubbleView(context);
                break;
            case 7:
                view = new EvaluatorMoveView(context);
                break;
            case 8:
                view = new EvaluatorAttrView(context);
                break;
            case 9:
                view = new PageTurningView(context);
                break;
            case 10:
                view = new NetColorView(context);
                break;
            case 11:
                view = new PuzzleView(context);
                break;
            default:
                view = new View(context);
                break;
        }
        llALl.addView(view);
    }
}
