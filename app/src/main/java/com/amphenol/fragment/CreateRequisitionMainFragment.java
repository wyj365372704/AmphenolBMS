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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
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
import com.amphenol.adapter.FirstRequisitionForMaterListAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Requisition;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CreateRequisitionMainFragment extends Fragment {
    private static final int REQUEST_CODE_CREATE_REQUISITION = 0X13;
    private static final int REQUEST_CODE_FOR_SCAN = 0x14;
    private View rootView = null;
    private RecyclerView mRecyclerView;
    private Spinner mSpinner;
    private ImageView mScanImageView;
    private TextView warehouseTextView;
    private Button mInquireButton, mCreateButton;
    private EditText mLocationEditText;
    private TextView.OnEditorActionListener mOnEditorActionListener;
    private View.OnClickListener mOnClickListener;
    private LoadingDialog mLoadingDialog;
    private ArrayAdapter<String> mStringArrayAdapter;
    private FirstRequisitionForMaterListAdapter mFirstRequisitionForMaterListAdapter;
    private FirstRequisitionForMaterListAdapter.OnItemClickListener mOnItemClickListener;
    private Requisition requisition = new Requisition();
    private MyHandler myHandler;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private final int REQUEST_CODE_GET_MATER_LIST = 0x11;
    private int currentCheckedItemCount = 0;//当前被勾选上的item个数
    private final int REQUEST_CODE_GET_MATER = 0x12;
    private MainFragmentCallBack mainFragmentCallBack;

    public static CreateRequisitionMainFragment newInstance(MainFragmentCallBack mainFragmentCallBack) {

        Bundle args = new Bundle();
        CreateRequisitionMainFragment fragment = new CreateRequisitionMainFragment();
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
        rootView = inflater.inflate(R.layout.fragment_create_requisition_main, container, false);
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
        myHandler = new MyHandler();
        mStringArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, SessionManager.getShard_list(getContext()));
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        mStringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFirstRequisitionForMaterListAdapter = new FirstRequisitionForMaterListAdapter(getContext(), requisition.getRequisitionItems(), mOnItemClickListener);
    }

    private void initViews() {
        mScanImageView = (ImageView) rootView.findViewById(R.id.fragment_scan_iv);
        mScanImageView.setOnClickListener(mOnClickListener);
        mCreateButton = (Button) rootView.findViewById(R.id.fragment_create_requisition_create_bt);
        mCreateButton.setOnClickListener(mOnClickListener);
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
        mSpinner.setAdapter(mStringArrayAdapter);
    }

    private void initListeners() {
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
                if (currentCheckedItemCount == 1 && isChecked) {//弹出按钮
                    popUpButton();
                }
                if (currentCheckedItemCount == 0) {//收起按钮
                    collapseButton();
                }
            }

            @Override
            public void OnRequisitionQuantityChanged(int position, double quantity) {
                if (quantity > requisition.getRequisitionItems().get(position).getBranch().getQuantity()) {
                    ((BaseActivity) getActivity()).ShowToast("调拨数量不能大于库存数量" + requisition.getRequisitionItems().get(position).getBranch().getQuantity());
                }

            }
        };
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fragment_scan_iv:
                        startActivityForResult(new Intent(getActivity(), ScanActivity.class), REQUEST_CODE_FOR_SCAN);
                        break;
                    case R.id.fragment_purchase_receipt_inquire_bt:
                        boolean state = mInquireButton.getTag() == null ? false : (boolean) mInquireButton.getTag();
                        if (state) {//当前按钮状态为“清除”
                            requisition = new Requisition();
                            refreshShow();
                        } else {
                            handleScanCode(mLocationEditText.getText().toString().trim());
                        }
                        break;
                    case R.id.fragment_create_requisition_create_bt:
                        String materListString = "";
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
                                String from_warehouse = requisitionItem.getBranch().getMater().getWarehouse();
                                String from_shard = requisitionItem.getBranch().getMater().getShard();
                                String from_location = requisitionItem.getBranch().getMater().getLocation();
                                String target_warehouse = requisitionItem.getBranch().getMater().getWarehouse();
                                String target_shard = requisitionItem.getShard();
                                String target_location = requisitionItem.getLocation();
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        materListString = materListJsonObject.toString();
                        handlerCreateRequisition(materListString);
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
                        case REQUEST_CODE_GET_MATER_LIST:
                            DecodeManager.decodeCreaetRequisitionGetMaterList(jsonObject, requestCode, myHandler);
                            break;
                        case REQUEST_CODE_GET_MATER:
                            DecodeManager.decodeCreaetRequisitionGetMater(jsonObject, requestCode, myHandler);
                            break;
                        case REQUEST_CODE_CREATE_REQUISITION:
                            DecodeManager.decodeCreaetRequisitionCommit(jsonObject, requestCode, myHandler);
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

    private void handleInquireMater(String warehouse, String shard, String location, String mate, String branch, double quantity, String unit) {
        if (!CreateRequisitionMainFragment.this.isVisible())
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

    private void collapseButton() {
        if (mCreateButton.getVisibility() == View.GONE)
            return;
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mCreateButton.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mCreateButton.startAnimation(animation);
    }

    private void popUpButton() {
        if (mCreateButton.getVisibility() == View.VISIBLE)
            return;
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
        animation.setDuration(300);
        mCreateButton.setVisibility(View.VISIBLE);
        mCreateButton.startAnimation(animation);
    }

    private void refreshShow() {
        mFirstRequisitionForMaterListAdapter.setDate(requisition.getRequisitionItems());
        mFirstRequisitionForMaterListAdapter.notifyDataSetChanged();

        if (requisition.getRequisitionItems().size() == 0) {
            mInquireButton.setTag(false);
            mInquireButton.setText("查询");
            currentCheckedItemCount = 0;
            collapseButton();
            mLocationEditText.getText().clear();
            mLocationEditText.setHint("输入库位号");
            mLocationEditText.requestFocus();
        } else {
            currentCheckedItemCount = 0;
            mInquireButton.setText("清除");
            mInquireButton.setTag(true);
            mLocationEditText.getText().clear();
            mLocationEditText.setHint("在此扫描物料标签快速选中");
        }
    }

    /**
     * 处理扫描得到的二维码,执行联网查询操作
     */
    private void handleScanCode(String code) {

        if (!CreateRequisitionMainFragment.this.isVisible())
            return;
        if (requisition.getRequisitionItems().size() == 0) {//当前物料列表为空,查询物料列表
            code = CommonTools.decodeScanString(PropertiesUtil.getInstance(getContext()).getValue(PropertiesUtil.BARCODE_PREFIX_LOCATION,""), code);
            mLocationEditText.setText(code);
            Map<String, String> param = new HashMap<>();
            param.put("username", SessionManager.getUserName(getContext()));
            param.put("env", SessionManager.getEnv(getContext()));
            param.put("warehouse", SessionManager.getWarehouse(getContext()));
            if (mStringArrayAdapter.getCount() < 1) {
                ((BaseActivity) getActivity()).ShowToast("库位列表为空,不可创建调拨单");
                return;
            }
            param.put("shard", mStringArrayAdapter.getItem(mSpinner.getSelectedItemPosition()));
            param.put("location", code);
            NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_CREATE_REQUISITION_GET_MATER_LIST, getContext()), param, REQUEST_CODE_GET_MATER_LIST, mRequestTaskListener);
        } else {//当前物料列表不为空,扫描定位物料项
            String mater = CommonTools.decodeScanString(PropertiesUtil.getInstance(getContext()).getValue(PropertiesUtil.BARCODE_PREFIX_MATER,""), code);
            String branch = CommonTools.decodeScanString(PropertiesUtil.getInstance(getContext()).getValue(PropertiesUtil.BARCODE_PREFIX_BRANCH,""), code);
            if (TextUtils.isEmpty(mater)) {
                Toast.makeText(getContext(), "无效物料标签", Toast.LENGTH_SHORT).show();
                return;
            }
            mLocationEditText.setText("");
            int count = 0;
            for (int i = 0; i < requisition.getRequisitionItems().size(); i++) {
                if (TextUtils.equals(requisition.getRequisitionItems().get(i).getBranch().getMater().getNumber(), mater) && TextUtils.equals(requisition.getRequisitionItems().get(i).getBranch().getPo(), branch)) {
                    requisition.getRequisitionItems().get(i).setChecked(true);
                    count++;
                }
            }
            if (count == 0) {
                ((BaseActivity) getActivity()).ShowToast("该物料不在列表中");
            } else {
                mFirstRequisitionForMaterListAdapter.notifyDataSetChanged();
                ((BaseActivity) getActivity()).ShowToast("扫描选中了" + count + "个物料");
            }
        }
    }

    private void handlerCreateRequisition(String materListString) {
        if (!CreateRequisitionMainFragment.this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("mater_list", materListString);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_CREATE_REQUISITION_COMMIT, getContext()), param, REQUEST_CODE_CREATE_REQUISITION, mRequestTaskListener);
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
            mLocationEditText.setText(code);
            handleScanCode(mLocationEditText.getText().toString().trim());
        }

    }

    public interface MainFragmentCallBack extends Serializable {
        void gotoSecondFragment(Requisition.RequisitionItem requisitionItem);
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
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("查询失败");
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
                case REQUEST_CODE_CREATE_REQUISITION:
                    if (bundle.getInt("code") == 1) {
                        String number = bundle.getString("number");
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("调拨单创建成功");
                        builder.setMessage("新创建调拨单号:"+number);
                        builder.setCancelable(true);
                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requisition = new Requisition();
                                refreshShow();
                            }
                        });
                        builder.create().show();

                    } else {
                        ((BaseActivity) getActivity()).ShowToast("调拨单创建失败");
                    }
                    break;
            }
        }
    }
}
