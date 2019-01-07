package com.example.rico.customerview;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by Tmp on 2018/12/18.
 */
public class FirstAdapter extends BaseAdapter<String> {
    public FirstAdapter(Context context) {
        super(context);
    }

    @Override
    protected int bindLayout() {
        return R.layout.item;
    }

    @Override
    protected void bindHolder(@NonNull BaseViewHolder holder, int i) {
        holder.setText(R.id.tv, getItem(i));
    }
}
