package com.example.rico.customerview.bean;

import android.graphics.Bitmap;

public class PointData {

    // 标识内容
    private String message;
    // 标识点半径 单位：dp
    private int radius = 20;

    // 提示框内文字大小 单位:sp

    private int textSize = 14;
    // 标识点的颜色
    private int pointColor;
    private String pointColorStr = "#333333";

    // 显示框的颜色
    private int rectColor;
    private String rectColorStr = "#333333";

    // 显示框内文字的颜色
    private int textColor;
    private String textColorStr = "#ffffff";

    // 上次文字显示框所处位置 0默认，需要计算为 1 左边 2右边
    private int lastRectPos;

    public static final int left = 1, right = 2, defaultPos = 0;

    // 传入的图片 实际使用时的图片
    private Bitmap bitmap, realBitmap;
    private int bitmapRes;

    public Bitmap getRealBitmap() {
        return realBitmap;
    }

    public void setRealBitmap(Bitmap realBitmap) {
        this.realBitmap = realBitmap;
    }

    public int getBitmapRes() {
        return bitmapRes;
    }

    // 设置图片
    public void setBitmapRes(int bitmapRes, int bitmapWidth, int bitmapHeight) {
        this.bitmapRes = bitmapRes;
        this.bitmapWidth = bitmapWidth;
        this.bitmapHeight = bitmapHeight;
    }

    // 标记点图片的大小
    private int bitmapWidth, bitmapHeight;

    public Bitmap getBitmap() {
        return bitmap;
    }

    // 设置图片
    public void setBitmap(Bitmap bitmap, int bitmapWidth, int bitmapHeight) {
        this.bitmap = bitmap;
        this.bitmapWidth = bitmapWidth;
        this.bitmapHeight = bitmapHeight;
    }

    public int getBitmapWidth() {
        return bitmapWidth;
    }

    public int getBitmapHeight() {
        return bitmapHeight;
    }


    public int getLastRectPos() {
        return lastRectPos;
    }

    public void setLastRectPos(int lastRectPos) {
        this.lastRectPos = lastRectPos;
    }

    // x、y轴的比例 实际画点的位置
    private float x, y, realX, realY;

    public int getRectColor() {
        return rectColor;
    }

    public void setRectColor(int rectColor) {
        this.rectColor = rectColor;
    }

    public String getRectColorStr() {
        return rectColorStr == null ? "" : rectColorStr;
    }

    public void setRectColorStr(String rectColorStr) {
        this.rectColorStr = rectColorStr;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public String getTextColorStr() {
        return textColorStr == null ? "" : textColorStr;
    }

    public void setTextColorStr(String textColorStr) {
        this.textColorStr = textColorStr;
    }


    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public float getRealX() {
        return realX;
    }

    public void setRealX(float realX) {
        this.realX = realX;
    }

    public float getRealY() {
        return realY;
    }

    public void setRealY(float realY) {
        this.realY = realY;
    }

    public String getPointColorStr() {
        return pointColorStr == null ? "" : pointColorStr;
    }

    public void setPointColorStr(String pointColorStr) {
        this.pointColorStr = pointColorStr;
    }

    public String getMessage() {
        return message == null ? "" : message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getPointColor() {
        return pointColor;
    }

    public void setPointColor(int color) {
        this.pointColor = color;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
