package com.example.rico.customerview.bean;

import android.content.Intent;

/**
 * Created by Tmp on 2019/5/16.
 */
public class ItemInfo{
    public Intent intent;
    public String itemName;
    public int type;

    public ItemInfo(String itemName, Intent intent) {
        this.intent = intent;
        this.itemName = itemName;
    }

    public ItemInfo(String itemName,int type) {
        this.itemName = itemName;
        this.type = type;
    }
}
