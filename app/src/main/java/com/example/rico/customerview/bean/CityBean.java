package com.example.rico.customerview.bean;

import java.util.ArrayList;

/**
 * Created by Tmp on 2019/8/13.
 */
public class CityBean {

    // 市级名称
    private String name;

    // 区级名称链表
    private ArrayList<String> area;

    public String getName() {
        return name == null ? "" : name;
    }

    public ArrayList<String> getArea() {
        if (area == null) {
            return area = new ArrayList<>();
        }
        return area;
    }
}

