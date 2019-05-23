package com.example.rico.customerview.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
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
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"删除",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
