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
import com.amphenol.amphenol.R;
import com.amphenol.entity.Mater;
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
public class ProductionInquireMainMaterFragment extends Fragment {
    private static final int REQUEST_CODE_GET_MATER = 0x10;
    private WorkOrder mWorkOrder;
    private boolean enable ;//当前fragment在viewPager活动下时,为true

    private ProductionInquireMaterAdapter.OnItemClickListener mOnItemClickListener;
    private ProductionInquireMaterFragmentCallBack mProductionInquireMaterFragmentCallBack;
    private View rootView = null;
    private RecyclerView mRecyclerView;
    private ProductionInquireMaterAdapter mProductionInquireMaterAdapter;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private MyHandler myHandler;

    public static ProductionInquireMainMaterFragment newInstance(String title, WorkOrder workOrder, ProductionInquireMaterFragmentCallBack productionInquireMaterFragmentCallBack) {

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putParcelable("workOrder",workOrder);
        ProductionInquireMainMaterFragment fragment = new ProductionInquireMainMaterFragment();
        fragment.setArguments(args);
        fragment.mProductionInquireMaterFragmentCallBack = productionInquireMaterFragmentCallBack;
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
        rootView = inflater.inflate(R.layout.fragment_production_inquire_mater, container, false);
        initListeners();
        initDate();
        initViews();
        refreshShow(mWorkOrder);
        return rootView;
    }

    private void initListeners() {
        mOnItemClickListener = new ProductionInquireMaterAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                handleInquireMater(SessionManager.getWarehouse(getContext()),mWorkOrder.getMaterProducts().get(position).getNumber(),position);
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
                        case REQUEST_CODE_GET_MATER:
                            DecodeManager.decodeProductionOrderInquireGetMater(jsonObject, requestCode, myHandler);
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
    private void handleInquireMater(String warehouse, String mater,int position) {
        if (!this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("warehouse", warehouse);
        param.put("mate",mater);
        param.put("position",String.valueOf(position));
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_CREATE_REQUISITION_GET_MATER, getContext()), param, REQUEST_CODE_GET_MATER, mRequestTaskListener);
    }

    private void initDate() {
        enable = true;
        mProductionInquireMaterAdapter = new ProductionInquireMaterAdapter(getContext(), mWorkOrder.getMaterProducts(), mOnItemClickListener);
        myHandler = new MyHandler();
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_production_inquire_mater_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mProductionInquireMaterAdapter);
    }

    public void refreshShow(WorkOrder workOrder) {
        mWorkOrder = workOrder;
        getArguments().putParcelable("workOrder",mWorkOrder);
        if (enable) {
            mProductionInquireMaterAdapter.setDate(mWorkOrder.getMaterProducts());
            mProductionInquireMaterAdapter.notifyDataSetChanged();
        }
    }

    public interface ProductionInquireMaterFragmentCallBack {
        /**
         * 进入详细页面
         *
         * @param materProduct
         */
        void gotoDetailPage(WorkOrder.MaterProduct materProduct);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        enable = false;
        getArguments().putBoolean("enable",enable);
        getArguments().putParcelable("workOrder",mWorkOrder);
    }
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_GET_MATER:
                    if (bundle.getInt("code") == 1) {
                        String mater_desc = bundle.getString("mater_desc");
                        int position = bundle.getInt("position");
                        mWorkOrder.getMaterProducts().get(position).setDesc(mater_desc);
                        if (mProductionInquireMaterFragmentCallBack != null) {
                            mProductionInquireMaterFragmentCallBack.gotoDetailPage( mWorkOrder.getMaterProducts().get(position));
                        }
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("获取物料明细失败");
                    }
                    break;
            }
        }
    }
}
