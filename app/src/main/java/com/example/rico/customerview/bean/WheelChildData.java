package com.example.rico.customerview.bean;

import java.util.ArrayList;

/**
 * Created by Tmp on 2019/8/20.
 */
public class WheelChildData {
    String data;
    ArrayList<String> strings;

    public ArrayList<String> getStrings() {
        if (strings == null) {
            return strings = new ArrayList<>();
        }
        return strings;
    }

    public void setStrings(ArrayList<String> strings) {
        this.strings = strings;
    }

    public String getData() {
        return data == null ? "" : data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
