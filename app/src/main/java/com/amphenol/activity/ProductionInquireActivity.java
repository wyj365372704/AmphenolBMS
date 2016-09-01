package com.amphenol.activity;

import android.os.Bundle;

import com.amphenol.amphenol.R;
import com.amphenol.fragment.HairMaterMainFragment;
import com.amphenol.fragment.ProductionInquireMainFragment;

/**
 * 生产订单查询
 */
public class ProductionInquireActivity extends BaseActivity {
    private ProductionInquireMainFragment mProductionInquireMainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            return;
        mProductionInquireMainFragment = ProductionInquireMainFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_fast_requisition_fl, mProductionInquireMainFragment).commit();
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_production_inquire);
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
}
