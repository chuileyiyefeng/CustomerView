package com.example.rico.customerview.activity;

import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.example.rico.customerview.BaseActivity;
import com.example.rico.customerview.R;
import com.example.rico.customerview.view.ArcSeekBarView;
import com.example.rico.customerview.view.BaseView;
import com.example.rico.customerview.view.BezierView;
import com.example.rico.customerview.view.EvaluatorAttrView;
import com.example.rico.customerview.view.EvaluatorMoveView;
import com.example.rico.customerview.view.FillTypeView;
import com.example.rico.customerview.view.FishSwimView;
import com.example.rico.customerview.view.FlowView;
import com.example.rico.customerview.view.HandWritingView;
import com.example.rico.customerview.view.RegionClickView;
import com.example.rico.customerview.view.WaveBubbleView;

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
            case 5:
                view = new FillTypeView(this);
                break;
            case 6:
                view = new RegionClickView(this);
                break;
            case 7:
                view = new HandWritingView(this);
                break;
            case 8:
                view = new ArcSeekBarView(this);
                break;
            case 9:
                view = new BaseView(this);
                break;
            case 12:
                view = new WaveBubbleView(this);
                break;
            case 13:
                view = new FishSwimView(this);
                break;
            case 15:
                view = new EvaluatorMoveView(this);
                break;
            case 16:
                view = new EvaluatorAttrView(this);
                break;
            default:
                view = new View(this);
                break;
        }
        llALl.addView(view);
    }
}
