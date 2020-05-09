package com.example.rico.customerview.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.rico.customerview.R;
import com.example.rico.customerview.bean.LayoutChangeListener;
import com.example.rico.customerview.view.MyZoomImageView;
import com.example.rico.util.BitmapLruCache;

/**
 * Created by Tmp on 2019/11/8.
 */
public class ImageFragment extends BaseFragment implements RequestListener<Drawable> {
    MyZoomImageView ivPic;


    @Override
    protected int bindLayout() {
        return R.layout.fragment_iv;
    }

    @Override
    protected void initView() {
        if (getArguments() != null) {
            String url = getArguments().getString("url");
            String thumbnail = getArguments().getString("thumbnail");
            BitmapLruCache cache = BitmapLruCache.getInstance();
            Bitmap bitmap = cache.getBitmapFromMemory(thumbnail);
            ivPic = (MyZoomImageView) findViewById(R.id.iv_pic);
            ivPic.setCanLayoutChange(false);
            if (getContext() != null) {
                if (bitmap != null) {
                    Glide.with(getContext())
                            .load(url)
                            .placeholder(new BitmapDrawable(getResources(), bitmap))
                            .listener(this)
//                            .diskCacheStrategy(DiskCacheStrategy.NONE)//不使用缓存
//                            .skipMemoryCache(true)
                            .into(ivPic);
                } else {
                    Glide.with(getContext())
                            .load(url)
                            .listener(this)
//                            .diskCacheStrategy(DiskCacheStrategy.NONE)//不使用缓存
//                            .skipMemoryCache(true)
                            .into(ivPic);
                }
            }
//            fresco加载方法，matrix不适用
//            ivPic.setCanLayoutChange(true);
//            Uri uri = Uri.parse(url);
//            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
//            GenericDraweeHierarchy hierarchy;
//            if (getMessage() != null) {
//                builder.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
//                        .setProgressBarImage(new ProgressBarDrawable());
//                if (bitmap != null) {
//                    builder.setPlaceholderImage(new BitmapDrawable(getResources(), bitmap))
//                            .setPlaceholderImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
//                }
//                hierarchy = builder.build();
//                ivPic.setImageURI(uri);
//                ivPic.setHierarchy(hierarchy);
//            }
            ivPic.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            });
            ivPic.setLayoutChangeListener(new LayoutChangeListener() {
                @Override
                public void change(float x, float y, int heightDistance) {
                    if (picLayoutChange != null) {
                        Log.e("layoutChange", "initView: " + x + " " + y);
                        picLayoutChange.change(x, y, heightDistance);
                    }
                }

                @Override
                public void release() {
                    picLayoutChange.release();
                }

            });
        }
    }

    public void picSetOriginScale(){
        ivPic.setOriginScale();
    }
    public float getMatrixScale() {
        return ivPic.getMatrixScale();
    }

    PicLayoutChange picLayoutChange;

    public void setChange(PicLayoutChange change) {
        this.picLayoutChange = change;
    }

    // 图片加载监听
    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
        ivPic.setCanLayoutChange(true);
        return false;
    }

    @Override
    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
        ivPic.setCanLayoutChange(true);
        return false;
    }

    public interface PicLayoutChange {
        void change(float x, float y, int heightDistance);

        void release();
    }
}
