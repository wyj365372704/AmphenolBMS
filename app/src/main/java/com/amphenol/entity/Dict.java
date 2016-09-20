package com.amphenol.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Carl on 2016-09-20 020.
 */
public class Dict implements Parcelable{
    private String id;
    private String text;

    public Dict(String id, String text) {
        super();
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * 为什么要重写toString()呢？
     * <p>
     * 因为适配器在显示数据的时候，如果传入适配器的对象不是字符串的情况下，直接就使用对象.toString()
     */
    @Override
    public String toString() {
        return text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}