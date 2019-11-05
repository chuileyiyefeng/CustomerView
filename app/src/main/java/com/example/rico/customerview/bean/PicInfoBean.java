package com.example.rico.customerview.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Tmp on 2019/11/4.
 */
public class PicInfoBean implements Serializable {

    private int position;
    private ArrayList<String> strings;
    private ArrayList<MyRect> myRectList;

    public ArrayList<String> getStrings() {
        if (strings == null) {
            return strings = new ArrayList<>();
        }
        return strings;
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
