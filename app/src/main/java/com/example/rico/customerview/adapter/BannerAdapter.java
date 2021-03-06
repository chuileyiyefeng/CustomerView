package com.example.rico.customerview.adapter;

import android.content.Context;
import androidx.annotation.NonNull;

import com.example.rico.customerview.R;

/**
 * Created by Tmp on 2019/7/23.
 */
public class BannerAdapter extends BaseAdapter<String> {

    public BannerAdapter(Context context) {
        super(context);
    }

    @Override
    protected int bindLayout() {
        return R.layout.item_banner;
    }

    @Override
    protected void bindHolder(@NonNull BaseViewHolder holder, int i) {
        holder.setText(R.id.tv, getItem(i));
    }

}
