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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.activity.ScanActivity;
import com.amphenol.adapter.FirstRequisitionForMaterListAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Requisition;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.Char2BigUtil;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;
import com.baoyz.actionsheet.ActionSheet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FastRequisitionMainFragment extends BaseFragment {
    private static final int REQUEST_CODE_GET_MATER_LIST = 0x10;
    private static final int REQUEST_CODE_COMMIT = 0x11;
    private static final int REQUEST_CODE_GET_MATER = 0x12;
    private static final int REQUEST_CODE_FOR_SCAN_MATER = 0x13;
    private static final int REQUEST_CODE_FOR_SCAN_TARGET_LOCATION = 0X14;
    private static final int REQUEST_CODE_FOR_SCAN_FROM_LOCATION = 15;
    private View rootView = null;
    private TextView wareHouseTextView;
    private EditText materEditText, fromLocationEditText, targetLocationEditText;
    private Button mInquireButton, mSubmitButton;
    private RecyclerView mRecyclerView;
    private View.OnClickListener mOnClickListener;
    private ImageView mImageButton;
    private Requisition requisition = new Requisition();
    private FirstRequisitionForMaterListAdapter firstRequisitionForMaterListAdapter;
    private FirstRequisitionForMaterListAdapter.OnItemClickListener mOnItemClickListener;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private MyHandler myHandler = new MyHandler();
    private int currentCheckedItemCount = 0;
    private MainFragmentCallBack mainFragmentCallBack;
    private ActionSheet.ActionSheetListener mActionSheetListener;

    public static FastRequisitionMainFragment newInstance(MainFragmentCallBack mainFragmentCallBack) {

        Bundle args = new Bundle();
        FastRequisitionMainFragment fragment = new FastRequisitionMainFragment();
        fragment.mainFragmentCallBack = mainFragmentCallBack;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_fast_requisition_main, container, false);
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        initListeners();
        initData();
        initViews();
        return rootView;
    }

    private void initData() {

        firstRequisitionForMaterListAdapter = new FirstRequisitionForMaterListAdapter(getContext(), requisition.getRequisitionItems(), mOnItemClickListener);
    }

    private void initViews() {
        mImageButton = (ImageView) rootView.findViewById(R.id.toolbar_menu);
        mImageButton.setOnClickListener(mOnClickListener);
        wareHouseTextView = (TextView) rootView.findViewById(R.id.fragment_fast_requisition_main_warehouse_in_tv);
        wareHouseTextView.setText(SessionManager.getWarehouse(getContext()));
        materEditText = (EditText) rootView.findViewById(R.id.fragment_fast_requisition_main_mater_in_et);
        fromLocationEditText = (EditText) rootView.findViewById(R.id.fragment_fast_requisition_main_from_shard_et);
        mInquireButton = (Button) rootView.findViewById(R.id.fragment_fast_requisition_main_inquire_bt);
        mInquireButton.setOnClickListener(mOnClickListener);
        mSubmitButton = (Button) rootView.findViewById(R.id.fragment_fast_requisition_main_submit_bt);
        mSubmitButton.setOnClickListener(mOnClickListener);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_purchase_receipt_content_rl);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(firstRequisitionForMaterListAdapter);
    }

    private void initListeners() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fragment_fast_requisition_main_inquire_bt:
                        boolean state = mInquireButton.getTag() == null ? false : (boolean) mInquireButton.getTag();
                        if (state) {//当前按钮状态为“清除”
                            requisition = new Requisition();
                            refreshShow();
                        } else {
                            handleInquire(materEditText.getText().toString().trim(), fromLocationEditText.getText().toString().trim());
                        }
                        break;
                    case R.id.fragment_fast_requisition_main_submit_bt:
                        if (currentCheckedItemCount < 1) {
                            ((BaseActivity) getActivity()).ShowToast("还没有勾选任何物料项");
                            break;
                        }

                        AlertDialog.Builder builder3 = new AlertDialog.Builder(getContext());
                        final View view = LayoutInflater.from(getContext()).inflate(R.layout.fast_requisition_submit_layout, null);
                        builder3.setTitle("快速调拨").setView(view);
                        builder3.setNegativeButton("取消", null);
                        builder3.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (TextUtils.isEmpty(targetLocationEditText.getText().toString().trim())) {
                                    ((BaseActivity) getActivity()).ShowToast("请先输入到库位");
                                } else {
                                    String materListString;
                                    JSONObject materListJsonObject = new JSONObject();
                                    try {
                                        JSONArray materListJsonArray = new JSONArray();
                                        for (Requisition.RequisitionItem requisitionItem : requisition.getRequisitionItems()) {
                                            if (!requisitionItem.isChecked())
                                                continue;
                                            double quantity = requisitionItem.getQuantity();
                                            if (quantity > requisitionItem.getBranch().getQuantity()) {
                                                ((BaseActivity) getActivity()).ShowToast("存在调拨数量大于库存数量的调拨项,请检查后重试!");
                                                return;
                                            }
                                            if (quantity <= 0) {
                                                ((BaseActivity) getActivity()).ShowToast("存在调拨数量小于0的调拨项,请检查后重试!");
                                                return;
                                            }
                                            String from_warehouse = requisitionItem.getBranch().getMater().getWarehouse();
                                            String from_shard = requisitionItem.getBranch().getMater().getShard();
                                            String from_location = requisitionItem.getBranch().getMater().getLocation();
                                            String target_warehouse = requisitionItem.getBranch().getMater().getWarehouse();
                                            String target_shard = requisitionItem.getBranch().getMater().getShard();
                                            String target_location = targetLocationEditText.getText().toString().trim();
                                            String mater = requisitionItem.getBranch().getMater().getNumber();
                                            String branch = requisitionItem.getBranch().getPo();

                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("from_warehouse", from_warehouse);
                                            jsonObject.put("from_shard", from_shard);
                                            jsonObject.put("from_location", from_location);
                                            jsonObject.put("target_warehouse", target_warehouse);
                                            jsonObject.put("target_shard", target_shard);
                                            jsonObject.put("target_location", target_location);
                                            jsonObject.put("mater", mater);
                                            jsonObject.put("branch", branch);
                                            jsonObject.put("quantity", quantity);
                                            materListJsonArray.put(jsonObject);
                                        }
                                        materListJsonObject.put("mater_list", materListJsonArray);
                                        materListString = materListJsonObject.toString();
                                        handlerFastRequisition(materListString);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        builder3.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                targetLocationEditText = null;
                            }
                        });
                        builder3.create().show();
                        targetLocationEditText = (EditText) view.findViewById(R.id.fragment_fast_requisition_main_target_shard_et);
                        break;
                    case R.id.toolbar_menu:
                        ActionSheet.createBuilder(getContext(), getFragmentManager())
                                .setCancelButtonTitle("取消")
                                .setOtherButtonTitles("扫描物料标签", "扫描从库位标签", "扫描到库位标签")
                                .setCancelableOnTouchOutside(true)
                                .setListener(mActionSheetListener).show();
                        break;
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
                        startActivityForResult(new Intent(getActivity(), ScanActivity.class), REQUEST_CODE_FOR_SCAN_MATER);
                        break;
                    case 1:
                        startActivityForResult(new Intent(getActivity(), ScanActivity.class), REQUEST_CODE_FOR_SCAN_FROM_LOCATION);
                        break;
                    case 2:
                        startActivityForResult(new Intent(getActivity(), ScanActivity.class), REQUEST_CODE_FOR_SCAN_TARGET_LOCATION);
                        break;
                }
            }
        };
        mOnItemClickListener = new FirstRequisitionForMaterListAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                handleInquireMater(requisition.getRequisitionItems().get(position).getBranch().getMater().getWarehouse(), requisition.getRequisitionItems().get(position).getBranch().getMater().getShard(), requisition.getRequisitionItems().get(position).getBranch().getMater().getLocation(), requisition.getRequisitionItems().get(position).getBranch().getMater().getNumber(), requisition.getRequisitionItems().get(position).getBranch().getPo(), requisition.getRequisitionItems().get(position).getBranch().getQuantity(), requisition.getRequisitionItems().get(position).getBranch().getMater().getUnit());
            }

            @Override
            public void OnItemCheckedChanged(int position, boolean isChecked) {
                if (isChecked)
                    currentCheckedItemCount++;
                else
                    currentCheckedItemCount--;
                if (currentCheckedItemCount < 0)
                    currentCheckedItemCount = 0;
            }

            @Override
            public void OnRequisitionQuantityChanged(int position, double quantity) {
                if (quantity > requisition.getRequisitionItems().get(position).getBranch().getQuantity()) {
                    ((BaseActivity) getActivity()).ShowToast("调拨数量不能大于库存数量" + requisition.getRequisitionItems().get(position).getBranch().getQuantity());
                }
                if (quantity <= 0) {
                    ((BaseActivity) getActivity()).ShowToast("调拨数量必须大于0");
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
                        case REQUEST_CODE_GET_MATER_LIST:
                            DecodeManager.decodeCreaetRequisitionGetMaterList(jsonObject, requestCode, myHandler);
                            break;
                        case REQUEST_CODE_COMMIT:
                            DecodeManager.decodeCreaetRequisitionCommit(jsonObject, requestCode, myHandler);
                            break;
                        case REQUEST_CODE_GET_MATER:
                            DecodeManager.decodeCreaetRequisitionGetMater(jsonObject, requestCode, myHandler);
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


    @Override
    protected void handleScanCode(String code) {
        String location = CommonTools.decodeScanString(PropertiesUtil.getInstance(getContext()).getValue(PropertiesUtil.BARCODE_PREFIX_LOCATION, ""), code);
        String mater = CommonTools.decodeScanString(PropertiesUtil.getInstance(getContext()).getValue(PropertiesUtil.BARCODE_PREFIX_MATER, ""), code);
        String branch = CommonTools.decodeScanString(PropertiesUtil.getInstance(getContext()).getValue(PropertiesUtil.BARCODE_PREFIX_BRANCH, ""), code);
        boolean state = mInquireButton.getTag() == null ? false : (boolean) mInquireButton.getTag();
        if (targetLocationEditText != null) {
            if (TextUtils.isEmpty(location)) {
                ((BaseActivity) getActivity()).ShowToast("无效库位标签");
            } else {
                targetLocationEditText.setText(location);
            }
            return;
        }
        if (state) {//当前按钮状态为“清除” ,扫码选中物料
            int count = 0;
            for (int i = 0; i < requisition.getRequisitionItems().size(); i++) {

                Requisition.RequisitionItem requisitionItem = requisition.getRequisitionItems().get(i);
                if ((TextUtils.isEmpty(branch) ? true : TextUtils.equals(requisitionItem.getBranch().getPo(), branch))
                        && TextUtils.equals(requisitionItem.getBranch().getMater().getNumber(), mater)
                        && (TextUtils.isEmpty(location) ? true : TextUtils.equals(requisitionItem.getBranch().getMater().getLocation(), location))) {
                    if (!requisitionItem.isChecked()) {
                        currentCheckedItemCount++;
                        requisitionItem.setChecked(true);
                        requisition.getRequisitionItems().add(0,requisition.getRequisitionItems().remove(i));
                        firstRequisitionForMaterListAdapter.notifyDataSetChanged();
                        mRecyclerView.scrollToPosition(0);
                    }else{
                        mRecyclerView.scrollToPosition(i);
                    }
                    ((BaseActivity)getActivity()).ShowToast("扫描成功并勾选");
                    break;
                }
                count++;
            }
            if (count == requisition.getRequisitionItems().size()) {
                ((BaseActivity) getActivity()).ShowToast("该物料不在列表中");
            }
            materEditText.getText().clear();
        } else {
            materEditText.setText(mater);
            fromLocationEditText.setText(location);
        }
    }

    private void refreshShow() {
        materEditText.requestFocus();
        firstRequisitionForMaterListAdapter.setDate(requisition.getRequisitionItems());
        firstRequisitionForMaterListAdapter.notifyDataSetChanged();

        if (requisition.getRequisitionItems().size() == 0) {
            mInquireButton.setTag(false);
            mInquireButton.setText("查询");
            currentCheckedItemCount = 0;
            materEditText.getText().clear();
            fromLocationEditText.getText().clear();
            materEditText.setHint("输入物料编号");
            materEditText.requestFocus();
        } else {
            currentCheckedItemCount = 0;
            mInquireButton.setText("清除");
            mInquireButton.setTag(true);
            materEditText.getText().clear();
            materEditText.setHint("在此扫描物料快速选中");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleInquire(String mater, String location) {
        if (TextUtils.isEmpty(mater) && TextUtils.isEmpty(location)) {
            ((BaseActivity) getActivity()).ShowToast("至少输入物料或从库位一项");
            return;
        }
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("warehouse", SessionManager.getWarehouse(getContext()));
        param.put("location", location);
        param.put("mate", mater);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_CREATE_REQUISITION_GET_MATER_LIST, getContext()), param, REQUEST_CODE_GET_MATER_LIST, mRequestTaskListener);
    }

    private void handleInquireMater(String warehouse, String shard, String location, String mate, String branch, double quantity, String unit) {
        if (!FastRequisitionMainFragment.this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("warehouse", warehouse);
        param.put("shard", shard);
        param.put("location", location);
        param.put("mate", mate);
        param.put("branch", branch);
        param.put("quantity", quantity + "");
        param.put("unit", unit);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_CREATE_REQUISITION_GET_MATER, getContext()), param, REQUEST_CODE_GET_MATER, mRequestTaskListener);
    }

    private void handlerFastRequisition(String materListString) {
        if (!FastRequisitionMainFragment.this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("mater_list", materListString);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_FAST_REQUISITION_COMMIT, getContext()), param, REQUEST_CODE_COMMIT, mRequestTaskListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_SCAN_MATER && resultCode == Activity.RESULT_OK) {
            String code = data.getStringExtra("data").trim();
            handleScanCode(code);
        }
        if (requestCode == REQUEST_CODE_FOR_SCAN_FROM_LOCATION && resultCode == Activity.RESULT_OK) {
            String code = data.getStringExtra("data").trim();
            handleScanCode(code);
        }
        if (requestCode == REQUEST_CODE_FOR_SCAN_TARGET_LOCATION && resultCode == Activity.RESULT_OK) {
            String code = data.getStringExtra("data").trim();
            handleScanCode(code);
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_GET_MATER_LIST:
                    if (bundle.getInt("code") == 1) {
                        requisition = new Requisition();
                        requisition.setRequisitionItems(bundle.<Requisition.RequisitionItem>getParcelableArrayList("requisitionItems"));
                        refreshShow();
                    } else if (bundle.getInt("code") == 5) {
                        ((BaseActivity) getActivity()).ShowToast("查无结果");
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("查询失败");
                    }
                    break;
                case REQUEST_CODE_COMMIT:
                    if (bundle.getInt("code") == 1) {
                        ((BaseActivity) getActivity()).ShowToast("调拨成功");
                        requisition = new Requisition();
                        refreshShow();
                    } else if (bundle.getInt("code") == 5) {
                        ((BaseActivity) getActivity()).ShowToast("调拨失败:到库位不存在");
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("调拨失败");
                    }
                    break;
                case REQUEST_CODE_GET_MATER:
                    if (bundle.getInt("code") == 1) {
                        Requisition.RequisitionItem requisitionItem = bundle.getParcelable("requisitionItem");
                        if (mainFragmentCallBack != null) {
                            mainFragmentCallBack.gotoSecondFragment(requisitionItem);
                        }
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("获取物料明细失败");
                    }
                    break;
            }
        }
    }

    public interface MainFragmentCallBack extends Serializable {
        void gotoSecondFragment(Requisition.RequisitionItem requisitionItem);
    }
}
