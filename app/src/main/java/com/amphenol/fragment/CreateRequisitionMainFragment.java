package com.amphenol.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.adapter.FirstRequisitionForMaterListAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Requisition;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateRequisitionMainFragment extends Fragment {
    private View rootView = null;
    private RecyclerView mRecyclerView;
    private Spinner mSpinner;
    private TextView warehouseTextView;
    private Button mInquireButton;
    private EditText mLocationEditText;
    private TextView.OnEditorActionListener mOnEditorActionListener;
    private View.OnClickListener mOnClickListener;
    private LoadingDialog mLoadingDialog;
    private ArrayAdapter<String> mStringArrayAdapter;
    private List<String> shardStrings;
    private FirstRequisitionForMaterListAdapter mFirstRequisitionForMaterListAdapter;
    private FirstRequisitionForMaterListAdapter.OnItemClickListener mOnItemClickListener;
    private Requisition requisition = new Requisition();
    private MyHandler myHandler;
    private final int REQUEST_CODE_QUERY_SHARD_LIST = 0X10;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private final int REQUEST_CODE_GET_MATER_LIST = 0x11;

    public CreateRequisitionMainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_create_requisition_main, container, false);
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);

        initListeners();
        initData();
        initViews();
        InquireShards();
        return rootView;
    }

    private void initData() {
        myHandler = new MyHandler();
        shardStrings = new ArrayList<>();
        mStringArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, shardStrings);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        mStringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFirstRequisitionForMaterListAdapter = new FirstRequisitionForMaterListAdapter(getContext(), requisition.getRequisitionItems(), mOnItemClickListener);
    }

    private void initViews() {
        mInquireButton = (Button) rootView.findViewById(R.id.fragment_purchase_receipt_inquire_bt);
        mInquireButton.setOnClickListener(mOnClickListener);
        mLocationEditText = (EditText) rootView.findViewById(R.id.purchase_receipt_main_code_et);
        mLocationEditText.setOnEditorActionListener(mOnEditorActionListener);
        warehouseTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_main_warehouse_show_tv);
        warehouseTextView.setText(SessionManager.getWarehouse(getContext()));
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_purchase_receipt_content_rl);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mFirstRequisitionForMaterListAdapter);
        mSpinner = (Spinner) rootView.findViewById(R.id.fragment_create_requisition_main_shard_spinner);
    }

    private void initListeners() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fragment_purchase_receipt_inquire_bt:
                        boolean state = mInquireButton.getTag() == null ? false : (boolean) mInquireButton.getTag();
                        if (state) {//当前按钮状态为“清除”
                            requisition = new Requisition();

                        } else {
                            handleScanCode(mLocationEditText.getText().toString().trim());
                        }
                        break;
                }
            }
        };

        mOnEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                    handleScanCode(mLocationEditText.getText().toString().trim());
                    return true;
                }
                return false;
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
                        case REQUEST_CODE_QUERY_SHARD_LIST:
                            DecodeManager.decodeQueryShardList(jsonObject, requestCode, myHandler);
                            break;
                        case REQUEST_CODE_GET_MATER_LIST:
                            DecodeManager.decodeCreaetRequisitionGetMaterList(jsonObject, requestCode, myHandler);
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


    private void InquireShards() {
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("warehouse", SessionManager.getWarehouse(getContext()));
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_QUERY_SHARD_LIST, getContext()), param, REQUEST_CODE_QUERY_SHARD_LIST, mRequestTaskListener);
    }

    /**
     * 处理扫描得到的二维码,执行联网查询操作
     */
    private void handleScanCode(String code) {

        if (TextUtils.isEmpty(code))
            return;
        if (!CreateRequisitionMainFragment.this.isVisible())
            return;
        if (requisition.getRequisitionItems().size() == 0) {//当前物料列表为空,查询物料列表
            code = CommonTools.decodeScanString("L", code);
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(getContext(), "无效查询", Toast.LENGTH_SHORT).show();
                return;
            }
            mLocationEditText.setText(code);
            Map<String, String> param = new HashMap<>();
            param.put("username", SessionManager.getUserName(getContext()));
            param.put("env", SessionManager.getEnv(getContext()));
            param.put("warehouse", SessionManager.getWarehouse(getContext()));
            param.put("shard", mStringArrayAdapter.getItem(mSpinner.getSelectedItemPosition()));
            param.put("location", code);
            NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_CREATE_REQUISITION_GET_MATER_LIST, getContext()), param, REQUEST_CODE_GET_MATER_LIST, mRequestTaskListener);
        } else {//当前物料列表不为空,扫描定位物料项
            code = CommonTools.decodeScanString("B", code);
            if (code == null) return;
            mLocationEditText.setText("");

//            for (int i = 0; i < purchase.getPurchaseItems().size(); i++) {
//                if (TextUtils.equals(purchase.getPurchaseItems().get(i).getMater().getNumber(), code)) {
//                    handleInquireMater(purchase.getNumber(), purchase.getPurchaseItems().get(i).getNumber());
//                    break;
//                }else{
//                    Toast.makeText(getContext(),"无效物料标签",Toast.LENGTH_SHORT).show();
//                }
//            }
        }
    }


    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_QUERY_SHARD_LIST:
                    if (bundle.getInt("code") == 1) {
                        shardStrings.clear();
                        shardStrings.addAll(bundle.getStringArrayList("shardList"));
                        mStringArrayAdapter.notifyDataSetChanged();
                        mSpinner.setAdapter(mStringArrayAdapter);
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("获取子库列表失败");
                    }
                    break;
                case REQUEST_CODE_GET_MATER_LIST:
                    if (bundle.getInt("code") == 1) {
                        requisition.getRequisitionItems().clear();
                        requisition.getRequisitionItems().addAll((ArrayList<Requisition.RequisitionItem>) bundle.getSerializable("requisitionItems"));
                        mFirstRequisitionForMaterListAdapter.notifyDataSetChanged();
                    } else {

                    }
                    break;
            }
        }
    }
}
