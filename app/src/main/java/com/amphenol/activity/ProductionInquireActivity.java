package com.amphenol.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amphenol.adapter.ProductionInquireViewPagerAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Requisition;
import com.amphenol.entity.WorkOrder;
import com.amphenol.fragment.ProductionInquireExecutionFragment;
import com.amphenol.fragment.ProductionInquireMaterFragment;
import com.amphenol.fragment.ProductionInquireStepFragment;

/**
 * 生产订单查询
 */
public class ProductionInquireActivity extends BaseActivity {
    private static final int REQUEST_CODE_FOR_SCAN = 0x10;
    private ProductionInquireExecutionFragment mProductionInquireExecutionFragment;
    private ProductionInquireMaterFragment mProductionInquireMaterFragment;
    private ProductionInquireStepFragment mProductionInquireStepFragment;

    private ProductionInquireViewPagerAdapter mProductionInquireViewPagerAdapter;

    private ViewPager mViewPager;
    private EditText mEditText;
    private Button mInquireButton;
    private ImageView mScanImageView;

    private WorkOrder mWorkOrder = new WorkOrder();

    private View.OnClickListener mOnClickListener;
    private TextView.OnEditorActionListener mOnEditorActionListener;

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
        mEditText = (EditText) findViewById(R.id.purchase_receipt_main_code_et);
        mInquireButton = (Button) findViewById(R.id.fragment_purchase_receipt_inquire_bt);
        mScanImageView = (ImageView) findViewById(R.id.fragment_purchase_receipt_scan_iv);

        mScanImageView.setOnClickListener(mOnClickListener);
        mInquireButton.setOnClickListener(mOnClickListener);
        mEditText.setOnEditorActionListener(mOnEditorActionListener);
    }

    @Override
    public void initListeners() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fragment_purchase_receipt_scan_iv:
                        startActivityForResult(new Intent(ProductionInquireActivity.this, ScanActivity.class), REQUEST_CODE_FOR_SCAN);
                        break;
                    case R.id.fragment_purchase_receipt_inquire_bt:
                        boolean state = mInquireButton.getTag() == null ? false : (boolean) mInquireButton.getTag();
                        if (state) {//当前按钮状态为“清除”
                            mWorkOrder = new WorkOrder();
                        } else {
                        }
                        break;
                }
            }
        };
        mOnEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        };
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
