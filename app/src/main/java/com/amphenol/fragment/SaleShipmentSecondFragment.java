package com.amphenol.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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
import com.amphenol.adapter.SaleShipmentSecondAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Pick;
import com.amphenol.entity.Shipment;
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

public class SaleShipmentSecondFragment extends BaseFragment {
    private static final int REQUEST_CODE_INQUIRE = 0X10;
    private static final int REQUEST_CODE_SUBMIT = 0X11;
    private static final int REQUEST_CODE_CANCEL = 0x12;
    private static final int REQUEST_CODE_FOR_SCAN_BRANCH = 0X13;
    private static final int REQUEST_CODE_FOR_SCAN_LOCATION = 0X14;
    private View rootView;
    private TextView materNumberTextView, mShipmentTextView, mPlanQuantityTextView, mClientTextView, mShipmentQuantityTextView;
    private Spinner mSpinner;
    private EditText mLocationEditText, mBoxNumberEditText, mBoxQuantityEditText;
    private Button mInquireButton, mAddButton, mCancelButton;
    private ImageView mImageView;
    private View.OnClickListener mOnClickListener;
    private ArrayAdapter<String> mStringArrayAdapter;
    private ArrayList<String> mShardStrings = new ArrayList<>();
    private Shipment.ShipmentItem mShipmentItem = new Shipment.ShipmentItem();
    private SaleShipmentSecondAdapter mSaleShipmentSecondAdapter;
    private SaleShipmentSecondAdapter.OnItemClickListener mOnItemClickListener;
    private RecyclerView mRecyclerView;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private SecondFragmentCallBack mSecondFragmentCallBack;
    private MyHandler myHandler = new MyHandler();
    private ActionSheet.ActionSheetListener mActionSheetListener;


    public static SaleShipmentSecondFragment newInstance(Shipment.ShipmentItem shipmentItem, ArrayList<String> shards, SecondFragmentCallBack mSecondFragmentCallBack) {

        Bundle args = new Bundle();
        args.putParcelable("shipmentItem", shipmentItem);
        args.putStringArrayList("shards", shards);
        SaleShipmentSecondFragment fragment = new SaleShipmentSecondFragment();
        fragment.mSecondFragmentCallBack = mSecondFragmentCallBack;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mShipmentItem = args.getParcelable("shipmentItem");
            mShardStrings = args.getStringArrayList("shards");
            mShardStrings.add(0, "ALL");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_sale_shipment_second, container, false);
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
        mSaleShipmentSecondAdapter = new SaleShipmentSecondAdapter(getContext(), mShipmentItem.getShipmentItemBranchItems(), mOnItemClickListener);
    }

    private void initViews() {
        mImageView = (ImageView) rootView.findViewById(R.id.toolbar_menu);
        mImageView.setOnClickListener(mOnClickListener);
        materNumberTextView = (TextView) rootView.findViewById(R.id.fragment_sale_shipment_mater_in_tv);
        mShipmentTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_second_one_mater_in_tv);
        mPlanQuantityTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_second_one_plain_quantity_in_tv);
        mClientTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_second_one_desc_in_tv);
        mShipmentQuantityTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_second_one_hair_quantity_in_tv);
        mSpinner = (Spinner) rootView.findViewById(R.id.fragment_fast_requisition_main_mater_in_et);
        mLocationEditText = (EditText) rootView.findViewById(R.id.fragment_sale_shipment_location_in_tv);
        mBoxNumberEditText = (EditText) rootView.findViewById(R.id.fragment_fast_requisition_main_from_location_et);
        mBoxQuantityEditText = (EditText) rootView.findViewById(R.id.fragment_fast_requisition_main_branch_et);
        mInquireButton = (Button) rootView.findViewById(R.id.fragment_fast_requisition_main_inquire_bt);
        mInquireButton.setOnClickListener(mOnClickListener);
        mAddButton = (Button) rootView.findViewById(R.id.fragment_fast_requisition_main_submit_bt);
        mAddButton.setOnClickListener(mOnClickListener);
        mCancelButton = (Button) rootView.findViewById(R.id.fragment_fast_requisition_main_cancel_bt);
        mCancelButton.setOnClickListener(mOnClickListener);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_purchase_receipt_content_rl);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mSaleShipmentSecondAdapter);
        mSpinner.setAdapter(mStringArrayAdapter);
    }

    private void refreshShow() {
        mBoxNumberEditText.setText(mShipmentItem.getBoxNumber());
        mBoxQuantityEditText.setText(mShipmentItem.getBoxQuantity() + "");
        materNumberTextView.setText(mShipmentItem.getMater().getNumber());
        mShipmentTextView.setText(mShipmentItem.getShipment().getNumber() + "-" + mShipmentItem.getPldln());
        mPlanQuantityTextView.setText(mShipmentItem.getQuantity() + "");
        mClientTextView.setText(mShipmentItem.getC6cvnb() + "-" + mShipmentItem.getCdfcnb());

        try {
            int position = mStringArrayAdapter.getPosition(mShipmentItem.getMater().getShard());
            mSpinner.setSelection(position);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        mLocationEditText.setText(mShipmentItem.getMater().getLocation());
//        functionFIFO();
        mSaleShipmentSecondAdapter.setDate(mShipmentItem.getShipmentItemBranchItems());
        mSaleShipmentSecondAdapter.notifyDataSetChanged();
        functionCalculateHairQuantity();
        if (mShipmentItem.getShipmentItemBranchItems().size() == 0) {
            mInquireButton.setTag(false);
            mInquireButton.setText("查询");
            mLocationEditText.getText().clear();
            mLocationEditText.getText().clear();
        } else {
            mInquireButton.setText("清除");
            mInquireButton.setTag(true);
        }
    }

    /**
     * 先入先出勾选
     */
    private synchronized void functionFIFO() {
        for (Shipment.ShipmentItem.ShipmentItemBranchItem shipmentItemBranchItem : mShipmentItem.getShipmentItemBranchItems()) {
            if (mShipmentItem.getShipmentQuantity() < mShipmentItem.getQuantity()) {//出货数量小于计划数量,
                if (mShipmentItem.getShipmentQuantity() + shipmentItemBranchItem.getBranch().getQuantity() > mShipmentItem.getQuantity()) {//加上这个物料后,出货数量大于计划数量
                    shipmentItemBranchItem.setQuantity(mShipmentItem.getQuantity() - mShipmentItem.getShipmentQuantity());
                } else {
                    shipmentItemBranchItem.setQuantity(shipmentItemBranchItem.getBranch().getQuantity());
                }
                mShipmentItem.setShipmentQuantity(mShipmentItem.getShipmentQuantity() + shipmentItemBranchItem.getQuantity());
                shipmentItemBranchItem.setChecked(true);
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
                            mShipmentItem.setShipmentQuantity(0);
                            mShipmentItem.setShipmentItemBranchItems(new ArrayList<Shipment.ShipmentItem.ShipmentItemBranchItem>());
                            refreshShow();
                        } else {
                            handleInquireMater(mShipmentItem.getShipment().getWarehouse(), mShipmentItem.getShipment().getNumber(), mShipmentItem.getPldln(),
                                    mShipmentItem.getMater().getNumber(), mSpinner.getSelectedItemPosition() == 0 ? "" : mStringArrayAdapter.getItem(mSpinner.getSelectedItemPosition()),
                                    mLocationEditText.getText().toString().trim(), mShipmentItem.getQuantity(), mShipmentItem.getC6cvnb(), mShipmentItem.getCdfcnb());
                        }
                        break;
                    case R.id.fragment_fast_requisition_main_submit_bt:
                        if (mShipmentItem.getShipmentQuantity() <= 0) {
                            ((BaseActivity) getActivity()).ShowToast("出货数量非法");
                            return;
                        }

                        for (Shipment.ShipmentItem.ShipmentItemBranchItem shipmentItemBranchItem : mShipmentItem.getShipmentItemBranchItems()) {
                            if (shipmentItemBranchItem.isChecked() && shipmentItemBranchItem.getQuantity() > shipmentItemBranchItem.getBranch().getQuantity()) {
                                ((BaseActivity) getActivity()).ShowToast("存在出货数量大于库存数量的项目,检查后重试");
                                return;
                            }
                        }

                        if (mShipmentItem.getShipmentQuantity() > mShipmentItem.getQuantity()) {
                            ((BaseActivity) getActivity()).ShowToast("出货数量不能大于计划数量");
                            return;
                        }

                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                        builder2.setTitle("确认出货").setMessage("将要进行确认出货?");
                        builder2.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String mater_list = "";
                                try {
                                    JSONArray jsonArray = new JSONArray();
                                    for (Shipment.ShipmentItem.ShipmentItemBranchItem shipmentItemBranchItem : mShipmentItem.getShipmentItemBranchItems()) {
                                        if (shipmentItemBranchItem.isChecked()) {
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("mater", shipmentItemBranchItem.getBranch().getMater().getNumber());
                                            jsonObject.put("branch", shipmentItemBranchItem.getBranch().getPo());
                                            jsonObject.put("location", shipmentItemBranchItem.getBranch().getMater().getLocation());
                                            jsonObject.put("quantity", shipmentItemBranchItem.getQuantity());
                                            jsonObject.put("shard", shipmentItemBranchItem.getBranch().getMater().getShard());
                                            jsonArray.put(jsonObject);
                                        }
                                    }
                                    mater_list = new JSONObject().put("mater_list", jsonArray).toString();
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                                handleEnsure(mShipmentItem.getShipment().getWarehouse(), mShipmentItem.getShipment().getNumber(),
                                        mShipmentItem.getPldln(), mShipmentItem.getShipmentQuantity(), mShipmentItem.getBoxln(), mShipmentItem.getBoxNumber(), mShipmentItem.getBoxQuantity(),
                                        mater_list);
                            }
                        });
                        builder2.create().show();

                        break;
                    case R.id.fragment_fast_requisition_main_cancel_bt:
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("终止出货").setMessage("将要进行终止出货?");
                        builder.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handlerCancel(mShipmentItem.getShipment().getWarehouse(), mShipmentItem.getShipment().getNumber(), mShipmentItem.getPldln());
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
                }
            }
        };

        mOnItemClickListener = new SaleShipmentSecondAdapter.OnItemClickListener() {
            @Override
            public void OnItemCheckedChanged(int position, boolean isChecked) {
                mShipmentItem.getShipmentItemBranchItems().get(position).setChecked(isChecked);
                functionCalculateHairQuantity();
            }

            @Override
            public void OnRequisitionQuantityChanged(int position, double quantity, double quantityBefore) {
                if (quantity > mShipmentItem.getShipmentItemBranchItems().get(position).getBranch().getQuantity()) {
                    ((BaseActivity) getActivity()).ShowToast("出货数量不能大于库存数量");
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
                            DecodeManager.decodeSaleShipmentGetDetail(jsonObject, requestCode, myHandler);
                            break;
                        case REQUEST_CODE_SUBMIT:
                            DecodeManager.decodeSaleShipmentEnsure(jsonObject, requestCode, myHandler);
                            break;
                        case REQUEST_CODE_CANCEL:
                            DecodeManager.decodeSaleShipmentCancel(jsonObject, requestCode, myHandler);
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

    private void handlerCancel(String warehouse, String pldno, String pldln) {
        if (!SaleShipmentSecondFragment.this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("warehouse", warehouse);
        param.put("pldno", pldno);
        param.put("pldln", pldln);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_SALE_SHIPMENT_CANCEL, getContext()), param, REQUEST_CODE_CANCEL, mRequestTaskListener);
    }

    private void handleEnsure(String warehouse, String pldno, String pldln, double actual_quantity, String boxln, String boxnm, double boxes, String materList) {
        if (!SaleShipmentSecondFragment.this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("warehouse", warehouse);
        param.put("pldno", pldno);
        param.put("pldln", pldln);
        param.put("actual_quantity", actual_quantity + "");
        param.put("mater_list", materList);
        param.put("boxln", boxln);
        param.put("boxnm", boxnm);
        param.put("boxes", boxes + "");

        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_SALE_SHIPMENT_ENSURE, getContext()), param, REQUEST_CODE_SUBMIT, mRequestTaskListener);
    }

    /**
     * 计算当前已勾选的物料发料数量之和,并反馈至"发料数量"显示
     */
    private synchronized void functionCalculateHairQuantity() {
        mShipmentItem.setShipmentQuantity(0);
        for (Shipment.ShipmentItem.ShipmentItemBranchItem shipmentItemBranchItem : mShipmentItem.getShipmentItemBranchItems()) {
            if (shipmentItemBranchItem.isChecked()) {
                mShipmentItem.setShipmentQuantity(mShipmentItem.getShipmentQuantity() + shipmentItemBranchItem.getQuantity());
            }
        }
        mShipmentQuantityTextView.setText(mShipmentItem.getShipmentQuantity() + "");
    }

    private void handleInquireMater(String warehouse, String pldno, String pldln, String mater, String shard, String location, double plan_quantity, String c6cvnb, String cdfcnb) {
        if (!SaleShipmentSecondFragment.this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("warehouse", warehouse);
        param.put("pldno", pldno);
        param.put("pldln", pldln);
        param.put("mater", mater);
        param.put("shard", shard);
        param.put("location", location);
        param.put("plan_quantity", String.valueOf(plan_quantity));
        param.put("c6cvnb", c6cvnb);
        param.put("cdfcnb", cdfcnb);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_SALE_SHIPMENT_QUERY_ITEM, getContext()), param, REQUEST_CODE_INQUIRE, mRequestTaskListener);
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

    @Override
    protected void handleScanCode(String code) {
        if (TextUtils.isEmpty(code))
            return;
        if (!SaleShipmentSecondFragment.this.isVisible())
            return;

        String materNumber = CommonTools.decodeScanString(PropertiesUtil.getInstance(getContext()).getValue(PropertiesUtil.BARCODE_PREFIX_MATER, ""), code);
        String location = CommonTools.decodeScanString(PropertiesUtil.getInstance(getContext()).getValue(PropertiesUtil.BARCODE_PREFIX_LOCATION, ""), code);
        String branch = CommonTools.decodeScanString(PropertiesUtil.getInstance(getContext()).getValue(PropertiesUtil.BARCODE_PREFIX_BRANCH, ""), code);

        if(mShipmentItem.getShipmentItemBranchItems().size()==0){
            if(!TextUtils.isEmpty(location)){
                mLocationEditText.setText(location);
            }
            return;
        }

        if (TextUtils.isEmpty(materNumber) || !TextUtils.equals(materNumber, mShipmentItem.getMater().getNumber())) {
            ((BaseActivity) getActivity()).ShowToast("扫描失败,不是正确的物料标签卡");
            return;
        }

        for (int i = 0; i < mShipmentItem.getShipmentItemBranchItems().size(); i++) {
            Shipment.ShipmentItem.ShipmentItemBranchItem item = mShipmentItem.getShipmentItemBranchItems().get(i);
            if (item.getBranch().getPo().equals(branch) && item.getBranch().getMater().getLocation().equals(location)) {
                if (!item.isChecked()) {
                    if (mShipmentItem.getShipmentQuantity() + item.getBranch().getQuantity() > mShipmentItem.getQuantity()) {//加上这个物料后,发料数量大于计划数量
                        item.setQuantity(mShipmentItem.getQuantity() - mShipmentItem.getShipmentQuantity());
                    } else {
                        item.setQuantity(item.getBranch().getQuantity());
                    }
                    item.setChecked(true);
                    mShipmentItem.getShipmentItemBranchItems().add(0,mShipmentItem.getShipmentItemBranchItems().remove(i));
                    mSaleShipmentSecondAdapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(0);
                    functionCalculateHairQuantity();
                } else {
                    mRecyclerView.scrollToPosition(i);
                }

                ((BaseActivity) getActivity()).ShowToast("扫描成功并勾选");
                return;
            }
        }
        ((BaseActivity) getActivity()).ShowToast("扫描失败,该物料不在可选列表中");
    }

    public interface SecondFragmentCallBack extends Serializable {
        /**
         * @param pldln
         */
        void itemBeenClosed(String pldln);

        /**
         * @param pldln
         */

        void itemBeenSured(String pldln);
    }


    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_INQUIRE:
                    if (bundle.getInt("code") == 1) {
                        mShipmentItem = bundle.getParcelable("shipmentItem");
                        refreshShow();

                        if (mShipmentItem.getShipmentItemBranchItems().size() == 0)
                            ((BaseActivity) getActivity()).ShowToast("查无物料库存信息");
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("获取物料明细失败");
                    }
                    break;
                case REQUEST_CODE_SUBMIT:
                    if (mSecondFragmentCallBack != null) {
                        if (bundle.getInt("code") == 1) {
                            Toast.makeText(getContext(), "出货成功", Toast.LENGTH_SHORT).show();
                            mSecondFragmentCallBack.itemBeenSured(mShipmentItem.getPldln());
                        } else if (bundle.getInt("code") == 5) {
                            Toast.makeText(getContext(), "出货失败,目标子库和库位不匹配", Toast.LENGTH_SHORT).show();
                        } else if (bundle.getInt("code") == 6) {
                            Toast.makeText(getContext(), "出货失败,库存数量不足", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "出货失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case REQUEST_CODE_CANCEL:
                    if (mSecondFragmentCallBack != null) {
                        if (bundle.getInt("code") == 1) {
                            Toast.makeText(getContext(), "终止出货成功", Toast.LENGTH_SHORT).show();
                            mSecondFragmentCallBack.itemBeenClosed(mShipmentItem.getPldln());
                        } else {
                            Toast.makeText(getContext(), "终止出货失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    }
}