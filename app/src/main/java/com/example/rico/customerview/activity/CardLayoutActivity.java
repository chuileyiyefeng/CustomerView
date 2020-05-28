package com.example.rico.customerview.activity;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.rico.customerview.R;
import com.example.rico.customerview.adapter.CardAdapter;
import com.example.rico.customerview.layoutManager.CardLayoutManager;
import com.example.rico.customerview.layoutManager.CardTouchListener;

/**
 * Created by Tmp on 2019/7/26.
 */
public class CardLayoutActivity extends BaseActivity {
    RecyclerView rv;
    CardAdapter adapter;

    @Override
    public int bindLayout() {
        return R.layout.activity_recycler;
    }

    @Override
    public void doBusiness() {
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new CardLayoutManager());
        adapter = new CardAdapter(this);
        rv.setAdapter(adapter);
        for (int i = 0; i < 10; i++) {
            adapter.addItem("这是第" + i + "个");
        }
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new CardTouchListener(adapter));
        itemTouchHelper.attachToRecyclerView(rv);
    }
}
