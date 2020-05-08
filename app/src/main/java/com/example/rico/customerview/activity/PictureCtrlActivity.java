package com.example.rico.customerview.activity;

import android.widget.Toast;

import com.example.rico.customerview.R;
import com.example.rico.customerview.view.JustScaleImageView;
import com.example.rico.customerview.view.TouchToAddView;

public class PictureCtrlActivity extends BaseActivity implements TouchToAddView.PointClickListener {
    @Override
    public int bindLayout() {
        return R.layout.activity_picture_ctrl;
    }

    @Override
    public void doBusiness() {
        JustScaleImageView iv = findViewById(R.id.iv);
//        Glide.with(this).load("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=342882021,2221042042&fm=26&gp=0.jpg").into(iv);
        iv.addProportion(0.1f, 0.1f);
        iv.addProportion(0.2f, 0.2f);
        iv.addProportion(0.3f, 0.3f);
        iv.addProportion(0.4f, 0.4f);
        iv.addProportion(0.7f, 0.7f);
        TouchToAddView tt=findViewById(R.id.tt);
        tt.setPointClickListener(this);

    }

    @Override
    public void click(int position) {
        Toast.makeText(this, "点击了" + position, Toast.LENGTH_SHORT).show();
    }
}
