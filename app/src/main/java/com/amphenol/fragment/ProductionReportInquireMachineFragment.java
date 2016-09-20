package com.amphenol.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Employee;
import com.amphenol.entity.Machine;

/**
 * Created by Carl on 2016-09-19 019.
 */
public class ProductionReportInquireMachineFragment extends Fragment {
    private Machine machine;
    private View rootView;
    private Button mStorButton;
    private TextView numberTextView, nameTextView, departmentTextView, typeTextView, stateTextView;

    public static ProductionReportInquireMachineFragment newInstance(Machine machine) {
        Bundle args = new Bundle();
        args.putParcelable("machine", machine);
        ProductionReportInquireMachineFragment fragment = new ProductionReportInquireMachineFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            machine = args.getParcelable("machine");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_production_report_machine_detail, container, false);
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        initListeners();
        initDate();
        initViews();
        refreshShow();
        return rootView;
    }

    private void refreshShow() {
        numberTextView.setText(machine.getNumber());
        nameTextView.setText(machine.getName());
        departmentTextView.setText(machine.getDepartment());
        typeTextView.setText(machine.getType());
        stateTextView.setText(machine.getState() == Employee.STATE_CODE_ON ? "忙碌" : machine.getState() == Employee.STATE_CODE_OFF ? "空闲" : "");
        if (machine.getState() == Employee.STATE_CODE_ON) {
            mStorButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.negative_button_background));
            mStorButton.setText("离开作业");
        } else if (machine.getState() == Employee.STATE_CODE_OFF) {
            mStorButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.positive_button_background));
            mStorButton.setText("加入作业");
        }
    }


    private void initViews() {
        numberTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_warehouse_tv_in);
        nameTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_department_tv_in);
        departmentTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_product_order_number_tv_in);
        typeTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_order_state_tv_in);
        stateTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_product_desc_tv_in);
    }

    private void initDate() {

    }

    private void initListeners() {

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("machine", machine);
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
