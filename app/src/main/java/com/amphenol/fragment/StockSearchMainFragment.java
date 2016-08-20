package com.amphenol.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.activity.ScanActivity;
import com.amphenol.adapter.FirstRequisitionForMaterListAdapter;
import com.amphenol.adapter.StockSearchAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Mater;
import com.amphenol.entity.Requisition;
import com.amphenol.ui.LoadingDialog;
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

public class StockSearchMainFragment extends Fragment {
    private static final int REQUEST_CODE_GET_MATER_LIST = 0x10;
    private static final int REQUEST_CODE_GET_MATER = 0x12;
    private static final int REQUEST_CODE_FOR_SCAN_MATER = 0x13;
    private static final int REQUEST_CODE_FOR_SCAN_FROM_LOCATION = 15;
    private View rootView = null;
    private TextView wareHouseTextView;
    private EditText materEditText, locationEditText;
    private Spinner shardSpinner;
    private Button mInquireButton;
    private RecyclerView mRecyclerView;
    private View.OnClickListener mOnClickListener;
    private ImageView mImageButton;
    private ArrayAdapter<String> mStringArrayAdapter;
    private ArrayList<Mater.Branch> branches = new ArrayList<>();
    private StockSearchAdapter mStockSearchAdapter;
    private StockSearchAdapter.OnItemClickListener mOnItemClickListener;
    private TextView.OnEditorActionListener mOnEditorActionListener;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private MyHandler myHandler = new MyHandler();
    private MainFragmentCallBack mainFragmentCallBack;
    private ActionSheet.ActionSheetListener mActionSheetListener;

    public static StockSearchMainFragment newInstance(MainFragmentCallBack mainFragmentCallBack) {

        Bundle args = new Bundle();
        StockSearchMainFragment fragment = new StockSearchMainFragment();
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
        rootView = inflater.inflate(R.layout.fragment_stock_search_main, container, false);
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
        mStringArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, SessionManager.getShard_list(getContext()));
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        mStringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStockSearchAdapter = new StockSearchAdapter(getContext(), branches, mOnItemClickListener);
    }

    private void initViews() {
        mImageButton = (ImageView) rootView.findViewById(R.id.toolbar_menu);
        mImageButton.setOnClickListener(mOnClickListener);
        wareHouseTextView = (TextView) rootView.findViewById(R.id.fragment_fast_requisition_main_warehouse_in_tv);
        wareHouseTextView.setText(SessionManager.getWarehouse(getContext()));
        materEditText = (EditText) rootView.findViewById(R.id.fragment_fast_requisition_main_mater_in_et);
        materEditText.setOnEditorActionListener(mOnEditorActionListener);
        locationEditText = (EditText) rootView.findViewById(R.id.fragment_fast_requisition_main_from_shard_et);
        locationEditText.setOnEditorActionListener(mOnEditorActionListener);
        shardSpinner = (Spinner) rootView.findViewById(R.id.fragment_fast_requisition_main_shard_spinner);
        shardSpinner.setAdapter(mStringArrayAdapter);
        mInquireButton = (Button) rootView.findViewById(R.id.fragment_fast_requisition_main_inquire_bt);
        mInquireButton.setOnClickListener(mOnClickListener);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_purchase_receipt_content_rl);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mStockSearchAdapter);
    }

    private void initListeners() {
        mOnEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (v.getId()) {
                    case R.id.fragment_fast_requisition_main_mater_in_et:
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            handleScanMater(v, materEditText.getText().toString());
                            return true;
                        }
                        break;
                    case R.id.fragment_fast_requisition_main_from_shard_et:
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            handleScanFromLocation(v, locationEditText.getText().toString());
                            return true;
                        }
                        break;
                }
                return false;
            }
        };
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fragment_fast_requisition_main_inquire_bt:
                        boolean state = mInquireButton.getTag() == null ? false : (boolean) mInquireButton.getTag();
                        if (state) {//当前按钮状态为“清除”
                            branches.clear();
                            refreshShow();
                        } else {
                            handleInquire(materEditText.getText().toString().trim(), locationEditText.getText().toString().trim());
                        }
                        break;
                    case R.id.toolbar_menu:
                        ActionSheet.createBuilder(getContext(), getFragmentManager())
                                .setCancelButtonTitle("取消")
                                .setOtherButtonTitles("扫描库位标签","扫描物料标签")
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
                        startActivityForResult(new Intent(getActivity(), ScanActivity.class), REQUEST_CODE_FOR_SCAN_FROM_LOCATION);
                        break;
                    case 1:
                        startActivityForResult(new Intent(getActivity(), ScanActivity.class), REQUEST_CODE_FOR_SCAN_MATER);
                        break;
                }
            }
        };
        mOnItemClickListener = new StockSearchAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                handleInquireMater(branches.get(position).getMater().getWarehouse(), branches.get(position).getMater().getShard(), branches.get(position).getMater().getLocation(), branches.get(position).getMater().getNumber(), branches.get(position).getPo(), branches.get(position).getQuantity(), branches.get(position).getMater().getUnit());
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
                            DecodeManager.decodeStockSearchGetMaterList(jsonObject, requestCode, myHandler);
                            break;
                        case REQUEST_CODE_GET_MATER:
                            DecodeManager.decodeStockSearchGetMater(jsonObject, requestCode, myHandler);
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


    private void handleScanFromLocation(TextView v, String code) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
        code = CommonTools.decodeScanString("L", code);
        locationEditText.setText(code);
        materEditText.requestFocus();
    }

    private void handleScanMater(TextView v, String code) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
        String mater = CommonTools.decodeScanString("M", code);
        String branchPo = CommonTools.decodeScanString("B", code);
        boolean state = mInquireButton.getTag() == null ? false : (boolean) mInquireButton.getTag();
        if (state) {//当前按钮状态为“清除” ,扫码选中物料
            int position = 0;
            for (Mater.Branch branch : branches) {
                if (TextUtils.equals(branch.getPo(), branchPo) && TextUtils.equals(branch.getMater().getNumber(), mater)) {
                    handleInquireMater(branches.get(position).getMater().getWarehouse(), branches.get(position).getMater().getShard(), branches.get(position).getMater().getLocation(), branches.get(position).getMater().getNumber(), branches.get(position).getPo(), branches.get(position).getQuantity(), branches.get(position).getMater().getUnit());
                    mStockSearchAdapter.notifyItemChanged(position);
                    break;
                }
                position++;
            }
            if (position == branches.size()) {
                ((BaseActivity) getActivity()).ShowToast("该物料不在列表中");
            }
            materEditText.getText().clear();
        } else {
            materEditText.setText(mater);
            locationEditText.requestFocus();
        }
    }

    private void refreshShow() {
        materEditText.requestFocus();
        mStockSearchAdapter.notifyDataSetChanged();

        if (branches.size() == 0) {
            mInquireButton.setTag(false);
            mInquireButton.setText("查询");
            materEditText.getText().clear();
            locationEditText.getText().clear();
            materEditText.setHint("输入物料编号");
            materEditText.requestFocus();
        } else {
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
        if (mStringArrayAdapter.getCount() == 0) {
            ((BaseActivity) getActivity()).ShowToast("子库列表为空,不可查询");
            return;
        }
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("warehouse", SessionManager.getWarehouse(getContext()));
        param.put("location", location);
        param.put("mate", mater);
        param.put("shard",mStringArrayAdapter.getItem(shardSpinner.getSelectedItemPosition()));
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_CREATE_REQUISITION_GET_MATER_LIST, getContext()), param, REQUEST_CODE_GET_MATER_LIST, mRequestTaskListener);
    }

    private void handleInquireMater(String warehouse, String shard, String location, String mate, String branch, double quantity, String unit) {
        if (!StockSearchMainFragment.this.isVisible())
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_SCAN_MATER && resultCode == Activity.RESULT_OK) {
            String code = data.getStringExtra("data").trim();
            handleScanMater(materEditText, code);
        }
        if (requestCode == REQUEST_CODE_FOR_SCAN_FROM_LOCATION && resultCode == Activity.RESULT_OK) {
            String code = data.getStringExtra("data").trim();
            handleScanFromLocation(locationEditText, code);
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_GET_MATER_LIST:
                    if (bundle.getInt("code") == 1) {
                        branches.clear();
                        branches.addAll(bundle.<Mater.Branch>getParcelableArrayList("branches"));
                        refreshShow();
                    } else if (bundle.getInt("code") == 5) {
                        ((BaseActivity) getActivity()).ShowToast("查无结果");
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("查询失败");
                    }
                    break;
                case REQUEST_CODE_GET_MATER:
                    if (bundle.getInt("code") == 1) {
                        Mater.Branch branch = (Mater.Branch) bundle.get("branch");
                        if (mainFragmentCallBack != null) {
                            mainFragmentCallBack.gotoSecondFragment(branch);
                        }
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("获取物料明细失败");
                    }
                    break;
            }
        }
    }

    public interface MainFragmentCallBack extends Serializable {
        void gotoSecondFragment(Mater.Branch branch);
    }
}
