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
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.activity.ScanActivity;
import com.amphenol.adapter.PurchaseAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Purchase;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class PurchaseReceiptMainFragment extends BaseFragment {
    private static final int REQUEST_CODE_FOR_SCAN = 0X11;
    private static final int REQUEST_CODE_QUERY_RECEIPT = 0X12;
    private static final int REQUEST_CODE_QUERY_RECEIPT_ITEM = 0x13;
    private View rootView = null;
    private RecyclerView mRecyclerView;
    private PurchaseAdapter mFirstReceiptAdapter;
    private PurchaseAdapter.OnItemClickListener mOnItemClickListener;
    private View.OnClickListener mOnClickListener;
    private MainFragmentCallBack mainFragmentCallBack;
    private EditText mCodeEditText;
    private Button mInquireButton;
    private ImageView mScanImageView;
    private TextView mFirmTextView, mPurchaseNumberTextView, mStatusTextView;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private Purchase purchase = new Purchase();
    private MyHandler myHandler = new MyHandler();

    public static PurchaseReceiptMainFragment newInstance(MainFragmentCallBack mainFragmentCallBack) {

        Bundle args = new Bundle();
        PurchaseReceiptMainFragment fragment = new PurchaseReceiptMainFragment();
        fragment.mainFragmentCallBack = mainFragmentCallBack ;
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
        rootView = inflater.inflate(R.layout.fragment_purchase_receipt, container, false);
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
        mOnItemClickListener = new PurchaseAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                handleInquireMater(purchase.getNumber(), purchase.getPurchaseItems().get(position).getNumber());
            }
        };
//        mOnEditorActionListener = new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    if (imm.isActive()) {
//                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
//                    }
//                    handleScanCode(mCodeEditText.getText().toString().trim());
//                    return true;
//                }
//                return false;
//            }
//        };
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
                            purchase = new Purchase();
                            refreshShow(purchase);
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
                        case REQUEST_CODE_QUERY_RECEIPT:
                            DecodeManager.decodeQueryReceipt(jsonObject, requestCode, myHandler);
                            break;
                        case REQUEST_CODE_QUERY_RECEIPT_ITEM:
                            DecodeManager.decodeQueryReceiptItem(jsonObject, requestCode, myHandler);
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
        mRecyclerView.setAdapter(mFirstReceiptAdapter);
        mCodeEditText = (EditText) rootView.findViewById(R.id.purchase_receipt_main_code_et);

        mScanImageView = (ImageView) rootView.findViewById(R.id.fragment_purchase_receipt_scan_iv);
        mScanImageView.setOnClickListener(mOnClickListener);

        mInquireButton = (Button) rootView.findViewById(R.id.fragment_purchase_receipt_inquire_bt);
        mInquireButton.setOnClickListener(mOnClickListener);
    }

    private void initData() {
        mFirstReceiptAdapter = new PurchaseAdapter(getContext(), purchase.getPurchaseItems(), mOnItemClickListener);
    }

    /**
     * 处理扫描得到的二维码,执行联网查询操作
     */
    @Override
    protected void handleScanCode(String code) {

        if (TextUtils.isEmpty(code))
            return;
        if (!PurchaseReceiptMainFragment.this.isVisible())
            return;
        if (TextUtils.isEmpty(purchase.getNumber())) {//当前收货单为空，扫码查询收货单
            code = CommonTools.decodeScanString("S", code);
            mCodeEditText.setText(code);
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(getContext(), "无效查询", Toast.LENGTH_SHORT).show();
                return;
            }
            Map<String, String> param = new HashMap<>();
            param.put("username", SessionManager.getUserName(getContext()));
            param.put("env", SessionManager.getEnv(getContext()));
            param.put("delive_code", code);
            NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_QUERY_RECEIPT, getContext()), param, REQUEST_CODE_QUERY_RECEIPT, mRequestTaskListener);
        } else {//当前收货单不为空，扫码查询物料
            code = CommonTools.decodeScanString("M", code);
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(getContext(), "无效物料标签", Toast.LENGTH_SHORT).show();
                return;
            }
            mCodeEditText.setText("");

            for (int i = 0; i < purchase.getPurchaseItems().size(); i++) {
                if (TextUtils.equals(purchase.getPurchaseItems().get(i).getMater().getNumber(), code)) {
                    handleInquireMater(purchase.getNumber(), purchase.getPurchaseItems().get(i).getNumber());
                    return;
                }
            }
            Toast.makeText(getContext(), "该物料不在列表中", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 查询物料详细信息
     *
     * @param purchaseNumber     送货单号码
     * @param purchaseItemNumber 送货单行号
     */
    private void handleInquireMater(final String purchaseNumber, final String purchaseItemNumber) {
        if (!PurchaseReceiptMainFragment.this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("receipt_number", purchaseNumber);
        param.put("receipt_line", purchaseItemNumber);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_QUERY_RECEIPT_ITEM, getContext()), param, REQUEST_CODE_QUERY_RECEIPT_ITEM, mRequestTaskListener);

    }

    /**
     * 刷新显示
     */
    private void refreshShow(Purchase receipt) {
        this.purchase = receipt;
        //开始更新界面
        mFirmTextView.setText(receipt.getFirm().trim());
        mPurchaseNumberTextView.setText(receipt.getNumber().trim());
        mStatusTextView.setText(receipt.getStatus() == Purchase.STATUS_FINISHED ? "收货完成" : receipt.getStatus() == Purchase.STATUS_NO_RECEIPT ? "未收货" : receipt.getStatus() == Purchase.STATUS_PART_RECEIPT ? "部分收货" : "");
        mCodeEditText.requestFocus();
        mFirstReceiptAdapter.setDate(receipt.getPurchaseItems());
        mFirstReceiptAdapter.notifyDataSetChanged();
        if (TextUtils.isEmpty(receipt.getNumber())) {
            mInquireButton.setTag(false);
            mInquireButton.setText("查询");
            mCodeEditText.getText().clear();
            mCodeEditText.setHint("输入送货单号");

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
        for (int i = 0; i < purchase.getPurchaseItems().size(); i++) {
            if (TextUtils.equals(purchase.getPurchaseItems().get(i).getNumber(), purchaseItemNumber)) {
                purchase.getPurchaseItems().remove(i);
                mFirstReceiptAdapter.notifyDataSetChanged();
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
        void gotoSecondFragment(Purchase.PurchaseItem purchaseItem);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_QUERY_RECEIPT:
                    if (bundle.getInt("code") == 1) {
                        purchase = bundle.getParcelable("purchase");
                        refreshShow(purchase);
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("无效收货单");
                    }
                    break;
                case REQUEST_CODE_QUERY_RECEIPT_ITEM:
                    if (bundle.getInt("code") == 1) {
                        Purchase.PurchaseItem purchaseItem = bundle.getParcelable("purchaseItem");
                        if (mainFragmentCallBack != null) {
                            mainFragmentCallBack.gotoSecondFragment(purchaseItem);
                        }
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("获取物料明细失败");
                    }

                    break;
            }
        }
    }
}
