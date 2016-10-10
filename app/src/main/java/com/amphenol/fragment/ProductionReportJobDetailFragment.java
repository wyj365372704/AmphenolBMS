package com.amphenol.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.adapter.MyFragmentViewPagerAdapter;
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
public class ProductionReportJobDetailFragment extends Fragment {
    private static final int REQUEST_CODE_FINISH_INQUIRE = 0X10;
    private View rootView;
    private TextView actionBarTitleView;

    private Job mJob;

    private ViewPager mViewPager;
    private MyFragmentViewPagerAdapter mMyFragmentViewPagerAdapter;

    private ProductionReportJobDetailJobFragment mProductionReportJobDetailJobFragment;
    private ProductionReportJobDetailEmployeeFragment mProductionReportJobDetailEmployeeFragment;
    private ProductionReportJobDetailMachineFragment mProductionReportJobDetailMachineFragment;

    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private View.OnClickListener mOnClickListener;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private MyHandler myHandler;

    public static ProductionReportJobDetailFragment newInstance(Job job) {

        Bundle args = new Bundle();
        args.putParcelable("job", job);
        ProductionReportJobDetailFragment fragment = new ProductionReportJobDetailFragment();
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
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_production_report_job_detail, container, false);
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        initListeners();
        initData();
        initViews();
        return rootView;
    }


    private void initViews() {
        mViewPager = (ViewPager) rootView.findViewById(R.id.vp);
        mViewPager.setAdapter(mMyFragmentViewPagerAdapter);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);

        actionBarTitleView = (TextView) rootView.findViewById(R.id.actitle);

        rootView.findViewById(R.id.toolbar_menu).setOnClickListener(mOnClickListener);
    }

    private void initData() {
        myHandler = new MyHandler();
        mProductionReportJobDetailJobFragment = ProductionReportJobDetailJobFragment.newInstance("作业", mJob);
        mProductionReportJobDetailEmployeeFragment = ProductionReportJobDetailEmployeeFragment.newInstance("员工", mJob);
        mProductionReportJobDetailMachineFragment = ProductionReportJobDetailMachineFragment.newInstance("设备", mJob);
        mMyFragmentViewPagerAdapter = new MyFragmentViewPagerAdapter(getChildFragmentManager(),
                new Fragment[]{mProductionReportJobDetailJobFragment, mProductionReportJobDetailEmployeeFragment, mProductionReportJobDetailMachineFragment});
        mOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        actionBarTitleView.setText("作业详细");
                        break;
                    case 1:
                        actionBarTitleView.setText("员工详细");
                        break;
                    case 2:
                        actionBarTitleView.setText("设备详细");
                        break;
                }
            }
        };
    }

    private void initListeners() {

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.toolbar_menu:

                        handlerFinishJob(mJob.getJobNumber());
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
                        case REQUEST_CODE_FINISH_INQUIRE:
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

    /**
     * 结束作业
     */
    private void handlerFinishJob(String jobNumber) {
        if (!this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("job_number", jobNumber);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_PRODUCTION_REPORT_JOB_FINISH_INQUIRE, getContext()), param, REQUEST_CODE_FINISH_INQUIRE, mRequestTaskListener);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("job", mJob);
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
                case REQUEST_CODE_FINISH_INQUIRE:
                    if (bundle.getInt("code") == 1) {
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.activity_purchase_receipt_fl, ProductionReportJobFinishFragment.newInstance(mJob, bundle.getDouble("artificial_hours"), bundle.getDouble("machine_hours")));
                        transaction.addToBackStack(null);
                        transaction.commitAllowingStateLoss();
                        break;
                    }else{
                        ((BaseActivity)getActivity()).ShowToast("操作失败");
                    }
            }
        }
    }
}
