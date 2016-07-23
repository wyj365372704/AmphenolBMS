package com.amphenol.Manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Carl on 2016/7/15/015.
 */
public class SPManager {
    private final String SP_NAME = "amphenol_sp";
    private static SPManager spManager;
    SharedPreferences mSharedPreferences;

    private SPManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static SPManager getInstance(Context context) {
        if (spManager == null) {
            spManager = new SPManager(context);
        }
        return spManager;
    }

    public void putSP(String key, String value) {
        mSharedPreferences.edit().putString(key, value).commit();
    }

    public String getSP(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    ;
}
