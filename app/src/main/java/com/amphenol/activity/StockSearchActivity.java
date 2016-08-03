package com.amphenol.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Mater;
import com.amphenol.fragment.StockSearchMainFragment;
import com.amphenol.fragment.StockSearchSecondFragment;

public class StockSearchActivity extends BaseActivity {
    private StockSearchMainFragment mStockSearchMainFragment;
    private StockSearchSecondFragment mStockSearchSecondFragment;
    private StockSearchMainFragment.MainFragmentCallBack mainFragmentCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStockSearchMainFragment = StockSearchMainFragment.newInstance(mainFragmentCallBack);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_stock_search_fl, mStockSearchMainFragment).commitAllowingStateLoss();
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
        mainFragmentCallBack = new StockSearchMainFragment.MainFragmentCallBack() {
            @Override
            public void gotoSecondFragment(Mater.Branch branch) {
                mStockSearchSecondFragment = StockSearchSecondFragment.newInstance(branch);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.activity_stock_search_fl, mStockSearchSecondFragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }
        };
    }

    @Override
    public void initData() {

    }
}
