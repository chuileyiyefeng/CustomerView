package com.example.rico.util;

import android.animation.ValueAnimator;
import androidx.annotation.NonNull;

import java.lang.reflect.Field;

/**
 * Created by Tmp on 2019/1/4.
 * 一些参考
 */
public class SomeThing {

//    方法名及读取的路径
//    Environment.getDataDirectory() = /data
//    Environment.getDownloadCacheDirectory() = /cache
//    Environment.getExternalStorageDirectory() = /mnt/sdcard
//    Environment.getExternalStoragePublicDirectory(“test”) = /mnt/sdcard/test
//    Environment.getRootDirectory() = /system
//    getPackageCodePath() = /data/app/com.my.app-1.apk
//    getPackageResourcePath() = /data/app/com.my.app-1.apk
//    getCacheDir() = /data/data/com.my.app/cache
//    getDatabasePath(“test”) = /data/data/com.my.app/databases/test
//    getDir(“test”, Context.MODE_PRIVATE) = /data/data/com.my.app/app_test
//    getExternalCacheDir() = /mnt/sdcard/Android/data/com.my.app/cache
//    getExternalFilesDir(“test”) = /mnt/sdcard/Android/data/com.my.app/files/test
//    getExternalFilesDir(null) = /mnt/sdcard/Android/data/com.my.app/files
//    getFilesDir() = /data/data/com.my.app/files


    //    重置动画时长，假如手动关闭动画，动画将不会再运行
    public static void resetDurationScale() {
        try {
            getField().setFloat(null, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private static Field getField() throws NoSuchFieldException {
        Field field = ValueAnimator.class.getDeclaredField("sDurationScale");
        field.setAccessible(true);
        return field;
    }

//    画文字居中时候，文字的y值   baseLine是居中线的y值   float y=baseLine+(metrics.bottom - metrics.top)/2 - metrics.bottom;
}
