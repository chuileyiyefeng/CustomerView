package com.example.rico.customerview.activity;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.rico.customerview.R;
import com.example.rico.customerview.adapter.CardAdapter;
import com.example.rico.customerview.layoutManager.CardItemHelper;
import com.example.rico.customerview.layoutManager.CardLayoutManager;
import com.example.rico.customerview.layoutManager.CardTouchCallback;

/**
 * Created by Tmp on 2019/7/26.
 */
public class CardLayoutActivity extends BaseActivity {
    RecyclerView rv;
    CardAdapter adapter;

    @Override
    public int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void doBusiness() {
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new CardLayoutManager());
        adapter = new CardAdapter(this);
        rv.setAdapter(adapter);
        for (int i = 0; i < 10; i++) {
            adapter.addItem("i");
        }
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new CardTouchCallback(mCardItemHelper));
        itemTouchHelper.attachToRecyclerView(rv);
    }
    private CardItemHelper mCardItemHelper = new CardItemHelper() {
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };
}
