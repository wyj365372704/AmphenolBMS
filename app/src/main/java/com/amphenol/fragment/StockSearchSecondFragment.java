package com.amphenol.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Mater;
import com.amphenol.entity.Requisition;

/**
 * 采购收货_物料明细
 */
public class StockSearchSecondFragment extends Fragment {
    private View rootView = null;
    private TextView materNumberTextView, materDescTextView, materFormatTextView, branchTextView, quantityTextView, unitTextView, currentShardTextView, currentLocationTextView;
    private Mater.Branch branch;

    public static StockSearchSecondFragment newInstance(Mater.Branch branch) {

        Bundle args = new Bundle();
        args.putParcelable("branch",branch);
        StockSearchSecondFragment fragment = new StockSearchSecondFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args!=null){
            branch = args.getParcelable("branch");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_fast_requisition_second, container, false);
        initListeners();
        initViews();
        initData();
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        return rootView;
    }

    private void initListeners() {
    }

    private void initViews() {
        materNumberTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_mater_number_tv);
        materDescTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_mater_desc_tv);
        materFormatTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_mater_format_tv);
        branchTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_branch_number_tv);
        quantityTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_quantity_tv);
        unitTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_unity_tv);
        currentShardTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_current_shard_tv);
        currentLocationTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_current_location_tv);
    }

    private void initData() {
        materNumberTextView.setText(branch.getMater().getNumber());
        materDescTextView.setText(branch.getMater().getDesc());
        materFormatTextView.setText(branch.getMater().getFormat());
        branchTextView.setText(branch.getPo());
        quantityTextView.setText(branch.getQuantity()+"");
        unitTextView.setText(branch.getMater().getUnit());
        currentShardTextView.setText(branch.getMater().getShard());
        currentLocationTextView.setText(branch.getMater().getLocation());
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
