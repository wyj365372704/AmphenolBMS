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
import com.amphenol.adapter.CheckRequisitionAdapter;
import com.amphenol.adapter.HairMaterMainAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Pick;
import com.amphenol.entity.Requisition;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HairMaterMainFragment extends Fragment {
    private static final int REQUEST_CODE_GET_PICK_LIST = 0x10;
    private static final int REQUEST_CODE_GET_PICK = 0x11;
    private static final int REQUEST_CODE_FOR_SCAN = 0x12;
    private View rootView = null;
    private ImageView mScanImageView;
    private RecyclerView mRecyclerView;
    private TextView mPickNumberTextView, mWorkOrderTextView, mFounderTextView, mDepartmentTextView, mCreateDateTextView, mTypeTextView;
    private Button mInquireButton;
    private EditText mPickNumberEditText;
    private TextView.OnEditorActionListener mOnEditorActionListener;
    private View.OnClickListener mOnClickListener;
    private LoadingDialog mLoadingDialog;
    private HairMaterMainAdapter mHairMaterMainAdapter;
    private HairMaterMainAdapter.OnItemClickListener mOnItemClickListener;
    private Pick pick = new Pick();
    private MyHandler myHandler;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private MainFragmentCallBack mainFragmentCallBack;


    public static HairMaterMainFragment newInstance(MainFragmentCallBack mainFragmentCallBack) {
        Bundle args = new Bundle();
        HairMaterMainFragment fragment = new HairMaterMainFragment();
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
        rootView = inflater.inflate(R.layout.fragment_hair_mater_main, container, false);
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
        mPickNumberEditText.requestFocus();
    }

    private void initData() {
        myHandler = new MyHandler();
        mHairMaterMainAdapter = new HairMaterMainAdapter(getContext(), pick.getPickItems(), mOnItemClickListener);
    }

    private void initViews() {
        mTypeTextView = (TextView) rootView.findViewById(R.id.fragment_hair_mater_main_type_tv);
        mScanImageView = (ImageView) rootView.findViewById(R.id.fragment_scan_iv);
        mScanImageView.setOnClickListener(mOnClickListener);
        mPickNumberEditText = (EditText) rootView.findViewById(R.id.purchase_receipt_main_code_et);
        mPickNumberEditText.setOnEditorActionListener(mOnEditorActionListener);
        mInquireButton = (Button) rootView.findViewById(R.id.fragment_purchase_receipt_inquire_bt);
        mInquireButton.setOnClickListener(mOnClickListener);
        mPickNumberTextView = (TextView) rootView.findViewById(R.id.fragment_check_requisition_main_requisition_tv);
        mWorkOrderTextView = (TextView) rootView.findViewById(R.id.fragment_check_requisition_main_state_tv);
        mFounderTextView = (TextView) rootView.findViewById(R.id.fragment_check_requisition_main_creater_tv);
        mDepartmentTextView = (TextView) rootView.findViewById(R.id.fragment_check_requisition_main_department_tv);
        mCreateDateTextView = (TextView) rootView.findViewById(R.id.fragment_check_requisition_main_create_date_tv);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_purchase_receipt_content_rl);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mHairMaterMainAdapter);
    }

    private void initListeners() {
        mOnItemClickListener = new HairMaterMainAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                handleInquireMater(pick.getPickItems().get(position).getBranch().getMater().getWarehouse(),pick.getNumber(),pick.getPickItems().get(position).getPickLine(),pick.getPickItems().get(position).getBranch().getMater().getNumber(),pick.getPickItems().get(position).getBranch().getMater().getUnit(),pick.getPickItems().get(position).getBranch().getMater().getShard(),pick.getPickItems().get(position).getBranch().getMater().getLocation(),pick.getPickItems().get(position).getBranch().getPo(),pick.getPickItems().get(position).getQuantity(),pick.getDepartment(),pick.getWorkOrder(),pick.getPickItems().get(position).getSequence());
            }
        };
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fragment_purchase_receipt_inquire_bt:
                        boolean state = mInquireButton.getTag() == null ? false : (boolean) mInquireButton.getTag();
                        if (state) {//当前按钮状态为“清除”
                            pick = new Pick();
                            refreshShow();
                        } else {
                            handleScanCode(mPickNumberEditText.getText().toString().trim());
                        }
                        break;
                    case R.id.fragment_scan_iv:
                        startActivityForResult(new Intent(getActivity(), ScanActivity.class), REQUEST_CODE_FOR_SCAN);
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
                    handleScanCode(mPickNumberEditText.getText().toString().trim());
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
                        case REQUEST_CODE_GET_PICK_LIST:
                            DecodeManager.decodeHairMaterGetPickList(jsonObject, requestCode, myHandler);
                            break;
                        case REQUEST_CODE_GET_PICK:
                            DecodeManager.decodeHairMaterGetMaterList(jsonObject, requestCode, myHandler);
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

    private void handleInquireMater(String warehouse,String pickNumber,String pickLine ,String mate,String unit, String shard,String location,String branch,double quantity,String department,String workOrder,String sequence) {
        if (!HairMaterMainFragment.this.isVisible())
            return;
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getContext()));
        param.put("env", SessionManager.getEnv(getContext()));
        param.put("warehouse",warehouse);
        param.put("pick_number",pickNumber);
        param.put("pick_line",pickLine);
        param.put("mater", mate);
        param.put("unit", unit);
        param.put("shard",shard);
        param.put("location",location);
        param.put("branch",branch);

        param.put("quantity",quantity+"");//计划数量
        param.put("department",department);
        param.put("workOrder",workOrder);
        param.put("sequence",sequence);
        NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_HAIR_MATER_GET_MATER_LIST, getContext()), param, REQUEST_CODE_GET_PICK, mRequestTaskListener);
    }


    private void refreshShow() {
        mHairMaterMainAdapter.setDate(pick.getPickItems());
        mHairMaterMainAdapter.notifyDataSetChanged();
        mPickNumberEditText.requestFocus();
        mPickNumberTextView.setText(pick.getNumber());
        mTypeTextView.setText(pick.getType() == Pick.TYPE_NORMAL ? "正常领料" : pick.getType() == Pick.TYPE_EXCEED ? "超发领料" : pick.getType() == Pick.TYPE_RETURN ? "退料" : "");
        mWorkOrderTextView.setText(pick.getWorkOrder());
        mFounderTextView.setText(pick.getFounder());
        mDepartmentTextView.setText(pick.getDepartment());
        mCreateDateTextView.setText(pick.getDate());
        if (TextUtils.isEmpty(pick.getNumber())) {
            mInquireButton.setTag(false);
            mInquireButton.setText("查询");
            mPickNumberEditText.getText().clear();
            mPickNumberEditText.setHint("输入领料单号");
        } else {
            mInquireButton.setText("清除");
            mInquireButton.setTag(true);
            mPickNumberEditText.getText().clear();
            mPickNumberEditText.setHint("在此扫描物料标签快速选中");
        }
    }

    /**
     * 移除一个项
     *
     * @param sequence 系统顺序号
     */
    public void refreshShow(String sequence) {
        for (int i = 0; i <pick.getPickItems().size(); i++) {
            if (TextUtils.equals(pick.getPickItems().get(i).getSequence(), sequence)) {
                pick.getPickItems().remove(i);
                mHairMaterMainAdapter.notifyDataSetChanged();
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
        if (!HairMaterMainFragment.this.isVisible())
            return;
        if (TextUtils.isEmpty(pick.getNumber())) {//查询物料列表
            code = CommonTools.decodeScanString("I", code);
            mPickNumberEditText.setText(code);
            Map<String, String> param = new HashMap<>();
            param.put("username", SessionManager.getUserName(getContext()));
            param.put("env", SessionManager.getEnv(getContext()));
            param.put("pick_number", code);
            NetWorkAccessTools.getInstance(getContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_HAIR_MATER_GET_PICK_LIST, getContext()), param, REQUEST_CODE_GET_PICK_LIST, mRequestTaskListener);
        } else {//扫描定位物料项
            mPickNumberEditText.setText("");
            code = CommonTools.decodeScanString("M", code);
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(getContext(), "无效物料标签", Toast.LENGTH_SHORT).show();
                return;
            }

            for (int position = 0; position < pick.getPickItems().size(); position++) {
                if (TextUtils.equals(pick.getPickItems().get(position).getBranch().getMater().getNumber(), code)) {
                    handleInquireMater(pick.getPickItems().get(position).getBranch().getMater().getWarehouse(),pick.getNumber(),pick.getPickItems().get(position).getPickLine(),pick.getPickItems().get(position).getBranch().getMater().getNumber(),pick.getPickItems().get(position).getBranch().getMater().getUnit(),pick.getPickItems().get(position).getBranch().getMater().getShard(),pick.getPickItems().get(position).getBranch().getMater().getLocation(),pick.getPickItems().get(position).getBranch().getPo(),pick.getPickItems().get(position).getQuantity(),pick.getDepartment(),pick.getWorkOrder(),pick.getPickItems().get(position).getSequence());
                    return;
                }
            }
            Toast.makeText(getContext(), "该物料不在列表中", Toast.LENGTH_SHORT).show();
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
            mPickNumberEditText.setText(code);
            handleScanCode(mPickNumberEditText.getText().toString().trim());
        }

    }

    public interface MainFragmentCallBack extends Serializable {
        void gotoSecondFragment(Pick.PickItem pickItem,ArrayList<String> shards);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_GET_PICK_LIST:
                    if (bundle.getInt("code") == 1) {
                        pick = bundle.getParcelable("pick");
                        refreshShow();
                    } else if (bundle.getInt("code") == 5) {
                        ((BaseActivity) getActivity()).ShowToast("无效领料单");
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("查询失败");
                    }
                    break;
                case REQUEST_CODE_GET_PICK:
                    if (bundle.getInt("code") == 1) {
                        Pick.PickItem pickItem =  bundle.getParcelable("pickItem");
                        ArrayList<String> shards = bundle.getStringArrayList("shards");
                        if (mainFragmentCallBack != null) {
                            mainFragmentCallBack.gotoSecondFragment(pickItem,shards);
                        }
                    } else {
                        ((BaseActivity) getActivity()).ShowToast("获取物料明细失败");
                    }
                    break;
            }
        }
    }

}
