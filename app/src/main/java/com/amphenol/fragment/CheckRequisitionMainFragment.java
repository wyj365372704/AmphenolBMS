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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.adapter.CheckRequisitionAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Requisition;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckRequisitionMainFragment extends Fragment {
    private static final int REQUEST_CODE_GET_MATER_LIST = 0x10;
    private static final int REQUEST_CODE_GET_MATER = 0x11;
    private View rootView = null;
    private RecyclerView mRecyclerView;
    private TextView mRequisitionTextView, mStateTextView, mCreaterTextView, mDepartmentTextView, mCreateDateTextView;
    private Button mInquireButton;
    private EditText mRequisitionEditText;
    private TextView.OnEditorActionListener mOnEditorActionListener;
    private View.OnClickListener mOnClickListener;
    private LoadingDialog mLoadingDialog;
    private CheckRequisitionAdapter mCheckRequisitionAdapter;
    private CheckRequisitionAdapter.OnItemClickListener mOnItemClickListener;
    private Requisition requisition = new Requisition();
    private MyHandler myHandler;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private MainFragmentCallBack mainFragmentCallBack;

    public CheckRequisitionMainFragment(MainFragmentCallBack mainFragmentCallBack) {
        this.mainFragmentCallBack = mainFragmentCallBack;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_check_requisition_main, container, false);
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        initListeners();
        initData();
        initViews();
//        refreshShow();
        return rootView;
    }

    private void initData() {
        myHandler = new MyHandler();
        mCheckRequisitionAdapter = new CheckRequisitionAdapter(getContext(), requisition.getRequisitionItems(), mOnItemClickListener);
    }

    private void initViews() {
        mRequisitionEditText = (EditText) rootView.findViewById(R.id.purchase_receipt_main_code_et);
        mRequisitionEditText.setOnEditorActionListener(mOnEditorActionListener);
        mInquireButton = (Button) rootView.findViewById(R.id.fragment_purchase_receipt_inquire_bt);
        mInquireButton.setOnClickListener(mOnClickListener);
        mRequisitionTextView = (TextView) rootView.findViewById(R.id.fragment_check_requisition_main_requisition_tv);
        mStateTextView = (TextView) rootView.findViewById(R.id.fragment_check_requisition_main_state_tv);
        mCreaterTextView = (TextView) rootView.findViewById(R.id.fragment_check_requisition_main_creater_tv);
        mDepartmentTextView = (TextView) rootView.findViewById(R.id.fragment_check_requisition_main_department_tv);
        mCreateDateTextView = (TextView) rootView.findViewById(R.id.fragment_check_requisition_main_create_date_tv);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_purchase_receipt_content_rl);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mCheckRequisitionAdapter);
    }

    private void initListeners() {
        mOnItemClickListener = new CheckRequisitionAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                handleInquireMater(requisition.getNumber(), requisition.getRequisitionItems().get(position).getNumber(), requisition.getRequisitionItems().get(position).getBranch().getMater().getNumber(), requisition.getRequisitionItems().get(position).getBranch().getPo(), requisition.getRequisitionItems().get(position).getQuantity(), requisition.getRequisitionItems().get(position).getBranch().getMater().getUnit());
            }
        };
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fragment_purchase_receipt_inquire_bt:
                        boolean state = mInquireButton.getTag() == null ? false : (boolean) mInquireButton.getTag();
                        if (state) {//当前按钮状态为“清除”
                            requisition = new Requisition();
                            refreshShow();
                        } else {
                            handleScanCode(mRequisitionEditText.getText().toString().trim());
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
                    handleScanCode(mRequisitionEditText.getText().toString().trim());
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
                            DecodeManager.decodeCheckRequisitionGetMaterList(jsonObject, requestCode, myHandler);
                            break;
                        case REQUEST_CODE_GET_MATER:
                            DecodeManager.decodeCheckRequisitionGetMater(jsonObject, requestCode, myHandler);
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

    private void handleInquireMater(String requisition, String requisitionLine, String mate, String branch, double quantity, String unit) {
        if (!CheckRequisitionMainFragment.this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("mate", mate);
        param.put("branch", branch);
        param.put("quantity", quantity + "");
        param.put("unit", unit);
        param.put("requisition", requisition);
        param.put("requisition_line", requisitionLine);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_CHECK_REQUISITION_GET_MATER_DETAIL, getContext()), param, REQUEST_CODE_GET_MATER, mRequestTaskListener);
    }


    private void refreshShow() {
        mCheckRequisitionAdapter.setDate(requisition.getRequisitionItems());
        mCheckRequisitionAdapter.notifyDataSetChanged();

        mRequisitionTextView.setText(requisition.getNumber());
        mStateTextView.setText(requisition.getStatus() == Requisition.STATUS_NO_REQUISITION ? "已创建" : requisition.getStatus() == Requisition.STATUS_HAS_REQUISITION ? "已完成" : requisition.getStatus() == Requisition.STATUS_CANCELED ? "已取消" : requisition.getStatus() == Requisition.STATUS_CLOSED ? "已关闭" : "");
        mCreaterTextView.setText(requisition.getFounder());
        mDepartmentTextView.setText(requisition.getDepartment());
        mCreateDateTextView.setText(requisition.getDate());
        if (TextUtils.isEmpty(requisition.getNumber())) {
            mInquireButton.setTag(false);
            mInquireButton.setText("查询");
            mRequisitionEditText.getText().clear();
            mRequisitionEditText.setHint("输入调拨单号");
            mRequisitionEditText.requestFocus();
        } else {
            mInquireButton.setText("清除");
            mInquireButton.setTag(true);
            mRequisitionEditText.getText().clear();
            mRequisitionEditText.setHint("在此扫描物料标签快速选中");
        }
    }

    /**
     * 移除一个项
     *
     * @param requisitionItemNumber
     */
    public void refreshShow(String requisitionItemNumber) {
        for (int i = 0; i < requisition.getRequisitionItems().size(); i++) {
            if (TextUtils.equals(requisition.getRequisitionItems().get(i).getNumber(), requisitionItemNumber)) {
                requisition.getRequisitionItems().remove(i);
                mCheckRequisitionAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * 处理扫描得到的二维码,执行联网查询操作
     */
    private void handleScanCode(String code) {
        if (TextUtils.isEmpty(code))
            return;
        if (!CheckRequisitionMainFragment.this.isVisible())
            return;
        if (requisition.getRequisitionItems().size() == 0) {//当前物料列表为空,查询物料列表
            code = CommonTools.decodeScanString("L", code);
            mRequisitionEditText.setText(code);
            Map<String, String> param = new HashMap<>();
            param.put("username", SessionManager.getUserName(getContext()));
            param.put("env", SessionManager.getEnv(getContext()));
            param.put("requisition", code);
            NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_CHECK_REQUISITION_GET_MATER_LIST, getContext()), param, REQUEST_CODE_GET_MATER_LIST, mRequestTaskListener);
        } else {//当前物料列表不为空,扫描定位物料项
            code = CommonTools.decodeScanString("B", code);
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(getContext(), "无效查询", Toast.LENGTH_SHORT).show();
                return;
            }
            mRequisitionEditText.setText("");

            for (int i = 0; i < requisition.getRequisitionItems().size(); i++) {

            }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public interface MainFragmentCallBack {
        void gotoSecondFragment(Requisition.RequisitionItem requisitionItem, ArrayList<String> shardStrings);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_GET_MATER_LIST:
                    if (bundle.getInt("code") == 1) {
                        requisition = (Requisition) bundle.getSerializable("requisition");
                        refreshShow();
                    } else if (bundle.getInt("code") == 5) {
                        ((BaseActivity) getActivity()).ShowToast("无效调拨单");
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("查询失败");
                    }
                    break;
                case REQUEST_CODE_GET_MATER:
                    if (bundle.getInt("code") == 1) {
                        Requisition.RequisitionItem requisitionItem = (Requisition.RequisitionItem) bundle.get("requisitionItem");
                        ArrayList<String> shardStrings = bundle.getStringArrayList("target_shard_list");
                        if (mainFragmentCallBack != null) {
                            mainFragmentCallBack.gotoSecondFragment(requisitionItem, shardStrings);
                        }
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("获取物料明细失败");
                    }
                    break;
            }
        }
    }
}
