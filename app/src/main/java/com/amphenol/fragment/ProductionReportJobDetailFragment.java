package com.amphenol.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.amphenol.adapter.MyFragmentViewPagerAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Job;

/**
 * Created by Carl on 2016-09-19 019.
 */
public class ProductionReportJobDetailFragment extends Fragment {
    private View rootView;

    private Job mJob;

    private ViewPager mViewPager;
    private MyFragmentViewPagerAdapter mMyFragmentViewPagerAdapter;

    private ProductionReportJobDetailJobFragment mProductionReportJobDetailJobFragment;
    private ProductionReportJobDetailEmployeeFragment mProductionReportJobDetailEmployeeFragment;
    private ProductionReportJobDetailMachineFragment mProductionReportJobDetailMachineFragment;

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
    }

    private void initData() {
        mProductionReportJobDetailJobFragment = ProductionReportJobDetailJobFragment.newInstance("作业", mJob);
        mProductionReportJobDetailEmployeeFragment = ProductionReportJobDetailEmployeeFragment.newInstance("员工", mJob);
        mProductionReportJobDetailMachineFragment = ProductionReportJobDetailMachineFragment.newInstance("设备", mJob);
        mMyFragmentViewPagerAdapter = new MyFragmentViewPagerAdapter(getChildFragmentManager(),
                new Fragment[]{mProductionReportJobDetailJobFragment, mProductionReportJobDetailEmployeeFragment, mProductionReportJobDetailMachineFragment});
    }

    private void initListeners() {

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
}
