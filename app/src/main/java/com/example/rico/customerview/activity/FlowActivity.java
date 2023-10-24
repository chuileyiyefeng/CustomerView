package com.example.rico.customerview.activity;

import android.view.View;
import android.widget.TextView;

import com.example.rico.customerview.R;
import com.example.rico.customerview.view.FlowExpandView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tmp on 2019/2/19.
 */
public class FlowActivity extends BaseActivity {
    FlowExpandView flow;
    ArrayList<String> orininList;

    @Override
    public int bindLayout() {
        return R.layout.flow;
    }

    @Override
    public void doBusiness() {
        orininList = new ArrayList<>();
        addData();
        flow = findViewById(R.id.flow);
        flow.setMargin(5, 5);
        addView(orininList);
    }

    private void addData() {
        for (int i = 0; i < 1; i++) {
            orininList.add("战争女神 ");
//            list.add("蒙多战争女神战争女神战争女神战争女神战争女神");
            orininList.add("德玛西亚皇子");
            orininList.add("殇之木乃伊");
            orininList.add("狂战士");
            orininList.add("布里茨克拉克");
            orininList.add("冰晶凤凰");
            orininList.add("德邦总管");
            orininList.add("野兽之灵乌迪尔");
            orininList.add("塞恩");
            orininList.add("诡术妖姬");
            orininList.add("永恒梦魇");
            orininList.add("诺克萨斯之手");
        }
    }

    private void addView(List<String> list) {
        flow.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            TextView view = new TextView(this);
//            view.setTextColor(getResources().getPointColor(R.color.black));
            int dis = (int) (getResources().getDisplayMetrics().density * 10);
            view.setPadding(dis, dis, dis, dis);
            view.setText(list.get(i));
            view.setTextSize(14);
            view.setBackgroundResource(R.drawable.blue_border);
            flow.addView(view);
        }
        addExpandView();
    }

    private  boolean isExpand = false;
    private void addExpandView() {
        TextView view = new TextView(this);
//            view.setTextColor(getResources().getPointColor(R.color.black));
        int dis = (int) (getResources().getDisplayMetrics().density * 10);
        view.setPadding(dis, dis, dis, dis);
        view.setText("展开");
        view.setTextSize(14);
        view.setBackgroundResource(R.drawable.blue_border);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpand) {
                    flow.close();
                } else {
                    flow.expand();
                }
                isExpand = !isExpand;
            }
        });
        flow.addExpandView(view);
    }

}
