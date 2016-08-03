package com.amphenol.activity;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Purchase;
import com.amphenol.fragment.PurchaseReceiptMainFragment;
import com.amphenol.fragment.PurchaseReceiptSecondFragment;

/**
 * 采购收货
 */
public class PurchaseReceiptActivity extends BaseActivity {
    private PurchaseReceiptMainFragment mPurchaseReceiptMainFragment;
    private PurchaseReceiptSecondFragment mPurchaseReceiptSecondFragment;
    private PurchaseReceiptSecondFragment.SecondFragmentCallBack secondFragmentCallBack;
    private PurchaseReceiptMainFragment.MainFragmentCallBack mainFragmentCallBack;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_purchase_receipt);
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListeners() {
        mainFragmentCallBack = new PurchaseReceiptMainFragment.MainFragmentCallBack() {
            @Override
            public void gotoSecondFragment(Purchase.PurchaseItem purchaseItem) {
                mPurchaseReceiptSecondFragment = PurchaseReceiptSecondFragment.newInstance(secondFragmentCallBack, purchaseItem);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.activity_purchase_receipt_fl, mPurchaseReceiptSecondFragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }
        };
        secondFragmentCallBack = new PurchaseReceiptSecondFragment.SecondFragmentCallBack() {
            @Override
            public void itemBeenClosed(String shdhh) {
                getSupportFragmentManager().popBackStack();//当前fragment退栈
                if(mPurchaseReceiptMainFragment!=null){
                    mPurchaseReceiptMainFragment.refreshShow(shdhh);
                }
            }

            @Override
            public void itemBeenSured(String shdhh) {
                getSupportFragmentManager().popBackStack();//栈顶出栈
                if(mPurchaseReceiptMainFragment!=null){
                    mPurchaseReceiptMainFragment.refreshShow(shdhh);
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
        mPurchaseReceiptMainFragment = PurchaseReceiptMainFragment.newInstance(mainFragmentCallBack);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_purchase_receipt_fl, mPurchaseReceiptMainFragment).commitAllowingStateLoss();
    }
}
