package com.example.rico;

import android.app.Activity;
import android.app.Application;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by Tmp on 2020/1/3.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);

        //以下设置全局黑白
//        Paint mPaint = new Paint();
//        ColorMatrix mColorMatrix = new ColorMatrix();
//        mColorMatrix.setSaturation(0);
//        mPaint.setColorFilter(new ColorMatrixColorFilter(mColorMatrix));
//
//        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
//            @Override
//            public void onActivityCreated(@NonNull Activity activity, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//                View decorView = activity.getWindow().getDecorView();
//                decorView.setLayerType(View.LAYER_TYPE_HARDWARE, mPaint);
//            }
//
//            @Override
//            public void onActivityStarted(@NonNull Activity activity) {
//
//            }
//
//            @Override
//            public void onActivityResumed(@NonNull Activity activity) {
//
//            }
//
//            @Override
//            public void onActivityPaused(@NonNull Activity activity) {
//
//            }
//
//            @Override
//            public void onActivityStopped(@NonNull Activity activity) {
//
//            }
//
//            @Override
//            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
//
//            }
//
//            @Override
//            public void onActivityDestroyed(@NonNull Activity activity) {
//
//            }
//        });


    }

}
