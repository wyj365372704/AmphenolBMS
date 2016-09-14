package com.amphenol.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.test.ActivityUnitTestCase;
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
public class ProductionInquireStepDetailFragment extends Fragment {
    private View rootView = null;
    private WorkOrder.Step mStep;

    TextView numberTextView, nameTextView, standardWorkingHoursTextView, actualWorkingHoursTextView,
            outsourceOrderNumberTextView, outsourceSupplierTextView, outsourceCostTextView, outsourceCostUnitTextView, tbcTextView;

    public static ProductionInquireStepDetailFragment newInstance(WorkOrder.Step step) {

        Bundle args = new Bundle();
        args.putParcelable("step", step);
        ProductionInquireStepDetailFragment fragment = new ProductionInquireStepDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mStep = args.getParcelable("step");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_production_inquire_step_detail, container, false);
        initViews();
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        return rootView;
    }

    private void initViews() {
        numberTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_step_step_number_tv_in);
        nameTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_step_step_name_tv_in);
        standardWorkingHoursTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_step_standard_work_hours_tv_in);
        actualWorkingHoursTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_step_actual_work_hours_tv_in);
        outsourceOrderNumberTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_step_outsourcing_order_number_tv_in);
        outsourceSupplierTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_step_outsourcing_supplier_tv_in);
        outsourceCostTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_step_outsourcing_cost_tv_in);
        outsourceCostUnitTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_step_outsourcing_cost_unit_tv_in);
        tbcTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_step_tbc_tv_in);


        numberTextView.setText(mStep.getStepNumber());
        nameTextView.setText(mStep.getStepName());
        standardWorkingHoursTextView.setText(mStep.getStandardWorkingHours() + "");
        actualWorkingHoursTextView.setText(mStep.getActualWorkingHours() + "");
        outsourceOrderNumberTextView.setText(mStep.getOutsourcingPurchaseOrderNumber());
        outsourceSupplierTextView.setText(mStep.getOutsourcingSupplier());
        outsourceCostTextView.setText(mStep.getOutsourcingCosts() + "");
        outsourceCostUnitTextView.setText(mStep.getOutsourcing_costs_unit() + "");
        tbcTextView.setText(mStep.getTBC());
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
