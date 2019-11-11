package com.example.rico.customerview.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.rico.customerview.R;

/**
 * Created by Tmp on 2019/11/1.
 */
public class PicViewAdapter extends BaseAdapter<String> {

    public PicViewAdapter(Context context) {
        super(context);
    }

    @Override
    protected int bindLayout() {
        return R.layout.item_pic;
    }

    @Override
    protected void bindHolder(@NonNull BaseViewHolder holder, int i) {
        ImageView iv = holder.itemView.findViewById(R.id.iv_pic);
        Glide.with(context).load(getItem(i)).into(iv);
    }
}
