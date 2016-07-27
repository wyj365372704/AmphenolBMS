package com.amphenol.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amphenol.amphenol.R;
import com.amphenol.fragment.CreateRequisitionMainFragment;
import com.amphenol.fragment.PurchaseReceiptMainFragment;

/**
 * 创建调拨单
 */
public class CreateRequisitionActivity extends BaseActivity {
    private CreateRequisitionMainFragment createRequisitionMainFragment ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createRequisitionMainFragment = new CreateRequisitionMainFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_create_requisition_fl, createRequisitionMainFragment).commit();
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_create_requisition);
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
