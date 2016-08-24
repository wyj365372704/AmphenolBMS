package com.amphenol.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amphenol.amphenol.R;

/**
 * Created by Carl on 2016-08-23 023.
 */
public class ProductionInquireExecutionFragment extends Fragment {
    private View rootView = null;
    public static ProductionInquireExecutionFragment newInstance(String title) {

        Bundle args = new Bundle();
        args.putString("title",title);
        ProductionInquireExecutionFragment fragment = new ProductionInquireExecutionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args!=null){

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_production_inquire_execution, container, false);
        initViews();
        return rootView;
    }

    private void initViews() {

    }

}
