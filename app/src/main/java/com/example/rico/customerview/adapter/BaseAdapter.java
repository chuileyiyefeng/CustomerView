package com.example.rico.customerview.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Tmp on 2018/12/18.
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> implements View.OnClickListener {
    protected Context context;
    protected List<T> list;


    public BaseAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }


    protected abstract int bindLayout();

    private int createTime;

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        createTime++;
//        Log.e("createHolder", "onCreateViewHolder: " + createTime);
        return new BaseViewHolder(LayoutInflater.from(context).inflate(bindLayout(), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, final int i) {
        bindHolder(holder, i);
        holder.itemView.setTag(i);
        holder.itemView.setOnClickListener(this);
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

    public void removePosition(int position) {
        list.remove(position);
        notifyDataSetChanged();
    }

    public void clearAllItem() {
        int previousSize = list.size();
        list.clear();
        notifyItemRangeRemoved(0, previousSize);
    }

    public T getItem(int position) {
        return list.get(position);
    }

    private ItemClick itemClick;

    public void addItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        if (itemClick != null) {
            itemClick.itemClick(position);
        }
    }

    public interface ItemClick {
        void itemClick(int position);
    }
}
