package com.example.rico.customerview.bean;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Tmp on 2019/11/4.
 */
public class PicInfoBean implements Parcelable {

    private int position;
    private ArrayList<String> strings;
    private ArrayList<String> originStrings;
    private ArrayList<MyRect> myRectList;
    private ArrayList<Bitmap> bitmaps;

    protected PicInfoBean(Parcel in) {
        position = in.readInt();
        strings = in.createStringArrayList();
        originStrings = in.createStringArrayList();
        myRectList = in.createTypedArrayList(MyRect.CREATOR);
        bitmaps = in.createTypedArrayList(Bitmap.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(position);
        dest.writeStringList(strings);
        dest.writeStringList(originStrings);
        dest.writeTypedList(myRectList);
        dest.writeTypedList(bitmaps);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PicInfoBean> CREATOR = new Creator<PicInfoBean>() {
        @Override
        public PicInfoBean createFromParcel(Parcel in) {
            return new PicInfoBean(in);
        }

        @Override
        public PicInfoBean[] newArray(int size) {
            return new PicInfoBean[size];
        }
    };

    public ArrayList<Bitmap> getBitmaps() {
        if (bitmaps == null) {
            return bitmaps = new ArrayList<>();
        }
        return bitmaps;
    }

    public void setBitmaps(ArrayList<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
    }

    public PicInfoBean() {
    }



    public ArrayList<String> getStrings() {
        if (strings == null) {
            return strings = new ArrayList<>();
        }
        return strings;
    }

    public ArrayList<String> getOriginStrings() {
        if (originStrings == null) {
            return originStrings = new ArrayList<>();
        }
        return originStrings;
    }

    public void setOriginStrings(ArrayList<String> originStrings) {
        this.originStrings = originStrings;
    }

    public ArrayList<MyRect> getMyRectList() {
        if (myRectList == null) {
            return myRectList = new ArrayList<>();
        }
        return myRectList;
    }

    public void setMyRectList(ArrayList<MyRect> myRectList) {
        this.myRectList = myRectList;
    }

    public void setStrings(ArrayList<String> strings) {
        this.strings = strings;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


}
