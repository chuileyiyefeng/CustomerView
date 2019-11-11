package com.example.rico.customerview.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.rico.customerview.R;
import com.example.rico.customerview.StatusBarUtil;
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
        StatusBarUtil.setTransparent(this);
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
        for (int i = 0; i < 2; i++) {
            strings.add("http://images.open68.cn/images/dabe6894-1829-42b9-906d-3a72dbbae213.png");
            strings.add("http://img.hb.aicdn.com/f22df3bca217f7435b6b7d3c66bc6d21d72b60c3a70eb-yctL70_fw658");
            strings.add("http://img.hb.aicdn.com/266e4c85ef38c4ef468dd28cc5ae9deba47080867d89-urhOsD_fw658");
            strings.add("http://img.hb.aicdn.com/652b269af2818f6f1c468399e00152d73d0a7beb29d1e-2vnLBW_fw658");
            strings.add("http://img.hb.aicdn.com/b8ce046106dc17ebb3782351f2a493b52daf149611f57-YkEgOp_fw658");
        }
        adapter.addItem(strings);
        adapter.addItemClick(this);
        bean = new PicInfoBean();
        bean.setStrings(strings);
    }

    @Override
    public void itemClick(int position) {
        if (bean.getMyRectList().size() == 0) {
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
        Intent intent = new Intent(PictureViewerActivity.this, PictureDetailActivity.class);
        bean.setPosition(position);
        intent.putExtra("picInfoBean", bean);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
