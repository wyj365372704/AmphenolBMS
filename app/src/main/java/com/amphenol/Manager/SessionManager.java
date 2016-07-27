package com.amphenol.Manager;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carl on 2016/7/15/015.
 */
public class SessionManager {
    private static String userName = "";
    private static String env = "";
    private static String warehouse = "";
    private static List<String> warehouse_list = new ArrayList<>();

    public static String getUserName(Context mContext) {
        if (TextUtils.isEmpty(userName)) {
            userName = SPManager.getInstance(mContext).getSP("user_name", "");
        }
        return userName;
    }

    public static void setUserName(String userName, Context mContext) {
        SPManager.getInstance(mContext).putSP("user_name", userName);
        SessionManager.userName = userName;
    }

    public static String getEnv(Context mContext) {
        if (TextUtils.isEmpty(env)) {
            env = SPManager.getInstance(mContext).getSP("env", "");
        }
        return env;
    }

    public static void setEnv(String env, Context mContext) {
        SPManager.getInstance(mContext).putSP("env", env);
        SessionManager.env = env;
    }

    public static String getWarehouse(Context mContext) {
        if (TextUtils.isEmpty(warehouse)) {
            warehouse = SPManager.getInstance(mContext).getSP("warehouse", "");
        }
        return warehouse;
    }

    public static void setWarehouse(String warehouse, Context mContext) {
        SPManager.getInstance(mContext).putSP("warehouse", warehouse);
        SessionManager.warehouse = warehouse;
    }

    public static List<String> getWarehouse_list(Context mContext) {
        if (warehouse_list.size() == 0) {
            List<String> temp = com.alibaba.fastjson.JSONArray.parseArray(SPManager.getInstance(mContext).getSP("warehouse_list", ""), String.class);
            warehouse_list = temp == null ? warehouse_list : temp;
        }
        return warehouse_list;
    }

    public static void setWarehouse_list(List<String> warehouse_list, Context mContext) {
        SPManager.getInstance(mContext).putSP("warehouse_list", com.alibaba.fastjson.JSONArray.toJSONString(warehouse_list));
        SessionManager.warehouse_list = warehouse_list;
    }
}
