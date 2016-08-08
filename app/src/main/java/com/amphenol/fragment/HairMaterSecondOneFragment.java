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
    private static final int REQUEST_CODE_INQUIRE = 0X10;
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
    private RecyclerView mRecyclerView;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private MyHandler myHandler = new MyHandler();


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
        refreshShow();
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
        mInquireButton.setOnClickListener(mOnClickListener);
        mAddButton.setOnClickListener(mOnClickListener);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_purchase_receipt_content_rl);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(hairMaterSecondOneAdapter);
        mSpinner.setAdapter(mStringArrayAdapter);
    }

    private void refreshShow() {
        materNumberTextView.setText(mPickItem.getBranch().getMater().getNumber());
        materDescTextView.setText(mPickItem.getBranch().getMater().getDesc());
        mPlanQuantityTextView.setText(mPickItem.getQuantity() + "");
        mUnitTextView.setText(mPickItem.getBranch().getMater().getUnit());
        mWarehouseTextView.setText(mPickItem.getBranch().getMater().getWarehouse());
        mLocationEditText.setText(mPickItem.getBranch().getMater().getLocation());
        mBranchEditText.setText(mPickItem.getBranch().getPo());

        hairMaterSecondOneAdapter.setDate(mPickItem.getPickItemBranchItems());
        hairMaterSecondOneAdapter.notifyDataSetChanged();

        if (mPickItem.getPickItemBranchItems().size() > 0) {
            mInquireButton.setTag(false);
            mInquireButton.setText("查询");
            mLocationEditText.getText().clear();
            mBranchEditText.getText().clear();
        } else {
            mInquireButton.setText("清除");
            mInquireButton.setTag(true);
        }
    }

    private void initListeners() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fragment_fast_requisition_main_inquire_bt:
                        boolean state = mInquireButton.getTag() == null ? false : (boolean) mInquireButton.getTag();
                        if (state) {//当前按钮状态为“清除”
                            mPickItem = new Pick.PickItem();
                            refreshShow();
                        } else {
                            handleInquireMater(mPickItem.getBranch().getMater().getWarehouse(),mPickItem.getBranch().getMater().getNumber(),mPickItem.getBranch().getMater().getShard(),mPickItem.getBranch().getMater().getLocation(),mPickItem.getBranch().getPo(),mPickItem.getQuantity(),mPickItem.getPick().getDepartment(),mPickItem.getPick().getWorkOrder(),mPickItem.getSequence());
                        }
                        break;
                    case R.id.fragment_fast_requisition_main_submit_bt:
                        break;

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

        mRequestTaskListener = new NetWorkAccessTools.RequestTaskListener() {
            @Override
            public void onRequestStart(int requestCode) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                    mLoadingDialog = null;
                }
                mLoadingDialog = new LoadingDialog(getActivity());
                mLoadingDialog.show();
            }

            @Override
            public void onRequestLoading(int requestCode, long current, long count) {

            }

            @Override
            public void onRequestSuccess(JSONObject jsonObject, int requestCode) {
                try {
                    switch (requestCode) {
                        case REQUEST_CODE_INQUIRE:
                            DecodeManager.decodeHairMaterGetMaterList(jsonObject, requestCode, myHandler);
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    ((BaseActivity) getActivity()).ShowToast("服务器返回错误");
                } finally {
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                        mLoadingDialog = null;
                    }
                }
            }

            @Override
            public void onRequestFail(int requestCode, int errorNo) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                    mLoadingDialog = null;
                }
                if (errorNo == 0) {
                    ((BaseActivity) getActivity()).ShowToast("与服务器连接失败");
                } else {

                    ((BaseActivity) getActivity()).ShowToast("服务器返回错误");
                }
            }
        };
    }

    private void handleInquireMater(String warehouse,String mate, String shard,String location,String branch,double quantity,String department,String workOrder,String sequence) {
        if (!HairMaterSecondOneFragment.this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("warehouse",warehouse);
        param.put("mater", mate);
        param.put("shard",shard);
        param.put("location",location);
        param.put("branch",branch);

        param.put("quantity",quantity+"");//计划数量
        param.put("department",department);
        param.put("workOrder",workOrder);
        param.put("sequence",sequence);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_HAIR_MATER_GET_MATER_LIST, getContext()), param, REQUEST_CODE_INQUIRE, mRequestTaskListener);
    }

    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}