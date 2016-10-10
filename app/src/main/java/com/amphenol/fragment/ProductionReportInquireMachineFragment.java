package com.amphenol.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Employee;
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
public class ProductionReportInquireMachineFragment extends Fragment {
    private static final int REQUEST_CODE_OPTION = 0x01;
    private Job job;
    private Machine machine;
    private View rootView;
    private Button mStorButton;
    private TextView numberTextView, nameTextView, departmentTextView,  stateTextView;
    private OptionCallBack mOptionCallBack;
    private View.OnClickListener mOnClickListener;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private MyHandler myHandler = new MyHandler();

    public interface OptionCallBack {
        void refreshCallBack();
    }

    public static ProductionReportInquireMachineFragment newInstance(Job job, Machine machine, OptionCallBack optionCallBack) {
        Bundle args = new Bundle();
        args.putParcelable("job",job);
        args.putParcelable("machine", machine);
        ProductionReportInquireMachineFragment fragment = new ProductionReportInquireMachineFragment();
        fragment.mOptionCallBack = optionCallBack;
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            job = args.getParcelable("job");
            machine = args.getParcelable("machine");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_production_report_machine_detail, container, false);
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        initListeners();
        initDate();
        initViews();
        refreshShow();
        return rootView;
    }

    private void refreshShow() {
        numberTextView.setText(machine.getNumber());
        nameTextView.setText(machine.getName());
        departmentTextView.setText(machine.getDepartment());
        stateTextView.setText(machine.getState() == Employee.STATE_CODE_ON ? "忙碌" : machine.getState() == Employee.STATE_CODE_OFF ? "空闲" : "");
        if (machine.getState() == Employee.STATE_CODE_ON) {
            mStorButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.negative_button_background));
            mStorButton.setText("离开作业");
        } else if (machine.getState() == Employee.STATE_CODE_OFF) {
            mStorButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.positive_button_background));
            mStorButton.setText("加入作业");
        }
    }


    private void initViews() {
        mStorButton = (Button) rootView.findViewById(R.id.bt);
        numberTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_warehouse_tv_in);
        nameTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_department_tv_in);
        departmentTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_product_order_number_tv_in);
        stateTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_product_desc_tv_in);

        mStorButton.setOnClickListener(mOnClickListener);
    }

    private void initDate() {

    }

    private void initListeners() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.bt:
                        if (machine.getState() == Machine.STATE_CODE_ON) {
                            handleRemoveEmployee(job.getWorkOrder().getNumber(), job.getStepNumber(), job.getProprNumber(), job.getJobNumber(), machine.getNumber());
                        } else {
                            handleAddMachine(job.getWorkOrder().getNumber(), job.getStepNumber(), job.getProprNumber(), job.getJobNumber(), machine.getNumber());
                        }
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
                        case REQUEST_CODE_OPTION:
                            DecodeManager.decodeProductionReportAddOrRemoveOption(jsonObject, requestCode, myHandler);
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


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("machine", machine);
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

    private void handleAddMachine(String work_order, String step_number, String propr_number, String job_number, String machine_number) {
        if (!this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("warehouse", SessionManager.getWarehouse(getContext()));
        param.put("work_order", work_order);
        param.put("step_number", step_number);
        param.put("propr_number", propr_number);
        param.put("job_number", job_number);
        param.put("machine_number", machine_number);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_PRODUCTION_REPORT_MACHINE_ADD, getContext()), param, REQUEST_CODE_OPTION, mRequestTaskListener);
    }

    private void handleRemoveEmployee(String work_order, String step_number, String propr_number, String job_number, String machine_number) {
        if (!this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("warehouse", SessionManager.getWarehouse(getContext()));
        param.put("work_order", work_order);
        param.put("step_number", step_number);
        param.put("propr_number", propr_number);
        param.put("job_number", job_number);
        param.put("machine_number", machine_number);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_PRODUCTION_REPORT_MACHINE_REMOVE, getContext()), param, REQUEST_CODE_OPTION, mRequestTaskListener);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_OPTION:
                    if (bundle.getInt("code") == 1) {
                        mOptionCallBack.refreshCallBack();
                        getFragmentManager().popBackStack();
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("查询失败");
                    }
            }
        }
    }
}
