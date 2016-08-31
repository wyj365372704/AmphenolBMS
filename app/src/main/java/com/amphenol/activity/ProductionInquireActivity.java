package com.amphenol.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.adapter.ProductionInquireViewPagerAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.WorkOrder;
import com.amphenol.fragment.ProductionInquireExecutionFragment;
import com.amphenol.fragment.ProductionInquireMaterFragment;
import com.amphenol.fragment.ProductionInquireSaleFragment;
import com.amphenol.fragment.ProductionInquireStepFragment;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 生产订单查询
 */
public class ProductionInquireActivity extends BaseActivity {
    private static final int REQUEST_CODE_FOR_SCAN = 0x10;
    private static final int REQUEST_CODE_INQUIRE = 0x10;
    private ProductionInquireExecutionFragment mProductionInquireExecutionFragment;
    private ProductionInquireSaleFragment mProductionInquireSaleFragment;
    private ProductionInquireMaterFragment mProductionInquireMaterFragment;
    private ProductionInquireStepFragment mProductionInquireStepFragment;

    private ProductionInquireViewPagerAdapter mProductionInquireViewPagerAdapter;

    private ViewPager mViewPager;
    private EditText mWorkOrderEditText;
    private Button mInquireButton;
    private ImageView mScanImageView;

    private WorkOrder mWorkOrder = new WorkOrder();

    private View.OnClickListener mOnClickListener;
    private TextView.OnEditorActionListener mOnEditorActionListener;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
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
        setContentView(R.layout.activity_production_inquire);
    }

    @Override
    public void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.activity_production_inquire_vp);
        mViewPager.setAdapter(mProductionInquireViewPagerAdapter);
        mWorkOrderEditText = (EditText) findViewById(R.id.purchase_receipt_main_code_et);
        mInquireButton = (Button) findViewById(R.id.fragment_purchase_receipt_inquire_bt);
        mScanImageView = (ImageView) findViewById(R.id.fragment_purchase_receipt_scan_iv);

        mScanImageView.setOnClickListener(mOnClickListener);
        mInquireButton.setOnClickListener(mOnClickListener);
        mWorkOrderEditText.setOnEditorActionListener(mOnEditorActionListener);
    }

    @Override
    public void initListeners() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fragment_purchase_receipt_scan_iv:
                        startActivityForResult(new Intent(ProductionInquireActivity.this, ScanActivity.class), REQUEST_CODE_FOR_SCAN);
                        break;
                    case R.id.fragment_purchase_receipt_inquire_bt:
                        boolean state = mInquireButton.getTag() == null ? false : (boolean) mInquireButton.getTag();
                        if (state) {//当前按钮状态为“清除”
                            mWorkOrder = new WorkOrder();
                            refreshShow();
                        } else {
                            handleScanWorkOrder(mWorkOrderEditText.getText().toString());
                        }
                        break;
                }
            }
        };
        mOnEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
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
                mLoadingDialog = new LoadingDialog(ProductionInquireActivity.this);
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
                            DecodeManager.decodeProductionOrderInquire(jsonObject, requestCode, myHandler);
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
                    ProductionInquireActivity.this.ShowToast("与服务器连接失败");
                } else {
                    ProductionInquireActivity.this.ShowToast("服务器返回错误");
                }
            }
        };
    }

    private void refreshShow() {
        mProductionInquireExecutionFragment.refreshShow(mWorkOrder);
        mProductionInquireSaleFragment.refreshShow(mWorkOrder);

        if (TextUtils.isEmpty(mWorkOrder.getNumber())) {
            mInquireButton.setTag(false);
            mInquireButton.setText("查询");

        } else {
            mInquireButton.setText("清除");
            mInquireButton.setTag(true);
            mWorkOrderEditText.getText().clear();
        }
    }

    @Override
    public void initData() {
        mProductionInquireExecutionFragment = ProductionInquireExecutionFragment.newInstance("执行", mWorkOrder);
        mProductionInquireSaleFragment = ProductionInquireSaleFragment.newInstance("销售",mWorkOrder);
        mProductionInquireMaterFragment = ProductionInquireMaterFragment.newInstance("材料");
        mProductionInquireStepFragment = ProductionInquireStepFragment.newInstance("工序");
        mProductionInquireViewPagerAdapter = new ProductionInquireViewPagerAdapter(getSupportFragmentManager(),
                new Fragment[]{mProductionInquireExecutionFragment, mProductionInquireSaleFragment, mProductionInquireMaterFragment, mProductionInquireStepFragment});
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

    private void handleScanWorkOrder(String code) {
        if (TextUtils.isEmpty(code))
            return;
        code = CommonTools.decodeScanString("W", code);
        mWorkOrderEditText.setText(code);
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(getApplicationContext(), "无效查询", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getApplicationContext()));
        param.put("env", SessionManager.getEnv(getApplicationContext()));
        param.put("work_order", code);
        param.put("warehouse", SessionManager.getWarehouse(getApplicationContext()));
        NetWorkAccessTools.getInstance(getApplicationContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_PRODUCTION_ORDER_INQUIRE, getApplicationContext()), param, REQUEST_CODE_INQUIRE, mRequestTaskListener);
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
                    } else if (bundle.getInt("code") == 5) {
                        ShowToast("该生产工单不存在");
                    } else {
                        ShowToast("获取工单信息失败");
                    }
                    break;
            }
        }
    }
}
