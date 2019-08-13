package com.example.rico.customerview;

/**
 * Created by Tmp on 2019/8/13.
 */
public class CurveData {
    private int numerical;
    private String name;

    public CurveData(int numerical, String name) {
        this.numerical = numerical;
        this.name = name;
    }

    public int getNumerical() {
        return numerical;
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
