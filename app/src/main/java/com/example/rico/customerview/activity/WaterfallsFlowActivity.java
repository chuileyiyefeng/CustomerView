package com.example.rico.customerview.activity;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.Toast;

import com.example.rico.customerview.R;
import com.example.rico.customerview.adapter.BaseAdapter;
import com.example.rico.customerview.adapter.LayoutManagerAdapter;
import com.example.rico.customerview.layoutManager.FlowLayoutExpandManager;
import com.example.rico.customerview.layoutManager.FlowLayoutManager;

import java.util.List;

/**
 * Created by Tmp on 2019/7/3.
 */
public class WaterfallsFlowActivity extends BaseActivity {
    RecyclerView rv;
    LayoutManagerAdapter adapter;
    FlowLayoutExpandManager manager;

    @Override
    public int bindLayout() {
        return R.layout.activity_recycler;
    }

    boolean isChange = false;

    @Override
    public void doBusiness() {
        rv = findViewById(R.id.rv);
        adapter = new LayoutManagerAdapter(this);
        manager = new FlowLayoutExpandManager(5);
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);
        manager.setChangeListener(new FlowLayoutExpandManager.ChangeItemToExpandListener() {
            @Override
            public void goChange(int position) {
                rv.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!isChange) {
                            adapter.getDataList().set(position, "展开");
                            adapter.notifyItemChanged(position);
                            isChange = true;
                        }
                    }
                });

            }
        });
        setData();
        adapter.addItemClick(new BaseAdapter.ItemClick() {
            @Override
            public void itemClick(int position) {
                Toast.makeText(WaterfallsFlowActivity.this, "show" + position, Toast.LENGTH_SHORT).show();
            }
        });
        adapter.setOpenListener(new LayoutManagerAdapter.OpenClickListener() {
            @Override
            public void openClick(boolean isOpen) {
                if (isOpen) {
                    adapter.clearAllItem();
                    manager.setNeedShowAll(true);
                    setData();
                    rv.requestLayout();
                } else {
                    isChange = false;
                    manager.setNeedShowAll(false);
                    rv.requestLayout();
                }
            }
        });
    }


    private void setData() {
        for (int i = 0; i < 1; i++) {
            adapter.addItem("战争女神 " + 0);
            adapter.addItem("蒙多战争女神战争女神战争女神战争女神战争女神战争女神战争女神战争女神战争女神");
            adapter.addItem("德玛西亚皇子" + 1);
            adapter.addItem("殇之木乃伊" + 2);
            adapter.addItem("狂战士" + 3);
            adapter.addItem("布里茨克拉克" + 4);
            adapter.addItem("冰晶凤凰" + 5);
            adapter.addItem("德邦总管" + 6);
            adapter.addItem("野兽之灵乌迪尔" + 7);
            adapter.addItem("塞恩" + 8);
            adapter.addItem("诡术妖姬" + 9);
            adapter.addItem("永恒梦魇" + 10);
            adapter.addItem("诺克萨斯之手" + 11);
        }
        adapter.addItem("收起");
    }
}
