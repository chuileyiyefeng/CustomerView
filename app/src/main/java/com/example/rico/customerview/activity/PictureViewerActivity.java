package com.example.rico.customerview.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.rico.customerview.R;
import com.example.rico.customerview.adapter.BaseAdapter;
import com.example.rico.customerview.adapter.PicViewAdapter;
import com.example.rico.customerview.bean.PicInfoBean;

/**
 * Created by Tmp on 2019/11/1.
 */
public class PictureViewerActivity extends BaseActivity implements BaseAdapter.ItemClick {
    RecyclerView rvPic;
    PicViewAdapter adapter;
    GridLayoutManager manager;

    @Override
    public int bindLayout() {
        return R.layout.activity_picture_viewer;
    }

    @Override
    public void doBusiness() {
        rvPic = findViewById(R.id.rv_pic);
        manager = new GridLayoutManager(this, 3);
        rvPic.setLayoutManager(manager);
        adapter = new PicViewAdapter(this);
        rvPic.setAdapter(adapter);
        for (int i = 0; i < 9; i++) {
            adapter.addItem("http://images.open68.cn/images/dabe6894-1829-42b9-906d-3a72dbbae213.png");
        }
        adapter.addItemClick(this);
    }

    @Override
    public void itemClick(int position) {
        View itemView = manager.findViewByPosition(position);
        if (itemView == null) {
            return;
        }
        Intent intent = new Intent(this, PictureDetailActivity.class);
        PicInfoBean bean = new PicInfoBean();
        bean.setUrl(adapter.getItem(position));
        Rect rect = new Rect();
        itemView.getGlobalVisibleRect(rect);
        bean.setTop(rect.top);
        bean.setLeft(rect.left);
        bean.setRight(rect.right);
        bean.setBottom(rect.bottom);
        intent.putExtra("picInfoBean", bean);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
