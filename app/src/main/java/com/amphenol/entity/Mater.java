package com.amphenol.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 物料实体类
 * Created by Carl on 2016/7/12/012.
 */
public class Mater implements Serializable{
    public static final int BRANCH_CONTROL = 1, BRANCH_NO_CONTROL = 0;//批次控制 1：控制    0：不控制
    public static final int STATUS_NO_RECEIPT = 10, STATUS_HAS_RECEIPT = 50, STATUS_CLOSED = 60;//物料状态    10：未收货    50：已收货    60：已关闭
    private String shdhm = "";//收货单号码
    private String shdhh = "";//收货单行号
    private String po = "";//采购单-项次
    private String mate_number = "";//物料编号
    private String mate_desc = "";//物料描述
    private String mate_format = "";//物料规格
    private String purchase_unit = "";//采购单位
    private double plan_quantity = 0;//计划数量
    private int branch_control = 0;//批次管控
    private int status = 0;//状态
    private double actualQuantity = 0;//实际总数
    private String actualUnit = "";//实际单重单位
    private double actualSingle = 0;//实际单重
    private String shard = "";//收货子库
    private String location = "";//收货库位
    private List<Branch> branches = new ArrayList<>();//批次信息

    public String getShdhh() {
        return shdhh;
    }

    public String getMate_number() {
        return mate_number;
    }

    public String getMate_desc() {
        return mate_desc;
    }

    public String getMate_format() {
        return mate_format;
    }

    public String getPurchase_unit() {
        return purchase_unit;
    }

    public double getPlan_quantity() {
        return plan_quantity;
    }

    public int getBranch_control() {
        return branch_control;
    }

    public int getStatus() {
        return status;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setShdhh(String shdhh) {
        this.shdhh = shdhh;
    }

    public void setMate_number(String mate_number) {
        this.mate_number = mate_number;
    }

    public void setMate_desc(String mate_desc) {
        this.mate_desc = mate_desc;
    }

    public void setMate_format(String mate_format) {
        this.mate_format = mate_format;
    }

    public void setPurchase_unit(String purchase_unit) {
        this.purchase_unit = purchase_unit;
    }

    public void setPlan_quantity(double plan_quantity) {
        this.plan_quantity = plan_quantity;
    }

    public void setBranch_control(int branch_control) {
        this.branch_control = branch_control;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }

    public double getActualQuantity() {
        return actualQuantity;
    }

    public void setActualQuantity(double actualQuantity) {
        this.actualQuantity = actualQuantity;
    }

    public String getActualUnit() {
        return actualUnit;
    }

    public void setActualUnit(String actualUnit) {
        this.actualUnit = actualUnit;
    }

    public double getActualSingle() {
        return actualSingle;
    }

    public void setActualSingle(double actualSingle) {
        this.actualSingle = actualSingle;
    }

    public String getShdhm() {
        return shdhm;
    }

    public void setShdhm(String shdhm) {
        this.shdhm = shdhm;
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

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }
}
