package com.example.rico.customerview.fragment;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.rico.customerview.R;

/**
 * Created by Tmp on 2019/11/8.
 */
public class ImageFragment extends BaseFragment {
    ImageView ivPic;

    @Override
    protected int bindLayout() {
        return R.layout.fragment_iv;
    }

    @Override
    protected void initView() {
        if (getArguments() != null) {
            String url = getArguments().getString("url");
            ivPic = (ImageView) findViewById(R.id.iv_pic);
            if (getContext() != null) {
                Glide.with(getContext()).load(url)
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)//不使用缓存
//                        .skipMemoryCache(true)
                        .into(ivPic);
            }
            ivPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity()!=null) {
                        getActivity().finish();
                    }
                }
            });

        }
    }
}
