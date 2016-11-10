package com.amphenol.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Carl on 2016/7/9/009.
 * 读取配置文件内容工具类
 */
public class PropertiesUtil {
    private final String PROPERTY_PATH = "config.properties";//配置文件名称，放置于assets下面
    public static final String SPLASH_DURATION_MS = "SPLASH_DURATION_MS";//splash 页面持续时间
    public static final String NETWORK_TIMEOUT_MS = "NETWORK_TIMEOUT_MS";//网络访问超时时间
    public static final String EXIST_TIME_INTERVAL_MILL = "EXIST_TIME_INTERVAL_MILL";//menuActivity 退出时间间隔

    public static final String NETWORK_RETRY_COUNT = "NETWORK_RETRY_COUNT";//网络访问超时时间

    public static final String INQUIRE_URL_PREFIX = "INQUIRE_URL_PREFIX";//网络访问url前缀
    public static final String ACTION_GET_ENV = "ACTION_GET_ENV";//action 后缀，获取登录环境map集
    public static final String ACTION_LOGIN_CHECK = "ACTION_LOGIN_CHECK";//action 后缀，获取登录环境map集
    public static final String ACTION_GET_MENU = "ACTION_GET_MENU";//action 后缀，获取功能菜单

    public static final String ACTION_QUERY_WAREHOUSE = "ACTION_QUERY_WAREHOUSE";//action 后缀，获取仓库及仓库列表
    public static final String ACTION_QUERY_SHARD_LIST = "ACTION_QUERY_SHARD_LIST";//action 后缀，获取子库列表

    public static final String ACTION_QUERY_RECEIPT = "ACTION_QUERY_RECEIPT";//action 后缀，查询收货单
    public static final String ACTION_QUERY_RECEIPT_ITEM = "ACTION_QUERY_RECEIPT_ITEM";//action 后缀，查询收货单物料明细
    public static final String ACTION_QUERY_RETURN = "ACTION_QUERY_RETURN";//action 后缀，查询退货单
    public static final String ACTION_QUERY_RETURN_ITEM = "ACTION_QUERY_RETURN_ITEM";//action 后缀，查询退货单物料明细

    public static final String ACTION_RECEIPT_CONFIRM = "ACTION_RECEIPT_CONFIRM";//action 后缀，确认物料收货
    public static final String ACTION_RECEIPT_CLOSE = "ACTION_RECEIPT_CLOSE";//action 后缀，关闭物料收货

    public static final String ACTION_CREATE_REQUISITION_GET_MATER_LIST = "ACTION_CREATE_REQUISITION_GET_MATER_LIST";//action 后缀,创建调拨单,查询物料列表
    public static final String ACTION_CREATE_REQUISITION_GET_MATER = "ACTION_CREATE_REQUISITION_GET_MATER";//action 后缀,创建调拨单/库存查询/生产订单-材料明细,查询物料明细
    public static final String ACTION_CREATE_REQUISITION_COMMIT = "ACTION_CREATE_REQUISITION_COMMIT";//action 后缀,创建调拨单,提交
    public static final String ACTION_FAST_REQUISITION_COMMIT = "ACTION_FAST_REQUISITION_COMMIT";//action 后缀,快速调拨,提交
    public static final String ACTION_CHECK_REQUISITION_GET_MATER_LIST = "ACTION_CHECK_REQUISITION_GET_MATER_LIST";//action 后缀,审核调拨单,查询物料列表
    public static final String ACTION_CHECK_REQUISITION_GET_MATER_DETAIL = "ACTION_CHECK_REQUISITION_GET_MATER_DETAIL";//action 后缀,审核调拨单,查询物料明细
    public static final String ACTION_CHECK_REQUISITION_SURE = "ACTION_CHECK_REQUISITION_SURE";//action 后缀,审核调拨单,确认过账
    public static final String ACTION_CHECK_REQUISITION_CANCEL = "ACTION_CHECK_REQUISITION_CANCEL";//action 后缀,审核调拨单,终止过账



    public static final String ACTION_HAIR_MATER_GET_PICK_LIST = "ACTION_HAIR_MATER_GET_PICK_LIST";//action 后缀,生产发料 领料项查询
    public static final String ACTION_HAIR_MATER_GET_MATER_LIST = "ACTION_HAIR_MATER_GET_MATER_LIST";//action 后缀,生产发料 物料列表查询
    public static final String ACTION_HAIR_MATER_SUBMIT = "ACTION_HAIR_MATER_SUBMIT";//action 后缀,生产发料 发料过账
    public static final String ACTION_HAIR_MATER_RETURN_SUBMIT = "ACTION_HAIR_MATER_RETURN_SUBMIT";//action 后缀,生产发料 退料过账
    public static final String ACTION_HAIR_MATER_CANCEL = "ACTION_HAIR_MATER_CANCEL";//action 后缀,生产发料 终止过账

    public static final String ACTION_PRODUCTION_STORAGE_INQUIRE = "ACTION_PRODUCTION_STORAGE_INQUIRE";//action 后缀,生产入库 - 工单查询
    public static final String ACTION_PRODUCTION_STORAGE_SUBMIT = "ACTION_PRODUCTION_STORAGE_SUBMIT";//action 后缀,生产入库 - 确认提交

    public static final String ACTION_PRODUCTION_ORDER_INQUIRE = "ACTION_PRODUCTION_ORDER_INQUIRE";//action 后缀, 生产订单查询
    public static final String ACTION_PRODUCTION_ORDER_INQUIRE_GET_STEP_OUTSOURCE_INFO = "ACTION_PRODUCTION_ORDER_INQUIRE_GET_STEP_OUTSOURCE_INFO";//action 后缀, 生产订单查询-获取工序外协信息

    public static final String ACTION_PRINT_MATER_LABEL_GET_INQUIRE = "ACTION_PRINT_MATER_LABEL_GET_INQUIRE";//action 后缀, 打印物料标签-物料查询

    public static final String ACTION_PRODUCTION_REPORT_GET_JOB_LIST = "ACTION_PRODUCTION_REPORT_GET_JOB_LIST";//action 后缀, 生产报工-获取作业列表
    public static final String ACTION_PRODUCTION_REPORT_GET_JOB_DETAIL = "ACTION_PRODUCTION_REPORT_GET_JOB_DETAIL";//action 后缀, 生产报工-获取作业列表

    public static final String ACTION_PRODUCTION_REPORT_JOB_FINISH_INQUIRE = "ACTION_PRODUCTION_REPORT_JOB_FINISH_INQUIRE";//action 后缀, 生产报工-结束作业-计算公式查询
    public static final String ACTION_PRODUCTION_REPORT_JOB_FINISH_SUBMIT = "ACTION_PRODUCTION_REPORT_JOB_FINISH_SUBMIT";//action 后缀, 生产报工-结束作业-提交
    public static final String ACTION_PRODUCTION_REPORT_EMPLOYEE_INQUIRE = "ACTION_PRODUCTION_REPORT_EMPLOYEE_INQUIRE";//action 后缀, 生产报工-查询员工信息
    public static final String ACTION_PRODUCTION_REPORT_MACHINE_INQUIRE = "ACTION_PRODUCTION_REPORT_MACHINE_INQUIRE";//action 后缀, 生产报工-查询设备信息
    public static final String ACTION_PRODUCTION_REPORT_EMPLOYEE_ADD = "ACTION_PRODUCTION_REPORT_EMPLOYEE_ADD";//action 后缀, 生产报工-中途加入员工
    public static final String ACTION_PRODUCTION_REPORT_EMPLOYEE_REMOVE = "ACTION_PRODUCTION_REPORT_EMPLOYEE_REMOVE";//action 后缀, 生产报工-中途离开员工
    public static final String ACTION_PRODUCTION_REPORT_MACHINE_ADD = "ACTION_PRODUCTION_REPORT_MACHINE_ADD";//action 后缀, 生产报工-中途加入设备
    public static final String ACTION_PRODUCTION_REPORT_MACHINE_REMOVE = "ACTION_PRODUCTION_REPORT_MACHINE_REMOVE";//action 后缀, 生产报工-中途离开设备

    public static final String ACTION_PRODUCTION_REPORT_ADD_NEW_JOB_WORK_ORDER_INQUIRE = "ACTION_PRODUCTION_REPORT_ADD_NEW_JOB_WORK_ORDER_INQUIRE";//action 后缀, 生产报工-新增作业-生产订单号查询
    public static final String ACTION_PRODUCTION_REPORT_ADD_NEW_JOB_EMPLOYEE_INQUIRE = "ACTION_PRODUCTION_REPORT_ADD_NEW_JOB_EMPLOYEE_INQUIRE";//action 后缀, 生产报工-新增作业-获取员工列表
    public static final String ACTION_PRODUCTION_REPORT_ADD_NEW_JOB_MACHINE_INQUIRE = "ACTION_PRODUCTION_REPORT_ADD_NEW_JOB_MACHINE_INQUIRE";//action 后缀, 生产报工-新增作业-获取设备列表
    public static final String ACTION_PRODUCTION_REPORT_ADD_NEW_JOB_SUBMIT = "ACTION_PRODUCTION_REPORT_ADD_NEW_JOB_SUBMIT";//action 后缀, 生产报工-新增作业-提交

    public static final String ACTION_INVENTORY_UPDATE_SUBMIT = "ACTION_INVENTORY_UPDATE_SUBMIT";//action 后缀, 物料盘点-确认
    public static final String ACTION_INVENTORY_ADD_QUERY = "ACTION_INVENTORY_ADD_QUERY";//action 后缀, 盘点-新增库存-查询物料

    private static PropertiesUtil mPropertiesUtil;
    private  Properties mProperties;

    private PropertiesUtil(Context context) {
        mProperties = new Properties();
        try {
            mProperties.load(context.getAssets().open(PROPERTY_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized PropertiesUtil getInstance(Context context) {
        if (mPropertiesUtil == null) {
            mPropertiesUtil = new PropertiesUtil(context);
        }
        return mPropertiesUtil;
    }

    public String getValue(String name, String defaultValue) {
        String result  = mProperties.getProperty(name);
        if(TextUtils.isEmpty(result)){
            Log.v(this.getClass().getSimpleName(),"property name error -name:"+name);
            result = defaultValue;
        }
        return result;
    }
}
