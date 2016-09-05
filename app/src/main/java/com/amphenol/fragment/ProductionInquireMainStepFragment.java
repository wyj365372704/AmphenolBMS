package com.amphenol.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amphenol.adapter.ProductionInquireMaterAdapter;
import com.amphenol.adapter.ProductionInquireStepAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.WorkOrder;

/**
 * Created by Carl on 2016-08-23 023.
 */
public class ProductionInquireMainStepFragment extends Fragment {
    private WorkOrder mWorkOrder;
    private boolean enable ;//当前fragment在viewPager活动下时,为true

    private ProductionInquireStepAdapter.OnItemClickListener mOnItemClickListener;
    private ProductionInquireStepFragmentCallBack mProductionInquireStepFragmentCallBack;
    private View rootView = null;
    private RecyclerView mRecyclerView;
    private ProductionInquireStepAdapter mProductionInquireStepAdapter;

    public static ProductionInquireMainStepFragment newInstance(String title, WorkOrder workOrder, ProductionInquireStepFragmentCallBack productionInquireStepFragmentCallBack) {

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putParcelable("workOrder",workOrder);
        ProductionInquireMainStepFragment fragment = new ProductionInquireMainStepFragment();
        fragment.setArguments(args);
        fragment.mProductionInquireStepFragmentCallBack = productionInquireStepFragmentCallBack;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mWorkOrder = args.getParcelable("workOrder");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_production_inquire_step, container, false);
        initListeners();
        initDate();
        initViews();
        refreshShow(mWorkOrder);
        return rootView;
    }

    private void initListeners() {
        mOnItemClickListener = new ProductionInquireStepAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                handleInquireStep(mWorkOrder.getSteps().get(position));
            }
        };
    }

    private void handleInquireStep(WorkOrder.Step step) {
        if (mProductionInquireStepFragmentCallBack != null) {
            mProductionInquireStepFragmentCallBack.gotoDetailPage(step);
        }
    }

    private void initDate() {
        enable = true;
        mProductionInquireStepAdapter = new ProductionInquireStepAdapter(getContext(), mWorkOrder.getSteps(), mOnItemClickListener);
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_production_inquire_mater_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mProductionInquireStepAdapter);
    }

    public void refreshShow(WorkOrder workOrder) {
        mWorkOrder = workOrder;
        getArguments().putParcelable("workOrder",mWorkOrder);
        if (enable) {
            mProductionInquireStepAdapter.setDate(mWorkOrder.getSteps());
            mProductionInquireStepAdapter.notifyDataSetChanged();
        }
    }

    public interface ProductionInquireStepFragmentCallBack {
        /**
         * 进入详细页面
         *
         */
        void gotoDetailPage(WorkOrder.Step step);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        enable = false;
        getArguments().putBoolean("enable",enable);
        getArguments().putParcelable("workOrder",mWorkOrder);
    }
}
