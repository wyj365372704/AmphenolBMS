package com.amphenol.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Pick;
import com.amphenol.fragment.HairMaterMainFragment;
import com.amphenol.fragment.HairMaterSecondFragment;
import com.amphenol.fragment.HairMaterSecondReturnBranchedFragment;
import com.amphenol.fragment.HairMaterSecondReturnNoBranchedFragment;

import java.util.ArrayList;

/**
 * 生产发料
 */
public class HairMaterActivity extends BaseActivity {
    private HairMaterMainFragment mHairMaterMainFragment;
    private HairMaterSecondFragment mHairMaterSecondFragment;
    private HairMaterSecondReturnBranchedFragment mHairMaterSecondReturnBranchedFragment;
    private HairMaterSecondReturnNoBranchedFragment mHairMaterSecondReturnNoBranchedFragment;
    private HairMaterMainFragment.MainFragmentCallBack mainFragmentCallBack;
    private HairMaterSecondFragment.SecondFragmentCallBack secondFragmentCallBack;
    private HairMaterSecondReturnBranchedFragment.SecondFragmentCallBack secondReturnBranchedFragmentCallBack;
    private HairMaterSecondReturnNoBranchedFragment.SecondFragmentCallBack secondReturnNoBranchedFragmentCallBack ;

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
                int type = pickItem.getPick().getType();

                if (type == Pick.TYPE_RETURN) {
                    int branched = pickItem.getBranched();
                    if(branched == Pick.PickItem.BRANCHED_YES){
                        mHairMaterSecondReturnBranchedFragment = HairMaterSecondReturnBranchedFragment.newInstance(pickItem, shards, secondReturnBranchedFragmentCallBack);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.activity_hair_mater_fl, mHairMaterSecondReturnBranchedFragment);
                        transaction.addToBackStack(null);
                        transaction.commitAllowingStateLoss();
                    }else{
                        mHairMaterSecondReturnNoBranchedFragment = HairMaterSecondReturnNoBranchedFragment.newInstance(pickItem, shards, secondReturnNoBranchedFragmentCallBack);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.activity_hair_mater_fl, mHairMaterSecondReturnNoBranchedFragment);
                        transaction.addToBackStack(null);
                        transaction.commitAllowingStateLoss();
                    }
                } else {
                    mHairMaterSecondFragment = HairMaterSecondFragment.newInstance(pickItem, shards, secondFragmentCallBack);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.activity_hair_mater_fl, mHairMaterSecondFragment);
                    transaction.addToBackStack(null);
                    transaction.commitAllowingStateLoss();
                }
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
        secondReturnBranchedFragmentCallBack = new HairMaterSecondReturnBranchedFragment.SecondFragmentCallBack() {
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
        secondReturnNoBranchedFragmentCallBack = new HairMaterSecondReturnNoBranchedFragment.SecondFragmentCallBack() {
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
}
