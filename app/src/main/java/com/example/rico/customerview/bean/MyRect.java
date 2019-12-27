package com.example.rico.customerview.bean;

import java.io.Serializable;

/**
 * Created by Tmp on 2019/11/5.
 */
public class MyRect implements Serializable {
    private int left, top, right, bottom;
    private float widthHeightScale;

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

    @Override
    public String toString() {
        return "left=" + left + " right=" + right + " top=" + top + " bottom=" + bottom;
    }

    public float getWidthHeightScale() {
        return widthHeightScale;
    }

    public void setWidthHeightScale(float widthHeightScale) {
        this.widthHeightScale = widthHeightScale;
    }

    public int getCenterX() {
        return (left + right) / 2;
    }

    public int getCenterY() {
        return (top + bottom) / 2;
    }

    public int getHeight() {
        return bottom - top;
    }

    public int getWidth() {
        return right - left;
    }
}

