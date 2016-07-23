package com.amphenol.entity;

import java.io.Serializable;

/**
 * Created by Carl on 2016/7/12/012.
 */
public class Branch extends Object{
    private String branchNumber;//到货单批明细行
    private String scpc;//生产批次
    private double jhsl;//计划数量
    private double sssl;//实收数量


    public Branch(String branchNumber, String scpc, double jhsl, double sssl) {
        this.branchNumber = branchNumber;
        this.scpc = scpc;
        this.jhsl = jhsl;
        this.sssl = sssl;
    }

    public String getBranchNumber() {
        return branchNumber;
    }

    public String getScpc() {
        return scpc;
    }

    public double getJhsl() {
        return jhsl;
    }

    public double getSssl() {
        return sssl;
    }
}
