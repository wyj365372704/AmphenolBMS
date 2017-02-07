package com.amphenol.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Pick;
import com.amphenol.entity.Shipment;
import com.amphenol.fragment.HairMaterMainFragment;
import com.amphenol.fragment.HairMaterSecondFragment;
import com.amphenol.fragment.HairMaterSecondReturnBranchedFragment;
import com.amphenol.fragment.HairMaterSecondReturnNoBranchedFragment;
import com.amphenol.fragment.SaleShipmentMainFragment;
import com.amphenol.fragment.SaleShipmentSecondFragment;

import java.util.ArrayList;

/**
 * 销售出货
 */
public class SaleShipmentActivity extends BaseActivity {
    private SaleShipmentMainFragment mSaleShipmentMainFragment;
    private SaleShipmentSecondFragment mSaleShipmentSecondFragment;
    private SaleShipmentMainFragment.MainFragmentCallBack mainFragmentCallBack;
    private SaleShipmentSecondFragment.SecondFragmentCallBack secondFragmentCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            return;
        mSaleShipmentMainFragment = SaleShipmentMainFragment.newInstance(mainFragmentCallBack);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_hair_mater_fl, mSaleShipmentMainFragment).commit();
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_sale_shipment);
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListeners() {
        mainFragmentCallBack = new SaleShipmentMainFragment.MainFragmentCallBack() {
            @Override
            public void gotoSecondFragment(Shipment.ShipmentItem shipmentItem, ArrayList<String> shards) {
                mSaleShipmentSecondFragment = SaleShipmentSecondFragment.newInstance(shipmentItem, shards, secondFragmentCallBack);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.activity_hair_mater_fl, mSaleShipmentSecondFragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }
        };
        secondFragmentCallBack = new SaleShipmentSecondFragment.SecondFragmentCallBack() {
            @Override
            public void itemBeenClosed(String sequence) {
                getSupportFragmentManager().popBackStack();//当前fragment退栈
                if (mSaleShipmentMainFragment != null) {
                    mSaleShipmentMainFragment.refreshShow(sequence);
                }
            }

            @Override
            public void itemBeenSured(String sequence) {
                getSupportFragmentManager().popBackStack();//当前fragment退栈
                if (mSaleShipmentMainFragment != null) {
                    mSaleShipmentMainFragment.refreshShow(sequence);
                }
            }
        };
    }

    @Override
    public void initData() {

    }
}
