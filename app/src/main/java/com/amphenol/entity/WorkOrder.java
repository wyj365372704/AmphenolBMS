package com.amphenol.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * 工单/生产订单实体类
 * Created by Carl on 2016-08-11 011.
 */
public class WorkOrder implements Parcelable {
    public static final int ORDER_STATE_ISSUED = 10, ORDER_STATE_BEGIN = 40, ORDER_STATE_MATER_FINISHED = 45,
            ORDER_STATE_PROCESS_FINISHED = 50, ORDER_STATE_FINISHED = 55, ORDER_STATE_CANCELED = 99, ORDER_STATE_NORMAL = 0;

    private String department = "";//部门
    private String number = "";//工单号/生产订单号
    private int state = ORDER_STATE_NORMAL;//工单状态/订单状态
    private Mater production = new Mater();//产品 ,包含了仓库,  产品名称,产品描述,产品规格
    private double quantityOrderProduct = 0;//生产订单数量
    private double quantityFinishedProduct = 0;//生产完工数量/生产已入库数量
    private double quantityRemainProduct = 0;//生产剩余未交货数量

    private String planStartDate = "";//计划开始日期
    private String planFinishDate = "";//计划完成日期
    private String ActualStartDate = "";//实际开始日期

    private String customerCode = "";//客户代码
    private String customerName = "";//客户名称
    private String customerPurchaseOrderNumber = "";//客户采购订单号
    private String saleOrderNumber = "";//销售订单号
    private double quantityOrderSale = 0;//销售订单数量
    private double quantityShipmentSale = 0;//销售出货数量
    private double quantityRemainSale = 0;//销售剩余数量
    private String complianceDate = "";//承诺日期

    private ArrayList<MaterProduct> materProducts = new ArrayList<>();//材料集合
    private ArrayList<Step> steps = new ArrayList<>();//工序集合

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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

    public Mater getProduction() {
        return production;
    }

    public void setProduction(Mater production) {
        this.production = production;
    }

    public double getQuantityOrderProduct() {
        return quantityOrderProduct;
    }

    public void setQuantityOrderProduct(double quantityOrderProduct) {
        this.quantityOrderProduct = quantityOrderProduct;
    }

    public double getQuantityFinishedProduct() {
        return quantityFinishedProduct;
    }

    public void setQuantityFinishedProduct(double quantityFinishedProduct) {
        this.quantityFinishedProduct = quantityFinishedProduct;
    }

    public double getQuantityRemainProduct() {
        return quantityRemainProduct;
    }

    public void setQuantityRemainProduct(double quantityRemainProduct) {
        this.quantityRemainProduct = quantityRemainProduct;
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

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
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

    public String getSaleOrderNumber() {
        return saleOrderNumber;
    }

    public void setSaleOrderNumber(String saleOrderNumber) {
        this.saleOrderNumber = saleOrderNumber;
    }

    public double getQuantityOrderSale() {
        return quantityOrderSale;
    }

    public void setQuantityOrderSale(double quantityOrderSale) {
        this.quantityOrderSale = quantityOrderSale;
    }

    public double getQuantityShipmentSale() {
        return quantityShipmentSale;
    }

    public void setQuantityShipmentSale(double quantityShipmentSale) {
        this.quantityShipmentSale = quantityShipmentSale;
    }

    public double getQuantityRemainSale() {
        return quantityRemainSale;
    }

    public void setQuantityRemainSale(double quantityRemainSale) {
        this.quantityRemainSale = quantityRemainSale;
    }

    public String getComplianceDate() {
        return complianceDate;
    }

    public void setComplianceDate(String complianceDate) {
        this.complianceDate = complianceDate;
    }

    public ArrayList<MaterProduct> getMaterProducts() {
        return materProducts;
    }

    public void setMaterProducts(ArrayList<MaterProduct> materProducts) {
        this.materProducts = materProducts;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }

    /**
     * 材料明细单位
     */
    public static class MaterProduct extends Mater {
        private String sequenceNumber = "";//材料序号
        private double planUsageAmount = 0;//计划用量
        private double actualUsageAmount = 0;//实际用量
        private String lastHairMaterDate = "";//最近一次发料时间

        public String getSequenceNumber() {
            return sequenceNumber;
        }

        public void setSequenceNumber(String sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }

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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(this.sequenceNumber);
            dest.writeDouble(this.planUsageAmount);
            dest.writeDouble(this.actualUsageAmount);
            dest.writeString(this.lastHairMaterDate);
        }

        public MaterProduct() {
        }

        protected MaterProduct(Parcel in) {
            super(in);
            this.sequenceNumber = in.readString();
            this.planUsageAmount = in.readDouble();
            this.actualUsageAmount = in.readDouble();
            this.lastHairMaterDate = in.readString();
        }

        public static final Creator<MaterProduct> CREATOR = new Creator<MaterProduct>() {
            @Override
            public MaterProduct createFromParcel(Parcel source) {
                return new MaterProduct(source);
            }

            @Override
            public MaterProduct[] newArray(int size) {
                return new MaterProduct[size];
            }
        };
    }

    /**
     * 工序明细单位
     */
    public static class Step implements Parcelable {
        private String stepNumber = "";//工序编号
        private String stepName = "";//工序名称
        private double standardWorkingHours = 0;//标准工时
        private double actualWorkingHours = 0;//实际工时
        private double outsourcingCosts = 0;//外协成本
        private String outsourcingSupplier = "";//外协供应商
        private String outsourcingPurchaseOrderNumber = "";//外协采购订单号

        public Step() {
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.stepNumber);
            dest.writeString(this.stepName);
            dest.writeDouble(this.standardWorkingHours);
            dest.writeDouble(this.actualWorkingHours);
            dest.writeDouble(this.outsourcingCosts);
            dest.writeString(this.outsourcingSupplier);
            dest.writeString(this.outsourcingPurchaseOrderNumber);
        }

        protected Step(Parcel in) {
            this.stepNumber = in.readString();
            this.stepName = in.readString();
            this.standardWorkingHours = in.readDouble();
            this.actualWorkingHours = in.readDouble();
            this.outsourcingCosts = in.readDouble();
            this.outsourcingSupplier = in.readString();
            this.outsourcingPurchaseOrderNumber = in.readString();
        }

        public static final Creator<Step> CREATOR = new Creator<Step>() {
            @Override
            public Step createFromParcel(Parcel source) {
                return new Step(source);
            }

            @Override
            public Step[] newArray(int size) {
                return new Step[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.department);
        dest.writeString(this.number);
        dest.writeInt(this.state);
        dest.writeParcelable(this.production, flags);
        dest.writeDouble(this.quantityOrderProduct);
        dest.writeDouble(this.quantityFinishedProduct);
        dest.writeDouble(this.quantityRemainProduct);
        dest.writeString(this.planStartDate);
        dest.writeString(this.planFinishDate);
        dest.writeString(this.ActualStartDate);
        dest.writeString(this.customerCode);
        dest.writeString(this.customerName);
        dest.writeString(this.customerPurchaseOrderNumber);
        dest.writeString(this.saleOrderNumber);
        dest.writeDouble(this.quantityOrderSale);
        dest.writeDouble(this.quantityShipmentSale);
        dest.writeDouble(this.quantityRemainSale);
        dest.writeString(this.complianceDate);
        dest.writeTypedList(this.materProducts);
        dest.writeTypedList(this.steps);
    }

    public WorkOrder() {
    }

    protected WorkOrder(Parcel in) {
        this.department = in.readString();
        this.number = in.readString();
        this.state = in.readInt();
        this.production = in.readParcelable(Mater.class.getClassLoader());
        this.quantityOrderProduct = in.readDouble();
        this.quantityFinishedProduct = in.readDouble();
        this.quantityRemainProduct = in.readDouble();
        this.planStartDate = in.readString();
        this.planFinishDate = in.readString();
        this.ActualStartDate = in.readString();
        this.customerCode = in.readString();
        this.customerName = in.readString();
        this.customerPurchaseOrderNumber = in.readString();
        this.saleOrderNumber = in.readString();
        this.quantityOrderSale = in.readDouble();
        this.quantityShipmentSale = in.readDouble();
        this.quantityRemainSale = in.readDouble();
        this.complianceDate = in.readString();
        this.materProducts = in.createTypedArrayList(MaterProduct.CREATOR);
        this.steps = in.createTypedArrayList(Step.CREATOR);
    }

    public static final Parcelable.Creator<WorkOrder> CREATOR = new Parcelable.Creator<WorkOrder>() {
        @Override
        public WorkOrder createFromParcel(Parcel source) {
            return new WorkOrder(source);
        }

        @Override
        public WorkOrder[] newArray(int size) {
            return new WorkOrder[size];
        }
    };
}
