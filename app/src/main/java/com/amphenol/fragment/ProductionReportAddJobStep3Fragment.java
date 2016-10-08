package com.amphenol.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Toast;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.activity.ScanActivity;
import com.amphenol.adapter.ProductionReportAddJobEmployeeListAdapter;
import com.amphenol.adapter.ProductionReportAddJobMachineListAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Employee;
import com.amphenol.entity.Machine;
import com.amphenol.entity.Requisition;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductionReportAddJobStep3Fragment extends Fragment {
    private static final int REQUEST_CODE_GET_MATER_LIST = 0x10;
    private static final int REQUEST_CODE_GET_MATER = 0x11;
    private static final int REQUEST_CODE_FOR_SCAN = 0x12;
    private static final int REQUEST_CODE_SUBMIT = 0X13;
    private View rootView = null;
    private ImageView mScanImageView;
    private RecyclerView mRecyclerView;
    private Button mInquireButton, mNextButton;
    private EditText mRequisitionEditText;
    private TextView.OnEditorActionListener mOnEditorActionListener;
    private View.OnClickListener mOnClickListener;
    private LoadingDialog mLoadingDialog;
    private ProductionReportAddJobMachineListAdapter mProductionReportAddJobMachineListAdapter;
    private MyHandler myHandler;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;

    private String work_order = "", step_number = "", propr_number = "",begin_time = "";
    private ArrayList<Employee> employees = new ArrayList<>();
    private ArrayList<Machine> machines = new ArrayList<>();

    public static ProductionReportAddJobStep3Fragment newInstance(String work_order, String step_number, String propr_number,String begin_time, ArrayList<Employee> employees, ArrayList<Machine> machines) {
        Bundle args = new Bundle();
        args.putString("work_order", work_order);
        args.putString("step_number", step_number);
        args.putString("propr_number", propr_number);
        args.putString("begin_time", begin_time);
        args.putParcelableArrayList("employees", employees);
        args.putParcelableArrayList("machines", machines);
        ProductionReportAddJobStep3Fragment fragment = new ProductionReportAddJobStep3Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            work_order = args.getString("work_order");
            step_number = args.getString("step_number");
            propr_number = args.getString("propr_number");
            begin_time = args.getString("begin_time");
            employees = args.getParcelableArrayList("employees");
            machines = args.getParcelableArrayList("machines");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_production_report_add_job_step_3, container, false);
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        initListeners();
        initData();
        initViews();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mRequisitionEditText.requestFocus();
    }

    private void initData() {
        myHandler = new MyHandler();
        mProductionReportAddJobMachineListAdapter = new ProductionReportAddJobMachineListAdapter(getContext(), machines);
    }

    private void initViews() {
        mScanImageView = (ImageView) rootView.findViewById(R.id.fragment_scan_iv);
        mScanImageView.setOnClickListener(mOnClickListener);
        mRequisitionEditText = (EditText) rootView.findViewById(R.id.purchase_receipt_main_code_et);
        mRequisitionEditText.setOnEditorActionListener(mOnEditorActionListener);
        mInquireButton = (Button) rootView.findViewById(R.id.fragment_purchase_receipt_inquire_bt);
        mInquireButton.setOnClickListener(mOnClickListener);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_purchase_receipt_content_rl);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mProductionReportAddJobMachineListAdapter);
        mNextButton = (Button) rootView.findViewById(R.id.fragment_create_requisition_create_bt);
        mNextButton.setOnClickListener(mOnClickListener);
    }

    private void initListeners() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fragment_purchase_receipt_inquire_bt:
                        boolean state = mInquireButton.getTag() == null ? false : (boolean) mInquireButton.getTag();
                        if (state) {//当前按钮状态为“非选”
                            for (Machine machine : machines) {
                                machine.setChecked(false);
                            }
                            mProductionReportAddJobMachineListAdapter.notifyDataSetChanged();
                            mInquireButton.setTag(!state);
                            mInquireButton.setText("全选");
                        } else {
                            for (Machine machine : machines) {
                                machine.setChecked(true);
                            }
                            mProductionReportAddJobMachineListAdapter.notifyDataSetChanged();
                            mInquireButton.setTag(!state);
                            mInquireButton.setText("非选");
                        }
                        break;
                    case R.id.fragment_scan_iv:
                        startActivityForResult(new Intent(getActivity(), ScanActivity.class), REQUEST_CODE_FOR_SCAN);
                        break;
                    case R.id.fragment_create_requisition_create_bt:
                        handleSubmit(work_order, step_number, propr_number, employees, machines);
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
                    handleScanCode(mRequisitionEditText.getText().toString().trim());
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
                        case REQUEST_CODE_SUBMIT:
                            DecodeManager.decodeProductionReportAddNewJobSubmit(jsonObject, requestCode, myHandler);
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

    private void handleSubmit(String work_order, String step_number, String propr_number, ArrayList<Employee> employees, ArrayList<Machine> machines) {
        if (!this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("work_order", work_order);
        param.put("propr_number", propr_number);
        param.put("step_number", step_number);
        param.put("begin_time", begin_time);


        JSONArray employeeJsonArray = new JSONArray();
        JSONArray machineJsonArray = new JSONArray();
        for (Employee employee : employees) {
            if(!employee.isChecked()){
                continue;
            }
            JSONObject employeeObject = new JSONObject();
            try {
                employeeObject.put("employee_number",employee.getNumber());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            employeeJsonArray.put(employeeObject);
        }
        for (Machine machine : machines) {
            if(!machine.isChecked()){
                continue;
            }
            JSONObject machineObject = new JSONObject();
            try {
                machineObject.put("machine_number",machine.getNumber());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            machineJsonArray.put(machineObject);
        }
        try {
            param.put("employee_list", new JSONObject().put("employee_list",employeeJsonArray).toString());
            param.put("machine_list",new JSONObject().put("machine_list",machineJsonArray).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_PRODUCTION_REPORT_ADD_NEW_JOB_SUBMIT, getContext()), param, REQUEST_CODE_SUBMIT, mRequestTaskListener);

    }


    /**
     * 处理扫描得到的二维码,执行联网查询操作
     */
    private void handleScanCode(String code) {
        if (TextUtils.isEmpty(code))
            return;
        if (!this.isVisible())
            return;
        {//扫描定位物料项
            mRequisitionEditText.setText("");
            String mater = CommonTools.decodeScanString("M", code);
            String branch = CommonTools.decodeScanString("B", code);
            if (TextUtils.isEmpty(mater)) {
                Toast.makeText(getContext(), "无效物料标签", Toast.LENGTH_SHORT).show();
                return;
            }

            int position = 0;
         /*   for (Requisition.RequisitionItem requisitionItem : requisition.getRequisitionItems()) {
                if (TextUtils.equals(requisitionItem.getBranch().getPo(), branch) && TextUtils.equals(requisitionItem.getBranch().getMater().getNumber(), mater)) {
                    handleInquireMater(requisition.getNumber(), requisition.getRequisitionItems().get(position).getNumber(), requisition.getRequisitionItems().get(position).getBranch().getMater().getNumber(), requisition.getRequisitionItems().get(position).getBranch().getPo(), requisition.getRequisitionItems().get(position).getQuantity(), requisition.getRequisitionItems().get(position).getBranch().getMater().getUnit());
                    mCheckRequisitionAdapter.notifyItemChanged(position);
                    break;
                }
                position++;
            }
            if (position == requisition.getRequisitionItems().size()) {
                ((BaseActivity) getActivity()).ShowToast("该物料不在列表中");
            }*/
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_SCAN && resultCode == Activity.RESULT_OK) {
            String code = data.getStringExtra("data").trim();
            mRequisitionEditText.setText(code);
            handleScanCode(mRequisitionEditText.getText().toString().trim());
        }
    }

    public interface MainFragmentCallBack extends Serializable {
        void gotoSecondFragment(Requisition.RequisitionItem requisitionItem, ArrayList<String> shardStrings);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_SUBMIT:
                    if (bundle.getInt("code") == 1) {
                        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        ProductionReportJobListFragment a= (ProductionReportJobListFragment) getFragmentManager().findFragmentByTag(ProductionReportJobListFragment.class.getName());
                        a.handleGetJobList();
                    } else if (bundle.getInt("code") == 5) {
                        ((BaseActivity) getActivity()).ShowToast("该作业任务已存在,不可重复添加");
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("新增作业失败");
                    }
                    break;
            }
        }
    }
}
