package com.amphenol.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.adapter.HairMaterSecondTwoAdapter;
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
 * 审核调拨单-物料明细
 */
public class HairMaterSecondTwoFragment extends Fragment {
    private View rootView;
    private TextView materNumberTextView, materDescTextView, mPlanQuantityTextView, mUnitTextView, mHairQuantityTextView;
    private Button mSubmitButton, mCancelButton;
    private View.OnClickListener mOnClickListener;
    private Pick.PickItem mPickItem = new Pick.PickItem();
    private RecyclerView mRecyclerView ;
    private HairMaterSecondTwoAdapter hairMaterSecondTwoAdapter ;
    private HairMaterSecondTwoAdapter.OnItemClickListener onItemClickListener ;


    public static HairMaterSecondTwoFragment newInstance( Pick.PickItem pickItem) {

        Bundle args = new Bundle();
        args.putSerializable("pickItem",pickItem);
        HairMaterSecondTwoFragment fragment = new HairMaterSecondTwoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mPickItem = (Pick.PickItem) args.get("pickItem");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_hair_mater_second_two, container, false);
        initListeners();
        initData();
        initViews();
        return rootView;
    }

    private void initData() {
        hairMaterSecondTwoAdapter = new HairMaterSecondTwoAdapter(getContext(),mPickItem.getPickItemBranchItems(),onItemClickListener);
    }

    private void initViews() {
        materNumberTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_second_one_mater_in_tv);
        materDescTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_second_one_desc_in_tv);
        mPlanQuantityTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_second_one_plain_quantity_in_tv);
        mUnitTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_second_one_unit_in_tv);
        mHairQuantityTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_second_one_hair_quantity_in_tv);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_purchase_receipt_content_rl);
        mSubmitButton = (Button) rootView.findViewById(R.id.fragment_fast_requisition_main_inquire_bt);
        mCancelButton = (Button) rootView.findViewById(R.id.fragment_fast_requisition_main_submit_bt);

        materNumberTextView.setText(mPickItem.getBranch().getMater().getNumber());
        materDescTextView.setText(mPickItem.getBranch().getMater().getDesc());
        mPlanQuantityTextView.setText(mPickItem.getQuantity() + "");
        mUnitTextView.setText(mPickItem.getBranch().getMater().getUnit());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(hairMaterSecondTwoAdapter);
    }

    private void initListeners() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                }
            }
        };
    }
}