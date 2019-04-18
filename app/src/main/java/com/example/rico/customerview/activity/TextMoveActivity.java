package com.example.rico.customerview.activity;

import android.view.View;

import com.example.rico.customerview.BaseActivity;
import com.example.rico.customerview.R;
import com.example.rico.customerview.view.ScrollTextView;

import java.util.ArrayList;

/**
 * Created by Tmp on 2018/12/18.
 * 缩放和旋转
 */
public class TextMoveActivity extends BaseActivity {
    ScrollTextView text;

    @Override
    public int bindLayout() {
        return R.layout.activity_scale_rote;
    }

    ArrayList<String> strings;

    @Override
    public void doBusiness() {
        text = findViewById(R.id.text_view);
        strings = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            strings.add("this");
            strings.add("中文");
            strings.add("红中");
            strings.add("corporation");
            strings.add("though");
            strings.add("international");
            strings.add("analysis");
            strings.add("information ");
            strings.add("account");
            strings.add("welcomes");
            strings.add("homepage");
            strings.add("communications");
        }
        text.addText(strings);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.startAnimator();
            }
        });
    }
}
