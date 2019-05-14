package com.example.rico.customerview.activity;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.example.rico.customerview.R;
import com.example.rico.customerview.view.FlowView;

import java.util.ArrayList;

/**
 * Created by Tmp on 2019/2/19.
 */
public class FlowActivity extends BaseActivity {
    FlowView flow;
    ArrayList<String> list;

    @Override
    public int bindLayout() {
        return R.layout.flow;
    }

    @Override
    public void doBusiness() {
        list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add("战争女神 ");
            list.add("蒙多战争女神战争女神战争女神战争女神战争女神");
            list.add("德玛西亚皇子");
            list.add("殇之木乃伊");
            list.add("狂战士");
            list.add("布里茨克拉克");
            list.add("冰晶凤凰");
            list.add("德邦总管");
            list.add("野兽之灵乌迪尔");
            list.add("塞恩");
            list.add("诡术妖姬");
            list.add("永恒梦魇");
            list.add("诺克萨斯之手");
        }
        flow = findViewById(R.id.flow);
        flow.setMargin(5, 5);
        for (int i = 0; i < list.size(); i++) {
            TextView view = new TextView(this);
            view.setTextColor(Color.parseColor("#43a2fb"));
            view.setPadding(10, 5, 10, 5);
            view.setText(list.get(i));
            view.setTextSize(20);
            view.setBackgroundResource(R.drawable.blue_border);
            if (list.get(i).equals("塞恩")) {
                view.setVisibility(View.GONE);
            }
            flow.addView(view);
        }
    }
}
