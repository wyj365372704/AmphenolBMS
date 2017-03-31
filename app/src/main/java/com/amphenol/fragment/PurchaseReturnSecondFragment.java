package com.amphenol.fragment;

import android.content.DialogInterface;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.adapter.ReturnsSecondAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Mater;
import com.amphenol.entity.Pick;
import com.amphenol.entity.Returns;
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

/**
 */
public class PurchaseReturnSecondFragment extends BaseFragment {
    private static final int REQUEST_CODE_RETURN_CONFIRM = 0X10;
    private static final int REQUEST_CODE_RETURN_CLOSE = 0X12;
    private View rootView = null;
    private RecyclerView mRecyclerView;
    private ReturnsSecondAdapter mSecondReceiptAdapter;
    private ReturnsSecondAdapter.OnItemClickListener mOnItemClickListener;
    private SecondFragmentCallBack mSecondFragmentCallBack;
    private AdapterView.OnItemSelectedListener mOnItemSelectedListener;
    private Spinner mSpinner;
    private TextView mMaterNumberTextView, mMaterDescTextView, mPlainQuantityTextView, mActualquantityTextView;
    private View dialogView;//弹窗dialog视图
    private Returns.ReturnsItem returnsItem;
    private ArrayList<Returns.ReturnsItemSource> mShowList;
    private ArrayAdapter<String> mStringArrayAdapter;
    private TextWatcher mSingleTextWatcher;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private View.OnClickListener mOnClickListener;
    private MyHandler myHandler = new MyHandler();
    private Button mCancelButton;
    private Button mSubmitButton;

    public static PurchaseReturnSecondFragment newInstance(SecondFragmentCallBack mSecondFragmentCallBack, Returns.ReturnsItem returnsItem ,PurchaseReturnSecondFragment.SecondFragmentCallBack secondFragmentCallBack) {

        Bundle args = new Bundle();
        args.putParcelable("returnsItem", returnsItem);
        PurchaseReturnSecondFragment fragment = new PurchaseReturnSecondFragment();
        fragment.mSecondFragmentCallBack = mSecondFragmentCallBack;
        fragment.setArguments(args);
        fragment.mSecondFragmentCallBack = secondFragmentCallBack;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            returnsItem = args.getParcelable("returnsItem");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_purchase_return_second, container, false);
        initListeners();
        initData();
        initViews();
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        return rootView;
    }

    private void initListeners() {
        mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (Returns.ReturnsItemSource returnsItemSource : returnsItem.getReturnsItemSources()) {
                    returnsItemSource.setChecked(false);
                }
                if (position == 0) {
                    mShowList.clear();
                    mShowList.addAll(returnsItem.getReturnsItemSources());
                    mSecondReceiptAdapter.notifyDataSetChanged();
                } else {
                    mShowList.clear();
                    for (Returns.ReturnsItemSource returnsItemSource : returnsItem.getReturnsItemSources()) {
                        if (TextUtils.equals(returnsItemSource.getMater().getShard().trim(), mStringArrayAdapter.getItem(position).trim())) {
                            mShowList.add(returnsItemSource);
                        }
                    }
                    mSecondReceiptAdapter.notifyDataSetChanged();
                }
                functionCalculateHairQuantity();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        mOnItemClickListener = new ReturnsSecondAdapter.OnItemClickListener() {
            @Override
            public void OnItemCheckedChanged(int position, boolean isChecked) {
                mShowList.get(position).setChecked(isChecked);
                functionCalculateHairQuantity();

            }

            @Override
            public void OnRequisitionQuantityChanged(int position, double quantity) {
                if (quantity > returnsItem.getReturnsItemSources().get(position).getQuantity()) {
                    ((BaseActivity) getActivity()).ShowToast("退料数量不能大于库存数量");
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
                        case REQUEST_CODE_RETURN_CONFIRM:
                            DecodeManager.decodeReturnConfirm(jsonObject, requestCode, myHandler);
                            break;
                        case REQUEST_CODE_RETURN_CLOSE:
                            DecodeManager.decodeReturnClose(jsonObject, requestCode, myHandler);
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
                try {
                     Double.parseDouble(String.valueOf(s));
                } catch (Throwable e) {
                    e.printStackTrace();
                    ((BaseActivity) getActivity()).ShowToast("实际单重输入非法");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fragment_fast_requisition_main_submit_bt:
                        if (returnsItem.getActualQuantity() <= 0) {
                            ((BaseActivity) getActivity()).ShowToast("退料数量非法");
                            return;
                        }

                        for (Returns.ReturnsItemSource returnsItemSource : returnsItem.getReturnsItemSources()) {
                            if (returnsItemSource.isChecked() && returnsItemSource.getEnableQuantity() > returnsItemSource.getQuantity()) {
                                ((BaseActivity) getActivity()).ShowToast("存在退料数量大于库存数量的项目,检查后重试");
                                return;
                            }
                        }

                        if (returnsItem.getActualQuantity() > returnsItem.getQuantity()) {
                            ((BaseActivity) getActivity()).ShowToast("勾选数量不能大于退料数量");
                            return;
                        }

                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                        builder2.setTitle("退料过账").setMessage("将要进行退料过账?");
                        builder2.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String mater_list = "";
                                try {
                                    JSONArray jsonArray = new JSONArray();
                                    for (Returns.ReturnsItemSource returnsItemSource : mShowList) {
                                        if (returnsItemSource.isChecked()) {
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("branch_desc", returnsItemSource.getPo());
                                            jsonObject.put("location", returnsItemSource.getMater().getLocation());
                                            jsonObject.put("return_quantity", returnsItemSource.getEnableQuantity());
                                            jsonArray.put(jsonObject);
                                        }
                                    }
                                    mater_list = new JSONObject().put("mater_list", jsonArray).toString();
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                                handleSubmit(returnsItem.getReturns().getNumber(),returnsItem.getNumber(),mater_list);
                            }
                        });
                        builder2.create().show();

                        break;
                    case R.id.fragment_fast_requisition_main_cancel_bt:
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("关闭退料").setMessage("将要进行关闭退料?");
                        builder.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handlerCancel(returnsItem.getReturns().getNumber(),returnsItem.getNumber());
                            }
                        });
                        builder.create().show();
                        break;
                }
            }
        };
    }

    private synchronized void functionCalculateHairQuantity() {
        returnsItem.setActualQuantity(0);
        for (Returns.ReturnsItemSource returnsItemSource : mShowList) {
            if (returnsItemSource.isChecked()) {
                returnsItem.setActualQuantity(returnsItem.getActualQuantity() + returnsItemSource.getEnableQuantity());
            }
        }
        mActualquantityTextView.setText(returnsItem.getActualQuantity() + "");
    }


    private void initViews() {
        mMaterNumberTextView = (TextView) rootView.findViewById(R.id.fragment_purchase_receipt_second_wlbh_in_tv);
        mMaterDescTextView = (TextView) rootView.findViewById(R.id.fragment_purchase_receipt_second_wlms_in_tv);
        mPlainQuantityTextView = (TextView) rootView.findViewById(R.id.fragment_purchase_receipt_second_jhsl_in_tv);
        mActualquantityTextView = (TextView) rootView.findViewById(R.id.fragment_purchase_receipt_second_shzzl_in_tv);
        mSpinner = (Spinner) rootView.findViewById(R.id.fragment_create_requisition_main_shard_spinner);
        mSpinner.setAdapter(mStringArrayAdapter);
        mSpinner.setOnItemSelectedListener(mOnItemSelectedListener);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_purchase_receipt_second_content_rl);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mSecondReceiptAdapter);
        mMaterNumberTextView.setText(returnsItem.getMater().getNumber());
        mMaterDescTextView.setText(returnsItem.getMater().getDesc().trim());
        mPlainQuantityTextView.setText(returnsItem.getQuantity() + "");
        mCancelButton = (Button) rootView.findViewById(R.id.fragment_fast_requisition_main_cancel_bt);
        mCancelButton.setOnClickListener(mOnClickListener);
        mSubmitButton = (Button) rootView.findViewById(R.id.fragment_fast_requisition_main_submit_bt);
        mSubmitButton.setOnClickListener(mOnClickListener);
    }

    private void initData() {
        mShowList = new ArrayList<>();
        mSecondReceiptAdapter = new ReturnsSecondAdapter(getContext(), mShowList, mOnItemClickListener);
        ArrayList<String> temp = new ArrayList<>();
        temp.add("ALL");
        temp.addAll(SessionManager.getShard_list(getContext()));
        mStringArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, temp);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        mStringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        UpdateActualQuantity();
//        updateReceiptTotalWeight();

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
     *
     * @param return_number 退货单号
     * @param return_line   退货单行号
     * @param mater_list    物料信息集合的json字符串
     */
    private void handleSubmit(String return_number, String return_line,  String mater_list) {
        if (!PurchaseReturnSecondFragment.this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("return_number", return_number);
        param.put("return_line", return_line);
        param.put("mater_list", mater_list);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_MATER_RETURN_CONFIRM, getContext()), param, REQUEST_CODE_RETURN_CONFIRM, mRequestTaskListener);
    }

    /**
     *
     * @param return_number 退货单号
     * @param return_line   退货单行号
     */
    private void handlerCancel(String return_number, String return_line) {
        if (!PurchaseReturnSecondFragment.this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("return_number", return_number);
        param.put("return_line", return_line);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_MATER_RETURN_CLOSE, getContext()), param, REQUEST_CODE_RETURN_CLOSE, mRequestTaskListener);
    }

    @Override
    protected void handleScanCode(String code) {
        if (TextUtils.isEmpty(code))
            return;
        if (!PurchaseReturnSecondFragment.this.isVisible())
            return;

        String materNumber = CommonTools.decodeScanString(PropertiesUtil.getInstance(getContext()).getValue(PropertiesUtil.BARCODE_PREFIX_MATER, ""), code);
        String location = CommonTools.decodeScanString(PropertiesUtil.getInstance(getContext()).getValue(PropertiesUtil.BARCODE_PREFIX_LOCATION, ""), code);
        String branch = CommonTools.decodeScanString(PropertiesUtil.getInstance(getContext()).getValue(PropertiesUtil.BARCODE_PREFIX_BRANCH, ""), code);

        if (TextUtils.isEmpty(materNumber) || !TextUtils.equals(materNumber, returnsItem.getMater().getNumber())) {
            ((BaseActivity) getActivity()).ShowToast("扫描失败,不是正确的物料标签卡");
            return;
        }
        for (int i = 0; i < mShowList.size(); i++) {
            Returns.ReturnsItemSource item =mShowList.get(i);
            if (TextUtils.equals(item.getPo().trim(),branch.trim()) && TextUtils.equals(item.getMater().getLocation().trim(),location.trim())) {
                if(!item.isChecked()){
                    if (returnsItem.getActualQuantity() + item.getQuantity() > returnsItem.getQuantity()) {//加上这个物料后,发料数量大于计划数量
                        item.setEnableQuantity(returnsItem.getQuantity() - returnsItem.getActualQuantity());
                    } else {
                        item.setEnableQuantity(item.getQuantity());
                    }
                    item.setChecked(true);
                    mShowList.add(0,mShowList.remove(i));
                    mSecondReceiptAdapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(0);
                    functionCalculateHairQuantity();
                }else{
                    mRecyclerView.scrollToPosition(i);
                }

                ((BaseActivity)getActivity()).ShowToast("扫描成功并勾选");
                return;
            }
        }
        ((BaseActivity) getActivity()).ShowToast("扫描失败,该物料不在可选列表中");
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
                case REQUEST_CODE_RETURN_CONFIRM:
                    if (mSecondFragmentCallBack != null) {
                        if (bundle.getInt("code") == 1) {
                            Toast.makeText(getContext(), "退货成功", Toast.LENGTH_SHORT).show();
                            mSecondFragmentCallBack.itemBeenSured(returnsItem.getNumber());
                        } else {
                            Toast.makeText(getContext(), "退货失败:"+bundle.getString("desc"), Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case REQUEST_CODE_RETURN_CLOSE:
                    if (mSecondFragmentCallBack != null) {
                        if (bundle.getInt("code") == 1) {
                            Toast.makeText(getContext(), "关闭成功", Toast.LENGTH_SHORT).show();
                            mSecondFragmentCallBack.itemBeenClosed(returnsItem.getNumber());
                        } else {
                            Toast.makeText(getContext(), "关闭失败:"+bundle.getString("desc"), Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    }
}
