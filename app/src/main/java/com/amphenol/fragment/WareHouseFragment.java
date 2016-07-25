package com.amphenol.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.MenuActivity;
import com.amphenol.amphenol.R;

import java.util.HashMap;


public class WareHouseFragment extends Fragment {
    private Spinner mWareHouseSpinner;
    private ArrayAdapter<String> mWareHouseStringArrayAdapter;
    private TextView mWareHouseTextView;
    private View rootView;
    private MyHandler myHandler;
    public WareHouseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        initView();
        return inflater.inflate(R.layout.fragment_ware_house, container, false);
    }

    private void initView() {
        mWareHouseSpinner = (Spinner) rootView.findViewById(R.id.fragment_set_up_warehouse_spinner);
        mWareHouseTextView = (TextView) rootView.findViewById(R.id.activity_set_up_warehouse_tv);
    }
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
}
