package com.amphenol.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Requisition;
import com.amphenol.fragment.CheckRequisitionMainFragment;
import com.amphenol.fragment.CheckRequisitionSecondFragment;
import com.amphenol.fragment.HairMaterMainFragment;

import java.util.ArrayList;

/**
 * 生产发料
 */
public class HairMaterActivity extends BaseActivity {
    private HairMaterMainFragment mHairMaterMainFragment;
    private CheckRequisitionSecondFragment mCheckRequisitionSecondFragment;
    private HairMaterMainFragment.MainFragmentCallBack mainFragmentCallBack;
    private CheckRequisitionSecondFragment.SecondFragmentCallBack mSecondFragmentCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            return;
        mHairMaterMainFragment = HairMaterMainFragment.newInstance(mainFragmentCallBack);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_hair_mater_fl, mHairMaterMainFragment).commit();
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_hair_mater);
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListeners() {

//        mainFragmentCallBack = new HairMaterMainFragment.MainFragmentCallBack() {
//            @Override
//            public void gotoSecondFragment(Requisition.RequisitionItem requisitionItem, ArrayList<String> shardStrings) {
//                mCheckRequisitionSecondFragment = CheckRequisitionSecondFragment.newInstance(requisitionItem, shardStrings, mSecondFragmentCallBack);
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.activity_hair_mater_fl, mCheckRequisitionSecondFragment);
//                transaction.addToBackStack(null);
//                transaction.commitAllowingStateLoss();
//            }
//        };

        mSecondFragmentCallBack = new CheckRequisitionSecondFragment.SecondFragmentCallBack() {
            @Override
            public void itemBeenClosed(String requisitionItemNumber) {
                getSupportFragmentManager().popBackStack();//当前fragment退栈
                if (mHairMaterMainFragment != null) {
                    mHairMaterMainFragment.refreshShow(requisitionItemNumber);
                }
            }

            @Override
            public void itemBeenSured(String requisitionItemNumber) {
                getSupportFragmentManager().popBackStack();//当前fragment退栈
                if (mHairMaterMainFragment != null) {
                    mHairMaterMainFragment.refreshShow(requisitionItemNumber);
                }
            }
        };

    }

    @Override
    public void initData() {

    }
}
