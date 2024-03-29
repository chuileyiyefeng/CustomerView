package com.example.rico.customerview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

    public void addItemNotRefresh(T t) {
        list.add(t);
    }

    public void addItem(Collection<T> collections) {
        list.addAll(collections);
//        notifyDataSetChanged();
        notifyItemRangeInserted(list.size() - 1, collections.size());
    }

    public void removePosition(int position) {
        list.remove(position);
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position,list.size());

        notifyItemRemoved(position);
        compatibilityDataSizeChanged(0);
        notifyItemRangeChanged(position, list.size() - position);
    }

    public void removeNotAnim(int position) {
        list.remove(position);
        notifyDataSetChanged();
    }

    private void compatibilityDataSizeChanged(int size) {
        final int dataSize = list == null ? 0 : list.size();
        if (dataSize == size) {
            notifyDataSetChanged();
        }
    }

    public void clearAllItem() {
        list.clear();
        notifyDataSetChanged();
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

    private DataChangeListener dataChangeListener;

    public void setDataChangeListener(DataChangeListener dataChangeListener) {
        this.dataChangeListener = dataChangeListener;
    }

    public interface DataChangeListener {
        void change();
    }
}
