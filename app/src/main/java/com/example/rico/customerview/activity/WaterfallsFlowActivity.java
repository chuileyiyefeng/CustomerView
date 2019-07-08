package com.example.rico.customerview.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.rico.customerview.R;
import com.example.rico.customerview.adapter.LayoutManagerAdapter;
import com.example.rico.customerview.view.MyItemDecoration;
import com.example.rico.customerview.view.MyLayoutManager;

/**
 * Created by Tmp on 2019/7/3.
 */
public class WaterfallsFlowActivity extends BaseActivity {
    RecyclerView rv;
    LayoutManagerAdapter adapter;

    @Override
    public int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void doBusiness() {
        rv = findViewById(R.id.rv);
        adapter = new LayoutManagerAdapter(this);
//        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setLayoutManager(new MyLayoutManager());
        rv.setAdapter(adapter);
        for (int i = 0; i < 5; i++) {
            adapter.addItem("战争女神 ");
            adapter.addItem("蒙多战争女神战争女神战争女神战争女神战争女神战争女神战争女神战争女神战争女神");
            adapter.addItem("德玛西亚皇子");
            adapter.addItem("殇之木乃伊");
            adapter.addItem("狂战士");
            adapter.addItem("布里茨克拉克");
            adapter.addItem("冰晶凤凰");
            adapter.addItem("德邦总管");
            adapter.addItem("野兽之灵乌迪尔");
            adapter.addItem("塞恩");
            adapter.addItem("诡术妖姬");
            adapter.addItem("永恒梦魇");
            adapter.addItem("诺克萨斯之手");
        }
    }
}
