package com.amphenol.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.amphenol.amphenol.R;
import com.amphenol.entity.Requisition;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 审核调拨单-物料明细
 */
public class CheckRequisitionSecondFragment extends Fragment {
    private static final int REQUEST_CODE_SUBMIT = 0X10;
    private static final int REQUEST_CODE_CANCEL = 0X11;
    private View rootView = null;
    private TextView fromWarehouseTextView, materNumberTextView, materDescTextView, materFormatTextView, branchTextView, requisitionQuantityTextView, unitTextView, fromShardTextView, fromLocationTextView;
    private EditText actualQuantityEditText, targetLocationEditText;
    private Spinner targetShardSpinner;
    private Button submitButton, cancelButton;
    private Requisition.RequisitionItem mRequisitionItem;
    private ArrayAdapter<String> mStringArrayAdapter;
    private ArrayList<String> shardStrings;
    private View.OnClickListener mOnClickListener;
    private TextWatcher mTextWatcher;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private MyHandler myHandler = new MyHandler();
    private SecondFragmentCallBack mSecondFragmentCallBack;
    private View dialogView;//弹窗dialog视图

    public static CheckRequisitionSecondFragment newInstance(Requisition.RequisitionItem mRequisitionItem, ArrayList<String> shards, SecondFragmentCallBack mSecondFragmentCallBack) {

        Bundle args = new Bundle();
        args.putSerializable("mRequisitionItem", mRequisitionItem);
        args.putSerializable("shards", shards);
        CheckRequisitionSecondFragment fragment = new CheckRequisitionSecondFragment();
        fragment.mSecondFragmentCallBack = mSecondFragmentCallBack;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mRequisitionItem = (Requisition.RequisitionItem) args.getSerializable("mRequisitionItem");
            shardStrings = (ArrayList<String>) args.getSerializable("shards");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_check_requisition_second, container, false);
        initListeners();
        initViews();
        initData();
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        return rootView;
    }

    private void initListeners() {
        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double quantity = 0;
                try {
                    quantity = Double.parseDouble(s.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    ((BaseActivity) getActivity()).ShowToast("实收数量输入非法");
                    return;
                }
                mRequisitionItem.setActualQuantity(quantity);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fragment_check_requisition_second_submit_bt:
                        double actualQuantity = 0;
                        try {
                            actualQuantity = Double.parseDouble(actualQuantityEditText.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            ((BaseActivity) getActivity()).ShowToast("实收数量输入非法");
                            break;
                        }
                        mRequisitionItem.setActualQuantity(actualQuantity);
                        if (TextUtils.isEmpty(targetLocationEditText.getText().toString())) {
                            ((BaseActivity) getActivity()).ShowToast("目标库位输入不能能空");
                            targetLocationEditText.requestFocus();
                            break;
                        }

                        final String beforeShard = mRequisitionItem.getShard();
                        final String afterShard = mStringArrayAdapter.getItem(targetShardSpinner.getSelectedItemPosition());
                        final String beforeLocation = mRequisitionItem.getLocation();
                        final String afterLocation = targetLocationEditText.getText().toString();


                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                        builder2.setTitle("确认过账").setMessage("将要对此物料进行确认过账?");
                        if (!TextUtils.equals(beforeShard, afterShard) || !TextUtils.equals(beforeLocation, afterLocation)) {
                            dialogView = LayoutInflater.from(getContext()).inflate(R.layout.check_requisition_sure_with_shard_or_location_changed_dialog, null, false);
                            builder2.setView(dialogView);
                        }
                        builder2.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean update = false;
                                if (!TextUtils.equals(beforeShard, afterShard) || !TextUtils.equals(beforeLocation, afterLocation) && dialogView != null) {
                                    CheckBox checkBox = (CheckBox) dialogView.findViewById(R.id.checkbox);
                                    if (checkBox.getVisibility() == View.VISIBLE) {
                                        if (checkBox.isChecked())
                                            update = true;
                                    }
                                }
                                handleCheckRequisitionSure(mRequisitionItem.getRequisition().getNumber(), mRequisitionItem.getNumber(), mRequisitionItem.getActualQuantity(), mRequisitionItem.getBranch().getMater().getWarehouse(), afterShard, afterLocation, update);
                            }
                        });
                        builder2.create().show();


                        break;

                    case R.id.fragment_check_requisition_second_cancel_bt:
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("终止过账").setMessage("将要对此物料终止过账?");
                        builder.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handleCheckRequisitionCancel();
                            }
                        });
                        builder.create().show();
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
                            DecodeManager.decodeCheckRequisitionSure(jsonObject, requestCode, myHandler);
                            break;
                        case REQUEST_CODE_CANCEL:
                            DecodeManager.decodeCheckRequisitionCancel(jsonObject, requestCode, myHandler);
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

    private void handleCheckRequisitionCancel() {
        if (!CheckRequisitionSecondFragment.this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("requisition", mRequisitionItem.getRequisition().getNumber());
        param.put("requisition_line", mRequisitionItem.getNumber());
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_CHECK_REQUISITION_CANCEL, getContext()), param, REQUEST_CODE_CANCEL, mRequestTaskListener);
    }

    private void handleCheckRequisitionSure(String requisition, String requisition_line, double actualQuantity, String warehouse, String afterShard, String afterLocation, boolean update) {

        if (!CheckRequisitionSecondFragment.this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("requisition", requisition);
        param.put("requisition_line", requisition_line);
        param.put("actual_quantity", actualQuantity + "");
        param.put("target_warehouse", warehouse);
        param.put("target_shard", afterShard);
        param.put("target_location", afterLocation);
        param.put("update", update ? "1" : "0");
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_CHECK_REQUISITION_SURE, getContext()), param, REQUEST_CODE_SUBMIT, mRequestTaskListener);
    }

    private void initViews() {
        actualQuantityEditText = (EditText) rootView.findViewById(R.id.fragment_create_requisition_second_actual_quantity_et);
        actualQuantityEditText.addTextChangedListener(mTextWatcher);
        targetLocationEditText = (EditText) rootView.findViewById(R.id.fragment_create_requisition_second_target_location_et);
        targetShardSpinner = (Spinner) rootView.findViewById(R.id.fragment_create_requisition_second_target_shard_spinner);
        fromWarehouseTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_from_warehouse_tv);
        materNumberTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_mater_number_tv);
        materDescTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_mater_desc_tv);
        materFormatTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_mater_format_tv);
        branchTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_branch_number_tv);
        requisitionQuantityTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_requisition_quantity_tv);
        unitTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_unity_tv);
        fromShardTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_current_shard_tv);
        fromLocationTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_current_location_tv);
        submitButton = (Button) rootView.findViewById(R.id.fragment_check_requisition_second_submit_bt);
        cancelButton = (Button) rootView.findViewById(R.id.fragment_check_requisition_second_cancel_bt);
        submitButton.setOnClickListener(mOnClickListener);
        cancelButton.setOnClickListener(mOnClickListener);
    }

    private void initData() {
        fromWarehouseTextView.setText(mRequisitionItem.getBranch().getMater().getWarehouse());
        fromShardTextView.setText(mRequisitionItem.getBranch().getMater().getShard());
        fromLocationTextView.setText(mRequisitionItem.getBranch().getMater().getLocation());

        materNumberTextView.setText(mRequisitionItem.getBranch().getMater().getNumber());
        materDescTextView.setText(mRequisitionItem.getBranch().getMater().getDesc());
        materFormatTextView.setText(mRequisitionItem.getBranch().getMater().getFormat());
        branchTextView.setText(mRequisitionItem.getBranch().getPo());
        requisitionQuantityTextView.setText(mRequisitionItem.getQuantity() + "");
        unitTextView.setText(mRequisitionItem.getBranch().getMater().getUnit());
        actualQuantityEditText.setText(mRequisitionItem.getActualQuantity() + "");
        targetLocationEditText.setText(mRequisitionItem.getLocation());

        if (shardStrings != null) {
            mStringArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, shardStrings);
            //第三步：为适配器设置下拉列表下拉时的菜单样式。
            mStringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            targetShardSpinner.setAdapter(mStringArrayAdapter);
        }
        targetLocationEditText.setText(mRequisitionItem.getLocation());
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
         * @param requisitionItemNumber 调拨单行号
         */
        void itemBeenClosed(String requisitionItemNumber);

        /**
         * @param requisitionItemNumber 调拨单行号
         */

        void itemBeenSured(String requisitionItemNumber);
    }


    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_CANCEL:
                    if (mSecondFragmentCallBack != null) {
                        if (bundle.getInt("code") == 1) {
                            Toast.makeText(getContext(), "终止过账成功", Toast.LENGTH_SHORT).show();
                            mSecondFragmentCallBack.itemBeenClosed(mRequisitionItem.getNumber());
                        } else {
                            Toast.makeText(getContext(), "终止过账失败", Toast.LENGTH_SHORT).show();
                        }

                    }
                    break;
                case REQUEST_CODE_SUBMIT:
                    if (mSecondFragmentCallBack != null) {
                        if (bundle.getInt("code") == 1) {
                            Toast.makeText(getContext(), "过账成功", Toast.LENGTH_SHORT).show();
                            mSecondFragmentCallBack.itemBeenSured(mRequisitionItem.getNumber());
                        } else {
                            Toast.makeText(getContext(), "过账失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    }
}
