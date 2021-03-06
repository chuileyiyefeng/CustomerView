package com.example.rico.customerview.activity;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.Toast;

import com.example.rico.customerview.R;
import com.example.rico.customerview.adapter.BaseAdapter;
import com.example.rico.customerview.adapter.LayoutManagerAdapter;
import com.example.rico.customerview.layoutManager.FlowLayoutManager;

/**
 * Created by Tmp on 2019/7/3.
 */
public class WaterfallsFlowActivity extends BaseActivity {
    RecyclerView rv;
    LayoutManagerAdapter adapter;

    @Override
    public int bindLayout() {
        return R.layout.activity_recycler;
    }

    @Override
    public void doBusiness() {
        rv = findViewById(R.id.rv);
        adapter = new LayoutManagerAdapter(this);
//        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setLayoutManager(new FlowLayoutManager());
//        rv.setLayoutManager(new GridLayoutManager(this,3));
//        rv.setLayoutManager(new SimpleLinearManager());
//        rv.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        rv.setAdapter(adapter);
        Log.e("adapter", "doBusiness: "+System.currentTimeMillis() );
        for (int i = 0; i < 10; i++) {
            adapter.addItem("战争女神 " + 0);
            adapter.addItem("蒙多战争女神战争女神战争女神战争女神战争女神战争女神战争女神战争女神战争女神");
            adapter.addItem("德玛西亚皇子"+1);
            adapter.addItem("殇之木乃伊"+2);
            adapter.addItem("狂战士"+3);
            adapter.addItem("布里茨克拉克"+4);
            adapter.addItem("冰晶凤凰"+5);
            adapter.addItem("德邦总管"+6);
            adapter.addItem("野兽之灵乌迪尔"+7);
            adapter.addItem("塞恩"+8);
            adapter.addItem("诡术妖姬"+9);
            adapter.addItem("永恒梦魇"+10);
            adapter.addItem("诺克萨斯之手"+11);
        }
        Log.e("adapter", "doBusiness: "+System.currentTimeMillis() );
        adapter.addItemClick(new BaseAdapter.ItemClick() {
            @Override
            public void itemClick(int position) {
                Toast.makeText(WaterfallsFlowActivity.this, "show" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
