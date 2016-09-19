package com.amphenol.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Carl on 2016-08-23 023.
 */
public class MyFragmentViewPagerAdapter extends FragmentStatePagerAdapter {
    private Fragment[] data;

    public MyFragmentViewPagerAdapter(FragmentManager fm, Fragment[] data) {
        super(fm);
        this.data = data;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.length;
    }

    @Override
    public Fragment getItem(int position) {
        return data[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return data[position].getArguments().getString("title");
    }
}
