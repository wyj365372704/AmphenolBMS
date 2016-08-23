package com.amphenol.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.amphenol.R;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统设置
 */
public class WareHouseSetUpActivity extends BaseActivity {
    private static final int REQUEST_CODE_QUERY_SHARD_LIST = 0x10;
    private Spinner mWareHouseSpinner;
    private String currentWareHouse = "";
    private ArrayAdapter<String> mWareHouseStringArrayAdapter;
    private AdapterView.OnItemSelectedListener mOnItemSelectedListener;
    private MyHandler myHandler = new MyHandler();
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_setup_ware_house);
    }

    @Override
    public void initViews() {
        mWareHouseSpinner = (Spinner) findViewById(R.id.activity_set_up_warehouse_spinner);
        mWareHouseSpinner.setOnItemSelectedListener(mOnItemSelectedListener);
        //第四步：将适配器添加到下拉列表上
        mWareHouseSpinner.setAdapter(mWareHouseStringArrayAdapter);
        mWareHouseSpinner.setSelection(mWareHouseStringArrayAdapter.getPosition(SessionManager.getWarehouse(getApplicationContext())));
    }

    @Override
    public void initListeners() {
        mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<String> warehouseList = SessionManager.getWarehouse_list(getApplicationContext());
                if (!TextUtils.equals(warehouseList.get(position), SessionManager.getWarehouse(getApplicationContext()))) {

                    InquireShards(warehouseList.get(position));
//                    SessionManager.setWarehouse(warehouseList.get(position), getApplicationContext());
//                    ShowToast("操作仓库修改为:" + warehouseList.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        mRequestTaskListener = new NetWorkAccessTools.RequestTaskListener() {
            @Override
            public void onRequestStart(int requestCode) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                    mLoadingDialog = null;
                }
                mLoadingDialog = new LoadingDialog(WareHouseSetUpActivity.this);
                mLoadingDialog.show();
            }

            @Override
            public void onRequestLoading(int requestCode, long current, long count) {

            }

            @Override
            public void onRequestSuccess(JSONObject jsonObject, int requestCode) {
                try {
                    switch (requestCode) {
                        case REQUEST_CODE_QUERY_SHARD_LIST:
                            DecodeManager.decodeQueryShardList(jsonObject, requestCode, myHandler);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ShowToast("服务器返回错误");
                } finally {
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                        mLoadingDialog = null;
                    }
                }
            }

            @Override
            public void onRequestFail(int requestCode, int errorNo) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                    mLoadingDialog = null;
                }
                if (errorNo == 0) {
                    ShowToast("与服务器连接失败");
                } else {
                    ShowToast("服务器返回错误");
                }
            }
        };
    }

    @Override
    public void initData() {
        List<String> warehouseList = SessionManager.getWarehouse_list(getApplicationContext());
        if (warehouseList == null)
            return;
        mWareHouseStringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, warehouseList);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        mWareHouseStringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void InquireShards(String warehouse) {
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getApplicationContext()));
        param.put("env", SessionManager.getEnv(getApplicationContext()));
        param.put("warehouse", warehouse);
        NetWorkAccessTools.getInstance(getApplicationContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_QUERY_SHARD_LIST, getApplicationContext()), param, REQUEST_CODE_QUERY_SHARD_LIST, mRequestTaskListener);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Map<String, String> params = (Map<String, String>) bundle.getSerializable("params");
            switch (msg.what) {
                case REQUEST_CODE_QUERY_SHARD_LIST:
                    if (bundle.getInt("code") == 1) {
                        SessionManager.setWarehouse(params.get("warehouse"), WareHouseSetUpActivity.this);
                        ArrayList<String> shardList = bundle.getStringArrayList("shardList");
                        SessionManager.setShard_list(shardList, getApplicationContext());
                        ShowToast("操作仓库修改为:" + SessionManager.getWarehouse(WareHouseSetUpActivity.this));
                    } else if(bundle.getInt("code") == 5) {
                        ShowToast("操作仓库修改失败:仓库不存在");
                    } else {
                        ShowToast("操作仓库修改失败");
                    }
                    break;
            }
        }
    }
}
