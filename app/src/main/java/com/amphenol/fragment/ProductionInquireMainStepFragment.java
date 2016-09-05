package com.amphenol.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.adapter.ProductionInquireMaterAdapter;
import com.amphenol.adapter.ProductionInquireStepAdapter;
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
 * Created by Carl on 2016-08-23 023.
 */
public class ProductionInquireMainStepFragment extends Fragment {
    private static final int REQUEST_CODE_GET_OUTSOURCE_INFO = 0X10;
    private WorkOrder mWorkOrder;
    private boolean enable ;//当前fragment在viewPager活动下时,为true

    private ProductionInquireStepAdapter.OnItemClickListener mOnItemClickListener;
    private ProductionInquireStepFragmentCallBack mProductionInquireStepFragmentCallBack;
    private View rootView = null;
    private RecyclerView mRecyclerView;
    private ProductionInquireStepAdapter mProductionInquireStepAdapter;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private MyHandler myHandler;

    public static ProductionInquireMainStepFragment newInstance(String title, WorkOrder workOrder, ProductionInquireStepFragmentCallBack productionInquireStepFragmentCallBack) {

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putParcelable("workOrder",workOrder);
        ProductionInquireMainStepFragment fragment = new ProductionInquireMainStepFragment();
        fragment.setArguments(args);
        fragment.mProductionInquireStepFragmentCallBack = productionInquireStepFragmentCallBack;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mWorkOrder = args.getParcelable("workOrder");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_production_inquire_step, container, false);
        initListeners();
        initDate();
        initViews();
        refreshShow(mWorkOrder);
        return rootView;
    }

    private void initListeners() {
        mOnItemClickListener = new ProductionInquireStepAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                handleInquireStep(mWorkOrder.getNumber(),mWorkOrder.getSteps().get(position).getStepNumber());
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
                        case REQUEST_CODE_GET_OUTSOURCE_INFO:
                            DecodeManager.decodeProductionOrderInquireGetStepOutsourceInfo(jsonObject, requestCode, myHandler);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ((BaseActivity) getActivity()).ShowToast("服务器返回错误");
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
                    ((BaseActivity) getActivity()).ShowToast("与服务器连接失败");
                } else {

                    ((BaseActivity) getActivity()).ShowToast("服务器返回错误");
                }
            }
        };
    }

    private void handleInquireStep(String workOrder,String stepNumber) {
        if (!this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("work_order",workOrder);
        param.put("step_number",stepNumber);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_PRODUCTION_ORDER_INQUIRE_GET_STEP_OUTSOURCE_INFO, getContext()), param, REQUEST_CODE_GET_OUTSOURCE_INFO, mRequestTaskListener);
    }

    private void initDate() {
        enable = true;
        mProductionInquireStepAdapter = new ProductionInquireStepAdapter(getContext(), mWorkOrder.getSteps(), mOnItemClickListener);
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_production_inquire_mater_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mProductionInquireStepAdapter);
    }

    public void refreshShow(WorkOrder workOrder) {
        mWorkOrder = workOrder;
        getArguments().putParcelable("workOrder",mWorkOrder);
        if (enable) {
            mProductionInquireStepAdapter.setDate(mWorkOrder.getSteps());
            mProductionInquireStepAdapter.notifyDataSetChanged();
        }
    }

    public interface ProductionInquireStepFragmentCallBack {
        /**
         * 进入详细页面
         *
         */
        void gotoDetailPage(WorkOrder.Step step);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        enable = false;
        getArguments().putBoolean("enable",enable);
        getArguments().putParcelable("workOrder",mWorkOrder);
    }

    private  class  MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_GET_OUTSOURCE_INFO:
                    if (bundle.getInt("code") == 1) {
                        String outsourcing_supplier = bundle.getString("outsourcing_supplier");
                        double outsourcing_costs = bundle.getDouble("outsourcing_costs");
                        String outsourcing_purchase_order_number = bundle.getString("outsourcing_supplier");
                        int position = bundle.getInt("position");
                        mWorkOrder.getSteps().get(position).setOutsourcingCosts(outsourcing_costs);
                        mWorkOrder.getSteps().get(position).setOutsourcingSupplier(outsourcing_supplier);
                        mWorkOrder.getSteps().get(position).setOutsourcingPurchaseOrderNumber(outsourcing_purchase_order_number);
                        if (mProductionInquireStepFragmentCallBack != null) {
                            mProductionInquireStepFragmentCallBack.gotoDetailPage( mWorkOrder.getSteps().get(position));
                        }
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("获取物料明细失败");
                    }
                    break;
            }
        }
    }
}
