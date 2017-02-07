package com.amphenol.fragment;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.activity.ScanActivity;
import com.amphenol.adapter.SaleShipmentMainAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Shipment;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SaleShipmentMainFragment extends BaseFragment {
    private static final int REQUEST_CODE_GET_SHIPMENT_LIST = 0x10;
    private static final int REQUEST_CODE_GET_DETAIL = 0x11;
    private static final int REQUEST_CODE_FOR_SCAN = 0x12;
    private static final int REQUEST_CODE_COMMIT = 0x13;
    private View rootView = null;
    private ImageView mScanImageView;
    private RecyclerView mRecyclerView;
    private TextView mWareHouseTextView, mPLdnoTextView, mClientTextView, mDepartmentTextView, mExpectedDateTextView;
    private Button mInquireButton, mCommitButton;
    private EditText mPldnoEditText;
    private View.OnClickListener mOnClickListener;
    private LoadingDialog mLoadingDialog;
    private SaleShipmentMainAdapter mSaleShipmentMainAdapter;
    private SaleShipmentMainAdapter.OnItemClickListener mOnItemClickListener;
    private Shipment shipment = new Shipment();
    private MyHandler myHandler;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private MainFragmentCallBack mainFragmentCallBack;


    public static SaleShipmentMainFragment newInstance(MainFragmentCallBack mainFragmentCallBack) {
        Bundle args = new Bundle();
        SaleShipmentMainFragment fragment = new SaleShipmentMainFragment();
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
        rootView = inflater.inflate(R.layout.fragment_sale_shipment_main, container, false);
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        initListeners();
        initData();
        initViews();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPldnoEditText.requestFocus();
    }

    private void initData() {
        myHandler = new MyHandler();
        mSaleShipmentMainAdapter = new SaleShipmentMainAdapter(getContext(), shipment.getShipmentItems(), mOnItemClickListener);
    }

    private void initViews() {
        mScanImageView = (ImageView) rootView.findViewById(R.id.fragment_scan_iv);
        mScanImageView.setOnClickListener(mOnClickListener);
        mPldnoEditText = (EditText) rootView.findViewById(R.id.purchase_receipt_main_code_et);
        mInquireButton = (Button) rootView.findViewById(R.id.fragment_purchase_receipt_inquire_bt);
        mInquireButton.setOnClickListener(mOnClickListener);
        mCommitButton = (Button) rootView.findViewById(R.id.fragment_sale_shipment_commit_bt);
        mCommitButton.setOnClickListener(mOnClickListener);
        mWareHouseTextView = (TextView) rootView.findViewById(R.id.fragment_check_requisition_main_requisition_tv);
        mPLdnoTextView = (TextView) rootView.findViewById(R.id.fragment_check_requisition_main_state_tv);
        mClientTextView = (TextView) rootView.findViewById(R.id.fragment_check_requisition_main_creater_tv);
        mDepartmentTextView = (TextView) rootView.findViewById(R.id.fragment_check_requisition_main_department_tv);
        mExpectedDateTextView = (TextView) rootView.findViewById(R.id.fragment_check_requisition_main_create_date_tv);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_purchase_receipt_content_rl);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mSaleShipmentMainAdapter);
    }

    private void initListeners() {
        mOnItemClickListener = new SaleShipmentMainAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                Shipment.ShipmentItem shipmentItem = shipment.getShipmentItems().get(position);
                handleInquireMater(shipment.getWarehouse(), shipment.getNumber(), shipmentItem.getPldln(),
                        shipmentItem.getMater().getNumber(), shipmentItem.getMater().getShard(), shipmentItem.getMater().getLocation(),shipmentItem.getQuantity(),shipmentItem.getC6cvnb(),shipmentItem.getCdfcnb());
            }
        };
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fragment_purchase_receipt_inquire_bt:
                        boolean state = mInquireButton.getTag() == null ? false : (boolean) mInquireButton.getTag();
                        if (state) {//当前按钮状态为“清除”
                            shipment = new Shipment();
                            refreshShow();
                        } else {
                            handleScanCode(mPldnoEditText.getText().toString().trim());
                        }
                        break;
                    case R.id.fragment_scan_iv:
                        startActivityForResult(new Intent(getActivity(), ScanActivity.class), REQUEST_CODE_FOR_SCAN);
                        break;
                    case R.id.fragment_sale_shipment_commit_bt:
                        if(shipment.getNumber().isEmpty()){
                            break;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("出货过账").setMessage("将要进行出货过账?");
                        builder.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handleCommit(shipment.getWarehouse(), shipment.getNumber());
                            }
                        });
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
                        case REQUEST_CODE_GET_SHIPMENT_LIST:
                            DecodeManager.decodeGetShipmentList(jsonObject, requestCode, myHandler);
                            break;
                        case REQUEST_CODE_GET_DETAIL:
                            DecodeManager.decodeSaleShipmentGetDetail(jsonObject, requestCode, myHandler);
                            break;
                        case REQUEST_CODE_COMMIT:
                            DecodeManager.decodeSaleShipmentCommit(jsonObject, requestCode, myHandler);
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

    private void handleInquireMater(String warehouse, String pldno, String pldln, String mater, String shard, String location,double plan_quantity,String c6cvnb,String cdfcnb) {
        if (!SaleShipmentMainFragment.this.isVisible())
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
        param.put("plan_quantity",String.valueOf(plan_quantity));
        param.put("c6cvnb", c6cvnb);
        param.put("cdfcnb", cdfcnb);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_SALE_SHIPMENT_QUERY_ITEM, getContext()), param, REQUEST_CODE_GET_DETAIL, mRequestTaskListener);
    }


    private void refreshShow() {
        mSaleShipmentMainAdapter.setDate(shipment.getShipmentItems());
        mSaleShipmentMainAdapter.notifyDataSetChanged();
        mWareHouseTextView.setText(shipment.getWarehouse());
        mPLdnoTextView.setText(shipment.getNumber());
        mClientTextView.setText(shipment.getClientNumber() + " " + shipment.getClientName());
        mDepartmentTextView.setText(shipment.getDepartment());
        mExpectedDateTextView.setText(shipment.getExpectedDate());
        if (TextUtils.isEmpty(shipment.getNumber())) {
            mInquireButton.setTag(false);
            mInquireButton.setText("查询");
            mPldnoEditText.getText().clear();
            mPldnoEditText.setHint("输入出货通知单号");
        } else {
            mInquireButton.setText("清除");
            mInquireButton.setTag(true);
            mPldnoEditText.getText().clear();
            mPldnoEditText.setHint("");
        }
    }

    /**
     * 移除一个项
     *
     * @param pldln 出货通知单明细
     */
    public void refreshShow(String pldln) {
        for (int i = 0; i < shipment.getShipmentItems().size(); i++) {
            if (TextUtils.equals(shipment.getShipmentItems().get(i).getPldln(), pldln)) {
                shipment.getShipmentItems().remove(i);
                mSaleShipmentMainAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    protected void handleScanCode(String code) {
        if (TextUtils.isEmpty(code))
            return;
        if (!SaleShipmentMainFragment.this.isVisible())
            return;

        if (TextUtils.isEmpty(shipment.getNumber())) {//查询物料列表
            code = CommonTools.decodeScanString(PropertiesUtil.getInstance(getContext()).getValue(PropertiesUtil.BARCODE_PREFIX_SALE_SHIPMENT, ""), code);
            mPldnoEditText.setText(code);
            Map<String, String> param = new HashMap<>();
            param.put("username", SessionManager.getUserName(getContext()));
            param.put("env", SessionManager.getEnv(getContext()));
            param.put("warehouse", SessionManager.getWarehouse(getContext()));
            param.put("pldno", code);
            NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_SALE_SHIPMENT_QUERY_HEADER, getContext()), param, REQUEST_CODE_GET_SHIPMENT_LIST, mRequestTaskListener);
        } else {//扫描定位物料项
            /*mPickNumberEditText.setText("");
            code = CommonTools.decodeScanString(PropertiesUtil.getInstance(getContext()).getValue(PropertiesUtil.BARCODE_PREFIX_MATER,""), code);
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(getContext(), "无效物料标签", Toast.LENGTH_SHORT).show();
                return;
            }

            for (int position = 0; position < pick.getPickItems().size(); position++) {
                if (TextUtils.equals(pick.getPickItems().get(position).getBranch().getMater().getNumber(), code)) {
                    handleInquireMater(pick.getPickItems().get(position).getBranch().getMater().getWarehouse(),pick.getNumber(),pick.getPickItems().get(position).getPickLine(),pick.getPickItems().get(position).getBranch().getMater().getNumber(),pick.getPickItems().get(position).getBranch().getMater().getUnit(),pick.getPickItems().get(position).getBranch().getMater().getShard(),pick.getPickItems().get(position).getBranch().getMater().getLocation(),pick.getPickItems().get(position).getBranch().getPo(),pick.getPickItems().get(position).getQuantity(),pick.getDepartment(),pick.getWorkOrder(),pick.getPickItems().get(position).getSequence(),String.valueOf(pick.getType()),pick.getPickItems().get(position).getBranched()+"");
                    return;
                }
            }
            Toast.makeText(getContext(), "该物料不在列表中", Toast.LENGTH_SHORT).show();*/
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_SCAN && resultCode == Activity.RESULT_OK) {
            String code = data.getStringExtra("data").trim();
            handleScanCode(code);
        }
    }
    private void handleCommit(String warehouse, String pldno) {
        if (!SaleShipmentMainFragment.this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("warehouse", warehouse);
        param.put("pldno", pldno);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_SALE_SHIPMENT_CANCEL, getContext()), param, REQUEST_CODE_COMMIT, mRequestTaskListener);
    }
    public interface MainFragmentCallBack extends Serializable {
        void gotoSecondFragment(Shipment.ShipmentItem shipmentItem, ArrayList<String> shards);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_GET_SHIPMENT_LIST:
                    if (bundle.getInt("code") == 1) {
                        shipment = bundle.getParcelable("shipment");
                        refreshShow();
                    } else if (bundle.getInt("code") == 5) {
                        ((BaseActivity) getActivity()).ShowToast("无效出货通知单");
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("查询失败");
                    }
                    break;
                case REQUEST_CODE_GET_DETAIL:
                    if (bundle.getInt("code") == 1) {
                        Shipment.ShipmentItem shipmentItem = bundle.getParcelable("shipmentItem");
                        ArrayList<String> shards = bundle.getStringArrayList("shards");
                        if (mainFragmentCallBack != null) {
                            mainFragmentCallBack.gotoSecondFragment(shipmentItem, shards);
                        }
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("获取明细失败");
                    }
                    break;
                case REQUEST_CODE_COMMIT:

                    break;
            }
        }
    }

}
