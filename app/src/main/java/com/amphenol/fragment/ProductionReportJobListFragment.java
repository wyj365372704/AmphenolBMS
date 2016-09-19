package com.amphenol.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.amphenol.adapter.ProductionReportJobListAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Job;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carl on 2016-09-19 019.
 */
public class ProductionReportJobListFragment extends Fragment {

    private View rootView;
    private LinearLayout mtipLinearLayout;
    private RecyclerView mRecyclerView;
    private ProductionReportJobListAdapter mProductionReportJobListAdapter;
    private ProductionReportJobListAdapter.OnItemClickListener mOnItemClickListener;
    private List<Job> date;

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
        return rootView;
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rl);
        mtipLinearLayout = (LinearLayout) rootView.findViewById(R.id.ll);
    }

    private void initData() {
        date = new ArrayList<>();
        mProductionReportJobListAdapter = new ProductionReportJobListAdapter(getContext(), date, mOnItemClickListener);
    }

    private void initListeners() {

        mOnItemClickListener = new ProductionReportJobListAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {

            }
        };
    }
}
