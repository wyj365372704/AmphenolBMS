package com.amphenol.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.adapter.ProductionReportJobDetailEmployeeListAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Employee;
import com.amphenol.entity.Job;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Carl on 2016-09-19 019.
 */
public class ProductionReportJobDetailEmployeeFragment extends Fragment {
    private static final int REQUEST_CODE_INQUIRE = 0X10;
    private View rootView = null;
    private Job mJob;

    private RecyclerView mRecyclerView;
    private ProductionReportJobDetailEmployeeListAdapter mProductionReportJobDetailEmployeeListAdapter;
    private ProductionReportJobDetailEmployeeListAdapter.OnItemClickListener mOnItemClickListener;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private MyHandler myHandler;

    public static ProductionReportJobDetailEmployeeFragment newInstance(String title, Job job) {

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putParcelable("job", job);
        ProductionReportJobDetailEmployeeFragment fragment = new ProductionReportJobDetailEmployeeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mJob = args.getParcelable("job");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_production_report_job_detail_employee, container, false);
        initListeners();
        initDate();
        initViews();
        refreshShow();
        return rootView;
    }

    private void initListeners() {
        mOnItemClickListener = new ProductionReportJobDetailEmployeeListAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                handleInquireEmployee(mJob.getWorkOrder().getNumber(),mJob.getStepNumber(),mJob.getProprNumber(),mJob.getJobNumber(), mJob.getEmployees().get(position).getNumber());
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
                            DecodeManager.decodeProductionReportEmployeeInquire(jsonObject, requestCode, myHandler);
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

    private void handleInquireEmployee(String workOrder,String stepNumber,String proprNumber,String jobNumber, String employeeNumber) {
        if (!this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("work_order", workOrder);
        param.put("step_number", stepNumber);
        param.put("propr_number", proprNumber);
        param.put("job_number", jobNumber);
        param.put("employee_number", employeeNumber);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_PRODUCTION_REPORT_EMPLOYEE_INQUIRE, getContext()), param, REQUEST_CODE_INQUIRE, mRequestTaskListener);
    }

    private void refreshShow() {

    }

    private void initViews() {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rl);
        mRecyclerView.setAdapter(mProductionReportJobDetailEmployeeListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void initDate() {
        myHandler = new MyHandler();
        mProductionReportJobDetailEmployeeListAdapter = new ProductionReportJobDetailEmployeeListAdapter(getContext(), mJob.getEmployees(), mOnItemClickListener);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_INQUIRE:
                    if (bundle.getInt("code") == 1) {
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.activity_purchase_receipt_fl, ProductionReportInquireEmployeeFragment.newInstance((Employee) bundle.getParcelable("employee")));
                        transaction.addToBackStack(null);
                        transaction.commitAllowingStateLoss();
                    }else{

                    }
            }

        }
    }
}
