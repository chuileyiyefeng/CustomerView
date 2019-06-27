package com.example.rico.customerview.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.rico.customerview.R;

/**
 * Created by Tmp on 2018/12/18.
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bindLayout());
        doBusiness();
        setStatusBarColor(R.color.white);
        setStatusTextDark(true);
    }

    public abstract int bindLayout();

    public abstract void doBusiness();

    //    隐藏状态栏
    protected void hideStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    //    设置状态栏颜色
    protected void setStatusBarColor(int statusColor) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(statusColor));
    }

    //    设置状态栏字体颜色 黑色或者白色
    protected void setStatusTextDark(boolean dark) {
        View decor = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (dark) {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
            View content = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            if (content != null) {
                content.setFitsSystemWindows(true);
            }

        }

    }

    //    透明状态栏
    protected void translucentStatusBar() {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        attributes.flags |= flagTranslucentStatus;
        window.setAttributes(attributes);

    }

    //    获取状态栏高度
    protected int getStatusBarHeight() {
        int result = 0;
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = getResources().getDimensionPixelOffset(resId);
        }
        return result;
    }

}
