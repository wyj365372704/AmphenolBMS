package com.amphenol.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Requisition;
import com.amphenol.fragment.CheckRequisitionMainFragment;
import com.amphenol.fragment.CheckRequisitionSecondFragment;

import java.util.ArrayList;

/**
 * 审核调拨单
 */
public class CheckRequisitionActivity extends BaseActivity {
    private CheckRequisitionMainFragment mCheckRequisitionMainFragment;
    private CheckRequisitionSecondFragment mCheckRequisitionSecondFragment ;
    private CheckRequisitionMainFragment.MainFragmentCallBack mainFragmentCallBack;
    private CheckRequisitionSecondFragment.SecondFragmentCallBack mSecondFragmentCallBack ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCheckRequisitionMainFragment = CheckRequisitionMainFragment.newInstance(mainFragmentCallBack);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_check_requisition_fl, mCheckRequisitionMainFragment).commitAllowingStateLoss();
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_check_requisition);
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListeners() {
        mainFragmentCallBack = new CheckRequisitionMainFragment.MainFragmentCallBack() {
            @Override
            public void gotoSecondFragment(Requisition.RequisitionItem requisitionItem , ArrayList<String> shardStrings) {
                mCheckRequisitionSecondFragment = CheckRequisitionSecondFragment.newInstance(requisitionItem,shardStrings,mSecondFragmentCallBack);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.activity_check_requisition_fl, mCheckRequisitionSecondFragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }
        };
        mSecondFragmentCallBack = new CheckRequisitionSecondFragment.SecondFragmentCallBack() {
            @Override
            public void itemBeenClosed(String requisitionItemNumber) {
                getSupportFragmentManager().popBackStack();//当前fragment退栈
                if(mCheckRequisitionMainFragment!=null){
                    mCheckRequisitionMainFragment.refreshShow(requisitionItemNumber);
                }
            }

            @Override
            public void itemBeenSured(String requisitionItemNumber) {
                getSupportFragmentManager().popBackStack();//当前fragment退栈
                if(mCheckRequisitionMainFragment!=null){
                    mCheckRequisitionMainFragment.refreshShow(requisitionItemNumber);
                }
            }
        };
    }

    @Override
    public void initData() {

    }
}
