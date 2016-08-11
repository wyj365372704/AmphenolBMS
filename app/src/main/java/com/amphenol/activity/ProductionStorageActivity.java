package com.amphenol.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.amphenol.amphenol.R;

/**
 * 生产入库
 */
public class ProductionStorageActivity extends BaseActivity {

    private ImageView mScanImageView;
    private EditText mWorkOrderEditText, mBranchEditText, mEachBoxQuantityEditText, mBoxQuantityEditText, mantissaEditText, mLocationEditText;
    private TextView mProductOrderNumberTextView, mProductDescTextView, mProductTextView, mOrderQuantityTextView, mStorageQuantityTextView, mUnitTextView, mTotalQuantityTextView, mWarehouseTextView;
    private Spinner mSpinner;
    private Button mInquireButton, mStorButton;
    private View.OnClickListener mOnClickListener;
    private MyHandler myHandler = new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_production_storage);
    }

    @Override
    public void initViews() {
        mScanImageView = (ImageView) findViewById(R.id.fragment_scan_iv);
        mWorkOrderEditText = (EditText) findViewById(R.id.purchase_receipt_main_code_et);
        mBranchEditText = (EditText) findViewById(R.id.fragment_fast_requisition_main_from_branch_et);
        mEachBoxQuantityEditText = (EditText) findViewById(R.id.fragment_fast_requisition_main_from_meixiangshuliang_et);
        mBoxQuantityEditText = (EditText) findViewById(R.id.fragment_fast_requisition_main_from_xiangshu_et);
        mantissaEditText = (EditText) findViewById(R.id.fragment_fast_requisition_main_from_weishu_et);
        mLocationEditText = (EditText) findViewById(R.id.fragment_fast_requisition_main_from_shard_et);
        mProductOrderNumberTextView = (TextView) findViewById(R.id.fragment_create_requisition_second_mater_number_tv);
        mProductDescTextView = (TextView) findViewById(R.id.fragment_create_requisition_second_mater_desc_tv);
        mProductTextView = (TextView) findViewById(R.id.activity_production_storage_product);
        mOrderQuantityTextView = (TextView) findViewById(R.id.fragment_create_requisition_second_quantity_tv);
        mStorageQuantityTextView = (TextView) findViewById(R.id.fragment_create_requisition_second_unity_tv);
        mUnitTextView = (TextView) findViewById(R.id.fragment_create_requisition_second_current_shard_tv);
        mTotalQuantityTextView = (TextView) findViewById(R.id.activity_production_storage_total);
        mWarehouseTextView = (TextView) findViewById(R.id.activity_production_storage_warehouse);
        mInquireButton = (Button) findViewById(R.id.fragment_fast_requisition_main_inquire_bt);
        mStorButton = (Button) findViewById(R.id.fragment_fast_requisition_main_submit_bt);


    }

    @Override
    public void initListeners() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
    }

    @Override
    public void initData() {

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
