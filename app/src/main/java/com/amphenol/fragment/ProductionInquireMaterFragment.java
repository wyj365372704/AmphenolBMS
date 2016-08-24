package com.amphenol.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amphenol.amphenol.R;

/**
 * Created by Carl on 2016-08-23 023.
 */
public class ProductionInquireMaterFragment extends Fragment {
    private View rootView = null;
    private RecyclerView mRecyclerView ;
    public static ProductionInquireMaterFragment newInstance(String title) {

        Bundle args = new Bundle();
        args.putString("title",title);
        ProductionInquireMaterFragment fragment = new ProductionInquireMaterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_production_inquire_mater, container, false);
        initViews();
        return rootView;
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_production_inquire_mater_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
