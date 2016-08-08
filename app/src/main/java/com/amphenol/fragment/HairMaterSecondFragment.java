package com.amphenol.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Pick;
import com.amphenol.entity.Requisition;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class HairMaterSecondFragment extends Fragment {
    private View rootView;
    private ViewPager mViewPager;
    private TextView mActionBarTitleTextView;
    private HairMaterSecondOneFragment hairMaterSecondOneFragment;
    private HairMaterSecondTwoFragment hairMaterSecondTwoFragment;
    private PagerAdapter mPagerAdapter;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private View.OnClickListener mOnClickListener;
    private Pick.PickItem mPickItem = new Pick.PickItem();
    private ArrayList<String> shards = new ArrayList<>();
    private HairMaterSecondTwoFragment.SecondFragmentCallBack secondFragmentCallBack;
    private SecondFragmentCallBack selfSecondFragmentCallBack;

    public static HairMaterSecondFragment newInstance(Pick.PickItem pickItem, ArrayList<String> shards,SecondFragmentCallBack secondFragmentCallBack) {
        Bundle args = new Bundle();
        args.putSerializable("pickItem", pickItem);
        args.putStringArrayList("shards", shards);
        HairMaterSecondFragment fragment = new HairMaterSecondFragment();
        fragment.selfSecondFragmentCallBack = secondFragmentCallBack;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mPickItem = (Pick.PickItem) args.getSerializable("pickItem");
            shards = args.getStringArrayList("shards");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_hair_mater_second, container, false);
        initListeners();
        initData();
        initViews();
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        return rootView;
    }

    private void initViews() {
        mActionBarTitleTextView = (TextView) rootView.findViewById(R.id.toolbar_title);
        mViewPager = (ViewPager) rootView.findViewById(R.id.fragment_hair_mater_second_vp);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        changeActionBarState(0);
    }

    private void initData() {
        hairMaterSecondOneFragment = HairMaterSecondOneFragment.newInstance(mPickItem, shards);
        hairMaterSecondTwoFragment = HairMaterSecondTwoFragment.newInstance(secondFragmentCallBack,mPickItem);
        mPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == 0)
                    return hairMaterSecondOneFragment;
                if (position == 1)
                    return hairMaterSecondTwoFragment;
                return null;
            }

            @Override
            public int getCount() {
                return 2;
            }
        };

    }

    private void changeActionBarState(int position) {
        switch (position) {
            case 0:
                mActionBarTitleTextView.setText("发料明细");
                break;
            case 1:
                mActionBarTitleTextView.setText("待发列表");
                break;
        }
    }

    private void initListeners() {
        mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeActionBarState(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                }
            }
        };
        secondFragmentCallBack = new HairMaterSecondTwoFragment.SecondFragmentCallBack() {
            @Override
            public void itemBeenClosed(String sequence) {
                if(selfSecondFragmentCallBack!=null){
                    selfSecondFragmentCallBack.itemBeenClosed(sequence);
                }
            }

            @Override
            public void itemBeenSured(String sequence) {
                if(selfSecondFragmentCallBack!=null){
                    selfSecondFragmentCallBack.itemBeenSured(sequence);
                }
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStack();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public interface SecondFragmentCallBack extends Serializable {
        /**
         * @param sequence 系统顺序号
         */
        void itemBeenClosed(String sequence);

        /**
         * @param sequence 系统顺序号
         */

        void itemBeenSured(String sequence);
    }
}