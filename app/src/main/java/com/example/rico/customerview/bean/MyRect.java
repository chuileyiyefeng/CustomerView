package com.example.rico.customerview.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tmp on 2019/11/5.
 */
public class MyRect implements Parcelable {
    private int left, top, right, bottom;
    private float widthHeightScale;
    public MyRect() {
    }


    protected MyRect(Parcel in) {
        left = in.readInt();
        top = in.readInt();
        right = in.readInt();
        bottom = in.readInt();
        widthHeightScale = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(left);
        dest.writeInt(top);
        dest.writeInt(right);
        dest.writeInt(bottom);
        dest.writeFloat(widthHeightScale);
    }

    public static final Creator<MyRect> CREATOR = new Creator<MyRect>() {
        @Override
        public MyRect createFromParcel(Parcel in) {
            return new MyRect(in);
        }

        @Override
        public MyRect[] newArray(int size) {
            return new MyRect[size];
        }
    };


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

    @Override
    public int describeContents() {
        return 0;
    }


}

