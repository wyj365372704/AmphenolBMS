package com.amphenol.fragment;

import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
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
 * 采购收货_物料明细
 */
public class InventorySecondFragment extends Fragment {
    private View rootView = null;
    private TextView materNumberTextView, materDescTextView, materFormatTextView, branchTextView, quantityTextView, unitTextView, currentShardTextView, currentLocationTextView;
    private Button mButton;
    private EditText actualQuantityEditText;
    private View.OnClickListener mOnClickListener;
    private Mater.Branch branch;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private static final int REQUEST_CODE_INVENTORY = 0X012;
    private LoadingDialog mLoadingDialog;
    private MyHandler myHandler = new MyHandler();
    private SecondFragmentCallBack mSecondFragmentCallBack;

    public static InventorySecondFragment newInstance(Mater.Branch branch, SecondFragmentCallBack secondFragmentCallBack) {

        Bundle args = new Bundle();
        args.putParcelable("branch", branch);
        InventorySecondFragment fragment = new InventorySecondFragment();
        fragment.setArguments(args);
        fragment.mSecondFragmentCallBack = secondFragmentCallBack;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            branch = args.getParcelable("branch");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_inventory_second, container, false);
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
                if (v.getId() == R.id.fragment_check_requisition_second_submit_bt) {
                    if (TextUtils.isEmpty(actualQuantityEditText.getText().toString())) {
                        ((BaseActivity) getActivity()).ShowToast("请输入实际库存");
                    } else {
                        final BigDecimal ia_quantity = new BigDecimal(actualQuantityEditText.getText().toString()).subtract(new BigDecimal(branch.getQuantity()));
//                        final double ia_quantity = Double.parseDouble(actualQuantityEditText.getText().toString()) - branch.getQuantity();
                        if (ia_quantity.compareTo(new BigDecimal(0)) == 0) {
                            ((BaseActivity) getActivity()).ShowToast("无需更新");
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("终止过账").setMessage("将要对此物料终止过账?");
                            builder.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    handleInventory(branch.getMater().getNumber(), branch.getMater().getLocation(), branch.getPo(), ia_quantity.setScale(4,ROUND_HALF_UP).toString());
                                }
                            });
                            builder.create().show();
                        }
                    }

                }
            }
        };
    }

    private void initViews() {
        actualQuantityEditText = (EditText) rootView.findViewById(R.id.fragment_create_requisition_second_target_location_et);

        mButton = (Button) rootView.findViewById(R.id.fragment_check_requisition_second_submit_bt);
        mButton.setOnClickListener(mOnClickListener);
        materNumberTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_mater_number_tv);
        materDescTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_mater_desc_tv);
        materFormatTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_mater_format_tv);
        branchTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_branch_number_tv);
        quantityTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_quantity_tv);
        unitTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_unity_tv);
        currentShardTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_current_shard_tv);
        currentLocationTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_current_location_tv);
    }

    private void initData() {
        materNumberTextView.setText(branch.getMater().getNumber());
        materDescTextView.setText(branch.getMater().getDesc());
        materFormatTextView.setText(branch.getMater().getFormat());
        branchTextView.setText(branch.getPo());
        quantityTextView.setText(branch.getQuantity() + "");
        unitTextView.setText(branch.getMater().getUnit());
        currentShardTextView.setText(branch.getMater().getShard());
        currentLocationTextView.setText(branch.getMater().getLocation());
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

    public interface SecondFragmentCallBack extends Serializable {
        void itemBeenSured();
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_INVENTORY:
                    if (mSecondFragmentCallBack != null) {
                        if (bundle.getInt("code") == 1) {
                            Toast.makeText(getContext(), "更新成功", Toast.LENGTH_SHORT).show();
                            mSecondFragmentCallBack.itemBeenSured();
                        } else {
                            Toast.makeText(getContext(), "更新失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;

            }
        }
    }
}
