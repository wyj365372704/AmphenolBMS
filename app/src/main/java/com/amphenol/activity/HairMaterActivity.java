package com.amphenol.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Pick;
import com.amphenol.fragment.HairMaterMainFragment;
import com.amphenol.fragment.HairMaterSecondFragment;

import java.util.ArrayList;

/**
 * 生产发料
 */
public class HairMaterActivity extends BaseActivity {
    private HairMaterMainFragment mHairMaterMainFragment;
    private HairMaterSecondFragment mHairMaterSecondFragment;
    private HairMaterMainFragment.MainFragmentCallBack mainFragmentCallBack;
    private HairMaterSecondFragment.SecondFragmentCallBack secondFragmentCallBack;

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
        mainFragmentCallBack = new HairMaterMainFragment.MainFragmentCallBack() {
            @Override
            public void gotoSecondFragment(Pick.PickItem pickItem, ArrayList<String> shards) {
                mHairMaterSecondFragment = HairMaterSecondFragment.newInstance(pickItem, shards, secondFragmentCallBack);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.activity_hair_mater_fl, mHairMaterSecondFragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }
        };
        secondFragmentCallBack = new HairMaterSecondFragment.SecondFragmentCallBack() {
            @Override
            public void itemBeenClosed(String sequence) {
                getSupportFragmentManager().popBackStack();//当前fragment退栈
                if (mHairMaterMainFragment != null) {
                    mHairMaterMainFragment.refreshShow(sequence);
                }
            }

            @Override
            public void itemBeenSured(String sequence) {
                getSupportFragmentManager().popBackStack();//当前fragment退栈
                if (mHairMaterMainFragment != null) {
                    mHairMaterMainFragment.refreshShow(sequence);
                }
            }
        };
    }

    @Override
    public void initData() {

    }

    public void abcd(View view) {
        if (mainFragmentCallBack != null) {
            mainFragmentCallBack.gotoSecondFragment(new Pick.PickItem(), new ArrayList<String>());
        }
    }
}
