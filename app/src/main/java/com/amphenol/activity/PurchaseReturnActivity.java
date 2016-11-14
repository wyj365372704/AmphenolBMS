package com.amphenol.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Purchase;
import com.amphenol.fragment.PurchaseReceiptSecondBranchedFragment;
import com.amphenol.fragment.PurchaseReturnMainFragment;
import com.amphenol.fragment.PurchaseReturnSecondFragment;

/**
 * 采购退货
 */
public class PurchaseReturnActivity extends BaseActivity {
    private PurchaseReturnMainFragment mPurchaseReturnMainFragment;
    private PurchaseReturnMainFragment.MainFragmentCallBack mainFragmentCallBack;
    private PurchaseReturnSecondFragment mPurchaseReturnSecondFragment;
    private PurchaseReturnSecondFragment.SecondFragmentCallBack mSecondFragmentCallBack;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_purchase_return);
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListeners() {
        mainFragmentCallBack = new PurchaseReturnMainFragment.MainFragmentCallBack() {
            @Override
            public void gotoSecondFragment(Purchase.PurchaseItem purchaseItem) {

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.activity_purchase_receipt_fl, mPurchaseReturnSecondFragment);
                    transaction.addToBackStack(null);
                    transaction.commitAllowingStateLoss();
            }
        };
    }

    @Override
    public void initData() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPurchaseReturnMainFragment = PurchaseReturnMainFragment.newInstance(mainFragmentCallBack);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_purchase_receipt_fl, mPurchaseReturnMainFragment).commitAllowingStateLoss();
    }
}
