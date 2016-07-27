package com.amphenol.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carl on 2016-07-23 023.
 * 调拨单实体
 */
public class Requisition implements Serializable{
    public static final int STATUS_NO_REQUISITION =10,STATUS_HAS_REQUISITION  = 20;//调拨单状态
    private String number = "";//调拨单号码
    private String founder = "";//创建人
    private String department = "";//创建部门
    private String date = "";//创建日期
    private int status = STATUS_NO_REQUISITION;//采购单状态 默认为未收货
    private List<RequisitionItem> requisitionItems = new ArrayList<>();

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFounder() {
        return founder;
    }

    public void setFounder(String founder) {
        this.founder = founder;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<RequisitionItem> getRequisitionItems() {
        return requisitionItems;
    }

    public void setRequisitionItems(List<RequisitionItem> requisitionItems) {
        this.requisitionItems = requisitionItems;
    }

    /**
     * 调拨单的调拨项
     */
    public static class RequisitionItem implements Serializable{
        private Requisition requisition ;//所在调拨单
        private double quantity = 0;//调拨数量
        private String number = "";//调拨单行号
        private Mater.Branch branch = new Mater.Branch();
        private boolean isChecked = false;

        public Requisition getRequisition() {
            return requisition;
        }

        public void setRequisition(Requisition requisition) {
            this.requisition = requisition;
        }

        public double getQuantity() {
            return quantity;
        }

        public void setQuantity(double quantity) {
            this.quantity = quantity;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public Mater.Branch getBranch() {
            return branch;
        }

        public void setBranch(Mater.Branch branch) {
            this.branch = branch;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }
}
