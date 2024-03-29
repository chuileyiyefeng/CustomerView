package com.example.rico.customerview.adapter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.rico.customerview.bean.ItemInfo;
import com.example.rico.customerview.R;
import com.example.rico.util.itemdrag.IItemHelper;

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
        ItemInfo item = list.get(fromPosition);
        list.remove(fromPosition);
        list.add(toPosition, item);
        notifyItemMoved(fromPosition, toPosition);
        notifyItemRangeChanged(Math.min(fromPosition, toPosition), Math.abs(fromPosition - toPosition) + 1);
    }

    @Override
    public void onItemDismiss(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }
}
