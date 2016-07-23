package com.amphenol.entity;

import android.graphics.drawable.Drawable;

/**
 * Created by Carl on 2016/7/11/011.
 * 菜单项实体
 */
public class MenuItem{
    private String title;
    private int code;
    private int imageRes;

    public MenuItem(String title, int code, int imageRes) {
        this.title = title;
        this.code = code;
        this.imageRes = imageRes;
    }

    public String getTitle() {
        return title;
    }

    public int getCode() {
        return code;
    }

    public int getImageRes() {
        return imageRes;
    }

}