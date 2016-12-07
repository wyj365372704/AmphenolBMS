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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SPManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.activity.ScanActivity;
import com.amphenol.adapter.PurchaseAdapter;
import com.amphenol.adapter.ReturnsAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Mater;
import com.amphenol.entity.Purchase;
import com.amphenol.entity.Returns;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PurchaseReturnMainFragment extends BaseFragment {
    private static final int REQUEST_CODE_FOR_SCAN = 0X11;
    private static final int REQUEST_CODE_QUERY_RETURN = 0X12;
    private static final int REQUEST_CODE_QUERY_RETURN_ITEM = 0x13;
    private View rootView = null;
    private RecyclerView mRecyclerView;
    private ReturnsAdapter mFirstReturnAdapter;
    private ReturnsAdapter.OnItemClickListener mOnItemClickListener;
    private View.OnClickListener mOnClickListener;
    private MainFragmentCallBack mainFragmentCallBack;
    private EditText mCodeEditText;
    private Button mInquireButton;
    private ImageView mScanImageView;
    private TextView mFirmTextView, mPurchaseNumberTextView, mStatusTextView;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private Returns returns = new Returns();
    private MyHandler myHandler = new MyHandler();

    public static PurchaseReturnMainFragment newInstance(MainFragmentCallBack mainFragmentCallBack) {

        Bundle args = new Bundle();
        PurchaseReturnMainFragment fragment = new PurchaseReturnMainFragment();
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
        rootView = inflater.inflate(R.layout.fragment_purchase_return, container, false);
        initListeners();
        initData();
        initViews();
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeEditText.requestFocus();
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

    private void initListeners() {
        mOnItemClickListener = new ReturnsAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                handleInquireMater(returns.getNumber(), position, returns.getReturnsItems().get(position).getMater().getNumber());
            }
        };
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fragment_purchase_receipt_scan_iv://扫描图片
                        startActivityForResult(new Intent(getActivity(), ScanActivity.class), REQUEST_CODE_FOR_SCAN);
                        break;
                    case R.id.fragment_purchase_receipt_inquire_bt://查询按钮
                        boolean state = mInquireButton.getTag() == null ? false : (boolean) mInquireButton.getTag();
                        if (state) {//当前按钮状态为“清除”
                            returns = new Returns();
                            refreshShow(returns);
                        } else {
                            handleScanCode(mCodeEditText.getText().toString().trim());
                        }
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
                        case REQUEST_CODE_QUERY_RETURN:
                            DecodeManager.decodeQueryReturn(jsonObject, requestCode, myHandler);
                            break;
                        case REQUEST_CODE_QUERY_RETURN_ITEM:
                            DecodeManager.decodeQueryReturnItem(jsonObject, requestCode, myHandler);
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

    private void initViews() {
        mFirmTextView = (TextView) rootView.findViewById(R.id.fragment_purchase_receipt_shcs_in_tv);
        mPurchaseNumberTextView = (TextView) rootView.findViewById(R.id.fragment_purchase_receipt_shdh_in_tv);
        mStatusTextView = (TextView) rootView.findViewById(R.id.fragment_purchase_receipt_zj_in_tv);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_purchase_receipt_content_rl);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mFirstReturnAdapter);
        mCodeEditText = (EditText) rootView.findViewById(R.id.purchase_receipt_main_code_et);

        mScanImageView = (ImageView) rootView.findViewById(R.id.fragment_purchase_receipt_scan_iv);
        mScanImageView.setOnClickListener(mOnClickListener);

        mInquireButton = (Button) rootView.findViewById(R.id.fragment_purchase_receipt_inquire_bt);
        mInquireButton.setOnClickListener(mOnClickListener);
    }

    private void initData() {
        mFirstReturnAdapter = new ReturnsAdapter(getContext(), returns.getReturnsItems(), mOnItemClickListener);
    }

    /**
     * 处理扫描得到的二维码,执行联网查询操作
     */
    protected void handleScanCode(String code) {

        if (TextUtils.isEmpty(code))
            return;
        if (!PurchaseReturnMainFragment.this.isVisible())
            return;
        if (TextUtils.isEmpty(returns.getNumber())) {//当前收货单为空，扫码查询收货单
            code = CommonTools.decodeScanString("V", code);
            mCodeEditText.setText(code);
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(getContext(), "无效查询", Toast.LENGTH_SHORT).show();
                return;
            }
            Map<String, String> param = new HashMap<>();
            param.put("username", SessionManager.getUserName(getContext()));
            param.put("env", SessionManager.getEnv(getContext()));
            param.put("return_number", code);
            NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_QUERY_RETURN, getContext()), param, REQUEST_CODE_QUERY_RETURN, mRequestTaskListener);
        } else {//当前收货单不为空，扫码查询物料
            code = CommonTools.decodeScanString("M", code);
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(getContext(), "无效物料标签", Toast.LENGTH_SHORT).show();
                return;
            }
            mCodeEditText.setText("");

            for (int i = 0; i < returns.getReturnsItems().size(); i++) {
                if (TextUtils.equals(returns.getReturnsItems().get(i).getMater().getNumber(), code)) {
                    handleInquireMater(returns.getNumber(),i,  returns.getReturnsItems().get(i).getMater().getNumber());

                    return;
                }
            }
            Toast.makeText(getContext(), "该物料不在列表中", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 查询物料详细信息
     */
    private void handleInquireMater(final String purchaseNumber, int position, final String mater) {
        if (!PurchaseReturnMainFragment.this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("receipt_number", purchaseNumber);
        param.put("mater", mater);
        param.put("position", position + "");
        param.put("warehouse", SessionManager.getWarehouse(getContext()));
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_QUERY_RETURN_ITEM, getContext()), param, REQUEST_CODE_QUERY_RETURN_ITEM, mRequestTaskListener);
    }

    /**
     * 刷新显示
     */
    private void refreshShow(Returns returns) {
        this.returns = returns;
        //开始更新界面
        mFirmTextView.setText(returns.getFirm().trim());
        mPurchaseNumberTextView.setText(returns.getNumber().trim());
        mStatusTextView.setText(returns.getStatus() == Returns.STATUS_CREATING ? "创建中"
                : returns.getStatus() == Returns.STATUS_FINISHED ? "退货完成"
                : returns.getStatus() == Returns.STATUS_NO_RETURN ? "未退货"
                : returns.getStatus() == Returns.STATUS_PART_RETURN ? "部分退货"
                : "");
        mCodeEditText.requestFocus();
        mFirstReturnAdapter.setDate(returns.getReturnsItems());
        mFirstReturnAdapter.notifyDataSetChanged();
        if (TextUtils.isEmpty(returns.getNumber())) {
            mInquireButton.setTag(false);
            mInquireButton.setText("查询");
            mCodeEditText.getText().clear();
            mCodeEditText.setHint("输入退货单号");
        } else {
            mInquireButton.setText("清除");
            mInquireButton.setTag(true);
            mCodeEditText.setText("");
            mCodeEditText.setHint("在此扫描物料标签快速选中");
        }
    }

    /**
     * 移除了一个物料，刷新list
     *
     * @param purchaseItemNumber 送货单行号
     */
    public void refreshShow(String purchaseItemNumber) {
        for (int i = 0; i < returns.getReturnsItems().size(); i++) {
            if (TextUtils.equals(returns.getReturnsItems().get(i).getNumber(), purchaseItemNumber)) {
                returns.getReturnsItems().remove(i);
                mFirstReturnAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_SCAN && resultCode == Activity.RESULT_OK) {
            String code = data.getStringExtra("data").trim();
            mCodeEditText.setText(code);
            handleScanCode(code);
        }
    }

    public interface MainFragmentCallBack extends Serializable {
        void gotoSecondFragment(Returns.ReturnsItem returnsItem);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_QUERY_RETURN:
                    if (bundle.getInt("code") == 1) {
                        returns = bundle.getParcelable("returns");
                        refreshShow(returns);
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("无效收货单");
                    }
                    break;
                case REQUEST_CODE_QUERY_RETURN_ITEM:
                    if (bundle.getInt("code") == 1) {
                        Returns.ReturnsItem returnsItemTemp = returns.getReturnsItems().get(bundle.getInt("position"));
                        Returns.ReturnsItem returnsItem = bundle.getParcelable("returnsItem");
                        returnsItem.setNumber(returnsItemTemp.getNumber());
                        returnsItem.getMater().setNumber(returnsItemTemp.getMater().getNumber());
                        returnsItem.setQuantity(returnsItemTemp.getQuantity());

                        Returns returns = new Returns();
                        returns.setNumber(returnsItemTemp.getReturns().getNumber());
                        returnsItem.setReturns(returns);
                        if (mainFragmentCallBack != null) {
                            mainFragmentCallBack.gotoSecondFragment(returnsItem);
                        }
                    } else if(bundle.getInt("code") == 5){
                        ((BaseActivity) getActivity()).ShowToast("未查询到此物料");
                    }else {
                        ((BaseActivity) getActivity()).ShowToast("获取物料明细失败");
                    }

                    break;
            }
        }
    }
}
