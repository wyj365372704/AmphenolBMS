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
import com.amphenol.entity.WorkOrder;

/**
 * Created by Carl on 2016-08-23 023.
 */
public class ProductionInquireMaterDetailFragment extends Fragment {
    private View rootView = null;
    private WorkOrder.MaterProduct mMaterProduct;

    TextView sequenceTextView, numberTextView, planUsageAmountTextView,
            actualUsageAmountTextView, unitTextView, descTextView, formatTextView, lastHairDateTextView;

    public static ProductionInquireMaterDetailFragment newInstance(WorkOrder.MaterProduct materProduct) {

        Bundle args = new Bundle();
        args.putParcelable("materProduct", materProduct);
        ProductionInquireMaterDetailFragment fragment = new ProductionInquireMaterDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mMaterProduct = args.getParcelable("materProduct");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_production_inquire_mater_detail, container, false);
        initViews();
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        return rootView;
    }

    private void initViews() {
        sequenceTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_mater_mater_sequence_tv_in);
        numberTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_mater_mater_number_tv_in);
        descTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_mater_mater_desc_tv_in);
        formatTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_mater_mater_form_tv_in);
        planUsageAmountTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_mater_plan_usage_quantity_tv_in);
        actualUsageAmountTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_mater_actual_usage_quantity_tv_in);
        unitTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_mater_unit_tv_in);
        lastHairDateTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_mater_last_hair_mater_date_tv_in);

        sequenceTextView.setText(mMaterProduct.getSequenceNumber());
        numberTextView.setText(mMaterProduct.getNumber());
        descTextView.setText(mMaterProduct.getDesc());
        formatTextView.setText(mMaterProduct.getFormat());
        planUsageAmountTextView.setText(mMaterProduct.getPlanUsageAmount()+"");
        actualUsageAmountTextView.setText(mMaterProduct.getActualUsageAmount()+"");
        unitTextView.setText(mMaterProduct.getUnit());
        lastHairDateTextView.setText(mMaterProduct.getLastHairMaterDate());

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
