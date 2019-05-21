package com.example.rico.customerview.adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.rico.customerview.R;

/**
 * Created by Tmp on 2019/5/20.
 */
public class DeleteAdapter extends BaseAdapter<String> {
    public DeleteAdapter(Context context) {
        super(context);
    }

    @Override
    protected int bindLayout() {
        return R.layout.item_delete;
    }

    @Override
    protected void bindHolder(@NonNull BaseViewHolder holder, int i) {
        holder.setText(R.id.tv, getItem(i));
    }
}
