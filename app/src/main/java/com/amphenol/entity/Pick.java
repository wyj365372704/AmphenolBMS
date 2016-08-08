package com.amphenol.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 领料单实体
 * Created by Carl on 2016-08-05 005.
 */
public class Pick implements Serializable {
    public static final int TYPE_NORMAL = 1, TYPE_EXCEED = 2, TYPE_RETURN = 3;
    public static final int STATE_BUILDING = 5, STATE_BUILDED = 10, STATE_FINISHED = 50;
    private String number = "";
    private String workOrder = "";
    private String founder = "";
    private String department = "";
    private String date = "";
    private int type = TYPE_NORMAL;
    private int state = STATE_FINISHED;
    private ArrayList<PickItem> pickItems = new ArrayList<>();

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(String workOrder) {
        this.workOrder = workOrder;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public ArrayList<PickItem> getPickItems() {
        return pickItems;
    }

    public void setPickItems(ArrayList<PickItem> pickItems) {
        this.pickItems = pickItems;
    }

    public static class PickItem implements Serializable {
        public static final int STATE_BUILDING = 5, STATE_BUILDED = 10, STATE_FINISHED = 50, STATE_CLOSED = 60;
        public static final int BRANCHED_YES = 1, BRANCHED_NO = 0;
        private String pickLine = "";
        private String sequence = "";
        private Mater.Branch branch = new Mater.Branch();
        private double quantity = 0;//计划数量
        private double hairQuantity = 0 ;//发料数量
        private int state = STATE_CLOSED;//领料单行状态
        private int branched = BRANCHED_NO;
        private ArrayList<PickItemBranchItem> pickItemBranchItems = new ArrayList<>();
        private Pick pick = new Pick();

        public String getPickLine() {
            return pickLine;
        }

        public void setPickLine(String pickLine) {
            this.pickLine = pickLine;
        }

        public String getSequence() {
            return sequence;
        }

        public void setSequence(String sequence) {
            this.sequence = sequence;
        }


        public double getQuantity() {
            return quantity;
        }

        public void setQuantity(double quantity) {
            this.quantity = quantity;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public int getBranched() {
            return branched;
        }

        public void setBranched(int branched) {
            this.branched = branched;
        }

        public Mater.Branch getBranch() {
            return branch;
        }

        public void setBranch(Mater.Branch branch) {
            this.branch = branch;
        }

        public ArrayList<PickItemBranchItem> getPickItemBranchItems() {
            return pickItemBranchItems;
        }

        public void setPickItemBranchItems(ArrayList<PickItemBranchItem> pickItemBranchItems) {
            this.pickItemBranchItems = pickItemBranchItems;
        }

        public Pick getPick() {
            return pick;
        }

        public void setPick(Pick pick) {
            this.pick = pick;
        }

        public double getHairQuantity() {
            return hairQuantity;
        }

        public void setHairQuantity(double hairQuantity) {
            this.hairQuantity = hairQuantity;
        }

        public static class PickItemBranchItem implements Serializable {
            private Mater.Branch branch = new Mater.Branch();
            private double quantity = 0;//发料数量
            private boolean checked = false;
            private PickItem pickItem = new PickItem();

            public PickItem getPickItem() {
                return pickItem;
            }

            public void setPickItem(PickItem pickItem) {
                this.pickItem = pickItem;
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
        }
    }
}
