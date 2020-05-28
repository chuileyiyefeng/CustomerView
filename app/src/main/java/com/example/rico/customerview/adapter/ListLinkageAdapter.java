package com.example.rico.customerview.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.rico.customerview.R;

import java.util.ArrayList;

/**
 * Created by Tmp on 2019/8/16.
 */
public class ListLinkageAdapter extends RecyclerView.Adapter<ListLinkageAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<String> strings;

    public ListLinkageAdapter(Context context) {
        this.context = context;
        strings = new ArrayList<>();
    }

    public ListLinkageAdapter(Context context, ArrayList<String> strings) {
        this.context = context;
        this.strings = strings;
    }

    public void addData(ArrayList<String> strings) {
        this.strings.clear();
        this.strings.addAll(strings);
        notifyDataSetChanged();
    }


    public void clearAllItem() {
        strings.clear();
        notifyDataSetChanged();
    }

    public void addItem(String str) {
        strings.add(str);
        notifyDataSetChanged();
    }

    public String getItem(int position) {
        return strings.get(position);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_text, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.setText(R.id.tv, strings.get(i));
    }

    @Override
    public int getItemCount() {
        return strings == null ? 0 : strings.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private View itemView;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        public void setText(int id, String text) {
            TextView tv = itemView.findViewById(id);
            tv.setText(text);
        }
    }
}
