package com.amphenol.Manager;

import android.content.Context;
import android.text.TextUtils;

/**
 * Created by Carl on 2016/7/15/015.
 */
public class SessionManager {
    private static String userName = "";
    private static String env = "";

    public static String getUserName(Context mContext) {
        if(TextUtils.isEmpty(userName)){
            userName = SPManager.getInstance(mContext).getSP("user_name","");
        }
        return userName;
    }

    public static void setUserName(String userName,Context mContext) {
        SPManager.getInstance(mContext).putSP("user_name",userName);
        SessionManager.userName = userName;
    }

    public static String getEnv(Context mContext) {
        if(TextUtils.isEmpty(env)){
            env = SPManager.getInstance(mContext).getSP("env","");
        }
        return env;
    }

    public static void setEnv(String env,Context mContext) {
        SPManager.getInstance(mContext).putSP("env",env);
        SessionManager.env = env;
    }
}
