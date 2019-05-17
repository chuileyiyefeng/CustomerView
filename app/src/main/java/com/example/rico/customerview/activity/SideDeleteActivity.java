package com.example.rico.customerview.activity;

import android.view.View;
import android.widget.Button;

import com.example.rico.customerview.R;
import com.example.rico.customerview.view.SideDeleteView;

/**
 * Created by Tmp on 2019/5/16.
 */
public class SideDeleteActivity extends BaseActivity {
    Button button;
    @Override
    public int bindLayout() {
        return R.layout.activity_side_delete;
    }

    @Override
    public void doBusiness() {
        final SideDeleteView deleteView=findViewById(R.id.side_delete);
        button=findViewById(R.id.btn_click);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteView.closeSide();
            }
        });
    }
}
