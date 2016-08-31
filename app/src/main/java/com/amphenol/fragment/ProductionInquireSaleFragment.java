package com.amphenol.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amphenol.amphenol.R;
import com.amphenol.entity.WorkOrder;

/**
 * Created by Carl on 2016-08-23 023.
 */
public class ProductionInquireSaleFragment extends Fragment {
    private WorkOrder mWorkOrder;

    private TextView mCustomerNameTextView, mCustomerNumberTextView, mCustomerPurchaseOrderNumberTextView,
            mOrderAmountTextView, mShipmentAmountTextView, mRemainAmountTextView, mComplianceDateTextView;

    private View rootView = null;

    public static ProductionInquireSaleFragment newInstance(String title, WorkOrder workOrder) {

        Bundle args = new Bundle();
        args.putString("title", title);
        ProductionInquireSaleFragment fragment = new ProductionInquireSaleFragment();
        fragment.setArguments(args);
        fragment.mWorkOrder = workOrder;
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
        rootView = inflater.inflate(R.layout.fragment_production_inquire_sale, container, false);
        initViews();
        return rootView;
    }

    private void initViews() {
        mCustomerNameTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_warehouse_tv_in);
        mCustomerNumberTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_department_tv_in);
        mCustomerPurchaseOrderNumberTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_product_order_number_tv_in);
        mOrderAmountTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_order_state_tv_in);
        mShipmentAmountTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_product_desc_tv_in);
        mRemainAmountTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_product_form_tv_in);
        mComplianceDateTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_ordered_quantity_tv_in);
    }

    public void refreshShow(WorkOrder workOrder) {
        mWorkOrder = workOrder;
        mCustomerNameTextView.setText(mWorkOrder.getCustomerName());
        mCustomerNumberTextView.setText(mWorkOrder.getCustomerCode());
        mCustomerPurchaseOrderNumberTextView.setText(mWorkOrder.getCustomerPurchaseOrderNumber());
        mOrderAmountTextView.setText(mWorkOrder.getQuantityOrderSale() + "");
        mShipmentAmountTextView.setText(mWorkOrder.getQuantityShipmentSale() + "");
        mRemainAmountTextView.setText(mWorkOrder.getQuantityRemainSale() + "");
        mComplianceDateTextView.setText(mWorkOrder.getComplianceDate());
    }
}
