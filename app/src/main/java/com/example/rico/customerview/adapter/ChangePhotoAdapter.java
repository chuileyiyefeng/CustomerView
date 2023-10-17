package com.example.rico.customerview.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.rico.customerview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: pan yi
 * @Date: 2023/10/11
 */
public class ChangePhotoAdapter extends BaseAdapter<String> {

    private int realWidth, realHeight;

    public ChangePhotoAdapter(Context context, int width) {
        super(context);
        realWidth = width;
        realHeight = width;
    }

    @Override
    protected int bindLayout() {
        return R.layout.adapter_photo;
    }

    @Override
    protected void bindHolder(@NonNull BaseViewHolder holder, int i) {
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        params.width = realWidth;
        params.height = realHeight;
        holder.itemView.setLayoutParams(params);
        final String path = getItem(i);
        ImageView ivPhoto = holder.getView(R.id.iv_photo);
        TextView tvIndex = holder.getView(R.id.tv_index);
        View view = holder.getView(R.id.view_bg);
        tvIndex.setText(path);
        ivPhoto.setOnClickListener(v -> {
            Toast.makeText(context, "当前下标 " + holder.getAdapterPosition() + " 源下标" + i, Toast.LENGTH_SHORT).show();
        });
//        Glide.with(context)
//                .load(path)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .into(ivPhoto);

    }


    public ArrayList<String> getData() {
        return (ArrayList<String>) list;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            boolean start = (boolean) payloads.get(0);
            View view = holder.getView(R.id.view_bg);
            if (start) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }

    }
}
