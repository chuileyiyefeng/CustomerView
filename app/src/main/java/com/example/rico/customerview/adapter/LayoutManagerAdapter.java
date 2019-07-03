package com.example.rico.customerview.adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.rico.customerview.R;

/**
 * Created by Tmp on 2019/7/3.
 */
public class LayoutManagerAdapter extends BaseAdapter<String> {
    public LayoutManagerAdapter(Context context) {
        super(context);
    }

    @Override
    protected int bindLayout() {
        return R.layout.manager_item;
    }

    @Override
    protected void bindHolder(@NonNull BaseViewHolder holder, int i) {
        holder.setText(R.id.tv, getItem(i));
    }
}
