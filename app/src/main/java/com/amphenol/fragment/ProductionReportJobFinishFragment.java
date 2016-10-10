package com.amphenol.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.amphenol.R;
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
public class ProductionReportJobFinishFragment extends Fragment {
    private static final int REQUEST_CODE_FINISH_SUBMIT = 0X10;
    private View rootView = null;
    private Job mJob;
    private double artificial_hours = 0, machine_hours = 0;
    private TextView jobNumberTextView, workOrderTextView, stepNameTextView, proprNameTextView, departmentTextView, createTimeTextView;
    private EditText mStepQuantityEditText, mArtificialHoursEditText, mMachineHoursEditText, mAbnormalHoursEditText, mAbnormalResaonEditText;
    private Button mButton;
    private View.OnClickListener mOnClickListener;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private MyHandler myHandler = new MyHandler();

    public static ProductionReportJobFinishFragment newInstance(Job job, double artificial_hours, double machine_hours) {

        Bundle args = new Bundle();
        args.putParcelable("job", job);
        args.putDouble("artificial_hours", artificial_hours);
        args.putDouble("machine_hours", machine_hours);
        ProductionReportJobFinishFragment fragment = new ProductionReportJobFinishFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mJob = args.getParcelable("job");
            artificial_hours = args.getDouble("artificial_hours");
            machine_hours = args.getDouble("machine_hours");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_production_report_job_finish, container, false);
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        initListener();
        initDate();
        initViews();
        refreshShow();

        return rootView;
    }

    private void initListener() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fragment_create_requisition_create_bt:
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("结束作业").setMessage("将要进行结束作业?");
                        builder.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String step_quantity = mStepQuantityEditText.getText().toString().trim();
                                String artificial_hours_after = mArtificialHoursEditText.getText().toString().trim();
                                String machine_hours_after = mMachineHoursEditText.getText().toString().trim();
                                String abnormal_hours = mAbnormalHoursEditText.getText().toString().trim();
                                String abnormal_reason = mAbnormalResaonEditText.getText().toString().trim();
                                if(TextUtils.isEmpty(step_quantity)){
                                    ((BaseActivity)getActivity()).ShowToast("工序数量输入不能为空");
                                    return;
                                }
                                if(TextUtils.isEmpty(artificial_hours_after)){
                                    ((BaseActivity)getActivity()).ShowToast("人工工时输入不能为空");
                                    return;
                                }
                                if(TextUtils.isEmpty(machine_hours_after)){
                                    ((BaseActivity)getActivity()).ShowToast("设备工时输入不能为空");
                                    return;
                                }
                                if(TextUtils.isEmpty(abnormal_hours)){
                                    abnormal_hours = "0";
                                }
                                handlerFinishJob(mJob.getJobNumber(),step_quantity,artificial_hours_after,machine_hours_after,abnormal_hours,abnormal_reason);
                            }
                        });
                        builder.create().show();
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
                        case REQUEST_CODE_FINISH_SUBMIT:
                            DecodeManager.decodeProductionReportJobFinishInquire(jsonObject, requestCode, myHandler);
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

    private void refreshShow() {
        jobNumberTextView.setText(mJob.getJobNumber());
        workOrderTextView.setText(mJob.getWorkOrder().getNumber());
        stepNameTextView.setText(mJob.getStepName());
        proprNameTextView.setText(mJob.getProprName());
        departmentTextView.setText(mJob.getWorkOrder().getDepartment());
        createTimeTextView.setText(mJob.getCreateTime());

        mArtificialHoursEditText.setText(String.valueOf(artificial_hours));
        mMachineHoursEditText.setText(String.valueOf(machine_hours));
    }

    private void initViews() {
        jobNumberTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_warehouse_tv_in);
        workOrderTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_department_tv_in);
        stepNameTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_product_order_number_tv_in);
        proprNameTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_order_state_tv_in);
        departmentTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_product_desc_tv_in);
        createTimeTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_product_form_tv_in);
        mButton = (Button) rootView.findViewById(R.id.fragment_create_requisition_create_bt);
        mButton.setOnClickListener(mOnClickListener);

        mStepQuantityEditText = (EditText) rootView.findViewById(R.id.fragment_production_report_step_quantity_et);
        mArtificialHoursEditText = (EditText) rootView.findViewById(R.id.fragment_production_report_artificial_hours_et);
        mMachineHoursEditText = (EditText) rootView.findViewById(R.id.fragment_production_report_machine_hours_et);
        mAbnormalHoursEditText = (EditText) rootView.findViewById(R.id.fragment_production_report_abnormal_hours_et);
        mAbnormalResaonEditText = (EditText) rootView.findViewById(R.id.fragment_production_report_abnormal_reason_et);

    }

    private void initDate() {

    }

    /**
     * 结束作业
     */
    private void handlerFinishJob(String jobNumber,String step_quantity,String artificial_hours_after,String machine_hours_after,String abnormal_hours,String abnormal_reason) {
        if (!this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("job_number", jobNumber);
        param.put("step_quantity", String.valueOf(step_quantity));
        param.put("artificial_hours_after", String.valueOf(artificial_hours_after));
        param.put("machine_hours_after", String.valueOf(machine_hours_after));
        param.put("abnormal_hours", String.valueOf(abnormal_hours));
        param.put("abnormal_reason", abnormal_reason);

        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_PRODUCTION_REPORT_JOB_FINISH_SUBMIT, getContext()), param, REQUEST_CODE_FINISH_SUBMIT, mRequestTaskListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStack();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_FINISH_SUBMIT:
                    if (bundle.getInt("code") == 1) {
                        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        ProductionReportJobListFragment a= (ProductionReportJobListFragment) getFragmentManager().findFragmentByTag(ProductionReportJobListFragment.class.getName());
                        a.handleGetJobList();
                        break;
                    }else{
                        ((BaseActivity)getActivity()).ShowToast("操作失败");
                    }
            }
        }
    }
}
