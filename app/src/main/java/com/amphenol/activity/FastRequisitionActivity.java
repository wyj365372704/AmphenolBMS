package com.amphenol.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amphenol.amphenol.R;
import com.amphenol.fragment.FastRequisitionMainFragment;

public class FastRequisitionActivity extends BaseActivity {
    FastRequisitionMainFragment mFastRequisitionMainFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFastRequisitionMainFragment = new FastRequisitionMainFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_fast_requisition_fl, mFastRequisitionMainFragment).commit();

    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_fast_requisition);
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
