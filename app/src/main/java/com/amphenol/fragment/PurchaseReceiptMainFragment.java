package com.amphenol.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.amphenol.adapter.FirstReceiptAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Branch;
import com.amphenol.entity.Mater;
import com.amphenol.entity.Receipt;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PurchaseReceiptMainFragment extends Fragment {
    private static final int REQUEST_CODE_FOR_SCAN = 0X11;
    private static final int REQUEST_CODE_QUERY_RECEIPT = 0X12;
    private static final int REQUEST_CODE_QUERY_RECEIPT_ITEM = 0x13;
    private View rootView = null;
    private RecyclerView mRecyclerView;
    private FirstReceiptAdapter mFirstReceiptAdapter;
    private FirstReceiptAdapter.OnItemClickListener mOnItemClickListener;
    private TextView.OnEditorActionListener mOnEditorActionListener;
    private View.OnClickListener mOnClickListener;
    private MainFragmentCallBack mainFragmentCallBack;
    private EditText mCodeEditText;
    private Button mInquireButton;
    private ImageView mScanImageView;
    private TextView mFirmTextView, mPurchaseNumberTextView, mStatusTextView;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private Receipt receipt = new Receipt();
    private MyHandler myHandler = new MyHandler();

    public PurchaseReceiptMainFragment(MainFragmentCallBack mainFragmentCallBack) {
        this.mainFragmentCallBack = mainFragmentCallBack;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_purchase_receipt, container, false);
        initListeners();
        initViews();
        initData();
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
        mOnItemClickListener = new FirstReceiptAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                handleInquireMater(receipt.getMaters().get(position).getShdhm(), receipt.getMaters().get(position).getShdhh());
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
                    handleScanCode(mCodeEditText.getText().toString().trim());
                    return true;
                }
                return false;
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
                            receipt = new Receipt();
                            refreshShow(receipt);
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

        mCodeEditText = (EditText) rootView.findViewById(R.id.purchase_receipt_main_code_et);

        mCodeEditText.setOnEditorActionListener(mOnEditorActionListener);

        mScanImageView = (ImageView) rootView.findViewById(R.id.fragment_purchase_receipt_scan_iv);
        mScanImageView.setOnClickListener(mOnClickListener);

        mInquireButton = (Button) rootView.findViewById(R.id.fragment_purchase_receipt_inquire_bt);
        mInquireButton.setOnClickListener(mOnClickListener);
    }

    private void initData() {
        mFirstReceiptAdapter = new FirstReceiptAdapter(getContext(), receipt.getMaters(), mOnItemClickListener);
        mRecyclerView.setAdapter(mFirstReceiptAdapter);
    }

    /**
     * 处理扫描得到的二维码,执行联网查询操作
     */
    private void handleScanCode(String code) {

        if (TextUtils.isEmpty(code))
            return;
        if (!PurchaseReceiptMainFragment.this.isVisible())
            return;
        if (TextUtils.isEmpty(receipt.getReceiptNumber())) {//当前收货单为空，扫码查询收货单
            code = decodeScanString("P", code);
            if (code == null) return;
            mCodeEditText.setText(code);
            Map<String, String> param = new HashMap<>();
            param.put("username", SessionManager.getUserName(getContext()));
            param.put("env", SessionManager.getEnv(getContext()));
            param.put("delive_code", code);
            NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_QUERY_RECEIPT, getContext()), param, REQUEST_CODE_QUERY_RECEIPT, mRequestTaskListener);
        } else {//当前收货单不为空，扫码查询物料
            code = decodeScanString("M",code);
            if (code == null) return;
            mCodeEditText.setText("");

            for (int i = 0; i < receipt.getMaters().size(); i++) {
                if (TextUtils.equals(receipt.getMaters().get(i).getMate_number(), code)) {
                    handleInquireMater(receipt.getMaters().get(i).getShdhm(), receipt.getMaters().get(i).getShdhh());
                    break;
                }else{
                    Toast.makeText(getContext(),"无效物料标签",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 解码扫码到的条码字符串，根据指定的前缀返回解码后的字符串
     * @param prefix 标签前缀 ，如 P   M   L  ,不需要加上‘*’
     * @param code   待解码字符串
     * @return
     */
    private String decodeScanString(String prefix, String code) {
        int startIndex = code.indexOf("*" + prefix);
        if (startIndex == -1) {//不含prefix的字符串，直接使用code进行查询

        } else {
            int endIndex = code.indexOf("*", startIndex + 1);
            endIndex = endIndex == -1 ? code.length() : endIndex;
            startIndex += 2;
            if (startIndex == endIndex) {
                Toast.makeText(getContext(), "无效查询", Toast.LENGTH_SHORT).show();
                return null;
            }
            code = code.substring(startIndex, endIndex);
        }
        return code;
    }

    /**
     * 查询物料详细信息
     *
     * @param shdhm
     * @param shdhh
     */
    private void handleInquireMater(final String shdhm, final String shdhh) {
        if (!PurchaseReceiptMainFragment.this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("receipt_number", shdhm);
        param.put("receipt_line", shdhh);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_QUERY_RECEIPT_ITEM, getContext()), param, REQUEST_CODE_QUERY_RECEIPT_ITEM, mRequestTaskListener);

    }

    /**
     * 刷新显示
     */
    private void refreshShow(Receipt receipt) {
        this.receipt = receipt;
        //开始更新界面
        mFirmTextView.setText(receipt.getFirm().trim());
        mPurchaseNumberTextView.setText(receipt.getReceiptNumber().trim());
        mStatusTextView.setText(receipt.getStatus() == Receipt.STATUS_FIINISHED ? "收货完成" : receipt.getStatus() == Receipt.STATUS_NO_RECEIPT ? "未收货" : receipt.getStatus() == Receipt.STATUS_PART_RECEIPT ? "部分收货" : "");

        mFirstReceiptAdapter.setMaters(receipt.getMaters());
        mFirstReceiptAdapter.notifyDataSetChanged();


        if (TextUtils.isEmpty(receipt.getReceiptNumber())) {
            mInquireButton.setTag(false);
            mInquireButton.setText("查询");
            mCodeEditText.getText().clear();
            mCodeEditText.setHint("输入送货单号");
            mCodeEditText.requestFocus();
        } else {
            mInquireButton.setText("清除");
            mInquireButton.setTag(true);
            mCodeEditText.setText("");
            mCodeEditText.setHint("在此扫描物料标签快速选中");
        }

    }

    /**
     * 移除了一个物料，刷新list
     * @param deletedMater
     */
    public void refreshShow(String deletedMater) {
        for (int i = 0; i < receipt.getMaters().size(); i++) {
            if (TextUtils.equals(receipt.getMaters().get(i).getShdhh(), deletedMater)) {
                receipt.getMaters().remove(i);
                mFirstReceiptAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Log.d("wyj", "onactivity result and the requecode is " + requestCode);
        if (requestCode == REQUEST_CODE_FOR_SCAN && resultCode == Activity.RESULT_OK) {
            String code = data.getStringExtra("data").trim();
            mCodeEditText.setText(code);
            handleScanCode(code);
        }
    }

    public interface MainFragmentCallBack {
        void gotoSecondFragment(Mater mater);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_QUERY_RECEIPT:
                    if (bundle.getInt("code") == 1) {
                        receipt = (Receipt) bundle.getSerializable("receipt");
                        refreshShow(receipt);
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("无效收货单");
                    }
                    break;
                case REQUEST_CODE_QUERY_RECEIPT_ITEM:
                    if (bundle.getInt("code") == 1) {
                        Mater mater = (Mater) bundle.get("mater");
                        if (mainFragmentCallBack != null) {
                            mainFragmentCallBack.gotoSecondFragment(mater);
                        }
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("获取物料明细失败");
                    }

                    break;
            }
        }
    }
}
