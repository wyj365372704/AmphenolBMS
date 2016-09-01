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
import com.amphenol.amphenol.R;
import com.amphenol.entity.WorkOrder;

/**
 * Created by Carl on 2016-08-23 023.
 */
public class ProductionInquireMainMaterFragment extends Fragment {
    private WorkOrder mWorkOrder;
    private boolean enable ;//当前fragment在viewPager活动下时,为true

    private ProductionInquireMaterAdapter.OnItemClickListener mOnItemClickListener;
    private ProductionInquireMaterFragmentCallBack mProductionInquireMaterFragmentCallBack;
    private View rootView = null;
    private RecyclerView mRecyclerView;
    private ProductionInquireMaterAdapter mProductionInquireMaterAdapter;

    public static ProductionInquireMainMaterFragment newInstance(String title, WorkOrder workOrder, ProductionInquireMaterFragmentCallBack productionInquireMaterFragmentCallBack) {

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putParcelable("workOrder",workOrder);
        ProductionInquireMainMaterFragment fragment = new ProductionInquireMainMaterFragment();
        fragment.setArguments(args);
        fragment.mProductionInquireMaterFragmentCallBack = productionInquireMaterFragmentCallBack;
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
        rootView = inflater.inflate(R.layout.fragment_production_inquire_mater, container, false);
        initListeners();
        initDate();
        initViews();
        refreshShow(mWorkOrder);
        return rootView;
    }

    private void initListeners() {
        mOnItemClickListener = new ProductionInquireMaterAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                handleInquireMater(mWorkOrder.getMaterProducts().get(position));
            }
        };
    }

    private void handleInquireMater(WorkOrder.MaterProduct materProduct) {
        if (mProductionInquireMaterFragmentCallBack != null) {
            mProductionInquireMaterFragmentCallBack.gotoDetailPage(materProduct);
        }
    }

    private void initDate() {
        enable = true;
        mProductionInquireMaterAdapter = new ProductionInquireMaterAdapter(getContext(), mWorkOrder.getMaterProducts(), mOnItemClickListener);
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_production_inquire_mater_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mProductionInquireMaterAdapter);
    }

    public void refreshShow(WorkOrder workOrder) {
        Log.d("wyj","refreshShow"+workOrder.getMaterProducts().size());
        mWorkOrder = workOrder;
        getArguments().putParcelable("workOrder",mWorkOrder);
        if (enable) {
            mProductionInquireMaterAdapter.setDate(mWorkOrder.getMaterProducts());
            mProductionInquireMaterAdapter.notifyDataSetChanged();
        }
    }

    public interface ProductionInquireMaterFragmentCallBack {
        /**
         * 进入详细页面
         *
         * @param materProduct
         */
        void gotoDetailPage(WorkOrder.MaterProduct materProduct);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        enable = false;
        getArguments().putBoolean("enable",enable);
        getArguments().putParcelable("workOrder",mWorkOrder);
    }
}
