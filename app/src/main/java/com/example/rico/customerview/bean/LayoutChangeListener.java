package com.example.rico.customerview.bean;

/**
 * Created by Tmp on 2019/12/20.
 */
public interface LayoutChangeListener {
    void change(float x, float y, int heightDistance);

    void release();
}
