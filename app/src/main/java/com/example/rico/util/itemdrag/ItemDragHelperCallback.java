package com.example.rico.util.itemdrag;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.rico.customerview.adapter.FirstAdapter;

/**
 * @Description:
 * @Author: pan yi
 * @Date: 2022/3/22
 */

public class ItemDragHelperCallback extends ItemTouchHelper.Callback {
    FirstAdapter mRecyclerAdapter;

    public ItemDragHelperCallback(FirstAdapter mRecyclerAdapter) {
        this.mRecyclerAdapter = mRecyclerAdapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN|ItemTouchHelper.START | ItemTouchHelper.END;
        final int swipeFlags = 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mRecyclerAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());

        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mRecyclerAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }
}