package com.example.rico.customerview.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.rico.customerview.R;

import java.util.List;

/**
 * Created by Tmp on 2019/7/3.
 */
public class LayoutManagerAdapter extends BaseAdapter<String> {
    public LayoutManagerAdapter(Context context) {
        super(context);
    }

    @Override
    protected int bindLayout() {
        return R.layout.manager_item;
    }

    @Override
    protected void bindHolder(@NonNull BaseViewHolder holder, int i) {
        holder.setText(R.id.tv, getItem(i));
        TextView tv = holder.getView(R.id.tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = getItem(i);
                if (content.equals("展开") || content.equals("收起")) {
                    if (listener != null) {
                        listener.openClick(getItem(i).equals("展开"));
                    }
                }
            }
        });
    }

    private OpenClickListener listener;

    public void setOpenListener(OpenClickListener listener) {
        this.listener = listener;
    }


    public interface OpenClickListener {
        void openClick(boolean isOpen);
    }

    public List<String> getDataList() {
        return list;
    }
}
