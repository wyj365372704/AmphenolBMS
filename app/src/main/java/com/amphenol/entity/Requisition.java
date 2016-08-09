package com.amphenol.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carl on 2016-07-23 023.
 * 调拨单实体
 */
public class Requisition implements Parcelable{
    public static final int STATUS_NO_REQUISITION =10,STATUS_HAS_REQUISITION  = 50, STATUS_CANCELED = 99,STATUS_CLOSED = 60;//调拨单状态 10 已创建,50 已完成,99已取消,60 已关闭
    private String number = "";//调拨单号码
    private String founder = "";//创建人
    private String department = "";//创建部门
    private String date = "";//创建日期
    private int status = 0;//状态
    private List<RequisitionItem> requisitionItems = new ArrayList<>();

    public Requisition() {
    }

    protected Requisition(Parcel in) {
        number = in.readString();
        founder = in.readString();
        department = in.readString();
        date = in.readString();
        status = in.readInt();
        requisitionItems = in.createTypedArrayList(RequisitionItem.CREATOR);
    }

    public static final Creator<Requisition> CREATOR = new Creator<Requisition>() {
        @Override
        public Requisition createFromParcel(Parcel in) {
            return new Requisition(in);
        }

        @Override
        public Requisition[] newArray(int size) {
            return new Requisition[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(number);
        dest.writeString(founder);
        dest.writeString(department);
        dest.writeString(date);
        dest.writeInt(status);
        dest.writeTypedList(requisitionItems);
    }

    /**
     * 调拨单的调拨项
     */
    public static class RequisitionItem implements Parcelable{
        private Requisition requisition ;//所在调拨单
        private double quantity = 0;//调拨数量
        private double actualQuantity = 0 ;//实收数量
        private String number = "";//调拨单行号
        private String shard = "";//目标子库
        private String location = "";//目标库位
        private Mater.Branch branch = new Mater.Branch();
        private boolean checked = false;

        public RequisitionItem() {
        }

        protected RequisitionItem(Parcel in) {
            requisition = in.readParcelable(Requisition.class.getClassLoader());
            quantity = in.readDouble();
            actualQuantity = in.readDouble();
            number = in.readString();
            shard = in.readString();
            location = in.readString();
            branch = in.readParcelable(Mater.Branch.class.getClassLoader());
            checked = in.readByte() != 0;
        }

        public static final Creator<RequisitionItem> CREATOR = new Creator<RequisitionItem>() {
            @Override
            public RequisitionItem createFromParcel(Parcel in) {
                return new RequisitionItem(in);
            }

            @Override
            public RequisitionItem[] newArray(int size) {
                return new RequisitionItem[size];
            }
        };

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
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public double getActualQuantity() {
            return actualQuantity;
        }

        public void setActualQuantity(double actualQuantity) {
            this.actualQuantity = actualQuantity;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(requisition,flags);
            dest.writeDouble(quantity);
            dest.writeDouble(actualQuantity);
            dest.writeString(number);
            dest.writeString(shard);
            dest.writeString(location);
            dest.writeParcelable(branch, flags);
            dest.writeByte((byte) (checked ? 1 : 0));
        }
    }
}
