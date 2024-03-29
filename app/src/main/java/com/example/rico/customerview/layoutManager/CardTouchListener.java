package com.example.rico.customerview.layoutManager;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.rico.customerview.adapter.BaseAdapter;

public class CardTouchListener extends ItemTouchHelper.SimpleCallback {

    private BaseAdapter adapter;

    public CardTouchListener(BaseAdapter adapter) {
        super(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT | ItemTouchHelper.DOWN | ItemTouchHelper.UP);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if (adapter.getItemCount()<=1) {
            return makeFlag(0,0);
        }
        return super.getMovementFlags(recyclerView, viewHolder);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int position = viewHolder.getAdapterPosition();
        adapter.removeNotAnim(position);
    }}
