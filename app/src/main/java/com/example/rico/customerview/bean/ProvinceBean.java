package com.example.rico.customerview.bean;

import java.util.ArrayList;

/**
 * Created by Tmp on 2019/8/13.
 */
public class ProvinceBean {

    // 省级名称
    private String name;

    // 市级名称列表
    private ArrayList<CityBean> city;

    public String getName() {
        return name == null ? "" : name;
    }

    public ArrayList<CityBean> getCity() {
        if (city == null) {
            return city = new ArrayList<>();
        }
        return city;
    }

}
