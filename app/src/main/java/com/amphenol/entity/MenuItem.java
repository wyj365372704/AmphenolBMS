package com.amphenol.entity;


/**
 * Created by Carl on 2016/7/11/011.
 * 菜单项实体
 */
public class MenuItem{
    public static final int MENU_CODE_PURCHASE_RECEIPT = 11;//采购收货
    public static final int MENU_CODE_PURCHASE_RETURN = 12;//采购退货
    public static final int MENU_CODE_HAIR_MATER = 23;//生产发料
    public static final int MENU_CODE_STOR_MATER = 24;//生产入库
    public static final int MENU_CODE_PRODUCT_INQUIRE = 21;//生产订单查询
    public static final int MENU_CODE_CREATE_REQUISITION = 31;//创建调拨单
    public static final int MENU_CODE_CHECK_REQUISITION = 32;//审核调拨单
    public static final int MENU_CODE_FAST_REQUISITION = 33;//快速调拨
    public static final int MENU_CODE_STOCK_SEARCH = 34;//库存查询
    public static final int MENU_CODE_SET_UP_SYSTEM = 91;//系统设置
    public static final int MENU_CODE_SET_UP_WAREHOUSE = 92;//仓库设置
    private String title;
    private int code;
    private int imageRes;

    public MenuItem(String title, int code, int imageRes) {
        this.title = title;
        this.code = code;
        this.imageRes = imageRes;
    }

    public String getTitle() {
        return title;
    }

    public int getCode() {
        return code;
    }

    public int getImageRes() {
        return imageRes;
    }

}