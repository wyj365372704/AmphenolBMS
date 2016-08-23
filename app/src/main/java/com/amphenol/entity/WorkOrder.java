package com.amphenol.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 工单/生产订单实体类
 * Created by Carl on 2016-08-11 011.
 */
public class WorkOrder implements Parcelable {
    public static final int ORDER_STATE_ISSUED = 10, ORDER_STATE_BEGINNNG = 40, ORDER_STATE_MATER_FINISHED = 45,
            ORDER_STATE_PROCESS_FINISHED = 50, ORDER_STATE_FINISHED = 55, ORDER_STATE_CANCELED = 99,ORDER_STATE_NORMAL = 0;
    private String number = "";//工单号
    private int state = ORDER_STATE_NORMAL;//工单状态
    private Mater mater = new Mater();//产品
    private double quantityOrder = 0;//订单数量
    private double quantityStoraged = 0;//已入库数量

    public WorkOrder() {
    }

    protected WorkOrder(Parcel in) {
        number = in.readString();
        state = in.readInt();
        mater = in.readParcelable(Mater.class.getClassLoader());
        quantityOrder = in.readDouble();
        quantityStoraged = in.readDouble();
    }

    public static final Creator<WorkOrder> CREATOR = new Creator<WorkOrder>() {
        @Override
        public WorkOrder createFromParcel(Parcel in) {
            return new WorkOrder(in);
        }

        @Override
        public WorkOrder[] newArray(int size) {
            return new WorkOrder[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(number);
        dest.writeInt(state);
        dest.writeParcelable(mater, flags);
        dest.writeDouble(quantityOrder);
        dest.writeDouble(quantityStoraged);
    }
}
