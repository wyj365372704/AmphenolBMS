package com.amphenol.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Mater;
import com.amphenol.entity.Purchase;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;

import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.amphenol.amphenol.R.id.fragment_purchase_receipt_second_sjdz_in_et;


public class PurchaseStorageSecondNoBranchedFragment extends BaseFragment {
    private static final int REQUEST_CODE_RECEIPT_CONFIRM = 0X10;
    private static final int REQUEST_CODE_RECEIPT_CLOSE = 0x11;
    private View rootView = null;
    private SecondFragmentCallBack mSecondFragmentCallBack;
    private View.OnClickListener mOnClickListener;
    private Button mCloseReceiptButton, mSureReceiptButton, mCameraButton;
    private TextView mMaterNumberTextView, mIsBranchTextView,
            mMaterDescTextView, mPurchaseUnitTextView,
            mPlainQuantityTextView, mActualPurchaseQuantityTextView, mActualSingleUnitTextView, mTotalWeightTextView;
    private EditText mLocationEditText, mActualSingleEditText, mActualQuantityEditText;
    private View dialogView;//弹窗dialog视图
    private Purchase.PurchaseItem mPurchaseItem;
    private TextWatcher mSingleTextWatcher, mTotalTextWatcher;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private MyHandler myHandler = new MyHandler();

    public static PurchaseStorageSecondNoBranchedFragment newInstance(SecondFragmentCallBack mSecondFragmentCallBack, Purchase.PurchaseItem mPurchaseItem) {

        Bundle args = new Bundle();
        args.putParcelable("mPurchaseItem", mPurchaseItem);
        PurchaseStorageSecondNoBranchedFragment fragment = new PurchaseStorageSecondNoBranchedFragment();
        fragment.mSecondFragmentCallBack = mSecondFragmentCallBack;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mPurchaseItem = args.getParcelable("mPurchaseItem");
//            mPurchaseItem.getMater().setQuantity(mPurchaseItem.getQuantity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_purchase_storage_second_no_branched, container, false);
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
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fragment_fast_requisition_main_cancel_bt:
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("关闭入库").setMessage("将要对此物料进行关闭入库?");
                        builder.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handlerCloseMater(mPurchaseItem.getPurchase().getNumber(), mPurchaseItem.getNumber());
                            }
                        });
                        builder.create().show();
                        break;
                    case R.id.fragment_fast_requisition_main_submit_bt:
                        if (TextUtils.isEmpty(mLocationEditText.getText().toString())) {
                            Toast.makeText(getContext(), "入库库位无效", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        final double singleBefore = mPurchaseItem.getMater().getSingle();
                        double singleAfter = 0;
                        double actualQuantity = 0;
                        try {
                            singleAfter = Double.parseDouble(mActualSingleEditText.getText().toString().trim());
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "实际单重无效", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            actualQuantity = Double.parseDouble(mActualQuantityEditText.getText().toString().trim());
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "实收总数无效", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                        builder2.setTitle("确认入库").setMessage("将要对此物料进行确认入库?");
                        if (singleAfter != singleBefore) {
                            dialogView = LayoutInflater.from(getContext()).inflate(R.layout.purchase_receipt_sure_with_actual_single_dialog, null, false);
                            builder2.setView(dialogView);
                        }
                        final double finalActualQuantity = actualQuantity;
                        final double finalSingleAfter = singleAfter;
                        builder2.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean update = false;
                                if (finalSingleAfter != singleBefore && dialogView != null) {
                                    CheckBox checkBox = (CheckBox) dialogView.findViewById(R.id.checkbox);
                                    if (checkBox.getVisibility() == View.VISIBLE) {
                                        if (checkBox.isChecked())
                                            update = true;
                                    }
                                }

                                handlerSureMater(mPurchaseItem.getPurchase().getNumber(), mPurchaseItem.getNumber(), finalSingleAfter, finalActualQuantity, mLocationEditText.getText().toString(), "", update);
                            }
                        });
                        builder2.create().show();
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
                        case REQUEST_CODE_RECEIPT_CONFIRM:
                            DecodeManager.decodeReceiptConfirm(jsonObject, requestCode, myHandler);
                            break;
                        case REQUEST_CODE_RECEIPT_CLOSE:
                            DecodeManager.decodeReceiptClose(jsonObject, requestCode, myHandler);
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

        mSingleTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double single = 0;
                try {
                    single = Double.parseDouble(String.valueOf(s));
                } catch (Throwable e) {
                    e.printStackTrace();
                    ((BaseActivity) getActivity()).ShowToast("实际单重输入非法");
                }
                updateReceiptTotalWeight();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mTotalTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double total = 0;
                try {
                    total = Double.parseDouble(String.valueOf(s));
                } catch (Throwable e) {
                    e.printStackTrace();
                    ((BaseActivity) getActivity()).ShowToast("实收总数输入非法");
                }
                updateReceiptTotalWeight();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    /**
     * 更新收货总重量
     */
    private void updateReceiptTotalWeight() {
        double single = 0;
        double total = 0;

        try {
            single = Double.parseDouble(mActualQuantityEditText.getText().toString().trim());
            total = Double.parseDouble(mActualSingleEditText.getText().toString().trim());
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (mPurchaseItem.getUnit() != null) {
            if (TextUtils.equals(mPurchaseItem.getMater().getUnit(), "GM") || TextUtils.equals(mPurchaseItem.getMater().getUnit(), "gm") || TextUtils.equals(mPurchaseItem.getMater().getUnit(), "G") || TextUtils.equals(mPurchaseItem.getMater().getUnit(), "g")) {
                mTotalWeightTextView.setText(new BigDecimal(Double.toString(single)).multiply(new BigDecimal(Double.toString(total))).divide(new BigDecimal(Double.toString(1000d))).toString());
            } else if (TextUtils.equals(mPurchaseItem.getMater().getUnit(), "KG") || TextUtils.equals(mPurchaseItem.getMater().getUnit(), "kg")) {
                mTotalWeightTextView.setText(new BigDecimal(Double.toString(single)).multiply(new BigDecimal(Double.toString(total))).toString());
            }
        }
    }

    private void initViews() {
        mCloseReceiptButton = (Button) rootView.findViewById(R.id.fragment_fast_requisition_main_submit_bt);
        mSureReceiptButton = (Button) rootView.findViewById(R.id.fragment_fast_requisition_main_cancel_bt);
        mCloseReceiptButton.setOnClickListener(mOnClickListener);
        mSureReceiptButton.setOnClickListener(mOnClickListener);
        mMaterNumberTextView = (TextView) rootView.findViewById(R.id.fragment_purchase_receipt_second_wlbh_in_tv);
        mMaterDescTextView = (TextView) rootView.findViewById(R.id.fragment_purchase_receipt_second_wlms_in_tv);
        mIsBranchTextView = (TextView) rootView.findViewById(R.id.fragment_purchase_receipt_second_pcgk_in_tv);
        mPurchaseUnitTextView = (TextView) rootView.findViewById(R.id.fragment_purchase_receipt_second_cgdw_in_tv);
        mPlainQuantityTextView = (TextView) rootView.findViewById(R.id.fragment_purchase_receipt_second_jhsl_in_tv);
        mActualPurchaseQuantityTextView = (TextView) rootView.findViewById(R.id.fragment_purchase_receipt_second_sszs_in_tv);
        mActualSingleEditText = (EditText) rootView.findViewById(fragment_purchase_receipt_second_sjdz_in_et);
        mActualSingleUnitTextView = (TextView) rootView.findViewById(R.id.fragment_purchase_receipt_second_sjdzdw_tv);
        mActualQuantityEditText = (EditText) rootView.findViewById(R.id.fragment_purchase_receipt_second_sszs_in_et);
        mTotalWeightTextView = (TextView) rootView.findViewById(R.id.fragment_purchase_receipt_second_shzzl_in_tv);
        mLocationEditText = (EditText) rootView.findViewById(R.id.fragment_purchase_receipt_second_shkw_et);

        if (mPurchaseItem.getMater().getBranchControl() == Mater.BRANCH_CONTROL)
            mActualQuantityEditText.setEnabled(false);
    }

    private void initData() {
        mMaterNumberTextView.setText(mPurchaseItem.getMater().getNumber().trim());
        mMaterDescTextView.setText(mPurchaseItem.getMater().getDesc().trim());
        mPurchaseUnitTextView.setText(mPurchaseItem.getUnit());
        mPlainQuantityTextView.setText(mPurchaseItem.getQuantity() + "");
        mActualPurchaseQuantityTextView.setText(mPurchaseItem.getMater().getQuantity() + "");
        mIsBranchTextView.setText(mPurchaseItem.getMater().getBranchControl() == Mater.BRANCH_CONTROL ? "是" : mPurchaseItem.getMater().getBranchControl() == Mater.BRANCH_NO_CONTROL ? "否" : "--");
        mActualSingleEditText.setText(mPurchaseItem.getMater().getSingle() + "");
        mActualSingleEditText.addTextChangedListener(mSingleTextWatcher);
        mActualSingleUnitTextView.setText(mPurchaseItem.getMater().getUnit());
        mLocationEditText.setText(mPurchaseItem.getMater().getLocation());
        mActualQuantityEditText.setText(mPurchaseItem.getMater().getQuantity() + "");
        mActualQuantityEditText.addTextChangedListener(mTotalTextWatcher);
        updateReceiptTotalWeight();
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

    /**
     * 处理关闭物料收货操作，进行联网
     *
     * @param shdhm
     * @param shdhh
     */
    private void handlerCloseMater(String shdhm, String shdhh) {
        if (!PurchaseStorageSecondNoBranchedFragment.this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("receipt_number", shdhm);
        param.put("receipt_line", shdhh);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_RECEIPT_CLOSE, getContext()), param, REQUEST_CODE_RECEIPT_CLOSE, mRequestTaskListener);
    }

    private void handlerSureMater(String shdhm, String shdhh, double actualSingle, double actualQuantity, String location, String branchListJson, boolean update) {
        if (!PurchaseStorageSecondNoBranchedFragment.this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("receipt_number", shdhm);
        param.put("receipt_line", shdhh);
        param.put("actual_single", actualSingle + "");
        param.put("actual_single_update", update ? "1" : "0");
        param.put("actual_quantity", actualQuantity + "");
        param.put("location", location);
        param.put("branch_list", branchListJson);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_RECEIPT_STORAGE_CONFIRM, getContext()), param, REQUEST_CODE_RECEIPT_CONFIRM, mRequestTaskListener);
    }

    @Override
    protected void handleScanCode(String message) {
        String code = CommonTools.decodeScanString(PropertiesUtil.getInstance(getContext()).getValue(PropertiesUtil.BARCODE_PREFIX_LOCATION, ""), message);
        if (TextUtils.isEmpty(code)) {
            ((BaseActivity) getActivity()).ShowToast("扫描失败,无效库位标签");
        } else {
            mLocationEditText.setText(code);
            ((BaseActivity) getActivity()).ShowToast("入库库位调整为:" + code);
        }
    }

    public interface SecondFragmentCallBack extends Serializable {
        /**
         * 通知物料收货被关闭
         *
         * @param shdhh 收货单行号
         */
        void itemBeenClosed(String shdhh);

        /**
         * 通知物料收货被关闭
         *
         * @param shdhh 收货单行号
         */
        void itemBeenSured(String shdhh);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_RECEIPT_CONFIRM:
                    if (mSecondFragmentCallBack != null) {
                        if (bundle.getInt("code") == 1) {
                            Toast.makeText(getContext(), "入库成功", Toast.LENGTH_SHORT).show();
                            mSecondFragmentCallBack.itemBeenSured(mPurchaseItem.getNumber());
                        } else {
                            Toast.makeText(getContext(), "入库失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case REQUEST_CODE_RECEIPT_CLOSE:
                    if (mSecondFragmentCallBack != null) {
                        if (bundle.getInt("code") == 1) {
                            Toast.makeText(getContext(), "关闭成功", Toast.LENGTH_SHORT).show();
                            mSecondFragmentCallBack.itemBeenClosed(mPurchaseItem.getNumber());

                        } else {
                            Toast.makeText(getContext(), "关闭失败", Toast.LENGTH_SHORT).show();
                        }

                    }
                    break;
            }
        }
    }
}
