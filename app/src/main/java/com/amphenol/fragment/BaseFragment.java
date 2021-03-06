package com.amphenol.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.amphenol.utils.PropertiesUtil;

/**
 * Created by Carl on 2016/7/15/015.
 */
public abstract class BaseFragment extends Fragment {
    ScannerReceiver mScannerReceiver = new ScannerReceiver();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        IntentFilter intentFilter = new IntentFilter(PropertiesUtil.getInstance(context).getValue(PropertiesUtil.SCANNER_BROADCAST_INTENT_ACTION, ""));
        context.registerReceiver(mScannerReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mScannerReceiver);
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
