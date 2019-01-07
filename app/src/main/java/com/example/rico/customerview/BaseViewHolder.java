package com.example.rico.customerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Tmp on 2018/12/18.
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {
    public View itemView;

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
    }

    public void setText(int id, String text) {
        TextView tv = itemView.findViewById(id);
        tv.setText(text);
    }
}
