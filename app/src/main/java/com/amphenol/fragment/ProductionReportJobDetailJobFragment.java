package com.amphenol.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Job;

/**
 * Created by Carl on 2016-09-19 019.
 */
public class ProductionReportJobDetailJobFragment extends Fragment {
    private View rootView = null;
    private Job mJob;

    private TextView jobNumberTextView, workOrderTextView, stepNameTextView, proprNameTextView, departmentTextView, createTimeTextView;

    public static ProductionReportJobDetailJobFragment newInstance(String title, Job job) {

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putParcelable("job", job);
        ProductionReportJobDetailJobFragment fragment = new ProductionReportJobDetailJobFragment();
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
        rootView = inflater.inflate(R.layout.fragment_production_report_job_detail_job, container, false);
        initDate();
        initViews();
        refreshShow();
        return rootView;
    }

    private void refreshShow() {
        jobNumberTextView.setText(mJob.getJobNumber());
        workOrderTextView.setText(mJob.getWorkOrder().getNumber());
        stepNameTextView.setText(mJob.getStepName());
        proprNameTextView.setText(mJob.getProprName());
        departmentTextView.setText(mJob.getWorkOrder().getDepartment());
        createTimeTextView.setText(mJob.getCreateTime());
    }

    private void initViews() {
        jobNumberTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_warehouse_tv_in);
        workOrderTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_department_tv_in);
        stepNameTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_product_order_number_tv_in);
        proprNameTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_order_state_tv_in);
        departmentTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_product_desc_tv_in);
        createTimeTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_product_form_tv_in);
    }

    private void initDate() {

    }
}
