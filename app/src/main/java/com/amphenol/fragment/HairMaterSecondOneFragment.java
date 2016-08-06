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
import com.amphenol.adapter.HairMaterSecondOneAdapter;
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
public class HairMaterSecondOneFragment extends Fragment {
    private View rootView;
    private TextView materNumberTextView, materDescTextView, mPlanQuantityTextView, mUnitTextView, mHairQuantityTextView,
            mWarehouseTextView;
    private Spinner mSpinner;
    private EditText mLocationEditText, mBranchEditText;
    private Button mInquireButton, mAddButton;
    private View.OnClickListener mOnClickListener;
    private ArrayAdapter<String> mStringArrayAdapter;
    private ArrayList<String> mShardStrings = new ArrayList<>();
    private Pick.PickItem mPickItem = new Pick.PickItem();
    private HairMaterSecondOneAdapter hairMaterSecondOneAdapter;
    private HairMaterSecondOneAdapter.OnItemClickListener mOnItemClickListener;
    private RecyclerView mRecyclerView ;


    public static HairMaterSecondOneFragment newInstance(Pick.PickItem pickItem, ArrayList<String> shards) {

        Bundle args = new Bundle();
        args.putSerializable("pickItem", pickItem);
        args.putStringArrayList("shards", shards);
        HairMaterSecondOneFragment fragment = new HairMaterSecondOneFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mPickItem = (Pick.PickItem) args.getSerializable("pickItem");
            mShardStrings = args.getStringArrayList("shards");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_hair_mater_second_one, container, false);
        initListeners();
        initData();
        initViews();
        return rootView;
    }

    private void initData() {
        mStringArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, mShardStrings);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        mStringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hairMaterSecondOneAdapter = new HairMaterSecondOneAdapter(getContext(), mPickItem.getPickItemBranchItems(), mOnItemClickListener);
    }

    private void initViews() {
        materNumberTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_second_one_mater_in_tv);
        materDescTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_second_one_desc_in_tv);
        mPlanQuantityTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_second_one_plain_quantity_in_tv);
        mUnitTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_second_one_unit_in_tv);
        mHairQuantityTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_second_one_hair_quantity_in_tv);
        mWarehouseTextView = (TextView) rootView.findViewById(R.id.fragment_fast_requisition_main_warehouse_in_tv);
        mSpinner = (Spinner) rootView.findViewById(R.id.fragment_fast_requisition_main_mater_in_et);
        mLocationEditText = (EditText) rootView.findViewById(R.id.fragment_fast_requisition_main_from_location_et);
        mBranchEditText = (EditText) rootView.findViewById(R.id.fragment_fast_requisition_main_branch_et);
        mInquireButton = (Button) rootView.findViewById(R.id.fragment_fast_requisition_main_inquire_bt);
        mAddButton = (Button) rootView.findViewById(R.id.fragment_fast_requisition_main_submit_bt);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_purchase_receipt_content_rl);

        materNumberTextView.setText(mPickItem.getBranch().getMater().getNumber());
        materDescTextView.setText(mPickItem.getBranch().getMater().getDesc());
        mPlanQuantityTextView.setText(mPickItem.getQuantity() + "");
        mUnitTextView.setText(mPickItem.getBranch().getMater().getUnit());
        mWarehouseTextView.setText(mPickItem.getBranch().getMater().getWarehouse());
        mLocationEditText.setText(mPickItem.getBranch().getMater().getLocation());
        mBranchEditText.setText(mPickItem.getBranch().getPo());
        mSpinner.setAdapter(mStringArrayAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(hairMaterSecondOneAdapter);
    }

    private void initListeners() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                }
            }
        };

        mOnItemClickListener = new HairMaterSecondOneAdapter.OnItemClickListener() {
            @Override
            public void OnItemCheckedChanged(int position, boolean isChecked) {

            }

            @Override
            public void OnRequisitionQuantityChanged(int position, double quantity) {

            }
        };
    }
}