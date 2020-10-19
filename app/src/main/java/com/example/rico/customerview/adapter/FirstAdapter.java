package com.example.rico.customerview.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.rico.customerview.bean.ItemInfo;
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
        holder.setText(R.id.tv_content, getItem(i).itemName);
    }
}
