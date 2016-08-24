package com.amphenol.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 工单/生产订单实体类
 * Created by Carl on 2016-08-11 011.
 */
public class WorkOrder {
    public static final int ORDER_STATE_ISSUED = 10, ORDER_STATE_BEGINNNG = 40, ORDER_STATE_MATER_FINISHED = 45,
            ORDER_STATE_PROCESS_FINISHED = 50, ORDER_STATE_FINISHED = 55, ORDER_STATE_CANCELED = 99, ORDER_STATE_NORMAL = 0;
    private String number = "";//工单号/生产订单号
    private int state = ORDER_STATE_NORMAL;//工单状态
    private Mater mater = new Mater();//产品
    private double quantityOrder = 0;//订单数量
    private double quantityStoraged = 0;//已入库数量/完工数量
    private double quantityRemain = 0;//剩余未交货数量
    private String department = "";//部门
    private String customer = "";//客户
    private String customerName = "";//客户名称
    private String customerPurchaseOrderNumber = "";//客户采购订单号

    private String planStartDate = "";//计划开始日期
    private String planFinishDate = "";//计划完成日期
    private String ActualStartDate = "";//实际开始日期

    private List<Mater> materList = new ArrayList<>();//材料集合
    private List<Step> stepList = new ArrayList<>();//工序集合

    public WorkOrder() {
    }

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

    public Mater getMater() {
        return mater;
    }

    public void setMater(Mater mater) {
        this.mater = mater;
    }

    public double getQuantityOrder() {
        return quantityOrder;
    }

    public void setQuantityOrder(double quantityOrder) {
        this.quantityOrder = quantityOrder;
    }

    public double getQuantityStoraged() {
        return quantityStoraged;
    }

    public void setQuantityStoraged(double quantityStoraged) {
        this.quantityStoraged = quantityStoraged;
    }

    /**
     * 材料明细单位
     */
    public static class Mater extends com.amphenol.entity.Mater {
        private double planUsageAmount = 0;//计划用量
        private double actualUsageAmount = 0;//实际用量
        private String lastHairMaterDate = "";//最近一次发料时间

        public double getPlanUsageAmount() {
            return planUsageAmount;
        }

        public void setPlanUsageAmount(double planUsageAmount) {
            this.planUsageAmount = planUsageAmount;
        }

        public double getActualUsageAmount() {
            return actualUsageAmount;
        }

        public void setActualUsageAmount(double actualUsageAmount) {
            this.actualUsageAmount = actualUsageAmount;
        }

        public String getLastHairMaterDate() {
            return lastHairMaterDate;
        }

        public void setLastHairMaterDate(String lastHairMaterDate) {
            this.lastHairMaterDate = lastHairMaterDate;
        }
    }

    /**
     * 工序明细单位
     */
    public class Step {
        private String stepNumbre = "";//工序编号
        private String stepName = "";//工序名称
        private double standardWorkingHours = 0;//标准工时
        private double actualWorkingHours = 0;//实际工时
        private double outsourcingCosts = 0;//外协成本
        private String outsourcingSupplier = "";//外协供应商
        private String outsourcingPurchaseOrderNumber = "";//外协采购订单号

        public String getStepNumbre() {
            return stepNumbre;
        }

        public void setStepNumbre(String stepNumbre) {
            this.stepNumbre = stepNumbre;
        }

        public String getStepName() {
            return stepName;
        }

        public void setStepName(String stepName) {
            this.stepName = stepName;
        }

        public double getStandardWorkingHours() {
            return standardWorkingHours;
        }

        public void setStandardWorkingHours(double standardWorkingHours) {
            this.standardWorkingHours = standardWorkingHours;
        }

        public double getActualWorkingHours() {
            return actualWorkingHours;
        }

        public void setActualWorkingHours(double actualWorkingHours) {
            this.actualWorkingHours = actualWorkingHours;
        }

        public double getOutsourcingCosts() {
            return outsourcingCosts;
        }

        public void setOutsourcingCosts(double outsourcingCosts) {
            this.outsourcingCosts = outsourcingCosts;
        }

        public String getOutsourcingSupplier() {
            return outsourcingSupplier;
        }

        public void setOutsourcingSupplier(String outsourcingSupplier) {
            this.outsourcingSupplier = outsourcingSupplier;
        }

        public String getOutsourcingPurchaseOrderNumber() {
            return outsourcingPurchaseOrderNumber;
        }

        public void setOutsourcingPurchaseOrderNumber(String outsourcingPurchaseOrderNumber) {
            this.outsourcingPurchaseOrderNumber = outsourcingPurchaseOrderNumber;
        }
    }

}
