package com.amphenol.QRCode.decode;

import android.os.Handler;

/**
 * Created by Carl on 2016/7/6/006.
 */
public interface QRInF {
    int getCropHeight();
    int getCropWidth();
    int getX();
    int getY();
    boolean isNeedCapture();
    Handler getHandler();
    void handleDecode(String result);
}
