package com.example.rico.util;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/**
 * Created by Tmp on 2019/12/30.
 */
public class BitmapLruCache {
    private LruCache<String, Bitmap> bitmapLruCache;

    private static BitmapLruCache instance;

    public static BitmapLruCache getInstance() {
        if (instance == null) {
            synchronized (BitmapLruCache.class) {
                if (instance == null) {
                    instance = new BitmapLruCache();
                }
            }
        }
        return instance;
    }

    private BitmapLruCache() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory() / 8;
        bitmapLruCache = new LruCache<String, Bitmap>(maxMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    public void putBitmapToMemory(String url, Bitmap bitmap) {
        try {
            if (url!=null&&bitmap!=null) {
                bitmapLruCache.put(url, bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmapFromMemory(String url) {
        try {
            return bitmapLruCache.get(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clearCache() {
        if (bitmapLruCache != null && bitmapLruCache.size() > 0) {
            Log.e("BitmapLruCache", "mMemoryCache.size() " + bitmapLruCache.size());
            bitmapLruCache.evictAll();
        }
    }
}
