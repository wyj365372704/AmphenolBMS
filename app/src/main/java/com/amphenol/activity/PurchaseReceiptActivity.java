package com.amphenol.activity;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Mater;
import com.amphenol.entity.Purchase;
import com.amphenol.fragment.PurchaseReceiptMainFragment;
import com.amphenol.fragment.PurchaseReceiptSecondBranchedFragment;
import com.amphenol.fragment.PurchaseReceiptSecondNoBranchedFragment;

/**
 * 采购收货
 */
public class PurchaseReceiptActivity extends BaseActivity {
    private PurchaseReceiptMainFragment mPurchaseReceiptMainFragment;
    private PurchaseReceiptSecondBranchedFragment mPurchaseReceiptSecondBranchedFragment;
    private PurchaseReceiptSecondBranchedFragment.SecondFragmentCallBack mSecondBranchedFragmentCallBack;
    private PurchaseReceiptSecondNoBranchedFragment mPurchaseReceiptSecondNoBranchedFragment;
    private PurchaseReceiptSecondNoBranchedFragment.SecondFragmentCallBack mSecondNoBranchedFragmentCallBack;
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

                if(purchaseItem.getMater().getBranchControl() == Mater.BRANCH_CONTROL){
                    mPurchaseReceiptSecondBranchedFragment = PurchaseReceiptSecondBranchedFragment.newInstance(mSecondBranchedFragmentCallBack, purchaseItem);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.activity_purchase_receipt_fl, mPurchaseReceiptSecondBranchedFragment);
                    transaction.addToBackStack(null);
                    transaction.commitAllowingStateLoss();
                }else{
                    mPurchaseReceiptSecondNoBranchedFragment = PurchaseReceiptSecondNoBranchedFragment.newInstance(mSecondNoBranchedFragmentCallBack, purchaseItem);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.activity_purchase_receipt_fl, mPurchaseReceiptSecondNoBranchedFragment);
                    transaction.addToBackStack(null);
                    transaction.commitAllowingStateLoss();
                }
            }
        };
        mSecondBranchedFragmentCallBack = new PurchaseReceiptSecondBranchedFragment.SecondFragmentCallBack() {
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
        mSecondNoBranchedFragmentCallBack = new PurchaseReceiptSecondNoBranchedFragment.SecondFragmentCallBack() {
            @Override
            public void itemBeenClosed(String shdhh) {
                getSupportFragmentManager().popBackStack();//当前fragment退栈
                if(mPurchaseReceiptMainFragment!=null){
                    mPurchaseReceiptMainFragment.refreshShow(shdhh);
                }
            }

            @Override
            public void itemBeenSured(String shdhh) {
                getSupportFragmentManager().popBackStack();//当前fragment退栈
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
