package com.amphenol.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Requisition;
import com.amphenol.fragment.CreateRequisitionSecondFragment;
import com.amphenol.fragment.FastRequisitionMainFragment;
import com.amphenol.fragment.FastRequisitionSecondFragment;

public class FastRequisitionActivity extends BaseActivity {
    private FastRequisitionMainFragment mFastRequisitionMainFragment;
    private FastRequisitionSecondFragment mFastRequisitionSecondFragment;
    private FastRequisitionMainFragment.MainFragmentCallBack mainFragmentCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFastRequisitionMainFragment = new FastRequisitionMainFragment(mainFragmentCallBack);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_fast_requisition_fl, mFastRequisitionMainFragment).commitAllowingStateLoss();

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
        mainFragmentCallBack = new FastRequisitionMainFragment.MainFragmentCallBack() {
            @Override
            public void gotoSecondFragment(Requisition.RequisitionItem requisitionItem) {
                mFastRequisitionSecondFragment = new FastRequisitionSecondFragment(requisitionItem);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.activity_fast_requisition_fl, mFastRequisitionSecondFragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }
        };
    }

    @Override
    public void initData() {

    }
}
