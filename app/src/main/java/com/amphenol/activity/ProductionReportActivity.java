package com.amphenol.activity;

import android.os.Bundle;

import com.amphenol.amphenol.R;
import com.amphenol.fragment.ProductionReportJobListFragment;


/**
 * Created by Carl on 2016-09-18 018.
 */
public class ProductionReportActivity extends BaseActivity {

    private ProductionReportJobListFragment mProductionReportJobListFragment;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_production_report);
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListeners() {
    }

    @Override
    public void initData() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProductionReportJobListFragment = mProductionReportJobListFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_purchase_receipt_fl, mProductionReportJobListFragment).commitAllowingStateLoss();
    }
}
