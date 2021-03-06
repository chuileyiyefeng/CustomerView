package com.example.rico.customerview.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.rico.customerview.R;
import com.example.rico.util.BitmapLruCache;

import java.util.ArrayList;

/**
 * Created by Tmp on 2019/11/1.
 */
public class PicViewAdapter extends BaseAdapter<String> {
    private ArrayList<Float> scales;

    public PicViewAdapter(Context context) {
        super(context);
        scales = new ArrayList<>();
    }

    @Override
    protected int bindLayout() {
        return R.layout.item_pic;
    }

    @Override
    protected void bindHolder(@NonNull BaseViewHolder holder, int i) {
        ImageView iv = holder.itemView.findViewById(R.id.iv_pic);
        if (scales.size() == i) {
            scales.add(i, 1f);
        }
        String url = getItem(i);
        Glide.with(context)
                .asBitmap()
                .load(url)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap bitmap, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Log.e("itemBitmap", "onResourceReady: " + bitmap.getHeight() + " " + bitmap.getWidth() + " " + bitmap.getAllocationByteCount());
                        scales.set(i, (float) bitmap.getWidth() / bitmap.getHeight());
                        BitmapLruCache.getInstance().putBitmapToMemory(url, bitmap);
                        return false;
                    }
                }).into(iv);
    }

    public float getWidthHeightScale(int position) {
        if (scales.size() > position) {
            return scales.get(position);
        }
        return 1f;
    }
}
