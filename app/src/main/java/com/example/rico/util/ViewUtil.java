package com.example.rico.util;

import android.animation.ValueAnimator;
import android.content.res.Resources;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;

/**
 * create by pan yi on 2021/3/24
 * desc : view工具类
 */
public class ViewUtil {
    /**
     * 如果动画被禁用，则重置动画缩放时长
     */
    public static void resetDurationScaleIfDisable() {
        if (getDurationScale() == 0) resetDurationScale();
    }

    /**
     * 重置动画缩放时长
     */
    public static void resetDurationScale() {
        try {
            getField().setFloat(null, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static float getDurationScale() {
        try {
            return getField().getFloat(null);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @NonNull
    private static Field getField() throws NoSuchFieldException {
        Field field = ValueAnimator.class.getDeclaredField("sDurationScale");
        field.setAccessible(true);
        return field;
    }

    public int dpToSp(int dp) {
        float scale = Resources.getSystem().getDisplayMetrics().density;
        float px = scale * dp;
        return (int) (px + 0.5f);
    }
}

