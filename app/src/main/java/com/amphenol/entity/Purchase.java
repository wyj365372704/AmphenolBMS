package com.amphenol.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carl on 2016-07-23 023.
 * 采购单实体
 */
public class Purchase implements Serializable{
    public static final int STATUS_NO_RECEIPT = 10, STATUS_PART_RECEIPT = 40, STATUS_FIINISHED = 50;//采购单状态    10：未收货    40：部分收货    50：收货完成
    private String number = "";//送货单号码
    private String firm = "";//厂商
    private int status = 10;//采购单状态 默认为未收货
    private List<PurchaseItem> purchaseItems = new ArrayList<>();

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFirm() {
        return firm;
    }

    public void setFirm(String firm) {
        this.firm = firm;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<PurchaseItem> getPurchaseItems() {
        return purchaseItems;
    }

    public void setPurchaseItems(List<PurchaseItem> purchaseItems) {
        this.purchaseItems = purchaseItems;
    }

    /**
     * 采购单的采购项
     */
    public static class PurchaseItem implements Serializable{
        public static final int STATUS_NO_RECEIPT = 10, STATUS_HAS_RECEIPT = 50, STATUS_CLOSED = 60;//采购单项状态    10：未收货    50：已收货    60：已关闭
        private Purchase purchase ;//所在送货单
        private String unit ="";//采购单位
        private double quantity = 0;//采购数量
        private String number = "";//送货单行号
        private String po = "";//采购单-项次
        private int state = 10;//采购单项状态 默认为未收货
        private Mater mater = new Mater();//物料
        private List<PurchaseItemBranchItem> purchaseItemBranchItems = new ArrayList<>();//采购单项次物料批次信息集合

        public Purchase getPurchase() {
            return purchase;
        }
        public void setPurchase(Purchase purchase) {
            this.purchase = purchase;
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

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public Mater getMater() {
            return mater;
        }

        public void setMater(Mater mater) {
            this.mater = mater;
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

        public List<PurchaseItemBranchItem> getPurchaseItemBranchItems() {
            return purchaseItemBranchItems;
        }

        public void setPurchaseItemBranchItems(List<PurchaseItemBranchItem> purchaseItemBranchItems) {
            this.purchaseItemBranchItems = purchaseItemBranchItems;
        }

        /**
         * 采购订单项下面的批次信息项实体
         */
        public static class PurchaseItemBranchItem{
            public PurchaseItemBranchItem(Mater.Branch branch, double actualQuantity) {
                this.branch = branch;
                this.actualQuantity = actualQuantity;
            }

            private Mater.Branch branch ;
            private double actualQuantity = 0 ;

            public Mater.Branch getBranch() {
                return branch;
            }

            public void setBranch(Mater.Branch branch) {
                this.branch = branch;
            }

            public double getActualQuantity() {
                return actualQuantity;
            }

            public void setActualQuantity(double actualQuantity) {
                this.actualQuantity = actualQuantity;
            }
        }
    }

}
