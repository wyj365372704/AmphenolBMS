package com.amphenol.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * 出货单实体
 */
public class Shipment implements Parcelable {
    private String warehouse = "";
    private String number = "";//出货单号
    private String clientNumber = "";
    private String clientName = "";
    private String department = "";
    private String expectedDate = "";
    private ArrayList<ShipmentItem> shipmentItems = new ArrayList<>();

    public Shipment() {
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getClientNumber() {
        return clientNumber;
    }

    public void setClientNumber(String clientNumber) {
        this.clientNumber = clientNumber;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(String expectedDate) {
        this.expectedDate = expectedDate;
    }

    public ArrayList<ShipmentItem> getShipmentItems() {
        return shipmentItems;
    }

    public void setShipmentItems(ArrayList<ShipmentItem> shipmentItems) {
        this.shipmentItems = shipmentItems;
    }


    public static class ShipmentItem implements Parcelable {
        private Shipment shipment = new Shipment();
        private String pldln = "";//出货通知单明细
        private String c6cvnb = "";//客户订单号
        private String cdfcnb = "";//客户订单行号
        private double quantity = 0;//计划数量
        private String unit = "";//计划数量单位
        private double shipmentQuantity = 0;//出货数量
        private String boxln = "";//箱明细行号
        private String boxNumber = "";//箱号
        private double boxQuantity = 0;//箱数
        private Mater mater = new Mater();
        private ArrayList<ShipmentItemBranchItem> shipmentItemBranchItems = new ArrayList<>();//待出货的物料集合

        public ShipmentItem() {
        }

        public String getBoxln() {
            return boxln;
        }

        public void setBoxln(String boxln) {
            this.boxln = boxln;
        }

        public String getBoxNumber() {
            return boxNumber;
        }

        public void setBoxNumber(String boxNumber) {
            this.boxNumber = boxNumber;
        }

        public double getBoxQuantity() {
            return boxQuantity;
        }

        public void setBoxQuantity(double boxQuantity) {
            this.boxQuantity = boxQuantity;
        }

        public Shipment getShipment() {
            return shipment;
        }

        public void setShipment(Shipment shipment) {
            this.shipment = shipment;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getPldln() {
            return pldln;
        }

        public void setPldln(String pldln) {
            this.pldln = pldln;
        }

        public String getC6cvnb() {
            return c6cvnb;
        }

        public void setC6cvnb(String c6cvnb) {
            this.c6cvnb = c6cvnb;
        }

        public String getCdfcnb() {
            return cdfcnb;
        }

        public void setCdfcnb(String cdfcnb) {
            this.cdfcnb = cdfcnb;
        }

        public double getQuantity() {
            return quantity;
        }

        public void setQuantity(double quantity) {
            this.quantity = quantity;
        }

        public double getShipmentQuantity() {
            return shipmentQuantity;
        }

        public void setShipmentQuantity(double shipmentQuantity) {
            this.shipmentQuantity = shipmentQuantity;
        }

        public ArrayList<ShipmentItemBranchItem> getShipmentItemBranchItems() {
            return shipmentItemBranchItems;
        }

        public void setShipmentItemBranchItems(ArrayList<ShipmentItemBranchItem> shipmentItemBranchItems) {
            this.shipmentItemBranchItems = shipmentItemBranchItems;
        }

        public Mater getMater() {
            return mater;
        }

        public void setMater(Mater mater) {
            this.mater = mater;
        }


        public static class ShipmentItemBranchItem implements Parcelable {
            private Mater.Branch branch = new Mater.Branch();
            private double quantity = 0;//出货数量
            private boolean checked = false;

            public ShipmentItemBranchItem() {
            }

            protected ShipmentItemBranchItem(Parcel in) {
                branch = in.readParcelable(Mater.Branch.class.getClassLoader());
                quantity = in.readDouble();
                checked = in.readByte() != 0;
            }

            public Mater.Branch getBranch() {
                return branch;
            }

            public void setBranch(Mater.Branch branch) {
                this.branch = branch;
            }

            public double getQuantity() {
                return quantity;
            }

            public void setQuantity(double quantity) {
                this.quantity = quantity;
            }

            public boolean isChecked() {
                return checked;
            }

            public void setChecked(boolean checked) {
                this.checked = checked;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeParcelable(branch, flags);
                dest.writeDouble(quantity);
                dest.writeByte((byte) (checked ? 1 : 0));
            }

            @Override
            public int describeContents() {
                return 0;
            }

            public static final Creator<ShipmentItemBranchItem> CREATOR = new Creator<ShipmentItemBranchItem>() {
                @Override
                public ShipmentItemBranchItem createFromParcel(Parcel in) {
                    return new ShipmentItemBranchItem(in);
                }

                @Override
                public ShipmentItemBranchItem[] newArray(int size) {
                    return new ShipmentItemBranchItem[size];
                }
            };
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.shipment, flags);
            dest.writeString(this.pldln);
            dest.writeString(this.c6cvnb);
            dest.writeString(this.cdfcnb);
            dest.writeDouble(this.quantity);
            dest.writeString(this.unit);
            dest.writeDouble(this.shipmentQuantity);
            dest.writeString(this.boxln);
            dest.writeString(this.boxNumber);
            dest.writeDouble(this.boxQuantity);
            dest.writeParcelable(this.mater, flags);
            dest.writeTypedList(this.shipmentItemBranchItems);
        }

        protected ShipmentItem(Parcel in) {
            this.shipment = in.readParcelable(Shipment.class.getClassLoader());
            this.pldln = in.readString();
            this.c6cvnb = in.readString();
            this.cdfcnb = in.readString();
            this.quantity = in.readDouble();
            this.unit = in.readString();
            this.shipmentQuantity = in.readDouble();
            this.boxln = in.readString();
            this.boxNumber = in.readString();
            this.boxQuantity = in.readDouble();
            this.mater = in.readParcelable(Mater.class.getClassLoader());
            this.shipmentItemBranchItems = in.createTypedArrayList(ShipmentItemBranchItem.CREATOR);
        }

        public static final Creator<ShipmentItem> CREATOR = new Creator<ShipmentItem>() {
            @Override
            public ShipmentItem createFromParcel(Parcel source) {
                return new ShipmentItem(source);
            }

            @Override
            public ShipmentItem[] newArray(int size) {
                return new ShipmentItem[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.warehouse);
        dest.writeString(this.number);
        dest.writeString(this.clientNumber);
        dest.writeString(this.clientName);
        dest.writeString(this.department);
        dest.writeString(this.expectedDate);
        dest.writeTypedList(this.shipmentItems);
    }

    protected Shipment(Parcel in) {
        this.warehouse = in.readString();
        this.number = in.readString();
        this.clientNumber = in.readString();
        this.clientName = in.readString();
        this.department = in.readString();
        this.expectedDate = in.readString();
        this.shipmentItems = in.createTypedArrayList(ShipmentItem.CREATOR);
    }

    public static final Creator<Shipment> CREATOR = new Creator<Shipment>() {
        @Override
        public Shipment createFromParcel(Parcel source) {
            return new Shipment(source);
        }

        @Override
        public Shipment[] newArray(int size) {
            return new Shipment[size];
        }
    };
}
