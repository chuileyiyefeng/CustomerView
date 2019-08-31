package com.example.rico.customerview.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.example.rico.customerview.R;
import com.example.rico.customerview.view.CornerLinearLayout;

/**
 * Created by Tmp on 2019/7/30.
 */
public class ExchangeItemAdapter extends BaseAdapter<Integer> {
    public ExchangeItemAdapter(Context context) {
        super(context);
    }

    @Override
    protected int bindLayout() {
        return R.layout.item_exchange;
    }

    @Override
    protected void bindHolder(@NonNull BaseViewHolder holder, int i) {
        ImageView iv = holder.itemView.findViewById(R.id.iv);
        iv.setImageResource(getItem(i));
        CornerLinearLayout layout = holder.itemView.findViewById(R.id.con_l);
        layout.setTag("" + i);
    }
}
