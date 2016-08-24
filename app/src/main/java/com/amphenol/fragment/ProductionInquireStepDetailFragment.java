package com.amphenol.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amphenol.amphenol.R;
import com.amphenol.entity.WorkOrder;

/**
 * Created by Carl on 2016-08-23 023.
 */
public class ProductionInquireStepDetailFragment extends Fragment {
    private View rootView = null;
    private WorkOrder mWorkOrder;

    public static ProductionInquireStepDetailFragment newInstance(WorkOrder workOrder) {

        Bundle args = new Bundle();
//        args.putParcelable("workOrder",workOrder);
        ProductionInquireStepDetailFragment fragment = new ProductionInquireStepDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args!=null){
            mWorkOrder = args.getParcelable("workOrder");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_production_inquire_step_detail, container, false);
        initViews();
        return rootView;
    }

    private void initViews() {

    }
}
