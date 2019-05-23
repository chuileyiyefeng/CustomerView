package com.example.rico.customerview.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Tmp on 2019/5/22.
 */
public class BaseDeleteViewHolder extends RecyclerView.ViewHolder {
    public View itemView, contentView, deleteView;

    public BaseDeleteViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
    }

    public void setText(int id, String text) {
        TextView tv = itemView.findViewById(id);
        tv.setText(text);
    }
}
