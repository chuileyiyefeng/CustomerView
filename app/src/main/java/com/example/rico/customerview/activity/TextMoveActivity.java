package com.example.rico.customerview.activity;

import android.view.View;

import com.example.rico.customerview.R;
import com.example.rico.customerview.view.ScrollTextView;
import com.example.rico.customerview.view.VerticalScrollTextView;

import java.util.ArrayList;

/**
 * Created by Tmp on 2018/12/18.
 * 缩放和旋转
 */
public class TextMoveActivity extends BaseActivity {
    ScrollTextView text;
    VerticalScrollTextView scrollTextView;

    @Override
    public int bindLayout() {
        return R.layout.activity_scale_rote;
    }

    ArrayList<String> strings;

    @Override
    public void doBusiness() {
        text = findViewById(R.id.text_view);
        scrollTextView = findViewById(R.id.scroll_tv);
        strings = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            strings.add("recognitions");
            strings.add("中文");
            strings.add("红中");
            strings.add("corporation");
            strings.add("technologies");
            strings.add("international");
            strings.add("analysis");
            strings.add("information");
            strings.add("partnership");
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
        scrollTextView.setStrings(strings);
        scrollTextView.setTextSize(30);
        findViewById(R.id.tv_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollTextView.startAnimator();
            }
        });
    }
}
