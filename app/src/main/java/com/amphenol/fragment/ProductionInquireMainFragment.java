package com.amphenol.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.activity.ScanActivity;
import com.amphenol.adapter.ProductionInquireViewPagerAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.WorkOrder;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Carl on 2016-09-01 001.
 */
public class ProductionInquireMainFragment extends Fragment {
    private static final int REQUEST_CODE_FOR_SCAN = 0x10;
    private static final int REQUEST_CODE_INQUIRE = 0x10;
    private ProductionInquireMainExecutionFragment mProductionInquireExecutionFragment;
    private ProductionInquireMainSaleFragment mProductionInquireSaleFragment;
    private ProductionInquireMainMaterFragment mProductionInquireMaterFragment;
    private ProductionInquireStepFragment mProductionInquireStepFragment;
    private ProductionInquireMainMaterFragment.ProductionInquireMaterFragmentCallBack mProductionInquireMaterFragmentCallBack;
    private ProductionInquireMaterDetailFragment mProductionInquireMaterDetailFragment;
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
    private View rootView;

    public static ProductionInquireMainFragment newInstance() {
        
        Bundle args = new Bundle();
        
        ProductionInquireMainFragment fragment = new ProductionInquireMainFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_production_inquire_main, container, false);
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        initListeners();
        initData();
        initViews();
        return rootView;
    }


    public void initViews() {
        mViewPager = (ViewPager) rootView.findViewById(R.id.activity_production_inquire_vp);
        mViewPager.setAdapter(mProductionInquireViewPagerAdapter);
        mWorkOrderEditText = (EditText) rootView.findViewById(R.id.purchase_receipt_main_code_et);
        mInquireButton = (Button) rootView.findViewById(R.id.fragment_purchase_receipt_inquire_bt);
        mScanImageView = (ImageView) rootView.findViewById(R.id.fragment_purchase_receipt_scan_iv);

        mScanImageView.setOnClickListener(mOnClickListener);
        mInquireButton.setOnClickListener(mOnClickListener);
        mWorkOrderEditText.setOnEditorActionListener(mOnEditorActionListener);
    }

    public void initListeners() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fragment_purchase_receipt_scan_iv:
                        startActivityForResult(new Intent(getContext(), ScanActivity.class), REQUEST_CODE_FOR_SCAN);
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
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                    handleScanWorkOrder(mWorkOrderEditText.getText().toString().trim());
                    return true;
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
                mLoadingDialog = new LoadingDialog(getActivity());
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
                    ((BaseActivity)getActivity()).ShowToast("服务器返回错误");
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
                    ((BaseActivity)getActivity()).ShowToast("与服务器连接失败");
                } else {
                    ((BaseActivity)getActivity()).ShowToast("服务器返回错误");
                }
            }
        };

        mProductionInquireMaterFragmentCallBack = new ProductionInquireMainMaterFragment.ProductionInquireMaterFragmentCallBack() {
            @Override
            public void gotoDetailPage(WorkOrder.MaterProduct materProduct) {
                mProductionInquireMaterDetailFragment = ProductionInquireMaterDetailFragment.newInstance(materProduct);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.activity_fast_requisition_fl, mProductionInquireMaterDetailFragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }
        };
    }

    private void refreshShow() {
        mProductionInquireExecutionFragment.refreshShow(mWorkOrder);
        mProductionInquireSaleFragment.refreshShow(mWorkOrder);
        mProductionInquireMaterFragment.refreshShow(mWorkOrder);

        if (TextUtils.isEmpty(mWorkOrder.getNumber())) {
            mInquireButton.setTag(false);
            mInquireButton.setText("查询");

        } else {
            mInquireButton.setText("清除");
            mInquireButton.setTag(true);
            mWorkOrderEditText.getText().clear();
        }
    }

    public void initData() {
        mProductionInquireExecutionFragment = ProductionInquireMainExecutionFragment.newInstance("执行", mWorkOrder);
        mProductionInquireSaleFragment = ProductionInquireMainSaleFragment.newInstance("销售", mWorkOrder);
        mProductionInquireMaterFragment = ProductionInquireMainMaterFragment.newInstance("材料",mWorkOrder,mProductionInquireMaterFragmentCallBack);
        mProductionInquireStepFragment = ProductionInquireStepFragment.newInstance("工序");
        mProductionInquireViewPagerAdapter = new ProductionInquireViewPagerAdapter(getChildFragmentManager(),
                new Fragment[]{mProductionInquireExecutionFragment, mProductionInquireSaleFragment, mProductionInquireMaterFragment, mProductionInquireStepFragment});
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_SCAN && resultCode == Activity.RESULT_OK) {
            String code = data.getStringExtra("data").trim();
            mWorkOrderEditText.setText(code);
            handleScanWorkOrder(code);
        }
    }

    private void handleScanWorkOrder(String code) {
        if (TextUtils.isEmpty(code))
            return;
        code = CommonTools.decodeScanString("W", code);
        mWorkOrderEditText.setText(code);
        if (TextUtils.isEmpty(code)) {
            ((BaseActivity)getActivity()).ShowToast("无效查询");
            return;
        }
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("work_order", code);
        param.put("warehouse", SessionManager.getWarehouse(getContext()));
        NetWorkAccessTools.getInstance(getActivity()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_PRODUCTION_ORDER_INQUIRE, getActivity()), param, REQUEST_CODE_INQUIRE, mRequestTaskListener);
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
                        ((BaseActivity)getActivity()).ShowToast("该生产工单不存在");
                    } else {
                        ((BaseActivity)getActivity()).ShowToast("获取工单信息失败");
                    }
                    break;
            }
        }
    }
}
