package com.amphenol.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.amphenol.R;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;

import org.json.JSONObject;

import java.util.ArrayList;

public class SplashActivity extends BaseActivity {
    private static final int REQUEST_CODE_GET_ENV = 0X10;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private MyHandler myHandler = new MyHandler();
    private long startTimeMS = 0;//发起网络访问的开始时间

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_splash);
    }

    @Override
    public void initViews() {
    }

    @Override
    public void initListeners() {
        mRequestTaskListener = new NetWorkAccessTools.RequestTaskListener() {
            @Override
            public void onRequestStart(int requestCode) {
                startTimeMS = System.currentTimeMillis();
            }

            @Override
            public void onRequestLoading(int requestCode, long current, long count) {

            }

            @Override
            public void onRequestSuccess(JSONObject jsonObject, int requestCode) {
                switch (requestCode) {
                    case REQUEST_CODE_GET_ENV:
                        try {
                            DecodeManager.decodeGetEnv(jsonObject, requestCode, myHandler);
                        } catch (Exception e) {
                            e.printStackTrace();
                            gotoLoginActivity(null);
                            ShowToast("服务器返回错误");
                        }
                        break;
                }
            }

            @Override
            public void onRequestFail(int requestCode,int errorNo) {
                if(errorNo == 0){
                    ShowToast("与服务器连接失败");
                }else{
                    ShowToast("服务器返回错误");
                }
                gotoLoginActivity(null);
            }
        };
    }

    @Override
    public void initData() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetWorkAccessTools netWorkAccessTools = NetWorkAccessTools.getInstance(getApplicationContext());
        netWorkAccessTools.getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_GET_ENV, getApplicationContext()), null, REQUEST_CODE_GET_ENV, mRequestTaskListener);
    }

    private void gotoLoginActivity(final ArrayList<String> envList) {
        long temp = Long.parseLong(PropertiesUtil.getInstance(null).getValue(PropertiesUtil.SPLASH_DURATION_MS, "2000")) - System.currentTimeMillis() + startTimeMS;//计划耗时 - 总耗时
        if (temp > 0) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    intent.putStringArrayListExtra("env_list", envList);
                    startActivity(intent);
                    finish();
                }
            }, temp);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putStringArrayListExtra("env_list", envList);
            startActivity(intent);
            finish();
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_GET_ENV:
                    ArrayList<String> envList = null;
                    if (bundle.getInt("code") == 1)
                        envList = bundle.getStringArrayList("env_list");
                    gotoLoginActivity(envList);
                    break;
            }
        }
    }
}