package com.example.rico.customerview.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.rico.customerview.R;

/**
 * Created by Tmp on 2019/11/5.
 */
public class PicDetailAdapter extends BaseAdapter<String> {
    public PicDetailAdapter(Context context) {
        super(context);
    }

    @Override
    protected int bindLayout() {
        return R.layout.item_pic_detail;
    }

    @Override
    protected void bindHolder(@NonNull BaseViewHolder holder, int i) {
        ImageView iv = holder.itemView.findViewById(R.id.iv);
//        Glide.with(context).load(getItem(i)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(iv);
        Glide.with(context).load(getItem(i)).into(iv);
    }
}
