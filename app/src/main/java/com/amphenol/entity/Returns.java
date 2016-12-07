package com.amphenol.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carl on 2016-07-23 023.
 * 退料单实体
 */
public class Returns implements Parcelable {
    public static final int STATUS_CREATING = 5,STATUS_NO_RETURN = 10, STATUS_PART_RETURN = 40, STATUS_FINISHED = 50,STATUS_NULL=  99;//退货单状态    10：未退货    40：部分退货    50：退货完成   99:置空
    private String number = "";//退货单号码
    private String firm = "";//厂商
    private int status = STATUS_NULL;//退货单状态
    private List<ReturnsItem> returnsItems = new ArrayList<>();

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

    public List<ReturnsItem> getReturnsItems() {
        return returnsItems;
    }

    public void setReturnsItems(List<ReturnsItem> returnsItems) {
        this.returnsItems = returnsItems;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.number);
        dest.writeString(this.firm);
        dest.writeInt(this.status);
        dest.writeTypedList(this.returnsItems);
    }

    public Returns() {
    }

    protected Returns(Parcel in) {
        this.number = in.readString();
        this.firm = in.readString();
        this.status = in.readInt();
        this.returnsItems = in.createTypedArrayList(ReturnsItem.CREATOR);
    }

    public static final Parcelable.Creator<Returns> CREATOR = new Parcelable.Creator<Returns>() {
        @Override
        public Returns createFromParcel(Parcel source) {
            return new Returns(source);
        }

        @Override
        public Returns[] newArray(int size) {
            return new Returns[size];
        }
    };


    /**
     * 退货单的退货项
     */
    public static class ReturnsItem implements Parcelable {
        private Returns returns;//所在退货单
        private String unit = "";//退货单位
        private double quantity = 0;//退货数量
        private double actualQuantity = 0 ;//实际退货数量
        private String number = "";//退货单行号
        private String po = "";//采购单-项次
        private Mater mater = new Mater();//物料

        private List<ReturnsItemSource> returnsItemSources = new ArrayList<>();

        public Returns getReturns() {
            return returns;
        }

        public void setReturns(Returns returns) {
            this.returns = returns;
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

        public Mater getMater() {
            return mater;
        }

        public void setMater(Mater mater) {
            this.mater = mater;
        }

        public double getActualQuantity() {
            return actualQuantity;
        }

        public void setActualQuantity(double actualQuantity) {
            this.actualQuantity = actualQuantity;
        }

        public List<ReturnsItemSource> getReturnsItemSources() {
            return returnsItemSources;
        }

        public void setReturnsItemSources(List<ReturnsItemSource> returnsItemSources) {
            this.returnsItemSources = returnsItemSources;
        }

        public ReturnsItem() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.returns, flags);
            dest.writeString(this.unit);
            dest.writeDouble(this.quantity);
            dest.writeDouble(this.actualQuantity);
            dest.writeString(this.number);
            dest.writeString(this.po);
            dest.writeParcelable(this.mater, flags);
            dest.writeTypedList(this.returnsItemSources);
        }

        protected ReturnsItem(Parcel in) {
            this.returns = in.readParcelable(Returns.class.getClassLoader());
            this.unit = in.readString();
            this.quantity = in.readDouble();
            this.actualQuantity = in.readDouble();
            this.number = in.readString();
            this.po = in.readString();
            this.mater = in.readParcelable(Mater.class.getClassLoader());
            this.returnsItemSources = in.createTypedArrayList(ReturnsItemSource.CREATOR);
        }

        public static final Creator<ReturnsItem> CREATOR = new Creator<ReturnsItem>() {
            @Override
            public ReturnsItem createFromParcel(Parcel source) {
                return new ReturnsItem(source);
            }

            @Override
            public ReturnsItem[] newArray(int size) {
                return new ReturnsItem[size];
            }
        };
    }

    public static class ReturnsItemSource extends Mater.Branch{
        private boolean checked =  false;
        private double enableQuantity = 0;//勾选上了的数量,供退货使用

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public double getEnableQuantity() {
            return enableQuantity;
        }

        public void setEnableQuantity(double enableQuantity) {
            this.enableQuantity = enableQuantity;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeByte(this.checked ? (byte) 1 : (byte) 0);
            dest.writeDouble(this.enableQuantity);
        }

        public ReturnsItemSource() {
        }

        protected ReturnsItemSource(Parcel in) {
            super(in);
            this.checked = in.readByte() != 0;
            this.enableQuantity = in.readDouble();
        }

        public static final Creator<ReturnsItemSource> CREATOR = new Creator<ReturnsItemSource>() {
            @Override
            public ReturnsItemSource createFromParcel(Parcel source) {
                return new ReturnsItemSource(source);
            }

            @Override
            public ReturnsItemSource[] newArray(int size) {
                return new ReturnsItemSource[size];
            }
        };
    }
}
