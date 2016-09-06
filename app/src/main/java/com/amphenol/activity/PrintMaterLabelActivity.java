package com.amphenol.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
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
import com.amphenol.amphenol.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.amphenol.entity.Mater;
import com.amphenol.entity.Purchase;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;
import com.baoyz.actionsheet.ActionSheet;
import com.graduate.squirrel.ui.wheel.ScreenInfo;
import com.graduate.squirrel.ui.wheel.WheelMain;
import com.hoin.btsdk.BluetoothService;

import org.json.JSONObject;

/**
 * Created by Carl on 2016-09-05 005.
 */
public class PrintMaterLabelActivity extends BaseActivity {
    private static final int REQUEST_CODE_FOR_SCAN = 0x10;
    private static final int REQUEST_CODE_INQUIRE_MATER = 0X11;
    private static final int REQUEST_ENABLE_BT = 0x12;
    private static final int REQUEST_CONNECT_DEVICE = 0x13;
    private ImageView mScanImageView;
    private Button mInquireButton;
    private TextView actionBarRight;
    private TextView materNumberTextView, materFormatTextView, materDescTextView, branchedTextView, unitTextView, singleUnitTextView, totalWeightTextView, mDateEditText;
    private View.OnClickListener mOnClickListener;
    private EditText materEditText, branchEditText, amountEditText, singleEditText, firmEditText;
    private TextView.OnEditorActionListener mOnEditorActionListener;

    private Mater.Branch branch;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private MyHandler myHandler = new MyHandler();
    private BluetoothService mService;
    private BluetoothDevice con_dev = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        materEditText.requestFocus();

        //蓝牙不可用
        if (mService.isAvailable() == false) {
            ShowToast("本设备蓝牙不可用,无法连接打印机!");
            actionBarRight.setText("本设备蓝牙不可用,无法连接打印机");
            actionBarRight.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.color_bluetooth_disable));
        } else if (mService.isBTopen() == false) {
            actionBarRight.setText("点击此处打开蓝牙");
            actionBarRight.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.color_printer_disconnected));
//            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (con_dev == null) {
                actionBarRight.setText("点击此处连接打印机");
                actionBarRight.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.color_printer_disconnected));
                String defaultPrinterAddress = SPManager.getInstance(this).getSP("printer_address_default", "");
                if (TextUtils.isEmpty(defaultPrinterAddress))
                    return;
                Set<BluetoothDevice> pairedBluetoothDevices = mService.getPairedDev();
                if (pairedBluetoothDevices != null) {
                    Iterator<BluetoothDevice> iterator = pairedBluetoothDevices.iterator();
                    while (iterator.hasNext()) {
                        BluetoothDevice device = iterator.next();
                        if (TextUtils.equals(defaultPrinterAddress, device.getAddress())) {
                            con_dev = mService.getDevByMac(device.getAddress());
                            mService.connect(con_dev);
                            return;
                        }
                    }
                }
            }
        }
    }
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_print_mater_label);
    }

    @Override
    public void initViews() {
        actionBarRight = (TextView) findViewById(R.id.toolbar_menu);
        actionBarRight.setOnClickListener(mOnClickListener);

        materNumberTextView = (TextView) findViewById(R.id.fragment_create_requisition_second_mater_number_tv);
        materFormatTextView = (TextView) findViewById(R.id.activity_print_mater_label_mater_format);
        materDescTextView = (TextView) findViewById(R.id.fragment_create_requisition_second_mater_desc_tv);
        branchedTextView = (TextView) findViewById(R.id.fragment_create_requisition_second_branched_tv);
        unitTextView = (TextView) findViewById(R.id.activity_print_mater_label_mater_unit);
        singleUnitTextView = (TextView) findViewById(R.id.activity_print_mater_label_mater_single_unit);
        totalWeightTextView = (TextView) findViewById(R.id.activity_print_mater_label_weight);

        branchEditText = (EditText) findViewById(R.id.fragment_fast_requisition_main_from_branch_et);
        amountEditText = (EditText) findViewById(R.id.fragment_fast_requisition_main_from_xiangshu_et);
        singleEditText = (EditText) findViewById(R.id.activity_print_mater_label_single);
        firmEditText = (EditText) findViewById(R.id.activity_print_mater_label_firm);


        materEditText = (EditText) findViewById(R.id.purchase_receipt_main_code_et);
        materEditText.setOnEditorActionListener(mOnEditorActionListener);

        mDateEditText = (TextView) findViewById(R.id.activity_print_mater_label_date);
        mDateEditText.setOnClickListener(mOnClickListener);

        mScanImageView = (ImageView) findViewById(R.id.fragment_purchase_receipt_scan_iv);
        mScanImageView.setOnClickListener(mOnClickListener);

        mInquireButton = (Button) findViewById(R.id.fragment_purchase_receipt_inquire_bt);
        mInquireButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public void initListeners() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.activity_print_mater_label_date:
                        Calendar calendar = Calendar.getInstance();
                        showSetDatePicker(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                        break;
                    case R.id.fragment_purchase_receipt_inquire_bt:
                        boolean state = mInquireButton.getTag() == null ? false : (boolean) mInquireButton.getTag();
                        if (state) {//当前按钮状态为“清除”
                            branch = new Mater.Branch();
                            refreshShow();
                        } else {
                            handleScanCode(materEditText.getText().toString().trim());
                        }
                        break;
                    case R.id.toolbar_menu:
                        //蓝牙不可用
                        if (mService.isAvailable() == false) {
                            ShowToast("本设备蓝牙不可用,无法连接打印机!");
                        } else if (mService.isBTopen() == false) {
                            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                        } else {
                            Intent serverIntent = new Intent(PrintMaterLabelActivity.this, DeviceListActivity.class);      //运行另外一个类的活动
                            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                        }

                        break;
                    case R.id.fragment_purchase_receipt_scan_iv:
                        startActivityForResult(new Intent(PrintMaterLabelActivity.this, ScanActivity.class), REQUEST_CODE_FOR_SCAN);
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
                    handleScanCode(materEditText.getText().toString().trim());
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
                mLoadingDialog = new LoadingDialog(PrintMaterLabelActivity.this);
                mLoadingDialog.show();
            }

            @Override
            public void onRequestLoading(int requestCode, long current, long count) {

            }

            @Override
            public void onRequestSuccess(JSONObject jsonObject, int requestCode) {
                try {
                    switch (requestCode) {
                        case REQUEST_CODE_INQUIRE_MATER:
                            DecodeManager.decodePrintMaterLabelInquire(jsonObject, requestCode, myHandler);
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    ShowToast("服务器返回错误");
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
                    ShowToast("与服务器连接失败");
                } else {
                    ShowToast("服务器返回错误");
                }
            }
        };
    }

    private void handleScanCode(String code) {
        if (TextUtils.isEmpty(code))
            return;
        String mater = CommonTools.decodeScanString("M", code);
        if (TextUtils.isEmpty(mater)) {
            ShowToast("无效物料标签");
            return;
        }
        String branch = "";
        if (code.contains("*B")) {
            branch = CommonTools.decodeScanString("B", code);
        }
        materEditText.setText("");
        handleInquireMater(mater, branch);
    }

    /**
     * 查询物料详细信息
     *
     * @param mater  送货单号码
     * @param branch 送货单行号
     */
    private void handleInquireMater(String mater, String branch) {
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getApplicationContext()));
        param.put("env", SessionManager.getEnv(getApplicationContext()));
        param.put("mater", mater);
        param.put("branch", branch);
        NetWorkAccessTools.getInstance(getApplicationContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_PRINT_MATER_LABEL_GET_INQUIRE, getApplicationContext()), param, REQUEST_CODE_INQUIRE_MATER, mRequestTaskListener);
    }

    private void refreshShow() {
        materNumberTextView.setText(branch.getMater().getNumber());
        materFormatTextView.setText(branch.getMater().getFormat());
        materDescTextView.setText(branch.getMater().getDesc());
        branchEditText.setText(branch.getMater().getBranchControl() == Mater.BRANCH_CONTROL ? "是" :
                branch.getMater().getBranchControl() == Mater.BRANCH_NO_CONTROL ? "否" : "");
        unitTextView.setText(branch.getMater().getUnit());
        singleUnitTextView.setText(branch.getMater().getSingleUnit());

        if (branch.getMater().getBranchControl() == Mater.BRANCH_CONTROL) {
            branchEditText.setEnabled(true);
            branchEditText.setText(branch.getPo());
        } else {
            branchEditText.setEnabled(false);
            branchEditText.setText("");
        }

        amountEditText.setText(branch.getMater().getQuantity() + "");
        singleEditText.setText(branch.getMater().getSingle() + "");

        if (TextUtils.isEmpty(branch.getMater().getNumber())) {
            mDateEditText.setText("");
            firmEditText.getText().clear();
        }
    }

    @Override
    public void initData() {
        branch = new Mater.Branch();
        mService = new BluetoothService(this, myHandler);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mService != null)
            mService.stop();
        mService = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_FOR_SCAN:
                if (resultCode == Activity.RESULT_OK) {
                    String code = data.getStringExtra("data").trim();
                    materEditText.setText(code);
                    handleScanCode(code);
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    ShowToast("蓝牙开启成功");
                }
                if (con_dev == null) {
                    actionBarRight.setText("点击此处连接打印机");
                    actionBarRight.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.color_printer_disconnected));
                }
                break;
            case REQUEST_CONNECT_DEVICE:     //请求连接某一蓝牙设备
                if (resultCode == Activity.RESULT_OK) {   //已点击搜索列表中的某个设备项
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);  //获取列表项中设备的mac地址
                    con_dev = mService.getDevByMac(address);
                    mService.connect(con_dev);
                }
                break;
        }
    }

    private void collapseButton() {
        if (mInquireButton.getVisibility() == View.GONE)
            return;
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mInquireButton.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mInquireButton.startAnimation(animation);
    }

    private void popUpButton() {
        if (mInquireButton.getVisibility() == View.VISIBLE)
            return;
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
        animation.setDuration(300);
        mInquireButton.setVisibility(View.VISIBLE);
        mInquireButton.startAnimation(animation);
    }

    private void showSetDatePicker(int year, int month, int day) {

        final WheelMain wheelMain;

        LayoutInflater inflater = LayoutInflater.from(this);
        View timepickerview = inflater.inflate(R.layout.timepicker, null);
        ScreenInfo screenInfo = new ScreenInfo(this);
        wheelMain = new WheelMain(timepickerview);
        wheelMain.screenheight = screenInfo.getHeight();
        wheelMain.initDateTimePicker(year, month, day);
        new AlertDialog.Builder(this)
                .setTitle("选择日期")
                .setView(timepickerview)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newDate = wheelMain.getTime();
                        String[] split = newDate.split("-");
                        if (split[1].length() == 1) {
                            split[1] = "0" + split[1];
                        }
                        if (split[2].length() == 1) {
                            split[2] = "0" + split[2];
                        }
                        newDate = split[0] + "/" + split[1] + "/" + split[2];
                        mDateEditText.setText(newDate);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_CODE_INQUIRE_MATER:
                    Bundle bundle = msg.getData();
                    if (bundle.getInt("code") == 1) {
                        branch = bundle.getParcelable("branch");
                        refreshShow();
                    } else {
                        ShowToast("查询失败");
                    }
                    break;
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:   //已连接
                            ShowToast("连接成功");
                            actionBarRight.setText("连接至:" + con_dev.getName());
                            actionBarRight.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.color_printer_connected));
                            SPManager.getInstance(PrintMaterLabelActivity.this).putSP("printer_address_default", con_dev.getAddress());
                        case BluetoothService.STATE_CONNECTING:  //正在连接
                            Log.d("蓝牙调试", "正在连接.....");
                            break;
                        case BluetoothService.STATE_LISTEN:     //监听连接的到来
                        case BluetoothService.STATE_NONE:
                            Log.d("蓝牙调试", "等待连接.....");
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_CONNECTION_LOST:    //蓝牙已断开连接
                    ShowToast("连接断开");
                    actionBarRight.setText("点击此处连接打印机");
                    actionBarRight.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.color_printer_disconnected));
                    con_dev = null;
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:     //无法连接设备
                    ShowToast("无法连接设备");
                    actionBarRight.setText("点击此处连接打印机");
                    actionBarRight.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.color_printer_disconnected));
                    con_dev = null;
                    break;
            }
        }
    }
}

