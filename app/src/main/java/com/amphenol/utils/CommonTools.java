package com.amphenol.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Carl on 2016/7/14/014.
 */
public class CommonTools {
    public static String getUrl(String actionCode, Context context) {
        String result = "";
        result += PropertiesUtil.getInstance(context).getValue(PropertiesUtil.INQUIRE_URL_PREFIX, "");
        result += PropertiesUtil.getInstance(context).getValue(actionCode, "");
        return result;
    }

    /**
     * 解码扫码到的条码字符串，根据指定的前缀返回解码后的字符串
     *
     * @param prefix 标签前缀 ，如 P   M   L  ,不需要加上‘*’
     * @param code   待解码字符串
     * @return
     */
    public static String decodeScanString(String prefix, String code) {
        if (TextUtils.isEmpty(prefix))
            return "";
        if (TextUtils.isEmpty(code))
            return "";
        int startIndex = code.indexOf("*" + prefix);
        if (startIndex == -1) {//不含prefix的字符串
            if (code.contains("*")) {
                return "";
            }
        } else {
            int endIndex = code.indexOf("*", startIndex + 1);
            endIndex = endIndex == -1 ? code.length() : endIndex;
            startIndex += (1 + prefix.length());
            if (startIndex == endIndex) {
//                Toast.makeText(getContext(), "无效查询", Toast.LENGTH_SHORT).show();
                return "";
            }
            code = code.substring(startIndex, endIndex);
        }
        return code;
    }
}
