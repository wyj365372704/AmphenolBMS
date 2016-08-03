package com.amphenol.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 物料实体类
 * Created by Carl on 2016/7/12/012.
 */
public class Mater implements Serializable {
    public static final int BRANCH_CONTROL = 1, BRANCH_NO_CONTROL = 0;//批次控制 1：控制    0：不控制
    private String number = "";//物料编号
    private String desc = "";//物料描述
    private String format = "";//物料规格
    private String unit = "";//单位
    private double quantity = 0;//数量
    private int branchControl = 0;//批次管控
    private double single = 0;//单重
    private String warehouse = "";//仓库
    private String shard = "";//收货子库
    private String location = "";//收货库位

    private List<Branch> branches = new ArrayList<>();//批次信息

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public int getBranchControl() {
        return branchControl;
    }

    public void setBranchControl(int branchControl) {
        this.branchControl = branchControl;
    }

    public double getSingle() {
        return single;
    }

    public void setSingle(double single) {
        this.single = single;
    }

    public String getShard() {
        return shard;
    }

    public void setShard(String shard) {
        this.shard = shard;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public static class Branch implements Serializable {
        private Mater mater ;//所在物料
        private String number;//批次行号
        private String po;//批次号
        private double quantity;//数量 ,

        public Branch() {
        }

        public Branch(String number, String po, double quantity) {
            this.number = number;
            this.po = po;
            this.quantity = quantity;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getPo() {
            return po;
        }

        public void setPo(String po) {
            this.po = po;
        }

        public double getQuantity() {
            return quantity;
        }

        public void setQuantity(double quantity) {
            this.quantity = quantity;
        }

        public Mater getMater() {
            return mater;
        }

        public void setMater(Mater mater) {
            this.mater = mater;
        }
    }
}
