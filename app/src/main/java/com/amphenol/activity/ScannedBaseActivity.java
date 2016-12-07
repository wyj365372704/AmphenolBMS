package com.amphenol.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;

import com.amphenol.utils.PropertiesUtil;

/**
 * Created by Carl on 2016-12-07 007.
 */

public abstract class ScannedBaseActivity extends BaseActivity {
    ScannerReceiver mScannerReceiver = new ScannerReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter(PropertiesUtil.getInstance(getApplicationContext()).getValue(PropertiesUtil.SCANNER_BROADCAST_INTENT_ACTION, ""));
        registerReceiver(mScannerReceiver, intentFilter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mScannerReceiver);
    }

    /**
     * 通知实现类处理扫描头获取到的数据
     *
     * @param message 原生数据
     */
    protected abstract void handleScanCode(String message);

    private class ScannerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(PropertiesUtil.getInstance(context).getValue(PropertiesUtil.SCANNER_BROADCAST_INTENT_TAG, ""));
            if (PropertiesUtil.getInstance(context).getValue(PropertiesUtil.SCANNER_BROADCAST_INTENT_ACTION, "").equals(intent.getAction())
                    && !TextUtils.isEmpty(message)) {
                handleScanCode(message);
            }
        }
    }
}
