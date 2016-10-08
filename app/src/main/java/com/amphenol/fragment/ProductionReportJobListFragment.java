package com.amphenol.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.adapter.ProductionReportJobListAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Job;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Carl on 2016-09-19 019.
 */
public class ProductionReportJobListFragment extends Fragment {

    private static final int REQUEST_CODE_GET_JOB_LIST = 0X10;
    private static final int REQUEST_CODE_GET_JOB_DETAIL = 0X11;
    private View rootView;
    private RecyclerView mRecyclerView;
    private ProductionReportJobListAdapter mProductionReportJobListAdapter;
    private ProductionReportJobListAdapter.OnItemClickListener mOnItemClickListener;
    private ArrayList<Job> date;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private MyHandler myHandler;

    private View.OnClickListener mOnClickListener;

    public static ProductionReportJobListFragment newInstance() {

        Bundle args = new Bundle();
        ProductionReportJobListFragment fragment = new ProductionReportJobListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            date = args.getParcelableArrayList("jobs");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_production_report_job_list, container, false);
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        initListeners();
        initData();
        initViews();
        handleGetJobList();
        return rootView;
    }

    /**
     * 联网获取jobList数据
     */
    public void handleGetJobList() {
//        if (!this.isVisible())
//            return;
        if (rootView == null)
            return;
        rootView.post(new Runnable() {
            @Override
            public void run() {
                Map<String, String> param = new HashMap<>();
                param.put("username", SessionManager.getUserName(getContext()));
                param.put("env", SessionManager.getEnv(getContext()));
                NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_PRODUCTION_REPORT_GET_JOB_LIST, getContext()), param, REQUEST_CODE_GET_JOB_LIST, mRequestTaskListener);
            }
        });
    }
    private void handleGetJobDetail(String jobNumber,String stepName,String stepNumber,String properName,String properNumber) {
        if (!this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("job_number", jobNumber);
        param.put("step_name", stepName);
        param.put("step_number",stepNumber);
        param.put("proper_name",properName);
        param.put("proper_number",properNumber);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_PRODUCTION_REPORT_GET_JOB_DETAIL, getContext()), param, REQUEST_CODE_GET_JOB_DETAIL, mRequestTaskListener);
    }
    private void initViews() {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rl);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mProductionReportJobListAdapter);
        rootView.findViewById(R.id.toolbar_menu).setOnClickListener(mOnClickListener);
    }

    private void initData() {
        myHandler = new MyHandler();
        date = new ArrayList<>();
        mProductionReportJobListAdapter = new ProductionReportJobListAdapter(getContext(), date, mOnItemClickListener);
    }

    private void initListeners() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.activity_purchase_receipt_fl, ProductionReportAddJobStep1Fragment.newInstance());
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }
        };
        mOnItemClickListener = new ProductionReportJobListAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                handleGetJobDetail(date.get(position).getJobNumber(),date.get(position).getStepName(),date.get(position).getStepNumber(),date.get(position).getProprName(),date.get(position).getProprNumber());
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
                        case REQUEST_CODE_GET_JOB_LIST:
                            DecodeManager.decodeProductionReportGetJobList(jsonObject, requestCode, myHandler);
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
                refreshShow();
            }
        };
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("jobs", date);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_GET_JOB_LIST:
                    if (bundle.getInt("code") == 1) {
                        ArrayList<Job> jobs = bundle.getParcelableArrayList("jobs");
                        if (jobs != null) {
                            date.clear();
                            date.addAll(jobs);
                            refreshShow();
                        }
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("获取作业信息失败");
                    }
                    break;
                case REQUEST_CODE_GET_JOB_DETAIL:
                    if (bundle.getInt("code") == 1) {
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.activity_purchase_receipt_fl, ProductionReportJobDetailFragment.newInstance((Job) bundle.getParcelable("job")));
                        transaction.addToBackStack(null);
                        transaction.commitAllowingStateLoss();
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("获取作业详细失败");
                    }
                    break;
            }
        }
    }

    private void refreshShow() {
        if (date.size() == 0) {
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mProductionReportJobListAdapter.notifyDataSetChanged();
        }
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

}
