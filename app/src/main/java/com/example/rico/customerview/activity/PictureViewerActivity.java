package com.example.rico.customerview.activity;

import android.content.Intent;
import android.graphics.Rect;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.rico.customerview.R;
import com.example.rico.customerview.adapter.BaseAdapter;
import com.example.rico.customerview.adapter.PicViewAdapter;
import com.example.rico.customerview.bean.MyRect;
import com.example.rico.customerview.bean.PicInfoBean;
import com.example.rico.util.BitmapLruCache;
import com.example.rico.util.StatusBarUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;

import java.util.ArrayList;

/**
 * Created by Tmp on 2019/11/1.
 */
public class PictureViewerActivity extends BaseActivity implements BaseAdapter.ItemClick {
    RecyclerView rvPic;
    PicViewAdapter adapter;
    GridLayoutManager manager;
    ArrayList<String> originUrls, thumbnails;


    @Override
    public int bindLayout() {
        StatusBarUtil.setTransparent(this);
        return R.layout.activity_picture_viewer;
    }

    PicInfoBean bean;

    @Override
    public void doBusiness() {
        rvPic = findViewById(R.id.rv_pic);
        originUrls = new ArrayList<>();
        thumbnails = new ArrayList<>();
        manager = new GridLayoutManager(this, 3);
        rvPic.setLayoutManager(manager);
        adapter = new PicViewAdapter(this);
        rvPic.setAdapter(adapter);
        originUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1574242290&di=5b43dcc02c7b41cba1992d34ca9353a5&imgtype=jpg&er=1&src=http%3A%2F%2Fwx3.sinaimg.cn%2Forj360%2F006dJMtQly1g72bexutpkj30ku60j4r3.jpg");
        originUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1574242314&di=6bcadae213ad1de4c7d1527164078bee&imgtype=jpg&er=1&src=http%3A%2F%2Fimg11.weikeimg.com%2Fdata%2Fuploads%2F2013%2F07%2F15%2F97030299451e390d182ba1.jpg");
        originUrls.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=342882021,2221042042&fm=26&gp=0.jpg");
        originUrls.add("https://desk-fd.zol-img.com.cn/t_s4096x2160c5/g1/M09/01/0E/ChMljl2uaa2IK3RxADmYD_Zm6C0AAQDVQC9xGUAOZgn852.jpg");

        thumbnails.addAll(originUrls);
        // 这里分为缩略图和原图的url
        originUrls.add("https://cdn.pixabay.com/photo/2015/03/17/14/05/sparkler-677774_960_720.jpg");
        originUrls.add("https://images.pexels.com/photos/36717/amazing-animal-beautiful-beautifull.jpg?auto=compress&cs=tinysrgb&dpr=3&h=750&w=1260");
        originUrls.add("https://images.pexels.com/photos/853199/pexels-photo-853199.jpeg?auto=compress&cs=tinysrgb&dpr=3&h=750&w=1260");

        thumbnails.add("https://cdn.pixabay.com/photo/2015/03/17/14/05/sparkler-677774__340.jpg");
        thumbnails.add("https://images.pexels.com/photos/36717/amazing-animal-beautiful-beautifull.jpg?auto=compress&cs=tinysrgb&dpr=1&w=500");
        thumbnails.add("https://images.pexels.com/photos/853199/pexels-photo-853199.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500");
        adapter.addItem(thumbnails);
        adapter.addItemClick(this);
        bean = new PicInfoBean();
        bean.setStrings(originUrls);

    }

    ArrayList<MyRect> myRectList;

    @Override
    public void itemClick(int position) {
        if (myRectList == null) {
            myRectList = new ArrayList<>();
        } else {
            myRectList.clear();
        }
        for (int i = 0; i < originUrls.size(); i++) {
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
            myRect.setWidthHeightScale(adapter.getWidthHeightScale(i));
        }
        bean.setOriginStrings(thumbnails);
        bean.setMyRectList(myRectList);
        Intent intent = new Intent(PictureViewerActivity.this, PictureDetailActivity.class);
        bean.setPosition(position);
        intent.putExtra("picInfoBean", bean);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BitmapLruCache.getInstance().clearCache();
    }

    @Override
    public void finish() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        //清空内存缓存（包括Bitmap缓存和未解码图片的缓存）
        imagePipeline.clearMemoryCaches();
        //清空硬盘缓存，一般在设置界面供用户手动清理
        imagePipeline.clearDiskCaches();

        //同时清理内存缓存和硬盘缓存
        imagePipeline.clearCaches();
        super.finish();
    }
}
