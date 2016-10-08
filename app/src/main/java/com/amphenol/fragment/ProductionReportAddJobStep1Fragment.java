package com.amphenol.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Dict;
import com.amphenol.entity.Employee;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;
import com.graduate.squirrel.ui.wheel.ScreenInfo;
import com.graduate.squirrel.ui.wheel.WheelMain;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Carl on 2016-09-20 020.
 */

public class ProductionReportAddJobStep1Fragment extends Fragment {
    private static final int REQUEST_CODE_INQUIRE = 0x10;
    private static final int REQUEST_CODE_NEXT = 0X11;
    private View rootView;
    private TextView mWorkOrderTextView,mDateEditText;
    private Spinner mStepSpinner, mProprSpinner;
    private Button mNextStepButton, mSearchButton;
    private View.OnClickListener mOnClickListener;
    private EditText mWorkOrderEditText;

    private String workOrder = "";
    private List<Dict> stepDictList, proprDictList;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private MyHandler myHandler = new MyHandler();

    private ArrayAdapter<Dict> mStepDictArrayAdapter, mProprDictArrayAdapter;

    public static ProductionReportAddJobStep1Fragment newInstance() {

        Bundle args = new Bundle();

        ProductionReportAddJobStep1Fragment fragment = new ProductionReportAddJobStep1Fragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_production_report_add_job_step_1, container, false);
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
        refreshShow();
    }

    private void initViews() {
        mSearchButton = (Button) rootView.findViewById(R.id.fragment_purchase_receipt_inquire_bt);
        mSearchButton.setOnClickListener(mOnClickListener);

        mNextStepButton = (Button) rootView.findViewById(R.id.fragment_create_requisition_create_bt);
        mNextStepButton.setOnClickListener(mOnClickListener);

        mWorkOrderEditText = (EditText) rootView.findViewById(R.id.purchase_receipt_main_code_et);

        mWorkOrderTextView = (TextView) rootView.findViewById(R.id.work_order);

        mStepSpinner = (Spinner) rootView.findViewById(R.id.step_spinner);
        mStepSpinner.setAdapter(mStepDictArrayAdapter);
        mProprSpinner = (Spinner) rootView.findViewById(R.id.propr_spinner);
        mProprSpinner.setAdapter(mProprDictArrayAdapter);

        mDateEditText = (TextView) rootView.findViewById(R.id.activity_print_mater_label_date);
        mDateEditText.setOnClickListener(mOnClickListener);
    }

    private void initData() {
        stepDictList = new ArrayList<>();
        proprDictList = new ArrayList<>();
        mStepDictArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, stepDictList);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        mStepDictArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mProprDictArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, proprDictList);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        mProprDictArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void initListeners() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.activity_print_mater_label_date:
                        Calendar calendar = Calendar.getInstance();
                        showSetDatePicker(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE));
                        break;
                    case R.id.fragment_create_requisition_create_bt:
                        if (TextUtils.isEmpty(workOrder)) {
                            ((BaseActivity) getActivity()).ShowToast("未查询到生产订单");
                            break;
                        }
                        if (mStepDictArrayAdapter == null || mStepDictArrayAdapter.getCount() == 0) {
                            ((BaseActivity) getActivity()).ShowToast("未查询到工序记录");
                            break;
                        }
                        if (mProprDictArrayAdapter == null || mProprDictArrayAdapter.getCount() == 0) {
                            ((BaseActivity) getActivity()).ShowToast("未查询到生产线记录");
                            break;
                        }
                        if (TextUtils.isEmpty(mDateEditText.getText().toString())) {
                            ((BaseActivity) getActivity()).ShowToast("先输入开始时间");
                            break;
                        }
                        handleEmployeesInquire(workOrder, mStepDictArrayAdapter.getItem(mStepSpinner.getSelectedItemPosition()).getId(),
                                mProprDictArrayAdapter.getItem(mProprSpinner.getSelectedItemPosition()).getId());
                        break;
                    case R.id.fragment_purchase_receipt_inquire_bt:
                        boolean state = mSearchButton.getTag() == null ? false : (boolean) mSearchButton.getTag();
                        if (state) {//当前按钮状态为“清除”
                            workOrder = "";
                            stepDictList.clear();
                            proprDictList.clear();
                            refreshShow();
                        } else {
                            handleScanWorkOrder(mWorkOrderEditText.getText().toString());
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
                        case REQUEST_CODE_INQUIRE:
                            DecodeManager.decodeProductionReportAddNewJobWorkOrderInquire(jsonObject, requestCode, myHandler);
                            break;
                        case REQUEST_CODE_NEXT:
                            DecodeManager.decodeProductionReportAddNewJobEmployeeInquire(jsonObject, requestCode, myHandler);
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
                if (errorNo == NetWorkAccessTools.ERROR_CODE_ACCESS_FAILED) {
                    ((BaseActivity) getActivity()).ShowToast("与服务器连接失败");
                } else {
                    ((BaseActivity) getActivity()).ShowToast("服务器返回错误");
                }
            }
        };
    }

    private void handleEmployeesInquire(String workOrder, String step_number, String propr_number) {
        if (!this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("work_order", workOrder);
        param.put("step_number", step_number);
        param.put("propr_number", propr_number);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_PRODUCTION_REPORT_ADD_NEW_JOB_EMPLOYEE_INQUIRE, getContext()), param, REQUEST_CODE_NEXT, mRequestTaskListener);
    }

    private void handleScanWorkOrder(String code) {
        if (TextUtils.isEmpty(code))
            return;
        code = CommonTools.decodeScanString("W", code);
        mWorkOrderEditText.setText(code);
        if (TextUtils.isEmpty(code)) {
            ((BaseActivity) getActivity()).ShowToast("无效查询");
            return;
        }
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("work_order", code);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_PRODUCTION_REPORT_ADD_NEW_JOB_WORK_ORDER_INQUIRE, getContext()), param, REQUEST_CODE_INQUIRE, mRequestTaskListener);
    }

    private void refreshShow() {
        mWorkOrderTextView.setText(workOrder);
        mProprDictArrayAdapter.notifyDataSetChanged();
        mStepDictArrayAdapter.notifyDataSetChanged();

        if (TextUtils.isEmpty(workOrder)) {
            mSearchButton.setTag(false);
            mSearchButton.setText("查询");
            mWorkOrderEditText.getText().clear();
            collapseButton();
            mWorkOrderEditText.requestFocus();
        } else {
            mSearchButton.setText("清除");
            mSearchButton.setTag(true);
            popUpButton();
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

    private void collapseButton() {
        if (mNextStepButton.getVisibility() == View.GONE)
            return;
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mNextStepButton.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mNextStepButton.startAnimation(animation);
    }

    private void popUpButton() {
        if (mNextStepButton.getVisibility() == View.VISIBLE)
            return;
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
        animation.setDuration(300);
        mNextStepButton.setVisibility(View.VISIBLE);
        mNextStepButton.startAnimation(animation);
    }
    private void showSetDatePicker(int year, int month, int day,int hour,int minute) {

        final WheelMain wheelMain;

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View timepickerview = inflater.inflate(R.layout.timepicker, null);
        ScreenInfo screenInfo = new ScreenInfo(getActivity());
        wheelMain = new WheelMain(timepickerview,true);
        wheelMain.screenheight = screenInfo.getHeight();
        wheelMain.initDateTimePicker(year, month, day,hour,minute);
        new AlertDialog.Builder(getActivity())
                .setTitle("选择日期")
                .setView(timepickerview)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mDateEditText.setText(wheelMain.getTime());
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_INQUIRE:
                    if (bundle.getInt("code") == 1) {
                        workOrder = bundle.getString("workOrder");
                        stepDictList.clear();
                        stepDictList.addAll((ArrayList) (bundle.getParcelableArrayList("stepDictList")));
                        proprDictList.clear();
                        proprDictList.addAll((ArrayList) bundle.getParcelableArrayList("proprDictList"));
                        refreshShow();
                    } else if (bundle.getInt("code") == 5) {
                        ((BaseActivity) getActivity()).ShowToast("不存在工序信息");
                    } else  if (bundle.getInt("code") == 6) {
                        ((BaseActivity) getActivity()).ShowToast("不存在生产线信息");
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("获取工单信息失败");
                    }
                    break;
                case REQUEST_CODE_NEXT:
                    if (bundle.getInt("code") == 1) {
                        ArrayList<Employee> employees = bundle.getParcelableArrayList("employees");
                        String work_order = bundle.getString("work_order");
                        String step_number = bundle.getString("step_number");
                        String propr_number = bundle.getString("propr_number");
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.activity_purchase_receipt_fl, ProductionReportAddJobStep2Fragment.newInstance(work_order,step_number,propr_number,mDateEditText.getText().toString().trim(),employees));
                        transaction.addToBackStack(null);
                        transaction.commitAllowingStateLoss();

                    } else if (bundle.getInt("code") == 5) {
                        ((BaseActivity) getActivity()).ShowToast("该作业任务已存在,不可重复添加");
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("获取作业信息失败");
                    }
                    break;
            }
        }
    }


}
