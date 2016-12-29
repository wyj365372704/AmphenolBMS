package com.amphenol.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Mater;
import com.amphenol.entity.Purchase;
import com.amphenol.fragment.PurchaseStorageMainFragment;
import com.amphenol.fragment.PurchaseStorageSecondBranchedFragment;
import com.amphenol.fragment.PurchaseStorageSecondNoBranchedFragment;

/**
 * 采购收货
 */
public class PurchaseStorageActivity extends BaseActivity {
    private PurchaseStorageMainFragment mPurchaseStorageMainFragment;
    private PurchaseStorageSecondBranchedFragment mPurchaseStorageSecondBranchedFragment;
    private PurchaseStorageSecondBranchedFragment.SecondFragmentCallBack mSecondBranchedFragmentCallBack;
    private PurchaseStorageSecondNoBranchedFragment mPurchaseStorageSecondNoBranchedFragment;
    private PurchaseStorageSecondNoBranchedFragment.SecondFragmentCallBack mSecondNoBranchedFragmentCallBack;
    private PurchaseStorageMainFragment.MainFragmentCallBack mainFragmentCallBack;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_purchase_receipt);
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListeners() {
        mainFragmentCallBack = new PurchaseStorageMainFragment.MainFragmentCallBack() {
            @Override
            public void gotoSecondFragment(Purchase.PurchaseItem purchaseItem) {

                if(purchaseItem.getMater().getBranchControl() == Mater.BRANCH_CONTROL){
                    mPurchaseStorageSecondBranchedFragment = PurchaseStorageSecondBranchedFragment.newInstance(mSecondBranchedFragmentCallBack, purchaseItem);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.activity_purchase_receipt_fl, mPurchaseStorageSecondBranchedFragment);
                    transaction.addToBackStack(null);
                    transaction.commitAllowingStateLoss();
                }else{
                    mPurchaseStorageSecondNoBranchedFragment = PurchaseStorageSecondNoBranchedFragment.newInstance(mSecondNoBranchedFragmentCallBack, purchaseItem);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.activity_purchase_receipt_fl, mPurchaseStorageSecondNoBranchedFragment);
                    transaction.addToBackStack(null);
                    transaction.commitAllowingStateLoss();
                }
            }
        };
        mSecondBranchedFragmentCallBack = new PurchaseStorageSecondBranchedFragment.SecondFragmentCallBack() {
            @Override
            public void itemBeenClosed(String shdhh) {
                getSupportFragmentManager().popBackStack();//当前fragment退栈
                if(mPurchaseStorageMainFragment!=null){
                    mPurchaseStorageMainFragment.refreshShow(shdhh);
                }
            }

            @Override
            public void itemBeenSured(String shdhh) {
                getSupportFragmentManager().popBackStack();//栈顶出栈
                if(mPurchaseStorageMainFragment!=null){
                    mPurchaseStorageMainFragment.refreshShow(shdhh);
                }
            }
        };
        mSecondNoBranchedFragmentCallBack = new PurchaseStorageSecondNoBranchedFragment.SecondFragmentCallBack() {
            @Override
            public void itemBeenClosed(String shdhh) {
                getSupportFragmentManager().popBackStack();//当前fragment退栈
                if(mPurchaseStorageMainFragment!=null){
                    mPurchaseStorageMainFragment.refreshShow(shdhh);
                }
            }

            @Override
            public void itemBeenSured(String shdhh) {
                getSupportFragmentManager().popBackStack();//当前fragment退栈
                if(mPurchaseStorageMainFragment!=null){
                    mPurchaseStorageMainFragment.refreshShow(shdhh);
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
        mPurchaseStorageMainFragment = PurchaseStorageMainFragment.newInstance(mainFragmentCallBack);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_purchase_receipt_fl, mPurchaseStorageMainFragment).commitAllowingStateLoss();
    }
}
