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
    protected void bindHolder(@NonNull BaseDeleteViewHolder holder, final int i) {
        holder.setText(R.id.tv_content, getItem(i));
        View deleteView=holder.deleteView;
        final TextView delete=deleteView.findViewById(R.id.tv_delete);
        TextView top=deleteView.findViewById(R.id.tv_top);
        TextView edit=deleteView.findViewById(R.id.tv_edit);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteListener!=null) {
                    deleteListener.click(i);
                }
                Toast.makeText(context,"删除 "+i,Toast.LENGTH_SHORT).show();
            }
        });
        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"置顶 "+i,Toast.LENGTH_SHORT).show();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"编辑 "+i,Toast.LENGTH_SHORT).show();
            }
        });
    }
    DeleteListener deleteListener;

    public void setDeleteListener(DeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public  interface  DeleteListener{
        void  click(int position);
    }
}
