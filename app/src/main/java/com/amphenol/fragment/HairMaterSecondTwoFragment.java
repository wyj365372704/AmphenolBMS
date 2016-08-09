package com.amphenol.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.adapter.HairMaterSecondTwoAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Pick;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 审核调拨单-物料明细
 */
public class HairMaterSecondTwoFragment extends Fragment {
    private static final int REQUEST_CODE_SUBMIT = 0X10;
    private static final int REQUEST_CODE_CANCEL = 0x11;
    private View rootView;
    private TextView materNumberTextView, materDescTextView, mPlanQuantityTextView, mUnitTextView, mHairQuantityTextView;
    private Button mSubmitButton, mCancelButton;
    private View.OnClickListener mOnClickListener;
    private Pick.PickItem mPickItem = new Pick.PickItem();
    private RecyclerView mRecyclerView;
    private HairMaterSecondTwoAdapter hairMaterSecondTwoAdapter;
    private HairMaterSecondTwoAdapter.OnItemClickListener onItemClickListener;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private MyHandler myHandler;
    private SecondFragmentCallBack mSecondFragmentCallBack;


    public static HairMaterSecondTwoFragment newInstance(SecondFragmentCallBack secondFragmentCallBack, Pick.PickItem pickItem) {

        Bundle args = new Bundle();
        args.putParcelable("pickItem", pickItem);
        HairMaterSecondTwoFragment fragment = new HairMaterSecondTwoFragment();
        fragment.mSecondFragmentCallBack = secondFragmentCallBack;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mPickItem = args.getParcelable("pickItem");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_hair_mater_second_two, container, false);
        initListeners();
        initData();
        initViews();
        return rootView;
    }

    private void initData() {
        hairMaterSecondTwoAdapter = new HairMaterSecondTwoAdapter(getContext(), mPickItem.getPickItemBranchItems(), onItemClickListener);
    }

    private void initViews() {
        materNumberTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_second_one_mater_in_tv);
        materDescTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_second_one_desc_in_tv);
        mPlanQuantityTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_second_one_plain_quantity_in_tv);
        mUnitTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_second_one_unit_in_tv);
        mHairQuantityTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_second_one_hair_quantity_in_tv);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_purchase_receipt_content_rl);
        mSubmitButton = (Button) rootView.findViewById(R.id.fragment_fast_requisition_main_inquire_bt);
        mCancelButton = (Button) rootView.findViewById(R.id.fragment_fast_requisition_main_submit_bt);

        materNumberTextView.setText(mPickItem.getBranch().getMater().getNumber());
        materDescTextView.setText(mPickItem.getBranch().getMater().getDesc());
        mPlanQuantityTextView.setText(mPickItem.getQuantity() + "");
        mUnitTextView.setText(mPickItem.getBranch().getMater().getUnit());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(hairMaterSecondTwoAdapter);

        mSubmitButton.setOnClickListener(mOnClickListener);
        mCancelButton.setOnClickListener(mOnClickListener);
    }

    private void initListeners() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fragment_fast_requisition_main_inquire_bt:
                        if (mPickItem.getHairQuantity() <= 0) {//发料数量为0
                            ((BaseActivity) getActivity()).ShowToast("你还未添加任何物料至待发列表");
                        }
                        if (mPickItem.getHairQuantity() > mPickItem.getQuantity()) {//发料数量大于计划数量
                            ((BaseActivity) getActivity()).ShowToast("发料数量不可大于计划数量");
                        } else {
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                            builder2.setTitle("立即过账").setMessage("将要进行发料过账?");
                            builder2.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    handleHairMater();
                                }
                            });
                            builder2.create().show();
                        }
                        break;
                    case R.id.fragment_fast_requisition_main_submit_bt:
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                        builder2.setTitle("终止过账").setMessage("将要进行终止过账?");
                        builder2.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handleCancelHairMater();
                            }
                        });
                        builder2.create().show();
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
            public void onRequestSuccess(org.json.JSONObject jsonObject, int requestCode) {
                try {
                    switch (requestCode) {
                        case REQUEST_CODE_SUBMIT:
                            DecodeManager.decodeHairMaterSubmit(jsonObject, requestCode, myHandler);
                            break;
                        case REQUEST_CODE_CANCEL:
                            DecodeManager.decodeHairMaterCancel(jsonObject, requestCode, myHandler);
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

            }
        };
    }

    /**
     * 终止过账
     */
    private void handleCancelHairMater() {
        if (!HairMaterSecondTwoFragment.this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("warehouse", mPickItem.getBranch().getMater().getWarehouse());
        param.put("department", mPickItem.getPick().getDepartment());
        param.put("work_order", mPickItem.getPick().getWorkOrder());
        param.put("sequence", mPickItem.getSequence());
        String mater_list = "";
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (Pick.PickItem.PickItemBranchItem pickItemBranchItem : mPickItem.getPickItemBranchItems()) {
                JSONObject materObject = new JSONObject();
                materObject.put("mater", pickItemBranchItem.getBranch().getMater().getNumber());
                materObject.put("branch", pickItemBranchItem.getBranch().getPo());
                materObject.put("location", pickItemBranchItem.getBranch().getMater().getLocation());
                materObject.put("quantity", pickItemBranchItem.getQuantity());
                jsonArray.add(materObject);
            }
            jsonObject.put("mater_list", jsonArray);
            mater_list = jsonObject.toJSONString();
        } catch (Exception e) {
            mater_list = "";
        }
        param.put("mater_list", mater_list);

        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_HAIR_MATER_SUBMIT, getContext()), param, REQUEST_CODE_SUBMIT, mRequestTaskListener);
    }

    /**
     * 发料过账
     */
    private void handleHairMater() {

    }

    public interface SecondFragmentCallBack extends Serializable {
        /**
         * @param sequence 系统顺序号
         */
        void itemBeenClosed(String sequence);

        /**
         * @param sequence 系统顺序号
         */

        void itemBeenSured(String sequence);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_CODE_CANCEL:
                    break;
                case REQUEST_CODE_SUBMIT:
                    break;
            }
        }
    }
}