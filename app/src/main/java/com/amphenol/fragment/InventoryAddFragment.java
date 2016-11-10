package com.amphenol.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.activity.ScanActivity;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Mater;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;

import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 */
public class InventoryAddFragment extends Fragment {
    private View rootView = null;
    private EditText materEditText;
    private Button mInquireButton;
    private ImageView mScanImageView;
    private TextView materNumberTextView, materDescTextView, materFormatTextView, unitTextView;
    private Button mAddButton;
    private EditText actualQuantityEditText, branchEditText, locationEditText;
    private View.OnClickListener mOnClickListener;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private MyHandler myHandler = new MyHandler();
    private AddFragmentCallBack mAddFragmentCallBack;
    private Mater mater = new Mater();
    private int REQUEST_CODE_FOR_SCAN = 0x12;
    private final int REQUEST_CODE_INQUIRE = 0X13;
    private final int REQUEST_CODE_INVENTORY = 0x14;

    public static InventoryAddFragment newInstance(AddFragmentCallBack addFragmentCallBack) {

        Bundle args = new Bundle();
        InventoryAddFragment fragment = new InventoryAddFragment();
        fragment.setArguments(args);
        fragment.mAddFragmentCallBack = addFragmentCallBack;
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
        rootView = inflater.inflate(R.layout.fragment_inventory_add, container, false);
        initListeners();
        initViews();
        initData();
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        materEditText.requestFocus();
    }

    private void initListeners() {
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
                            DecodeManager.decodeInventoryAddQuery(jsonObject, requestCode, myHandler);
                            break;
                        case REQUEST_CODE_INVENTORY:
                            DecodeManager.decodeInventoryUpdateSubmit(jsonObject, requestCode, myHandler);
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

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fragment_purchase_receipt_inquire_bt:
                        boolean state = mInquireButton.getTag() == null ? false : (boolean) mInquireButton.getTag();
                        if (state) {//当前按钮状态为“清除”
                            mater = new Mater();
                            refreshShow();
                        } else {
                            handleScanCode(materEditText.getText().toString().trim());
                        }
                        break;
                    case R.id.fragment_purchase_receipt_scan_iv:
                        startActivityForResult(new Intent(getActivity(), ScanActivity.class), REQUEST_CODE_FOR_SCAN);
                        break;
                    case R.id.fragment_fast_requisition_main_submit_bt:

                        if (mater.getBranchControl() == Mater.BRANCH_CONTROL && branchEditText.getText().toString().trim().isEmpty()) {
                            ((BaseActivity) getActivity()).ShowToast("该物料批次控制,请输入批号");
                        } else if (locationEditText.getText().toString().trim().isEmpty()) {
                            ((BaseActivity) getActivity()).ShowToast("请输入待入库位");
                        } else if (actualQuantityEditText.getText().toString().trim().isEmpty()) {
                            ((BaseActivity) getActivity()).ShowToast("请输入实际库存");
                        } else {
                            final BigDecimal ia_quantity = new BigDecimal(actualQuantityEditText.getText().toString());
                            if (ia_quantity.compareTo(new BigDecimal(0)) <= 0) {
                                ((BaseActivity) getActivity()).ShowToast("实际库存必须大于0");
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("新增物料").setMessage("将要进行新增物料?");
                                builder.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        handleInventory(mater.getNumber(), locationEditText.getText().toString().trim(), branchEditText.getText().toString().trim(), ia_quantity.setScale(4, ROUND_HALF_UP).toString());
                                    }
                                });
                                builder.create().show();
                            }
                        }
                        break;
                }
            }
        };
    }

    private void initViews() {
        actualQuantityEditText = (EditText) rootView.findViewById(R.id.fragment_inventory_add_actual_quantity_et);
        locationEditText = (EditText) rootView.findViewById(R.id.fragment_fast_requisition_main_from_shard_et);
        materEditText = (EditText) rootView.findViewById(R.id.purchase_receipt_main_code_et);
        actualQuantityEditText = (EditText) rootView.findViewById(R.id.fragment_inventory_add_actual_quantity_et);
        branchEditText = (EditText) rootView.findViewById(R.id.fragment_fast_requisition_main_from_branch_et);
        mAddButton = (Button) rootView.findViewById(R.id.fragment_fast_requisition_main_submit_bt);
        mAddButton.setOnClickListener(mOnClickListener);
        materNumberTextView = (TextView) rootView.findViewById(R.id.fragment_inventory_add_mater_tv_in);
        materDescTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_mater_desc_tv);
        materFormatTextView = (TextView) rootView.findViewById(R.id.fragment_inventory_add_format_tv_in);
        unitTextView = (TextView) rootView.findViewById(R.id.fragment_inventory_add_unit_tv_in);

        mScanImageView = (ImageView) rootView.findViewById(R.id.fragment_purchase_receipt_scan_iv);
        mScanImageView.setOnClickListener(mOnClickListener);

        mInquireButton = (Button) rootView.findViewById(R.id.fragment_purchase_receipt_inquire_bt);
        mInquireButton.setOnClickListener(mOnClickListener);
    }

    private void initData() {

    }

    private void refreshShow() {
        materNumberTextView.setText(mater.getNumber());
        materFormatTextView.setText(mater.getFormat());
        materDescTextView.setText(mater.getDesc());

        unitTextView.setText(mater.getUnit());

        if (mater.getBranchControl() == Mater.BRANCH_CONTROL) {
            branchEditText.setEnabled(true);
        } else {
            branchEditText.setEnabled(false);
            branchEditText.setText("");
        }

        if (TextUtils.isEmpty(mater.getNumber())) {
            mInquireButton.setTag(false);
            mInquireButton.setText("查询");
            materEditText.getText().clear();
            locationEditText.getText().clear();
            actualQuantityEditText.getText().clear();
            collapseButton();
        } else {
            mInquireButton.setText("清除");
            mInquireButton.setTag(true);
            materEditText.getText().clear();
            popUpButton();
        }
    }

    private void collapseButton() {
        if (mAddButton.getVisibility() == View.GONE)
            return;
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mAddButton.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mAddButton.startAnimation(animation);
    }

    private void popUpButton() {
        if (mAddButton.getVisibility() == View.VISIBLE)
            return;
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
        animation.setDuration(300);
        mAddButton.setVisibility(View.VISIBLE);
        mAddButton.startAnimation(animation);
    }

    private void handleScanCode(String code) {
        if (TextUtils.isEmpty(code))
            return;
        String mater = CommonTools.decodeScanString("M", code);
        if (TextUtils.isEmpty(mater)) {
            ((BaseActivity) getActivity()).ShowToast("无效物料标签");
            return;
        }
        materEditText.setText(mater);
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("warehouse", SessionManager.getWarehouse(getContext()));
        param.put("mater", mater);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_INVENTORY_ADD_QUERY, getContext()), param, REQUEST_CODE_INQUIRE, mRequestTaskListener);

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
        if (requestCode == REQUEST_CODE_FOR_SCAN && resultCode == Activity.RESULT_OK) {
            String code = data.getStringExtra("data").trim();
            handleScanCode(code);
        }
    }

    private void handleInventory(String mater, String location, String branch, String ia_quantity) {
        if (!this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("warehouse", SessionManager.getWarehouse(getContext()));
        param.put("mater", mater);
        param.put("location", location);
        param.put("branch", branch);
        param.put("ia_quantity", ia_quantity);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_INVENTORY_UPDATE_SUBMIT, getContext()), param, REQUEST_CODE_INVENTORY, mRequestTaskListener);
    }

    public interface AddFragmentCallBack extends Serializable {
        void itemBeenSured();
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_INQUIRE:
                    if (bundle.getInt("code") == 1) {
                        mater = bundle.getParcelable("mater");
                        refreshShow();
                    } else if (bundle.getInt("code") == 6) {
                        Toast.makeText(getContext(), "物料不存在", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "查询失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case REQUEST_CODE_INVENTORY:
                    if (bundle.getInt("code") == 1) {
                        Toast.makeText(getContext(), "新增成功", Toast.LENGTH_SHORT).show();
                        mater = new Mater();
                        refreshShow();
                    } else if (bundle.getInt("code") == 5) {
                        Toast.makeText(getContext(), "新增失败,待入库位不存在", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "新增失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }
}
