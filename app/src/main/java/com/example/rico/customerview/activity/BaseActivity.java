package com.example.rico.customerview.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Tmp on 2018/12/18.
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bindLayout());
        doBusiness();
    }

    public abstract int bindLayout();
    public abstract void doBusiness();
}
