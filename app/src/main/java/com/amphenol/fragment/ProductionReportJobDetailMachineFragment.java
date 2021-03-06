package com.amphenol.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.activity.ScanActivity;
import com.amphenol.adapter.ProductionReportJobDetailMachineListAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Job;
import com.amphenol.entity.Machine;
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
public class ProductionReportJobDetailMachineFragment extends BaseFragment {
    private static final int REQUEST_CODE_INQUIRE = 0x10;
    private static final int REQUEST_CODE_FOR_SCAN = 0x11;
    private static final int REQUEST_CODE_GET_JOB_DETAIL = 0x12;
    private View rootView = null;
    private Job mJob;

    private Button mInquireButton;
    private ImageView mScanImageView;
    private EditText mEditText;

    private RecyclerView mRecyclerView;
    private ProductionReportJobDetailMachineListAdapter.OnItemClickListener mOnItemClickListener;
    private ProductionReportJobDetailMachineListAdapter mProductionReportJobDetailMachineListAdapter;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private MyHandler myHandler;
    private View.OnClickListener mOnClickListener;
    private ProductionReportInquireMachineFragment.OptionCallBack mOptionCallBack;

    public static ProductionReportJobDetailMachineFragment newInstance(String title, Job job) {

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putParcelable("job", job);
        ProductionReportJobDetailMachineFragment fragment = new ProductionReportJobDetailMachineFragment();
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
        rootView = inflater.inflate(R.layout.fragment_production_report_job_detail_machine, container, false);
        initListeners();
        initDate();
        initViews();
        refreshShow();
        return rootView;
    }

    private void initListeners() {
        mOnItemClickListener = new ProductionReportJobDetailMachineListAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                handleInquireMachine(mJob.getWorkOrder().getNumber(), mJob.getStepNumber(), mJob.getProprNumber(), mJob.getJobNumber(), mJob.getMachines().get(position).getNumber());
            }
        };
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fragment_purchase_receipt_scan_iv:
                        startActivityForResult(new Intent(getContext(), ScanActivity.class), REQUEST_CODE_FOR_SCAN);
                        break;
                    case R.id.fragment_purchase_receipt_inquire_bt:
                        handleInquireMachine(mJob.getWorkOrder().getNumber(), mJob.getStepNumber(), mJob.getProprNumber(), mJob.getJobNumber(), mEditText.getText().toString().trim());
                        break;
                }
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
                            DecodeManager.decodeProductionReportMachineInquire(jsonObject, requestCode, myHandler);
                            break;
                        case REQUEST_CODE_GET_JOB_DETAIL:
                            DecodeManager.decodeProductionReportGetJobDetail(jsonObject, requestCode, myHandler);
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


        mOptionCallBack = new ProductionReportInquireMachineFragment.OptionCallBack() {

            @Override
            public void refreshCallBack() {
                handleGetJobDetail(mJob.getJobNumber(), mJob.getStepName(), mJob.getStepNumber(), mJob.getProprName(), mJob.getProprNumber());
            }
        };
    }


    private void handleGetJobDetail(final String jobNumber, final String stepName, final String stepNumber, final String properName, final String properNumber) {
        if (rootView == null)
            return;
        rootView.post(new Runnable() {
            @Override
            public void run() {
                Map<String, String> param = new HashMap<>();
                param.put("username", SessionManager.getUserName(getContext()));
                param.put("env", SessionManager.getEnv(getContext()));
                param.put("job_number", jobNumber);
                param.put("step_name", stepName);
                param.put("step_number", stepNumber);
                param.put("proper_name", properName);
                param.put("proper_number", properNumber);
                NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_PRODUCTION_REPORT_GET_JOB_DETAIL, getContext()), param, REQUEST_CODE_GET_JOB_DETAIL, mRequestTaskListener);
            }
        });

    }

    private void handleInquireMachine(String workOrder, String stepNumber, String proprNumber, String jobNumber, String machineNumber) {
        if (!this.isVisible())
            return;
        if (TextUtils.isEmpty(machineNumber))
            return;
        mEditText.getText().clear();
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("work_order", workOrder);
        param.put("step_number", stepNumber);
        param.put("propr_number", proprNumber);
        param.put("job_number", jobNumber);
        param.put("machine_number", machineNumber);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_PRODUCTION_REPORT_MACHINE_INQUIRE, getContext()), param, REQUEST_CODE_INQUIRE, mRequestTaskListener);
    }

    private void refreshShow() {

    }

    private void initViews() {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rl);
        mRecyclerView.setAdapter(mProductionReportJobDetailMachineListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mEditText = (EditText) rootView.findViewById(R.id.purchase_receipt_main_code_et);
        mInquireButton = (Button) rootView.findViewById(R.id.fragment_purchase_receipt_inquire_bt);
        mScanImageView = (ImageView) rootView.findViewById(R.id.fragment_purchase_receipt_scan_iv);

        mScanImageView.setOnClickListener(mOnClickListener);
        mInquireButton.setOnClickListener(mOnClickListener);
    }

    private void initDate() {
        myHandler = new MyHandler();
        mProductionReportJobDetailMachineListAdapter = new ProductionReportJobDetailMachineListAdapter(getContext(), mJob.getMachines(), mOnItemClickListener);
    }


    @Override
    protected void handleScanCode(String message) {
        if (TextUtils.isEmpty(message))
            return;
        if (!this.isVisible() || !this.getUserVisibleHint())
            return;

        String machine_number = CommonTools.decodeScanString(PropertiesUtil.getInstance(getContext()).getValue(PropertiesUtil.BARCODE_PREFIX_MACHINE, ""), message);
        if (TextUtils.isEmpty(machine_number)) {
            Toast.makeText(getContext(), "无效设备标签", Toast.LENGTH_SHORT).show();
            return;
        }
        mEditText.setText(machine_number);
        handleInquireMachine(mJob.getWorkOrder().getNumber(), mJob.getStepNumber(), mJob.getProprNumber(), mJob.getJobNumber(), machine_number);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_INQUIRE:
                    if (bundle.getInt("code") == 1) {
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.activity_purchase_receipt_fl, ProductionReportInquireMachineFragment.newInstance(mJob, (Machine) bundle.getParcelable("machine"), mOptionCallBack));
                        transaction.addToBackStack(null);
                        transaction.commitAllowingStateLoss();
                    } else if (bundle.getInt("code") == 5) {
                        ((BaseActivity) getActivity()).ShowToast("无该设备信息");
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("查询失败");
                    }
                    break;
                case REQUEST_CODE_GET_JOB_DETAIL:
                    if (bundle.getInt("code") == 1) {
                        mJob.getMachines().clear();
                        mJob.getMachines().addAll(((Job) bundle.getParcelable("job")).getMachines());
                        mProductionReportJobDetailMachineListAdapter.notifyDataSetChanged();
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("获取作业详细失败");
                    }
                    break;
            }

        }
    }
}
