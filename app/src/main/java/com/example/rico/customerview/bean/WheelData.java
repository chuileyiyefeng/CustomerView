package com.example.rico.customerview.bean;

import java.util.ArrayList;

/**
 * Created by Tmp on 2019/8/20.
 */
public class WheelData {
    public String data;

    public ArrayList<WheelChildData> childList;

    public String getData() {
        return data == null ? "" : data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public ArrayList<WheelChildData> getChildList() {
        if (childList == null) {
            return childList = new ArrayList<>();
        }
        return childList;
    }

    public void setChildList(ArrayList<WheelChildData> childList) {
        this.childList = childList;
    }
}
