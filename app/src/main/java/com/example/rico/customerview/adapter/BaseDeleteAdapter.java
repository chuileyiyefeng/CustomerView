package com.example.rico.customerview.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rico.customerview.R;
import com.example.rico.customerview.view.SideDeleteView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Tmp on 2019/5/22.
 */
public abstract class BaseDeleteAdapter<T> extends RecyclerView.Adapter<BaseDeleteViewHolder> {
    protected Context context;
    private List<T> list;

    public BaseDeleteAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    protected abstract int bindContentLayout();

    protected abstract int bindDeleteLayout();

    @NonNull
    @Override
    public BaseDeleteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        SideDeleteView parentView = (SideDeleteView) LayoutInflater.from(context).inflate(R.layout.item_base_delete, viewGroup, false);
        parentView.addView(LayoutInflater.from(context).inflate(bindContentLayout(), viewGroup, false));
        parentView.addView(LayoutInflater.from(context).inflate(bindDeleteLayout(), viewGroup, false));
        BaseDeleteViewHolder holder = new BaseDeleteViewHolder(parentView);
        holder.contentView = parentView.getChildAt(0);
        holder.deleteView = parentView.getChildAt(1);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseDeleteViewHolder holder, final int i) {
        SideDeleteView parentView = (SideDeleteView) holder.itemView;
        parentView.closeSideQuick();
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

    protected abstract void bindHolder(@NonNull BaseDeleteViewHolder holder, int i);

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void addItem(T t) {
        list.add(t);
        notifyItemInserted(list.size());
    }

    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        if (position != list.size()) {
            notifyItemRangeChanged(position, list.size() - position);
        }
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
