package com.amphenol.utils;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Carl on 2016/7/14/014.
 */
public class CommonTools {
    public static String getUrl(String actionCode, Context context) {
        String result = "";
        result+=PropertiesUtil.getInstance(context).getValue(PropertiesUtil.INQUIRE_URL_PREFIX,"");
        result+=PropertiesUtil.getInstance(context).getValue(actionCode,"");
        return result;
    }
}
