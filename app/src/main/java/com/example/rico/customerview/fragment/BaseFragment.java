package com.example.rico.customerview.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Tmp on 2019/7/2.
 */
public abstract class BaseFragment extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(bindLayout(), container, false);
            initView();
        }
        return view;
    }

    abstract int bindLayout();

    abstract protected void initView();

    protected View findViewById(int resId) {
        return view.findViewById(resId);
    }
}
