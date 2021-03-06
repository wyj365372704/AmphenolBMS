package com.amphenol.entity;


/**
 * Created by Carl on 2016/7/11/011.
 * 菜单项实体
 */
public class MenuItem{
    public static final int MENU_CODE_PURCHASE_RECEIPT = 11;//收货
    public static final int MENU_CODE_PURCHASE_RETURN = 12;//退货
    public static final int MENU_CODE_PURCHASE_STORAGE = 13;//采购入库
    public static final int MENU_CODE_HAIR_MATER = 23;//生产发料
    public static final int MENU_CODE_STOR_MATER = 24;//生产入库
    public static final int MENU_CODE_PRODUCT_REPORT = 25;//生产报工
    public static final int MENU_CODE_PRODUCT_INQUIRE = 21;//生产订单查询
    public static final int MENU_CODE_CREATE_REQUISITION = 31;//创建调拨单
    public static final int MENU_CODE_CHECK_REQUISITION = 32;//审核调拨单
    public static final int MENU_CODE_FAST_REQUISITION = 33;//快速调拨
    public static final int MENU_CODE_STOCK_SEARCH = 34;//库存查询
    public static final int MENU_CODE_PRINT_MATER_LABEL = 36;//打印物料标签
    public static final int MENU_CODE_INVENTORY = 37;//盘点
    public static final int MENU_CODE_SALES_SHIPMENTS = 43;//销售出货
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