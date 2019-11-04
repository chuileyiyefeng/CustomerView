package com.example.rico.customerview.bean;

import android.graphics.Rect;

import java.io.Serializable;

/**
 * Created by Tmp on 2019/11/4.
 */
public class PicInfoBean implements Serializable {
    private int left,top,right,bottom;
    private String url;

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public String getUrl() {
        return url == null ? "" : url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
