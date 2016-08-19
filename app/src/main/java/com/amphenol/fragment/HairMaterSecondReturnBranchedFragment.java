package com.amphenol.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.activity.ScanActivity;
import com.amphenol.adapter.HairMaterSecondTwoAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Mater;
import com.amphenol.entity.Pick;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;
import com.baoyz.actionsheet.ActionSheet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class HairMaterSecondReturnBranchedFragment extends Fragment {
    private static final int REQUEST_CODE_SUBMIT = 0X11;
    private static final int REQUEST_CODE_CANCEL = 0x12;
    private static final int REQUEST_CODE_FOR_SCAN_LOCATION = 0X14;
    private View rootView;
    private TextView materNumberTextView, materDescTextView, mReturnQuantityTextView, mUnitTextView, mActualQuantityTextView, mBranchedTextView, mWarehouseTextView;
    private Spinner mSpinner;
    private EditText mLocationEditText;
    private Button mAddButton, mCancelButton, mAddBranchButton;
    private ImageView mImageView;
    private View.OnClickListener mOnClickListener;
    private ArrayAdapter<String> mStringArrayAdapter;
    private ArrayList<String> mShardStrings = new ArrayList<>();
    private Pick.PickItem mPickItem = new Pick.PickItem();
    private HairMaterSecondTwoAdapter hairMaterSecondTwoAdapter;
    private HairMaterSecondTwoAdapter.OnItemClickListener mOnItemClickListener;
    private RecyclerView mRecyclerView;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private SecondFragmentCallBack mSecondFragmentCallBack;
    private MyHandler myHandler = new MyHandler();
    private ActionSheet.ActionSheetListener mActionSheetListener;


    public static HairMaterSecondReturnBranchedFragment newInstance(Pick.PickItem pickItem, ArrayList<String> shards, SecondFragmentCallBack mSecondFragmentCallBack) {

        Bundle args = new Bundle();
        args.putParcelable("pickItem", pickItem);
        args.putStringArrayList("shards", shards);
        HairMaterSecondReturnBranchedFragment fragment = new HairMaterSecondReturnBranchedFragment();
        fragment.mSecondFragmentCallBack = mSecondFragmentCallBack;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mPickItem = args.getParcelable("pickItem");
            mPickItem.getPickItemBranchItems().clear();
            mShardStrings = args.getStringArrayList("shards");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_hair_mater_second_return_branched, container, false);
        initListeners();
        initData();
        initViews();
        refreshShow();
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        mLocationEditText.requestFocus();
        return rootView;
    }

    private void initData() {
        mStringArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, mShardStrings);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        mStringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hairMaterSecondTwoAdapter = new HairMaterSecondTwoAdapter(getContext(), mPickItem.getPickItemBranchItems(), mOnItemClickListener);
    }

    private void initViews() {
        mAddBranchButton = (Button) rootView.findViewById(R.id.fragment_fast_requisition_main_add_branch_bt);
        mAddBranchButton.setOnClickListener(mOnClickListener);
        mBranchedTextView = (TextView) rootView.findViewById(R.id.fragment_fast_requisition_main_branched_in_tv);
        mImageView = (ImageView) rootView.findViewById(R.id.toolbar_menu);
        mImageView.setOnClickListener(mOnClickListener);
        materNumberTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_second_one_mater_in_tv);
        materDescTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_second_one_desc_in_tv);
        mReturnQuantityTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_second_one_plain_quantity_in_tv);
        mUnitTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_second_one_unit_in_tv);
        mWarehouseTextView = (TextView) rootView.findViewById(R.id.fragment_fast_requisition_main_warehouse_in_tv);
        mSpinner = (Spinner) rootView.findViewById(R.id.fragment_fast_requisition_main_mater_in_et);
        mActualQuantityTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_second_return_acqual_quantity_et);
        mLocationEditText = (EditText) rootView.findViewById(R.id.fragment_fast_requisition_main_from_location_et);
        mAddButton = (Button) rootView.findViewById(R.id.fragment_fast_requisition_main_submit_bt);
        mAddButton.setOnClickListener(mOnClickListener);
        mCancelButton = (Button) rootView.findViewById(R.id.fragment_fast_requisition_main_cancel_bt);
        mCancelButton.setOnClickListener(mOnClickListener);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_purchase_receipt_content_rl);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(hairMaterSecondTwoAdapter);
        mSpinner.setAdapter(mStringArrayAdapter);
    }

    private void refreshShow() {
        materNumberTextView.setText(mPickItem.getBranch().getMater().getNumber());
        materDescTextView.setText(mPickItem.getBranch().getMater().getDesc());
        mReturnQuantityTextView.setText(mPickItem.getQuantity() + "");
        mUnitTextView.setText(mPickItem.getBranch().getMater().getUnit());
        mWarehouseTextView.setText(mPickItem.getBranch().getMater().getWarehouse());
        mActualQuantityTextView.setText(mPickItem.getHairQuantity() + "");
        mLocationEditText.setText(mPickItem.getBranch().getMater().getLocation());
        mBranchedTextView.setText(mPickItem.getBranched() == Pick.PickItem.BRANCHED_YES ? "是" : "否");

        try {
            int position = mStringArrayAdapter.getPosition(mPickItem.getBranch().getMater().getShard());
            mSpinner.setSelection(position);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void initListeners() {
        mOnItemClickListener = new HairMaterSecondTwoAdapter.OnItemClickListener() {
            @Override
            public void OnItemClosed(final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("删除批次").setMessage("将要删除该添加的批次?");
                builder.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            mPickItem.getPickItemBranchItems().remove(position);
                            hairMaterSecondTwoAdapter.notifyDataSetChanged();
                            UpdateActualQuantity();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                    }
                });
                builder.create().show();
            }
        };

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fragment_fast_requisition_main_submit_bt:
                        if (mPickItem.getHairQuantity() <= 0) {
                            ((BaseActivity) getActivity()).ShowToast("实收数量非法,请先添加批次");
                            return;
                        }
                        if (mPickItem.getHairQuantity() > mPickItem.getQuantity()) {
                            ((BaseActivity) getActivity()).ShowToast("实收数量不能大于退料数量");
                            return;
                        }
                        String shard = "";
                        try{
                            shard = mStringArrayAdapter.getItem(mSpinner.getSelectedItemPosition());
                        }catch (Throwable e){
                            e.printStackTrace();
                        }finally {
                            if(TextUtils.isEmpty(shard)){
                                ((BaseActivity)getActivity()).ShowToast("子库不能为空");
                                return ;
                            }
                        }
                        final String location =  mLocationEditText.getText().toString().trim();
                        if(TextUtils.isEmpty(location)){
                            ((BaseActivity)getActivity()).ShowToast("库位不能为空");
                            return ;
                        }
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                        builder2.setTitle("发料过账").setMessage("将要进行发料过账?");
                        final String finalShard = shard;
                        builder2.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String branch_list = "";
                                try {
                                    JSONArray jsonArray = new JSONArray();
                                    for (Pick.PickItem.PickItemBranchItem pickItemBranchItem : mPickItem.getPickItemBranchItems()) {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("branch_number", pickItemBranchItem.getBranch().getPo());
                                        jsonObject.put("branch_quantity", pickItemBranchItem.getQuantity());
                                        jsonArray.put(jsonObject);
                                    }
                                    branch_list = new JSONObject().put("branch_list", jsonArray).toString();
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }

                                handleSubmit(mPickItem.getBranch().getMater().getWarehouse(), mPickItem.getPick().getDepartment(),
                                        mPickItem.getPick().getWorkOrder(), mPickItem.getSequence(),
                                        mPickItem.getPick().getNumber(), mPickItem.getPickLine(), mPickItem.getHairQuantity(),
                                        mPickItem.getBranch().getMater().getNumber(), finalShard,
                                        location, branch_list);
                            }
                        });
                        builder2.create().show();

                        break;
                    case R.id.fragment_fast_requisition_main_cancel_bt:
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("终止过账").setMessage("将要进行终止过账?");
                        builder.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handlerCancel(mPickItem.getBranch().getMater().getWarehouse(), mPickItem.getPick().getDepartment(), mPickItem.getPick().getWorkOrder(), mPickItem.getSequence(), mPickItem.getPick().getNumber(), mPickItem.getPickLine());
                            }
                        });
                        builder.create().show();
                        break;
                    case R.id.toolbar_menu:
                        ActionSheet.createBuilder(getContext(), getFragmentManager())
                                .setCancelButtonTitle("取消")
                                .setOtherButtonTitles("扫描库位标签")
                                .setCancelableOnTouchOutside(true)
                                .setListener(mActionSheetListener).show();
                        break;
                    case R.id.fragment_fast_requisition_main_add_branch_bt:
                        AlertDialog.Builder builder3 = new AlertDialog.Builder(getContext());
                        final View view = LayoutInflater.from(getContext()).inflate(R.layout.purchase_receipt_add_branch_layout, null);
                        builder3.setTitle("新增批次").setView(view);
                        builder3.setNegativeButton("取消", null);
                        builder3.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mSecondFragmentCallBack != null) {
                                    EditText ssslEditText = (EditText) view.findViewById(R.id.purchase_receipt_add_branch_sssl_et);
                                    EditText pchEditText = (EditText) view.findViewById(R.id.purchase_receipt_add_branch_pch_et);
                                    addBranch(pchEditText.getText().toString(), ssslEditText.getText().toString());
                                    hairMaterSecondTwoAdapter.notifyDataSetChanged();
                                    UpdateActualQuantity();
                                }
                            }
                        });
                        builder3.create().show();
                        break;
                }
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
                        case REQUEST_CODE_SUBMIT:
                            DecodeManager.decodeHairMaterSubmit(jsonObject, requestCode, myHandler);
                            break;
                        case REQUEST_CODE_CANCEL:
                            DecodeManager.decodeHairMaterCancel(jsonObject, requestCode, myHandler);
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
        mActionSheetListener = new ActionSheet.ActionSheetListener() {
            @Override
            public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

            }

            @Override
            public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                switch (index) {
                    case 0:
                        startActivityForResult(new Intent(getActivity(), ScanActivity.class), REQUEST_CODE_FOR_SCAN_LOCATION);
                        break;
                }
            }
        };
    }

    private void handlerCancel(String warehouse, String department, String workOrder, String sequence, String pickNumbere, String pickLine) {
        if (!HairMaterSecondReturnBranchedFragment.this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("warehouse", warehouse);
        param.put("department", department);
        param.put("work_order", workOrder);
        param.put("sequence", sequence);
        param.put("pick_number", pickNumbere);
        param.put("pick_line", pickLine);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_HAIR_MATER_CANCEL, getContext()), param, REQUEST_CODE_CANCEL, mRequestTaskListener);
    }

    private void handleSubmit(String warehouse, String department, String workOrder, String sequence, String pickNumbere, String pickLine, double actualQuantity, String mater, String shard, String location, String branchList) {
        if (!HairMaterSecondReturnBranchedFragment.this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("warehouse", warehouse);
        param.put("department", department);
        param.put("work_order", workOrder);
        param.put("sequence", sequence);
        param.put("pick_number", pickNumbere);
        param.put("pick_line", pickLine);
        param.put("actual_quantity", actualQuantity + "");
        param.put("mater", mater);
        param.put("shard", shard);
        param.put("location", location);
        param.put("branch_list", branchList);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_HAIR_MATER_RETURN_SUBMIT, getContext()), param, REQUEST_CODE_SUBMIT, mRequestTaskListener);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_SCAN_LOCATION && resultCode == Activity.RESULT_OK) {
            String code = data.getStringExtra("data").trim();
            handleScanLocation(mLocationEditText, code);
        }
    }

    private void handleScanLocation(EditText v, String code) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
        code = CommonTools.decodeScanString("L", code);
        v.setText(code);
    }

    /**
     * 增加批次， 在本地的item集合中追加branch ，不进行联网操作，确认收货时完成提交新增的branch
     *
     * @param branchPO       批次号
     * @param actualQuantity 实收数量
     */
    private void addBranch(String branchPO, String actualQuantity) {
        double num = 0;
        if (TextUtils.isEmpty(branchPO)) {
            Toast.makeText(getContext(), "批次增加失败:批次号不能为空", Toast.LENGTH_SHORT).show();
            return;
        } else {
            try {
                num = Double.parseDouble(actualQuantity);
            } catch (Exception e) {
                Toast.makeText(getContext(), "批次增加失败:实收数量输入非法", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Pick.PickItem.PickItemBranchItem pickItemBranchItem = new Pick.PickItem.PickItemBranchItem();
        pickItemBranchItem.setQuantity(num);
        pickItemBranchItem.setBranch(new Mater.Branch("", branchPO, 0));
        mPickItem.getPickItemBranchItems().add(pickItemBranchItem);
        Toast.makeText(getContext(), "增加成功", Toast.LENGTH_SHORT).show();
    }

    /**
     * 实收总数随着更改
     */
    private void UpdateActualQuantity() {
        double count = 0;
        for (Pick.PickItem.PickItemBranchItem pickItemBranchItem : mPickItem.getPickItemBranchItems()) {
            count += pickItemBranchItem.getQuantity();
        }
        mPickItem.setHairQuantity(count);
        mActualQuantityTextView.setText(mPickItem.getHairQuantity() + "");
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


    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_SUBMIT:
                    if (mSecondFragmentCallBack != null) {
                        if (bundle.getInt("code") == 1) {
                            Toast.makeText(getContext(), "过账成功", Toast.LENGTH_SHORT).show();
                            mSecondFragmentCallBack.itemBeenSured(mPickItem.getSequence());
                        } else if (bundle.getInt("code") == 5) {
                            Toast.makeText(getContext(), "过账失败,目标子库和库位不匹配", Toast.LENGTH_SHORT).show();
                        } else if (bundle.getInt("code") == 6) {
                            Toast.makeText(getContext(), "过账失败,库存数量不足", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "过账失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case REQUEST_CODE_CANCEL:
                    if (mSecondFragmentCallBack != null) {
                        if (bundle.getInt("code") == 1) {
                            Toast.makeText(getContext(), "终止过账成功", Toast.LENGTH_SHORT).show();
                            mSecondFragmentCallBack.itemBeenClosed(mPickItem.getSequence());
                        } else {
                            Toast.makeText(getContext(), "终止过账失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    }
}