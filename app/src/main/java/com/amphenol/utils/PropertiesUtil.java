package com.amphenol.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.FileInputStream;
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
    public static final String NETWORK_RETRY_COUNT = "NETWORK_RETRY_COUNT";//网络访问超时时间

    public static final String INQUIRE_URL_PREFIX = "INQUIRE_URL_PREFIX";//网络访问url前缀
    public static final String ACTION_GET_ENV = "ACTION_GET_ENV";//action 后缀，获取登录环境map集
    public static final String ACTION_LOGIN_CHECK = "ACTION_LOGIN_CHECK";//action 后缀，获取登录环境map集
    public static final String ACTION_GET_MENU = "ACTION_GET_MENU";//action 后缀，获取功能菜单
    public static final String ACTION_QUERY_RECEIPT = "ACTION_QUERY_RECEIPT";//action 后缀，查询收货单
    public static final String ACTION_QUERY_RECEIPT_ITEM = "ACTION_QUERY_RECEIPT_ITEM";//action 后缀，查询收货单物料明细

    public static final String ACTION_RECEIPT_CONFIRM = "ACTION_RECEIPT_CONFIRM";//action 后缀，确认物料收货
    public static final String ACTION_RECEIPT_CLOSE = "ACTION_RECEIPT_CLOSE";//action 后缀，关闭物料收货


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
