package com.example.rico.customerview.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.example.rico.customerview.R;

import java.util.List;

/**
 * Created by Tmp on 2019/11/5.
 */
public class PointAdapter extends BaseAdapter<Boolean> {
    public PointAdapter(Context context) {
        super(context);
    }

    @Override
    protected int bindLayout() {
        return R.layout.item_point;
    }

    @Override
    protected void bindHolder(@NonNull BaseViewHolder holder, int i) {
        View selectView = holder.getView(R.id.view_select);
        View unSelectView = holder.getView(R.id.view_un_select);
        boolean b = getItem(i);
        selectView.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
        unSelectView.setVisibility(b ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            boolean b = (boolean) payloads.get(0);
            View selectView = holder.getView(R.id.view_select);
            View unSelectView = holder.getView(R.id.view_un_select);
            selectView.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
            unSelectView.setVisibility(b ? View.INVISIBLE : View.VISIBLE);
        }

    }
}
