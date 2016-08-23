package com.amphenol.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.amphenol.adapter.ProductionInquireViewPagerAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.fragment.ProductionInquireExecutionFragment;
import com.amphenol.fragment.ProductionInquireMaterFragment;
import com.amphenol.fragment.ProductionInquireStepFragment;

/**
 * 生产订单查询
 */
public class ProductionInquireActivity extends BaseActivity {
    private ProductionInquireExecutionFragment mProductionInquireExecutionFragment;
    private ProductionInquireMaterFragment mProductionInquireMaterFragment;
    private ProductionInquireStepFragment mProductionInquireStepFragment;

    private ProductionInquireViewPagerAdapter mProductionInquireViewPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_production_inquire);
    }

    @Override
    public void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.activity_production_inquire_vp);
//        mPagerTabStrip = (PagerTabStrip) findViewById(R.id.activity_production_inquire_pts);

        mViewPager.setAdapter(mProductionInquireViewPagerAdapter);
    }

    @Override
    public void initListeners() {
    }

    @Override
    public void initData() {
        mProductionInquireExecutionFragment = ProductionInquireExecutionFragment.newInstance("执行");
        mProductionInquireMaterFragment = ProductionInquireMaterFragment.newInstance("材料");
        mProductionInquireStepFragment = ProductionInquireStepFragment.newInstance("工序");
        mProductionInquireViewPagerAdapter = new ProductionInquireViewPagerAdapter(getSupportFragmentManager(),
                new Fragment[]{mProductionInquireExecutionFragment, mProductionInquireMaterFragment, mProductionInquireStepFragment});
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
