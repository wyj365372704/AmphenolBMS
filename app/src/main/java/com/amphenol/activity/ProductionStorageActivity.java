package com.amphenol.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Mater;
import com.amphenol.entity.WorkOrder;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 生产入库
 */
public class ProductionStorageActivity extends BaseActivity {

    private static final int REQUEST_CODE_INQUIRE = 0X10;
    private static final int REQUEST_CODE_FOR_SCAN_WORK_ORDER = 0X11;
    private static final int REQUEST_CODE_SUBMIT = 0x12;
    private ImageView mScanImageView;
    private EditText mWorkOrderEditText, mBranchEditText, mEachBoxQuantityEditText, mBoxQuantityEditText, mantissaEditText, mLocationEditText;
    private TextView mOrderStateTextView, mProductOrderNumberTextView, mProductDescTextView, mProductTextView, mOrderQuantityTextView,
            mStorageQuantityTextView, mUnitTextView, mTotalQuantityTextView, mWarehouseTextView, mBranchedTextView;
    private Spinner shardSpinner;
    private Button mInquireButton, mStorButton;
    private View.OnClickListener mOnClickListener;
    private ArrayAdapter<String> mStringArrayAdapter;
    private TextView.OnEditorActionListener mOnEditorActionListener;
    private TextWatcher mEachBoxQuantityTextWatcher, mBoxQuantityTextWatcher, mMantissaTextWatcher;
    private MyHandler myHandler = new MyHandler();
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;

    private WorkOrder mWorkOrder;
    private View dialogView;//弹窗dialog视图


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshShow();
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_production_storage);
    }

    @Override
    public void initViews() {
        mOrderStateTextView = (TextView) findViewById(R.id.activity_production_order_state);
        mScanImageView = (ImageView) findViewById(R.id.fragment_purchase_receipt_scan_iv);
        mScanImageView.setOnClickListener(mOnClickListener);
        mWorkOrderEditText = (EditText) findViewById(R.id.purchase_receipt_main_code_et);
        mWorkOrderEditText.setOnEditorActionListener(mOnEditorActionListener);
        mBranchEditText = (EditText) findViewById(R.id.fragment_fast_requisition_main_from_branch_et);
        mBranchEditText.setOnEditorActionListener(mOnEditorActionListener);
        mEachBoxQuantityEditText = (EditText) findViewById(R.id.fragment_fast_requisition_main_from_meixiangshuliang_et);
//        mEachBoxQuantityEditText.setOnEditorActionListener(mOnEditorActionListener);
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
        mInquireButton = (Button) findViewById(R.id.fragment_purchase_receipt_inquire_bt);
        mInquireButton.setOnClickListener(mOnClickListener);
        mStorButton = (Button) findViewById(R.id.fragment_fast_requisition_main_submit_bt);
        mStorButton.setOnClickListener(mOnClickListener);
        mBranchedTextView = (TextView) findViewById(R.id.fragment_create_requisition_second_branched_tv);
        shardSpinner = (Spinner) findViewById(R.id.fragment_fast_requisition_main_shard_spinner);
        shardSpinner.setAdapter(mStringArrayAdapter);
        mEachBoxQuantityEditText.addTextChangedListener(mEachBoxQuantityTextWatcher);
        mBoxQuantityEditText.addTextChangedListener(mBoxQuantityTextWatcher);
        mantissaEditText.addTextChangedListener(mMantissaTextWatcher);
    }

    @Override
    public void initListeners() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fragment_purchase_receipt_inquire_bt:
                        boolean state = mInquireButton.getTag() == null ? false : (boolean) mInquireButton.getTag();
                        if (state) {//当前按钮状态为“清除”
                            mWorkOrder = new WorkOrder();
                            refreshShow();
                        } else {
                            handleScanWorkOrder(mWorkOrderEditText.getText().toString());
                        }
                        break;
                    case R.id.fragment_purchase_receipt_scan_iv:
                        startActivityForResult(new Intent(ProductionStorageActivity.this, ScanActivity.class), REQUEST_CODE_FOR_SCAN_WORK_ORDER);
                        break;
                    case R.id.fragment_fast_requisition_main_submit_bt:
                        if (TextUtils.isEmpty(mWorkOrder.getNumber())) {
                            ShowToast("未查询到工单详细");
                            break;
                        }
                        if (mWorkOrder.getState() != WorkOrder.ORDER_STATE_ISSUED && mWorkOrder.getState() != WorkOrder.ORDER_STATE_BEGINNNG && mWorkOrder.getState() != WorkOrder.ORDER_STATE_PROCESS_FINISHED) {
                            ShowToast("该生产订单状态不允许入库");
                            break;
                        }
                        if (mWorkOrder.getMater().getBranchControl() == Mater.BRANCH_CONTROL && TextUtils.isEmpty(mBranchEditText.getText().toString().trim())) {
                            ShowToast("该产品为受批次控制,请输入批号");
                            mBranchEditText.requestFocus();
                            break;
                        }
                        String branch = "";
                        if (mWorkOrder.getMater().getBranchControl() == Mater.BRANCH_CONTROL)
                            branch = mBranchEditText.getText().toString().trim();
                        double quantity = 0;
                        try {
                            quantity = Double.parseDouble(mTotalQuantityTextView.getText().toString());
                        } catch (Throwable e) {
                        }
                        if (quantity == 0) {
                            ShowToast("入库数量不能为0");
                            break;
                        }
                        if (mStringArrayAdapter.getCount() == 0) {
                            ShowToast("子库列表为空,请退出后重试");
                            break;
                        }
                        final String beforeShard = mWorkOrder.getMater().getShard();
                        final String afterShard = mStringArrayAdapter.getItem(shardSpinner.getSelectedItemPosition());

                        if (TextUtils.isEmpty(afterShard)) {
                            ShowToast("入库子库为空,不允许入库");
                            break;
                        }
                        final String beforeLocation = mWorkOrder.getMater().getLocation();
                        final String afterLocation = mLocationEditText.getText().toString();
                        if (TextUtils.isEmpty(afterLocation)) {
                            ShowToast("入库库位为空,不允许入库");
                            break;
                        }
                        final String finalBranch = branch;
                        final double finalQuantity = quantity;

                        AlertDialog.Builder builder2 = new AlertDialog.Builder(ProductionStorageActivity.this);
                        builder2.setTitle("确认入库").setMessage("将要进行入库操作?");
                        if (!TextUtils.equals(beforeShard, afterShard) || !TextUtils.equals(beforeLocation, afterLocation)) {
                            dialogView = LayoutInflater.from(ProductionStorageActivity.this).inflate(R.layout.check_requisition_sure_with_shard_or_location_changed_dialog, null, false);
                            builder2.setView(dialogView);
                        }
                        builder2.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean update = false;
                                if (!TextUtils.equals(beforeShard, afterShard) || !TextUtils.equals(beforeLocation, afterLocation) && dialogView != null) {
                                    CheckBox checkBox = (CheckBox) dialogView.findViewById(R.id.checkbox);
                                    if (checkBox.getVisibility() == View.VISIBLE) {
                                        if (checkBox.isChecked())
                                            update = true;
                                    }
                                }
                                handleStorage(mWorkOrder.getNumber(), finalBranch,
                                        finalQuantity, SessionManager.getWarehouse(getApplicationContext()),
                                        afterShard, afterLocation, update);
                            }
                        });
                        builder2.create().show();
                        break;
                }
            }
        };
        mEachBoxQuantityTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTotalQuantity();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mBoxQuantityTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTotalQuantity();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mMantissaTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTotalQuantity();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mOnEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (v.getId()) {
                    case R.id.purchase_receipt_main_code_et:
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm.isActive()) {
                                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                            }
                            handleScanWorkOrder(mWorkOrderEditText.getText().toString().trim());
                            return true;
                        }
                        break;
                    case R.id.fragment_fast_requisition_main_from_branch_et:
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm.isActive()) {
                                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                            }
                            handleScanBranchOrEachBoxQuantity(mBranchEditText.getText().toString().trim());
                            return true;
                        }
                        break;
                }

                return false;
            }
        };
        mRequestTaskListener = new NetWorkAccessTools.RequestTaskListener() {
            @Override
            public void onRequestStart(int requestCode) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                    mLoadingDialog = null;
                }
                mLoadingDialog = new LoadingDialog(ProductionStorageActivity.this);
                mLoadingDialog.show();
            }

            @Override
            public void onRequestLoading(int requestCode, long current, long count) {

            }

            @Override
            public void onRequestSuccess(JSONObject jsonObject, int requestCode) {
                try {
                    switch (requestCode) {
                        case REQUEST_CODE_INQUIRE:
                            DecodeManager.decodeProductionStorageInquire(jsonObject, requestCode, myHandler);
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
                if (errorNo == NetWorkAccessTools.ERROR_CODE_ACCESS_FAILED) {
                    ProductionStorageActivity.this.ShowToast("与服务器连接失败");
                } else {
                    ProductionStorageActivity.this.ShowToast("服务器返回错误");
                }
            }
        };
    }

    private void handleStorage(String workOrder, String branch, double quantity, String warehouse, String shard, String location, boolean update) {
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getApplicationContext()));
        param.put("env", SessionManager.getEnv(getApplicationContext()));
        param.put("work_order",workOrder);
        param.put("branch",branch);
        param.put("quantity",quantity+"");
        param.put("warehouse",warehouse);
        param.put("shard",shard);
        param.put("location",location);
        param.put("update", update ? "1" : "0");
        NetWorkAccessTools.getInstance(getApplicationContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_PRODUCTION_STORAGE_SUBMIT, getApplicationContext()), param, REQUEST_CODE_SUBMIT, mRequestTaskListener);
    }

    private void refreshShow() {
        mWorkOrderEditText.setText(mWorkOrder.getNumber());
        mWarehouseTextView.setText(mWorkOrder.getMater().getWarehouse());
        mOrderStateTextView.setText(mWorkOrder.getState() == WorkOrder.ORDER_STATE_BEGINNNG ? "开始生产" :
                mWorkOrder.getState() == WorkOrder.ORDER_STATE_CANCELED ? "取消" :
                        mWorkOrder.getState() == WorkOrder.ORDER_STATE_FINISHED ? "完成" :
                                mWorkOrder.getState() == WorkOrder.ORDER_STATE_ISSUED ? "已下达" :
                                        mWorkOrder.getState() == WorkOrder.ORDER_STATE_MATER_FINISHED ? "物料完成" :
                                                mWorkOrder.getState() == WorkOrder.ORDER_STATE_PROCESS_FINISHED ? "工序完成" : "");
        mProductOrderNumberTextView.setText(mWorkOrder.getNumber());
        mProductDescTextView.setText(mWorkOrder.getMater().getDesc());
        mProductTextView.setText(mWorkOrder.getMater().getNumber());
        updateTotalQuantity();
        mBranchedTextView.setText(mWorkOrder.getMater().getBranchControl() == Mater.BRANCH_CONTROL ? "是" : mWorkOrder.getMater().getBranchControl() == Mater.BRANCH_NO_CONTROL ? "否" : "");
        mBranchEditText.clearComposingText();
        mOrderQuantityTextView.setText(Double.toString(mWorkOrder.getQuantityOrder()));
        mStorageQuantityTextView.setText(Double.toString(mWorkOrder.getQuantityStoraged()));
        mUnitTextView.setText(mWorkOrder.getMater().getUnit());
        mEachBoxQuantityEditText.clearComposingText();
        mBoxQuantityEditText.clearComposingText();
        mantissaEditText.clearComposingText();
        shardSpinner.setSelection(0);
        try {
            int position = mStringArrayAdapter.getPosition(mWorkOrder.getMater().getShard());
            shardSpinner.setSelection(mStringArrayAdapter.getItemViewType(position));
        } catch (Throwable e) {
            e.printStackTrace();
        }

        mLocationEditText.clearComposingText();

        if (TextUtils.isEmpty(mWorkOrder.getNumber())) {
            mInquireButton.setTag(false);
            mInquireButton.setText("查询");
            mLocationEditText.getText().clear();
            mBranchEditText.getText().clear();
        } else {
            mInquireButton.setText("清除");
            mInquireButton.setTag(true);
        }
    }

    private void handleScanWorkOrder(String code) {
        if (TextUtils.isEmpty(code))
            return;
        code = CommonTools.decodeScanString("W", code);
        mWorkOrderEditText.setText(code);
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getApplicationContext()));
        param.put("env", SessionManager.getEnv(getApplicationContext()));
        param.put("work_order", code);
        param.put("warehouse", SessionManager.getWarehouse(getApplicationContext()));
        NetWorkAccessTools.getInstance(getApplicationContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_PRODUCTION_STORAGE_INQUIRE, getApplicationContext()), param, REQUEST_CODE_INQUIRE, mRequestTaskListener);
    }

    private void handleScanBranchOrEachBoxQuantity(String code) {
        if (TextUtils.isEmpty(code))
            return;
        String branch = "";
        branch = CommonTools.decodeScanString("B", code);
        mBranchEditText.setText(branch);

        String eachBoxQuantity = "";
        eachBoxQuantity = CommonTools.decodeScanString("Q", code);
        try {
            if (!TextUtils.isEmpty(eachBoxQuantity)) {
                double temp = Double.parseDouble(eachBoxQuantity);
                mEachBoxQuantityEditText.setText(Double.toString(temp));
            }
        } catch (Throwable e) {

        }

    }

    @Override
    public void initData() {
        mWorkOrder = new WorkOrder();
        mStringArrayAdapter = new ArrayAdapter<>(ProductionStorageActivity.this, android.R.layout.simple_spinner_item, SessionManager.getShard_list(ProductionStorageActivity.this));
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        mStringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

    private void updateTotalQuantity() {
        double eachBoxQuantity = 0d;
        double boxQuantity = 0d;
        double mantissa = 0d;
        try {
            eachBoxQuantity = Double.parseDouble(mEachBoxQuantityEditText.getText().toString());
        } catch (Throwable e) {
        }
        try {
            boxQuantity = Double.parseDouble(mBoxQuantityEditText.getText().toString());
        } catch (Throwable e) {
        }
        try {
            mantissa = Double.parseDouble(mantissaEditText.getText().toString());
        } catch (Throwable e) {
        }
        BigDecimal eachBoxQuantityBigDecimal = new BigDecimal(Double.toString(eachBoxQuantity));
        BigDecimal boxQuantityBigDecimal = new BigDecimal(Double.toString(boxQuantity));
        BigDecimal mantissaBigDecimal = new BigDecimal(Double.toString(mantissa));
        mTotalQuantityTextView.setText(eachBoxQuantityBigDecimal.multiply(boxQuantityBigDecimal).add(mantissaBigDecimal).toString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_SCAN_WORK_ORDER && resultCode == Activity.RESULT_OK) {
            String code = data.getStringExtra("data").trim();
            mWorkOrderEditText.setText(code);
            handleScanWorkOrder(mWorkOrderEditText.getText().toString());
        }
    }

    private void collapseButton() {
        if (mStorButton.getVisibility() == View.GONE)
            return;
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mStorButton.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mStorButton.startAnimation(animation);
    }

    private void popUpButton() {
        if (mStorButton.getVisibility() == View.VISIBLE)
            return;
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
        animation.setDuration(300);
        mStorButton.setVisibility(View.VISIBLE);
        mStorButton.startAnimation(animation);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_INQUIRE:
                    if (bundle.getInt("code") == 1) {
                        mWorkOrder = bundle.getParcelable("workOrder");
                        refreshShow();
                    } else {
                        ShowToast("获取工单信息失败");
                    }
                    break;
            }
        }
    }
}
