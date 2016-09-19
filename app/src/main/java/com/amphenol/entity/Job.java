package com.amphenol.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Carl on 2016-09-19 019.
 * 作业实体
 */
public class Job implements Parcelable{
    private String jobNumber = "";//作业号
    private WorkOrder workOrder = new WorkOrder();//所在生产订单
    private String stepNumber = "";//工序编号
    private String stepName = "";//工序名称
    private String proprNumber = "";//生产线编号
    private String proprName = "";//生产线名称
    private String createTime = "";//创建时间

    private ArrayList<Employee> employees = new ArrayList<>();

    private ArrayList<Machine> machines = new ArrayList<>();

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public WorkOrder getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(WorkOrder workOrder) {
        this.workOrder = workOrder;
    }

    public String getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(String stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getProprNumber() {
        return proprNumber;
    }

    public void setProprNumber(String proprNumber) {
        this.proprNumber = proprNumber;
    }

    public String getProprName() {
        return proprName;
    }

    public void setProprName(String proprName) {
        this.proprName = proprName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public ArrayList<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(ArrayList<Employee> employees) {
        this.employees = employees;
    }

    public ArrayList<Machine> getMachines() {
        return machines;
    }

    public void setMachines(ArrayList<Machine> machines) {
        this.machines = machines;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
