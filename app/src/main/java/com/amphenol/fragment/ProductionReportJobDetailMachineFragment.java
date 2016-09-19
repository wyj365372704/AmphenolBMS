package com.amphenol.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amphenol.adapter.ProductionReportJobDetailEmployeeListAdapter;
import com.amphenol.adapter.ProductionReportJobDetailMachineListAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Job;

/**
 * Created by Carl on 2016-09-19 019.
 */
public class ProductionReportJobDetailMachineFragment extends Fragment {
    private View rootView = null;
    private Job mJob;
    private RecyclerView mRecyclerView;
    private ProductionReportJobDetailMachineListAdapter.OnItemClickListener mOnItemClickListener;
    private ProductionReportJobDetailMachineListAdapter mProductionReportJobDetailMachineListAdapter;

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
        initDate();
        initViews();
        refreshShow();
        return rootView;
    }

    private void refreshShow() {

    }

    private void initViews() {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rl);
        mRecyclerView.setAdapter(mProductionReportJobDetailMachineListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void initDate() {
        mProductionReportJobDetailMachineListAdapter = new ProductionReportJobDetailMachineListAdapter(getContext(), mJob.getMachines(), mOnItemClickListener);
    }
}
