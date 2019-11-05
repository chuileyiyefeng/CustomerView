package com.example.rico.customerview.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.rico.customerview.R;
import com.example.rico.customerview.adapter.BaseAdapter;
import com.example.rico.customerview.adapter.PicViewAdapter;
import com.example.rico.customerview.bean.MyRect;
import com.example.rico.customerview.bean.PicInfoBean;

import java.util.ArrayList;

/**
 * Created by Tmp on 2019/11/1.
 */
public class PictureViewerActivity extends BaseActivity implements BaseAdapter.ItemClick {
    RecyclerView rvPic;
    PicViewAdapter adapter;
    GridLayoutManager manager;
    ArrayList<String> strings;

    @Override
    public int bindLayout() {
        return R.layout.activity_picture_viewer;
    }

    PicInfoBean bean;

    @Override
    public void doBusiness() {
        rvPic = findViewById(R.id.rv_pic);

        strings = new ArrayList<>();
        manager = new GridLayoutManager(this, 3);
        rvPic.setLayoutManager(manager);
        adapter = new PicViewAdapter(this);
        rvPic.setAdapter(adapter);
        for (int i = 0; i < 9; i++) {
            strings.add("http://images.open68.cn/images/dabe6894-1829-42b9-906d-3a72dbbae213.png");
        }
        adapter.addItem(strings);
        adapter.addItemClick(this);
        bean = new PicInfoBean();
        bean.setStrings(strings);
    }

    @Override
    public void itemClick(int position) {
        if (bean.getMyRectList().size()==0) {
            ArrayList<MyRect> myRectList = new ArrayList<>();
            for (int i = 0; i < strings.size(); i++) {
                MyRect myRect = new MyRect();
                myRectList.add(myRect);
                View itemView = manager.findViewByPosition(i);
                if (itemView == null) {
                    continue;
                }
                ImageView iv = itemView.findViewById(R.id.iv_pic);
                Rect rect = new Rect();
                iv.getGlobalVisibleRect(rect);
                myRect.setTop(rect.top);
                myRect.setLeft(rect.left);
                myRect.setRight(rect.right);
                myRect.setBottom(rect.bottom);

            }
            bean.setMyRectList(myRectList);
        }
        Intent intent = new Intent(this, PictureDetailActivity.class);
        bean.setPosition(position);
        intent.putExtra("picInfoBean", bean);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
