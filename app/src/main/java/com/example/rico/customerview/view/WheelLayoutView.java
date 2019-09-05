package com.example.rico.customerview.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.rico.customerview.adapter.ListLinkageAdapter;
import com.example.rico.customerview.bean.WheelChildData;
import com.example.rico.customerview.bean.WheelData;
import com.example.rico.customerview.layoutManager.WheelLayoutManager;

import java.util.ArrayList;

/**
 * Created by Tmp on 2019/8/20.
 */
public class WheelLayoutView extends LinearLayout {
    public WheelLayoutView(Context context) {
        this(context, null);
    }

    public WheelLayoutView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelLayoutView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.HORIZONTAL);
        infoList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ViewInfo info = new ViewInfo(context);
            infoList.add(info);
        }
    }

    private ArrayList<WheelData> wheelDataList;
    private ArrayList<ViewInfo> infoList;

    public void setData(ArrayList<WheelData> wheelDataList) {
        this.wheelDataList = wheelDataList;
        ViewInfo parent = infoList.get(0);
        parent.adapter.clearAllItem();

        ViewInfo middle = infoList.get(1);
        middle.adapter.clearAllItem();

        ViewInfo child = infoList.get(2);
        child.adapter.clearAllItem();

        for (int i = 0; i < wheelDataList.size(); i++) {
            parent.adapter.addItem(wheelDataList.get(i).getData());
        }

        ArrayList<WheelChildData> childList = wheelDataList.get(0).getChildList();
        for (int i = 0; i < childList.size(); i++) {
            middle.adapter.addItem(childList.get(i).getData());
        }

        ArrayList<String> strings = childList.get(0).getStrings();
        for (int i = 0; i < strings.size(); i++) {
            child.adapter.addItem(strings.get(i));
        }
    }

    private class ViewInfo {
        private RecyclerView rv;
        private ListLinkageAdapter adapter;
        private WheelLayoutManager manager;
        private LinearSnapHelper helper;
        private int childPosition;
        private int selectPosition;
        private boolean isDragging;

        void setSelectPosition() {
            this.selectPosition = 0;
        }

        private int getSelectPosition() {
            return selectPosition;
        }

        private WheelLayoutManager getManager() {
            return manager;
        }

        RecyclerView getRv() {
            return rv;
        }


        public ListLinkageAdapter getAdapter() {
            return adapter;
        }

        private ViewInfo(Context context) {
            LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1.0f;
            rv = new RecyclerView(context);
            rv.setLayoutParams(params);
            rv.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
            addView(rv);
            childPosition = getChildCount() - 1;
            adapter = new ListLinkageAdapter(context);
            manager = new WheelLayoutManager(context, "rv " + childPosition);
            helper = new LinearSnapHelper();
            helper.attachToRecyclerView(rv);
            rv.setAdapter(adapter);
            rv.setLayoutManager(manager);

            rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    dataChange(recyclerView, newState);
                }
            });
        }

        private void dataChange(@NonNull RecyclerView recyclerView, int newState) {
            View view = helper.findSnapView(manager);
            int position;
            if (view != null) {
                position = recyclerView.getChildLayoutPosition(view);
            } else {
                return;
            }
            if (newState == RecyclerView.SCROLL_STATE_IDLE && isDragging) {
                isDragging = false;
                selectPosition = position;
                ViewInfo parentInfo = infoList.get(0);
                ViewInfo middleInfo = infoList.get(1);
                ViewInfo childInfo = infoList.get(2);
                ArrayList<WheelChildData> middleList;
                ArrayList<String> childList;
                switch (childPosition) {
                    case 0:
                        middleList = wheelDataList.get(position).getChildList();
                        childList = middleList.get(0).getStrings();

                        middleInfo.getAdapter().clearAllItem();
                        childInfo.getAdapter().clearAllItem();
                        for (int i = 0; i < middleList.size(); i++) {
                            middleInfo.getAdapter().addItem(middleList.get(i).getData());
                        }
                        for (int i = 0; i < childList.size(); i++) {
                            childInfo.getAdapter().addItem(childList.get(i));
                        }
                        middleInfo.getRv().scrollToPosition(0);
                        childInfo.getRv().scrollToPosition(0);
                        middleInfo.getManager().setLayoutType(WheelLayoutManager.halfType);
                        childInfo.getManager().setLayoutType(WheelLayoutManager.halfType);
                        middleInfo.setSelectPosition();
                        childInfo.setSelectPosition();
                        break;
                    case 1:
                        middleList = wheelDataList.get(parentInfo.getSelectPosition()).getChildList();
                        childList = middleList.get(position).getStrings();
                        childInfo.getAdapter().clearAllItem();
                        for (int i = 0; i < childList.size(); i++) {
                            childInfo.getAdapter().addItem(childList.get(i));
                        }
                        childInfo.getRv().scrollToPosition(0);
                        childInfo.getManager().setLayoutType(WheelLayoutManager.halfType);
                        childInfo.setSelectPosition();
                        break;
                    case 2:
                        if (listener != null) {
                            middleList = wheelDataList.get(parentInfo.getSelectPosition()).getChildList();
                            childList = middleList.get(middleInfo.getSelectPosition()).getStrings();
                            listener.selected(childList.get(position), position);
                        }
                        break;
                }

            } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                isDragging = true;
            }
        }
    }

    public void setListener(SelectionListener listener) {
        this.listener = listener;
    }

    SelectionListener listener;

    public interface SelectionListener {
        void selected(String text, int position);
    }
}
