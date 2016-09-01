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
public class ProductionInquireMainExecutionFragment extends Fragment {
    private View rootView = null;
    private WorkOrder mWorkOrder;

    private boolean enable ;//当前fragment在viewPager活动下时,为true

    private TextView mWareHouseTextView, mDepartmentTextView, mOrderNumberTextView, mOrderStateTextView,
            mProductNameTextView, mProductDescTextView, mProductFormTextView, mOrderAmountTextView,
            mFinishedAmountTextView, mRemainAmountTextView, mPlanBeginDateTextView, mPlanFinishDateTextView,
            mActualBeginDateTextView;

    public static ProductionInquireMainExecutionFragment newInstance(String title, WorkOrder workOrder) {

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putParcelable("workOrder", workOrder);
        ProductionInquireMainExecutionFragment fragment = new ProductionInquireMainExecutionFragment();
        fragment.setArguments(args);
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
        rootView = inflater.inflate(R.layout.fragment_production_inquire_execution, container, false);
        initDate();
        initViews();
        refreshShow(mWorkOrder);
        return rootView;
    }

    private void initDate() {
        enable = true;
    }

    private void initViews() {
        mWareHouseTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_warehouse_tv_in);
        mDepartmentTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_department_tv_in);
        mOrderNumberTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_product_order_number_tv_in);
        mOrderStateTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_order_state_tv_in);
        mProductNameTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_product_name_tv_in);
        mProductDescTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_product_desc_tv_in);
        mProductFormTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_product_form_tv_in);
        mOrderAmountTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_ordered_quantity_tv_in);
        mFinishedAmountTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_finished_quantity_tv_in);
        mRemainAmountTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_remain_quantity_tv_in);
        mPlanBeginDateTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_plan_start_date_tv_in);
        mPlanFinishDateTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_plan_finish_date_tv_in);
        mActualBeginDateTextView = (TextView) rootView.findViewById(R.id.fragment_production_inquire_execution_actual_start_date_tv_in);
    }

    public void refreshShow(WorkOrder workOrder) {
        mWorkOrder = workOrder;
        getArguments().putParcelable("workOrder",mWorkOrder);
        if (enable) {
            mWareHouseTextView.setText(mWorkOrder.getProduction().getWarehouse());
            mDepartmentTextView.setText(mWorkOrder.getDepartment());
            mOrderNumberTextView.setText(mWorkOrder.getNumber());
            mOrderStateTextView.setText(mWorkOrder.getState() == WorkOrder.ORDER_STATE_CANCELED ? "订单取消" :
                    mWorkOrder.getState() == WorkOrder.ORDER_STATE_BEGIN ? "开始生产" :
                            mWorkOrder.getState() == WorkOrder.ORDER_STATE_ISSUED ? "已下达" :
                                    mWorkOrder.getState() == WorkOrder.ORDER_STATE_FINISHED ? "完成" :
                                            mWorkOrder.getState() == WorkOrder.ORDER_STATE_MATER_FINISHED ? "物料完成" :
                                                    mWorkOrder.getState() == WorkOrder.ORDER_STATE_PROCESS_FINISHED ? "工序完成" : "");
            mProductNameTextView.setText(mWorkOrder.getProduction().getNumber());
            mProductDescTextView.setText(mWorkOrder.getProduction().getDesc());
            mProductFormTextView.setText(mWorkOrder.getProduction().getFormat());
            mOrderAmountTextView.setText(mWorkOrder.getQuantityOrderProduct() + "");
            mFinishedAmountTextView.setText(mWorkOrder.getQuantityFinishedProduct() + "");
            mRemainAmountTextView.setText(mWorkOrder.getQuantityRemainProduct() + "");
            mPlanBeginDateTextView.setText(mWorkOrder.getPlanStartDate());
            mPlanFinishDateTextView.setText(mWorkOrder.getPlanFinishDate());
            mActualBeginDateTextView.setText(mWorkOrder.getActualStartDate());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        enable = false;
        getArguments().putBoolean("enable",enable);
        getArguments().putParcelable("workOrder",mWorkOrder);
    }
}
