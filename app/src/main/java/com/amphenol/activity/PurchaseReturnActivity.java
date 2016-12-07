package com.amphenol.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Mater;
import com.amphenol.entity.Purchase;
import com.amphenol.entity.Returns;
import com.amphenol.fragment.PurchaseReceiptSecondBranchedFragment;
import com.amphenol.fragment.PurchaseReturnMainFragment;
import com.amphenol.fragment.PurchaseReturnSecondFragment;

import java.util.ArrayList;

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
            public void gotoSecondFragment(Returns.ReturnsItem returnsItem) {
                mPurchaseReturnSecondFragment = PurchaseReturnSecondFragment.newInstance(mSecondFragmentCallBack, returnsItem, mSecondFragmentCallBack);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.activity_purchase_receipt_fl, mPurchaseReturnSecondFragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }
        };
        mSecondFragmentCallBack = new PurchaseReturnSecondFragment.SecondFragmentCallBack() {
            @Override
            public void itemBeenClosed(String sequence) {
                getSupportFragmentManager().popBackStack();//当前fragment退栈
                if (mPurchaseReturnMainFragment != null) {
                    mPurchaseReturnMainFragment.refreshShow(sequence);
                }
            }

            @Override
            public void itemBeenSured(String sequence) {
                getSupportFragmentManager().popBackStack();//当前fragment退栈
                if (mPurchaseReturnMainFragment != null) {
                    mPurchaseReturnMainFragment.refreshShow(sequence);
                }
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
