package com.example.rico.customerview;

/**
 * Created by Tmp on 2019/8/13.
 */
public class CurveData {
    private int numerical;
    private String name;
    private String data;

    public CurveData(int numerical, String name) {
        this.numerical = numerical;
        this.name = name;
        data=this.numerical+"kw";
    }

    public int getNumerical() {
        return numerical;
    }

    public String getData() {
        return data == null ? "" : data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setNumerical(int numerical) {
        this.numerical = numerical;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
