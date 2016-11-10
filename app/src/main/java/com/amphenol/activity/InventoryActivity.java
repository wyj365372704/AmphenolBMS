package com.amphenol.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Mater;
import com.amphenol.fragment.InventoryAddFragment;
import com.amphenol.fragment.InventoryMainFragment;
import com.amphenol.fragment.InventorySecondFragment;

public class InventoryActivity extends BaseActivity {
    private InventoryMainFragment mInventoryMainFragment;
    private InventorySecondFragment mInventorySecondFragment;
    private InventoryAddFragment mInventoryAddFragment;
    private InventoryMainFragment.MainFragmentCallBack mainFragmentCallBack;
    private InventorySecondFragment.SecondFragmentCallBack secondFragmentCallBack;
    private InventoryAddFragment.AddFragmentCallBack addFragmentCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInventoryMainFragment = InventoryMainFragment.newInstance(mainFragmentCallBack);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_stock_search_fl, mInventoryMainFragment).commitAllowingStateLoss();
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_stock_search);
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListeners() {
        mainFragmentCallBack = new InventoryMainFragment.MainFragmentCallBack() {
            @Override
            public void gotoSecondFragment(Mater.Branch branch) {
                mInventorySecondFragment = InventorySecondFragment.newInstance(branch,secondFragmentCallBack);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.activity_stock_search_fl, mInventorySecondFragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }

            @Override
            public void gotoAddFragment() {
                mInventoryAddFragment = InventoryAddFragment.newInstance(addFragmentCallBack);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.activity_stock_search_fl, mInventoryAddFragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }
        };
        secondFragmentCallBack = new InventorySecondFragment.SecondFragmentCallBack() {
            @Override
            public void itemBeenSured() {
                getSupportFragmentManager().popBackStack();//当前fragment退栈
                if (mInventoryMainFragment != null) {
                    mInventoryMainFragment.cleanView();
                }
            }
        };
        addFragmentCallBack = new InventoryAddFragment.AddFragmentCallBack() {
            @Override
            public void itemBeenSured() {

            }
        };
    }

    @Override
    public void initData() {

    }
}
