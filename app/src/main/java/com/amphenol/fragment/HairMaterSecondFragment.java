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
import android.util.Log;
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
import com.amphenol.adapter.HairMaterSecondOneAdapter;
import com.amphenol.amphenol.R;
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
public class HairMaterSecondFragment extends BaseFragment {
    private static final int REQUEST_CODE_INQUIRE = 0X10;
    private static final int REQUEST_CODE_SUBMIT = 0X11;
    private static final int REQUEST_CODE_CANCEL = 0x12;
    private static final int REQUEST_CODE_FOR_SCAN_BRANCH = 0X13;
    private static final int REQUEST_CODE_FOR_SCAN_LOCATION = 0X14;
    private View rootView;
    private TextView materNumberTextView, materDescTextView, mPlanQuantityTextView, mUnitTextView, mHairQuantityTextView,
            mWarehouseTextView;
    private Spinner mSpinner;
    private EditText mLocationEditText, mBranchEditText;
    private Button mInquireButton, mAddButton, mCancelButton;
    private ImageView mImageView;
    private View.OnClickListener mOnClickListener;
    private ArrayAdapter<String> mStringArrayAdapter;
    private ArrayList<String> mShardStrings = new ArrayList<>();
    private Pick.PickItem mPickItem = new Pick.PickItem();
    private HairMaterSecondOneAdapter hairMaterSecondOneAdapter;
    private HairMaterSecondOneAdapter.OnItemClickListener mOnItemClickListener;
    private RecyclerView mRecyclerView;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private SecondFragmentCallBack mSecondFragmentCallBack;
    private MyHandler myHandler = new MyHandler();
    private ActionSheet.ActionSheetListener mActionSheetListener;


    public static HairMaterSecondFragment newInstance(Pick.PickItem pickItem, ArrayList<String> shards, SecondFragmentCallBack mSecondFragmentCallBack) {

        Bundle args = new Bundle();
        args.putParcelable("pickItem", pickItem);
        args.putStringArrayList("shards", shards);
        HairMaterSecondFragment fragment = new HairMaterSecondFragment();
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
            mShardStrings = args.getStringArrayList("shards");
            mShardStrings.add(0, "ALL");
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
        hairMaterSecondOneAdapter = new HairMaterSecondOneAdapter(getContext(), mPickItem.getPickItemBranchItems(), mOnItemClickListener);
    }

    private void initViews() {
        mImageView = (ImageView) rootView.findViewById(R.id.toolbar_menu);
        mImageView.setOnClickListener(mOnClickListener);
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
        mInquireButton.setOnClickListener(mOnClickListener);
        mAddButton = (Button) rootView.findViewById(R.id.fragment_fast_requisition_main_submit_bt);
        mAddButton.setOnClickListener(mOnClickListener);
        mCancelButton = (Button) rootView.findViewById(R.id.fragment_fast_requisition_main_cancel_bt);
        mCancelButton.setOnClickListener(mOnClickListener);
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

        try {
            int position = mStringArrayAdapter.getPosition(mPickItem.getBranch().getMater().getShard());
            mSpinner.setSelection(position);
        } catch (Throwable e) {
            e.printStackTrace();
        }


        mLocationEditText.setText(mPickItem.getBranch().getMater().getLocation());
        mBranchEditText.setText(mPickItem.getBranch().getPo());
        functionFIFO();
        hairMaterSecondOneAdapter.setDate(mPickItem.getPickItemBranchItems());
        hairMaterSecondOneAdapter.notifyDataSetChanged();
        functionCalculateHairQuantity();
        if (mPickItem.getPickItemBranchItems().size() == 0) {
            mInquireButton.setTag(false);
            mInquireButton.setText("查询");
            mLocationEditText.getText().clear();
            mBranchEditText.getText().clear();
        } else {
            mInquireButton.setText("清除");
            mInquireButton.setTag(true);
        }
    }

    /**
     * 先入先出勾选
     */
    private synchronized void functionFIFO() {
        for (Pick.PickItem.PickItemBranchItem pickItemBranchItem : mPickItem.getPickItemBranchItems()) {
            if (mPickItem.getHairQuantity() < mPickItem.getQuantity()) {//发料数量小于计划数量,
                if (mPickItem.getHairQuantity() + pickItemBranchItem.getBranch().getQuantity() > mPickItem.getQuantity()) {//加上这个物料后,发料数量大于计划数量
                    pickItemBranchItem.setQuantity(mPickItem.getQuantity() - mPickItem.getHairQuantity());
                } else {
                    pickItemBranchItem.setQuantity(pickItemBranchItem.getBranch().getQuantity());
                }
                mPickItem.setHairQuantity(mPickItem.getHairQuantity() + pickItemBranchItem.getQuantity());
                pickItemBranchItem.setChecked(true);
            } else {
                break;
            }
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
                            mPickItem.setHairQuantity(0);
                            mPickItem.setPickItemBranchItems(new ArrayList<Pick.PickItem.PickItemBranchItem>());
                            refreshShow();
                        } else {
                            handleInquireMater(mPickItem.getBranch().getMater().getWarehouse(), mPickItem.getBranch().getMater().getUnit(),
                                    mPickItem.getBranch().getMater().getNumber(), mPickItem.getPick().getNumber(), mPickItem.getPickLine(),
                                    mSpinner.getSelectedItemPosition() == 0 ? "" : mStringArrayAdapter.getItem(mSpinner.getSelectedItemPosition()),
                                    mLocationEditText.getText().toString().trim(), mBranchEditText.getText().toString(), mPickItem.getQuantity(),
                                    mPickItem.getPick().getDepartment(), mPickItem.getPick().getWorkOrder(), mPickItem.getSequence(),
                                    String.valueOf(mPickItem.getPick().getType()), mPickItem.getBranched() + "");
                        }
                        break;
                    case R.id.fragment_fast_requisition_main_submit_bt:
                        if (mPickItem.getHairQuantity() <= 0) {
                            ((BaseActivity) getActivity()).ShowToast("发料数量非法");
                            return;
                        }

                        for (Pick.PickItem.PickItemBranchItem pickItemBranchItem : mPickItem.getPickItemBranchItems()) {
                            if (pickItemBranchItem.isChecked() && pickItemBranchItem.getQuantity() > pickItemBranchItem.getBranch().getQuantity()) {
                                ((BaseActivity) getActivity()).ShowToast("存在发料数量大于库存数量的项目,检查后重试");
                                return;
                            }
                        }

                        if (mPickItem.getHairQuantity() > mPickItem.getQuantity()) {
                            ((BaseActivity) getActivity()).ShowToast("发料数量不能大于计划数量");
                            return;
                        }

                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                        builder2.setTitle("发料过账").setMessage("将要进行发料过账?");
                        builder2.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String mater_list = "";
                                try {
                                    JSONArray jsonArray = new JSONArray();
                                    for (Pick.PickItem.PickItemBranchItem pickItemBranchItem : mPickItem.getPickItemBranchItems()) {
                                        if (pickItemBranchItem.isChecked()) {
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("mater", pickItemBranchItem.getBranch().getMater().getNumber());
                                            jsonObject.put("branch", pickItemBranchItem.getBranch().getPo());
                                            jsonObject.put("location", pickItemBranchItem.getBranch().getMater().getLocation());
                                            jsonObject.put("quantity", pickItemBranchItem.getQuantity());
                                            jsonObject.put("shard", pickItemBranchItem.getBranch().getMater().getShard());
                                            jsonArray.put(jsonObject);
                                        }
                                    }
                                    mater_list = new JSONObject().put("mater_list", jsonArray).toString();
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                                handleSubmit(mPickItem.getBranch().getMater().getWarehouse(), mPickItem.getPick().getDepartment(),
                                        mPickItem.getPick().getWorkOrder(), mPickItem.getSequence(), mater_list,
                                        mPickItem.getPick().getNumber(), mPickItem.getPickLine(), mPickItem.getHairQuantity());
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
                                .setOtherButtonTitles("扫描库位标签", "扫描批次标签")
                                .setCancelableOnTouchOutside(true)
                                .setListener(mActionSheetListener).show();
                        break;
                }
            }
        };

        mOnItemClickListener = new HairMaterSecondOneAdapter.OnItemClickListener() {
            @Override
            public void OnItemCheckedChanged(int position, boolean isChecked) {
                mPickItem.getPickItemBranchItems().get(position).setChecked(isChecked);
                functionCalculateHairQuantity();
            }

            @Override
            public void OnRequisitionQuantityChanged(int position, double quantity, double quantityBefore) {
                if (quantity > mPickItem.getPickItemBranchItems().get(position).getBranch().getQuantity()) {
                    ((BaseActivity) getActivity()).ShowToast("发料数量不能大于库存数量");
                }
                functionCalculateHairQuantity();
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
                    case 1:
                        startActivityForResult(new Intent(getActivity(), ScanActivity.class), REQUEST_CODE_FOR_SCAN_BRANCH);
                        break;
                }
            }
        };
    }

    private void handlerCancel(String warehouse, String department, String workOrder, String sequence, String pickNumbere, String pickLine) {
        if (!HairMaterSecondFragment.this.isVisible())
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

    private void handleSubmit(String warehouse, String department, String workOrder, String sequence, String materList, String pickNumbere, String pickLine, double actualQuantity) {
        if (!HairMaterSecondFragment.this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("warehouse", warehouse);
        param.put("department", department);
        param.put("work_order", workOrder);
        param.put("sequence", sequence);
        param.put("mater_list", materList);
        param.put("pick_number", pickNumbere);
        param.put("pick_line", pickLine);
        param.put("actual_quantity", actualQuantity + "");

        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_HAIR_MATER_SUBMIT, getContext()), param, REQUEST_CODE_SUBMIT, mRequestTaskListener);
    }

    /**
     * 计算当前已勾选的物料发料数量之和,并反馈至"发料数量"显示
     */
    private synchronized void functionCalculateHairQuantity() {
        mPickItem.setHairQuantity(0);
        for (Pick.PickItem.PickItemBranchItem pickItemBranchItem : mPickItem.getPickItemBranchItems()) {
            if (pickItemBranchItem.isChecked()) {
                mPickItem.setHairQuantity(mPickItem.getHairQuantity() + pickItemBranchItem.getQuantity());
            }
        }
        mHairQuantityTextView.setText(mPickItem.getHairQuantity() + "");
    }

    private void handleInquireMater(String warehouse, String unit, String mate, String pickNumber, String pickLine, String shard, String location, String branch, double quantity, String department, String workOrder, String sequence, String type, String branched) {
        if (!HairMaterSecondFragment.this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("warehouse", warehouse);
        param.put("pick_number", pickNumber);
        param.put("pick_line", pickLine);
        param.put("mater", mate);
        param.put("unit", unit);
        param.put("shard", shard);
        param.put("location", location);
        param.put("branch", branch);
        param.put("type", type);
        param.put("branched", branched);
        param.put("quantity", quantity + "");//计划数量
        param.put("department", department);
        param.put("workOrder", workOrder);
        param.put("sequence", sequence);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_HAIR_MATER_GET_MATER_LIST, getContext()), param, REQUEST_CODE_INQUIRE, mRequestTaskListener);
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
        if (requestCode == REQUEST_CODE_FOR_SCAN_BRANCH && resultCode == Activity.RESULT_OK) {
            String code = data.getStringExtra("data").trim();
            handleScanBranch(mBranchEditText, code);
        }
        if (requestCode == REQUEST_CODE_FOR_SCAN_LOCATION && resultCode == Activity.RESULT_OK) {
            String code = data.getStringExtra("data").trim();
            handleScanLocation(mLocationEditText, code);
        }
    }

    private void handleScanBranch(EditText v, String code) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
        code = CommonTools.decodeScanString("B", code);
        v.setText(code);
        v.requestFocus();
    }

    private void handleScanLocation(EditText v, String code) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
        code = CommonTools.decodeScanString("L", code);
        v.setText(code);
        mBranchEditText.requestFocus();
    }

    @Override
    protected void handleScanCode(String code) {
        if (TextUtils.isEmpty(code))
            return;
        if (!HairMaterSecondFragment.this.isVisible())
            return;

        String materNumber = CommonTools.decodeScanString(PropertiesUtil.getInstance(getContext()).getValue(PropertiesUtil.BARCODE_PREFIX_MATER, ""), code);
        String location = CommonTools.decodeScanString(PropertiesUtil.getInstance(getContext()).getValue(PropertiesUtil.BARCODE_PREFIX_LOCATION, ""), code);
        String branch = CommonTools.decodeScanString(PropertiesUtil.getInstance(getContext()).getValue(PropertiesUtil.BARCODE_PREFIX_BRANCH, ""), code);

        if (TextUtils.isEmpty(materNumber) || !TextUtils.equals(materNumber, mPickItem.getBranch().getMater().getNumber())) {
            ((BaseActivity) getActivity()).ShowToast("扫描失败,不是正确的物料标签卡");
            return;
        }
        for (int i = 0; i < mPickItem.getPickItemBranchItems().size(); i++) {
            Pick.PickItem.PickItemBranchItem item = mPickItem.getPickItemBranchItems().get(i);
            if (item.getBranch().getPo().equals(branch) && item.getBranch().getMater().getLocation().equals(location)) {
                if (mPickItem.getHairQuantity() + item.getBranch().getQuantity() > mPickItem.getQuantity()) {//加上这个物料后,发料数量大于计划数量
                    item.setQuantity(mPickItem.getQuantity() - mPickItem.getHairQuantity());
                } else {
                    item.setQuantity(item.getBranch().getQuantity());
                }
                item.setChecked(true);
                hairMaterSecondOneAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(i);
                functionCalculateHairQuantity();
                return;
            }
        }
        ((BaseActivity) getActivity()).ShowToast("扫描失败,该物料不在可选列表中");
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
                case REQUEST_CODE_INQUIRE:
                    if (bundle.getInt("code") == 1) {
                        mPickItem = bundle.getParcelable("pickItem");
                        refreshShow();

                        if (mPickItem.getPickItemBranchItems().size() == 0)
                            ((BaseActivity) getActivity()).ShowToast("查无物料库存信息");
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("获取物料明细失败");
                    }
                    break;
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