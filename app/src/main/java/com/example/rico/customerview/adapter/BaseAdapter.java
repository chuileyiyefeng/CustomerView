package com.example.rico.customerview.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Tmp on 2018/12/18.
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    protected Context context;
    private List<T> list;

    BaseAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }


    protected abstract int bindLayout();

    int createTime;

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        createTime++;
        Log.e("createHolder", "onCreateViewHolder: "+createTime );
        return new BaseViewHolder(LayoutInflater.from(context).inflate(bindLayout(), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, final int i) {
        bindHolder(holder, i);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClick != null) {
                    itemClick.itemClick(i);
                }
            }
        });
    }

    protected abstract void bindHolder(@NonNull BaseViewHolder holder, int i);

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void addItem(T t) {
        list.add(t);
        notifyItemInserted(list.size());
    }

    public void addItem(Collection<T> collections) {
        int currentSize = getItemCount();
        list.addAll(collections);
        notifyItemRangeInserted(currentSize, getItemCount());
    }

    public T getItem(int position) {
        return list.get(position);
    }

    private ItemClick itemClick;

    public void addItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public interface ItemClick {
        void itemClick(int position);
    }

}
