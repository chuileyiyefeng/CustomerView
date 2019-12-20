package com.example.rico.customerview.fragment;

import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.rico.customerview.R;
import com.example.rico.customerview.bean.LayoutChangeListener;
import com.example.rico.customerview.view.MyZoomImageView;

/**
 * Created by Tmp on 2019/11/8.
 */
public class ImageFragment extends BaseFragment {
    MyZoomImageView ivPic;

    @Override
    protected int bindLayout() {
        return R.layout.fragment_iv;
    }

    @Override
    protected void initView() {
        if (getArguments() != null) {
            String url = getArguments().getString("url");
            ivPic = (MyZoomImageView) findViewById(R.id.iv_pic);
            if (getContext() != null) {
                Glide.with(getContext()).load(url)
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)//不使用缓存
//                        .skipMemoryCache(true)
                        .into(ivPic);
            }
            ivPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            });
            ivPic.setLayoutChangeListener(new LayoutChangeListener() {
                @Override
                public void change(float x, float y,int heightDistance ) {
                    Log.e("layoutChange", "initView: " + x + " " + y);
                    if (picLayoutChange != null) {
                        picLayoutChange.change(x, y,heightDistance);
                    }
                }

                @Override
                public void release() {
                    
                }

            });
        }
    }

    PicLayoutChange picLayoutChange;

    public void setChange(PicLayoutChange change) {
        this.picLayoutChange = change;
    }

    public interface PicLayoutChange {
        void change(float x, float y,int heightDistance);
    }
}
