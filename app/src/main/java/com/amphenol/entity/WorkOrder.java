package com.amphenol.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * 工单/生产订单实体类
 * Created by Carl on 2016-08-11 011.
 */
public class WorkOrder implements Parcelable{
    public static final int ORDER_STATE_ISSUED = 10, ORDER_STATE_BEGINNNG = 40, ORDER_STATE_MATER_FINISHED = 45,
            ORDER_STATE_PROCESS_FINISHED = 50, ORDER_STATE_FINISHED = 55, ORDER_STATE_CANCELED = 99, ORDER_STATE_NORMAL = 0;
    private String number = "";//工单号/生产订单号
    private int state = ORDER_STATE_NORMAL;//工单状态
    private Mater production = new Mater();//产品 ,包含了仓库,  产品名称,产品描述,产品规格 信息

    private String department = "";//部门
    private String customer = "";//客户
    private String customerName = "";//客户名称
    private String customerPurchaseOrderNumber = "";//客户采购订单号

    private ArrayList<ProductionBranch> productionBranches = new ArrayList<>();//产品批次集合
    private ArrayList<MaterBranch> materBranches = new ArrayList<>();//材料集合
    private ArrayList<Step> steps = new ArrayList<>();//工序集合

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

    public Mater getProduction() {
        return production;
    }

    public void setProduction(Mater production) {
        this.production = production;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPurchaseOrderNumber() {
        return customerPurchaseOrderNumber;
    }

    public void setCustomerPurchaseOrderNumber(String customerPurchaseOrderNumber) {
        this.customerPurchaseOrderNumber = customerPurchaseOrderNumber;
    }

    public ArrayList<ProductionBranch> getProductionBranches() {
        return productionBranches;
    }

    public void setProductionBranches(ArrayList<ProductionBranch> productionBranches) {
        this.productionBranches = productionBranches;
    }

    public ArrayList<MaterBranch> getMaterBranches() {
        return materBranches;
    }

    public void setMaterBranches(ArrayList<MaterBranch> materBranches) {
        this.materBranches = materBranches;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    /**
     * 产品批次单位
     */
    public static class ProductionBranch extends Mater.Branch implements Parcelable{
        private WorkOrder workOrder = new WorkOrder();

        private String branch = "";//生产批次
        private double quantityOrder = 0;//订单数量
        private double quantityStoraged = 0;//已入库数量/完工数量
        private double quantityRemain = 0;//剩余未交货数量

        private String planStartDate = "";//计划开始日期
        private String planFinishDate = "";//计划完成日期
        private String ActualStartDate = "";//实际开始日期

        public WorkOrder getWorkOrder() {
            return workOrder;
        }

        public void setWorkOrder(WorkOrder workOrder) {
            this.workOrder = workOrder;
        }

        public String getBranch() {
            return branch;
        }

        public void setBranch(String branch) {
            this.branch = branch;
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

        public double getQuantityRemain() {
            return quantityRemain;
        }

        public void setQuantityRemain(double quantityRemain) {
            this.quantityRemain = quantityRemain;
        }

        public String getPlanStartDate() {
            return planStartDate;
        }

        public void setPlanStartDate(String planStartDate) {
            this.planStartDate = planStartDate;
        }

        public String getPlanFinishDate() {
            return planFinishDate;
        }

        public void setPlanFinishDate(String planFinishDate) {
            this.planFinishDate = planFinishDate;
        }

        public String getActualStartDate() {
            return ActualStartDate;
        }

        public void setActualStartDate(String actualStartDate) {
            ActualStartDate = actualStartDate;
        }
    }

    /**
     * 材料明细单位
     */
    public static class MaterBranch extends Mater.Branch implements Parcelable{
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
    public static class Step{
        private String stepNumber = "";//工序编号
        private String stepName = "";//工序名称
        private double standardWorkingHours = 0;//标准工时
        private double actualWorkingHours = 0;//实际工时
        private double outsourcingCosts = 0;//外协成本
        private String outsourcingSupplier = "";//外协供应商
        private String outsourcingPurchaseOrderNumber = "";//外协采购订单号

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
