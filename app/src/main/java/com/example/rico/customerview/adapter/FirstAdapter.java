package com.example.rico.customerview.adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.rico.customerview.ItemInfo;
import com.example.rico.customerview.R;

/**
 * Created by Tmp on 2018/12/18.
 */
public class FirstAdapter extends BaseAdapter<ItemInfo> {
    public FirstAdapter(Context context) {
        super(context);
    }

    @Override
    protected int bindLayout() {
        return R.layout.item;
    }

    @Override
    protected void bindHolder(@NonNull BaseViewHolder holder, int i) {
        holder.setText(R.id.tv, getItem(i).itemName);
    }
}
