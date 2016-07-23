package com.amphenol.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 收货单实体类
 * Created by Carl on 2016/7/14/014.
 */
public class Receipt implements Serializable{

    public static final int STATUS_NO_RECEIPT = 10, STATUS_PART_RECEIPT = 40, STATUS_FIINISHED = 50;//物料状态    10：未收货    40：部分收货    50：收货完成
    private String firm = "";//送货厂商
    private String receiptNumber = "";//收货单号码
    private int status = 0;//状态
    private List<Mater> maters = new ArrayList<>();//   物料集合

    public String getFirm() {
        return firm;
    }

    public void setFirm(String firm) {
        this.firm = firm;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Mater> getMaters() {
        return maters;
    }

    public void setMaters(List<Mater> maters) {
        if (maters == null)
            maters = new ArrayList<>();
        this.maters = maters;
    }
}
