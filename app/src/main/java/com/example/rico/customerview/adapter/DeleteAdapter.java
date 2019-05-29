package com.example.rico.customerview.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rico.customerview.R;

/**
 * Created by Tmp on 2019/5/20.
 */
public class DeleteAdapter extends BaseDeleteAdapter<String> {
    public DeleteAdapter(Context context) {
        super(context);
    }

    @Override
    protected int bindContentLayout() {
        return R.layout.item_content;
    }

    @Override
    protected int bindDeleteLayout() {
        return R.layout.item_delete;
    }

    @Override
    protected void bindHolder(@NonNull BaseDeleteViewHolder holder, int i) {
        holder.setText(R.id.tv, getItem(i));
        View deleteView=holder.deleteView;
        TextView delete=deleteView.findViewById(R.id.tv_delete);
        TextView top=deleteView.findViewById(R.id.tv_top);
        TextView edit=deleteView.findViewById(R.id.tv_edit);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"删除",Toast.LENGTH_SHORT).show();
            }
        });
        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"置顶",Toast.LENGTH_SHORT).show();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"编辑",Toast.LENGTH_SHORT).show();
            }
        });
    }
}