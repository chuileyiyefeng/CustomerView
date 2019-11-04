package com.example.rico.customerview.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.example.rico.customerview.R;
import com.example.rico.customerview.StatusBarUtil;

/**
 * Created by Tmp on 2018/12/18.
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bindLayout());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        StatusBarUtil.setStatusBarColor(this, R.color.white);
        StatusBarUtil.setStatusTextDark(this, true);
        doBusiness();
    }

    public abstract int bindLayout();

    public abstract void doBusiness();

    //    隐藏状态栏
    protected void hideStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


}
