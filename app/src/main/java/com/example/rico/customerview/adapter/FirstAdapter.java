package com.example.rico.customerview.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.rico.customerview.bean.ItemInfo;
import com.example.rico.customerview.R;
import com.example.rico.util.itemdrag.IItemHelper;
import com.example.rico.util.itemdrag.ItemDragHelperCallback;

/**
 * Created by Tmp on 2018/12/18.
 */
public class FirstAdapter extends BaseAdapter<ItemInfo> implements IItemHelper {
    public FirstAdapter(Context context) {
        super(context);
    }

    @Override
    protected int bindLayout() {
        return R.layout.item;
    }

    @Override
    protected void bindHolder(@NonNull BaseViewHolder holder, int i) {
        holder.setText(R.id.tv_content, getItem(i).itemName);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        ItemInfo prev = list.remove(fromPosition);
        list.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
        notifyItemRangeChanged(Math.min(fromPosition, toPosition), Math.abs(fromPosition -toPosition) +1);
    }

    @Override
    public void onItemDismiss(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }
}
