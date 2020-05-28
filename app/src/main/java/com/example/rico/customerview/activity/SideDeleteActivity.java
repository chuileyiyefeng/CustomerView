package com.example.rico.customerview.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;

import com.example.rico.customerview.R;
import com.example.rico.customerview.adapter.BaseDeleteAdapter;
import com.example.rico.customerview.adapter.DeleteAdapter;
import com.example.rico.customerview.view.MyItemDecoration;

/**
 * Created by Tmp on 2019/5/16.
 * 侧滑删除
 */
public class SideDeleteActivity extends BaseActivity {
    RecyclerView rvDelete;
    DeleteAdapter adapter;

    @Override
    public int bindLayout() {
        return R.layout.activity_side_delete;
    }

    @Override
    public void doBusiness() {
        rvDelete = findViewById(R.id.rv_delete);
        rvDelete.setLayoutManager(new LinearLayoutManager(this));
        rvDelete.addItemDecoration(new MyItemDecoration());
        adapter = new DeleteAdapter(this);
        for (int i = 0; i < 10; i++) {
            adapter.addItem("第 "+i+" 项");
        }
        rvDelete.setAdapter(adapter);
        adapter.setDeleteListener(new DeleteAdapter.DeleteListener() {

            @Override
            public void click(int position) {
                adapter.removeItem(position);
            }
        });
        adapter.addItemClick(new BaseDeleteAdapter.ItemClick() {
            @Override
            public void itemClick(int position) {
                Toast.makeText(SideDeleteActivity.this, position + "点击", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
