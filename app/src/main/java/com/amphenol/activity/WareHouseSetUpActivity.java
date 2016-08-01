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

import com.amphenol.Manager.SessionManager;
import com.amphenol.amphenol.R;

import java.util.List;

/**
 * 系统设置
 */
public class WareHouseSetUpActivity extends BaseActivity {
    private Spinner mWareHouseSpinner;
    private String currentWareHouse = "";
    private ArrayAdapter<String> mWareHouseStringArrayAdapter;
    private AdapterView.OnItemSelectedListener mOnItemSelectedListener;
    private MyHandler myHandler;

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
    }

    @Override
    public void initListeners() {
        mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<String> warehouseList = SessionManager.getWarehouse_list(getApplicationContext());
                if(!TextUtils.equals(warehouseList.get(position),SessionManager.getWarehouse(getApplicationContext()))){
                    SessionManager.setWarehouse(warehouseList.get(position),getApplicationContext());
                    ShowToast("操作仓库修改为:"+warehouseList.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
        //第四步：将适配器添加到下拉列表上
        mWareHouseSpinner.setAdapter(mWareHouseStringArrayAdapter);
        mWareHouseSpinner.setSelection(mWareHouseStringArrayAdapter.getPosition(SessionManager.getWarehouse(getApplicationContext())));
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

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

}
