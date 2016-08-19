package com.amphenol.activity;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Requisition;
import com.amphenol.fragment.CreateRequisitionMainFragment;
import com.amphenol.fragment.CreateRequisitionSecondFragment;

/**
 * 创建调拨单
 */
public class CreateRequisitionActivity extends BaseActivity {
    private CreateRequisitionMainFragment mCreateRequisitionMainFragment;
    private CreateRequisitionSecondFragment mCreateRequisitionSecondFragment;
    private CreateRequisitionMainFragment.MainFragmentCallBack mainFragmentCallBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCreateRequisitionMainFragment =CreateRequisitionMainFragment.newInstance(mainFragmentCallBack);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_create_requisition_fl, mCreateRequisitionMainFragment).commitAllowingStateLoss();
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
        mainFragmentCallBack = new CreateRequisitionMainFragment.MainFragmentCallBack() {
            @Override
            public void gotoSecondFragment(Requisition.RequisitionItem requisitionItem) {
                mCreateRequisitionSecondFragment = CreateRequisitionSecondFragment.newInstance(requisitionItem);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.activity_create_requisition_fl, mCreateRequisitionSecondFragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }
        };
    }

    @Override
    public void initData() {

    }
}
