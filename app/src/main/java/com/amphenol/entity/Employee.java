package com.amphenol.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Carl on 2016-09-19 019.
 * 员工
 */
public class Employee implements Parcelable{
    public static final int STATE_CODE_ON = 1,STATE_CODE_OFF = 0,STATE_CODE_DEFAULT = 9;
    private String number = "";//员工号
    private int state = STATE_CODE_DEFAULT;//状态
    private String startTime = "";//开始时间
    private String name = "";//员工姓名
    private String department = "";//所属部门
    private String type = "";//工种

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
