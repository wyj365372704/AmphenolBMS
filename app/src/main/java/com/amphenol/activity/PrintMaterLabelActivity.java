package com.amphenol.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SPManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.amphenol.R;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.amphenol.entity.Mater;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.Char2BigUtil;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;
import com.amphenol.utils.QRCodeUtil;
import com.btsdk.BluetoothService;
import com.btsdk.PrintPic;
import com.graduate.squirrel.ui.wheel.ScreenInfo;
import com.graduate.squirrel.ui.wheel.WheelMain;

import org.json.JSONObject;

/**
 * Created by Carl on 2016-09-05 005.
 */
public class PrintMaterLabelActivity extends ScannedBaseActivity {
    private static final int REQUEST_CODE_FOR_SCAN = 0x10;
    private static final int REQUEST_CODE_INQUIRE_MATER = 0X11;
    private static final int REQUEST_ENABLE_BT = 0x12;
    private static final int REQUEST_CONNECT_DEVICE = 0x13;
    private ImageView mScanImageView;
    private Button mInquireButton, mPrintButton;
    private TextView actionBarRight;
    private TextView materNumberTextView, materFormatTextView, materDescTextView, mBranchedTextView, unitTextView, singleUnitTextView, totalWeightTextView, mDateEditText;
    private View.OnClickListener mOnClickListener;
    private EditText materEditText, mBranchEditText, amountEditText, singleEditText, firmEditText;

    private Mater.Branch branch;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private MyHandler myHandler = new MyHandler();
    private BluetoothService mService;
    private BluetoothDevice con_dev = null;
    private TextWatcher mSingleTextWatcher;
    private TextWatcher mAmountTextWatcher;

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
            actionBarRight.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_bluetooth_disable));
        } else if (mService.isBTopen() == false) {
            actionBarRight.setText("点击此处打开蓝牙");
            actionBarRight.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_printer_disconnected));
//            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (con_dev == null) {
                actionBarRight.setText("点击此处连接打印机");
                actionBarRight.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_printer_disconnected));
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
        mPrintButton = (Button) findViewById(R.id.fragment_fast_requisition_main_submit_bt);
        mPrintButton.setOnClickListener(mOnClickListener);

        actionBarRight = (TextView) findViewById(R.id.toolbar_menu);
        actionBarRight.setOnClickListener(mOnClickListener);

        materNumberTextView = (TextView) findViewById(R.id.fragment_create_requisition_second_mater_number_tv);
        materFormatTextView = (TextView) findViewById(R.id.activity_print_mater_label_mater_format);
        materDescTextView = (TextView) findViewById(R.id.fragment_create_requisition_second_mater_desc_tv);
        mBranchedTextView = (TextView) findViewById(R.id.fragment_create_requisition_second_branched_tv);
        unitTextView = (TextView) findViewById(R.id.activity_print_mater_label_mater_unit);
        singleUnitTextView = (TextView) findViewById(R.id.activity_print_mater_label_mater_single_unit);
        totalWeightTextView = (TextView) findViewById(R.id.activity_print_mater_label_weight);

        mBranchEditText = (EditText) findViewById(R.id.fragment_fast_requisition_main_from_branch_et);
        mBranchEditText.setTransformationMethod(new Char2BigUtil());
        amountEditText = (EditText) findViewById(R.id.fragment_fast_requisition_main_from_xiangshu_et);
        amountEditText.addTextChangedListener(mAmountTextWatcher);
        singleEditText = (EditText) findViewById(R.id.activity_print_mater_label_single);
        singleEditText.addTextChangedListener(mSingleTextWatcher);
        firmEditText = (EditText) findViewById(R.id.activity_print_mater_label_firm);


        materEditText = (EditText) findViewById(R.id.purchase_receipt_main_code_et);

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
                        showSetDatePicker(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));
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
                    case R.id.fragment_fast_requisition_main_submit_bt:
                        if (con_dev == null) {
                            ShowToast("未连接上打印机");
                            break;
                        }
                        if (TextUtils.isEmpty(branch.getMater().getNumber())) {
                            ShowToast("未查询物料标签");
                            break;
                        }
                        if (branch.getMater().getBranchControl() == Mater.BRANCH_CONTROL && TextUtils.isEmpty(mBranchEditText.getText().toString().toUpperCase())) {
                            ShowToast("该物料批次控制,请输入批号");
                            break;
                        }

                        if (TextUtils.isEmpty(mDateEditText.getText().toString())) {
                            ShowToast("请选择日期");
                            break;
                        }
                        if (TextUtils.isEmpty(firmEditText.getText().toString())) {
                            ShowToast("请输入厂商");
                            break;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(PrintMaterLabelActivity.this);
                        builder.setTitle("确认打印").setMessage("将调用打印机" + con_dev.getName() + "执行打印任务?");
                        builder.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    handlePrint(branch.getMater().getNumber(), branch.getMater().getDesc(), branch.getMater().getFormat(),
                                            amountEditText.getText().toString().trim(), branch.getMater().getUnit(),
                                            singleEditText.getText().toString().trim(), branch.getMater().getSingleUnit(),
                                            totalWeightTextView.getText().toString().trim().replace("KG", ""), "KG",
                                            mDateEditText.getText().toString(), firmEditText.getText().toString().trim(),
                                            branch.getMater().getBranchControl() == Mater.BRANCH_CONTROL ? true : false, mBranchEditText.getText().toString().trim());
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        builder.create().show();
                        break;
                }
            }
        };

        mSingleTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double single = 0;
                try {
                    single = Double.parseDouble(String.valueOf(s));
                } catch (Throwable e) {
                    ShowToast("单重输入非法");
                }
                updateReceiptTotalWeight();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mAmountTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double total = 0;
                try {
                    total = Double.parseDouble(String.valueOf(s));
                } catch (Throwable e) {
                    ShowToast("实收总数输入非法");
                }
                updateReceiptTotalWeight();
            }

            @Override
            public void afterTextChanged(Editable s) {

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

    private void handlePrint(String number, String desc, String format, String amount,
                             String unit, String single, String singleUnit, String totalWeight, String weightUnit,
                             String date, String firm, boolean branchControl, String branch) throws UnsupportedEncodingException {
        Bitmap bitmap = fif(number, desc, format, amount, unit, branchControl ? branch : "", single, singleUnit, totalWeight, weightUnit, date, firm);
        byte[] sendData = null;
        PrintPic pg = new PrintPic();
        pg.initCanvas(bitmap.getWidth());
        pg.initPaint();
        pg.drawImage(0, 0, bitmap);
        sendData = pg.printDraw();
//        mService.write(new byte[]{0x1B,0x61,0x01});
        mService.write(sendData);   //打印byte流数据
        mService.write(new byte[]{0x1B, 0x4A, 0x60});

        Log.d("蓝牙调试", "" + sendData.length);

    }

    private Bitmap fif(String mater, String desc, String format, String amount, String unit, String branch, String single, String singleUnit, String weight, String weightUnit, String date, String firm) {
        String qCode = "*M" + mater + "*Q" + amount + "*B" + branch;

        float width = 580;
        int lineSpace = 18;
        int currentBottonBase = 0;
        float textSize = 24f;
        float height = 0;

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(textSize);

        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        height = 6 * (Math.abs(fontMetrics.top)) + 7 * lineSpace + Math.abs(fontMetrics.bottom);

        Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT);

        //画二维码
        Bitmap qrBirmap = QRCodeUtil.createQRImage(qCode, 3 * lineSpace + 3 * (int) Math.abs(fontMetrics.top), 3 * lineSpace + 3 * (int) Math.abs(fontMetrics.top), null);
        canvas.drawBitmap(qrBirmap, width - qrBirmap.getWidth() - lineSpace, lineSpace, paint);

        String message = "物料:" + mater;
        currentBottonBase += Math.abs(fontMetrics.top) + lineSpace;
        canvas.drawText(message, lineSpace, currentBottonBase, paint);

        message = "说明:" + desc;
        currentBottonBase += Math.abs(fontMetrics.top) + lineSpace;
        canvas.drawText(message, lineSpace, currentBottonBase, paint);

        message = "规格:" + format;
        currentBottonBase += Math.abs(fontMetrics.top) + lineSpace;
        canvas.drawText(message, lineSpace, currentBottonBase, paint);

        message = "数量:" + amount + " " + unit;
        currentBottonBase += Math.abs(fontMetrics.top) + lineSpace;
        canvas.drawText(message, lineSpace, currentBottonBase, paint);

        message = "批号:" + branch;
        canvas.drawText(message, canvas.getWidth() / 2, currentBottonBase, paint);

        message = "单重:" + single + " " + singleUnit;
        currentBottonBase += Math.abs(fontMetrics.top) + lineSpace;
        canvas.drawText(message, lineSpace, currentBottonBase, paint);

        message = "净重:" + weight + " " + weightUnit;
        canvas.drawText(message, canvas.getWidth() / 2, currentBottonBase, paint);

        message = "日期:" + date;
        currentBottonBase += Math.abs(fontMetrics.top) + lineSpace;
        canvas.drawText(message, lineSpace, currentBottonBase, paint);

        message = "厂商:" + firm;
        canvas.drawText(message, canvas.getWidth() / 2, currentBottonBase, paint);

        //画边框
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRect(lineSpace / 2, lineSpace / 2, width - lineSpace / 2, height - lineSpace / 2, paint);

        return bitmap;
    }

    private void updateReceiptTotalWeight() {
        double single = 0;
        double amount = 0;

        try {
            amount = Double.parseDouble(amountEditText.getText().toString().trim());
            single = Double.parseDouble(singleEditText.getText().toString().trim());
        } catch (Throwable e) {
        }

        if (!TextUtils.isEmpty(branch.getMater().getUnit())) {
            if (TextUtils.equals(branch.getMater().getSingleUnit(), "GM") || TextUtils.equals(branch.getMater().getSingleUnit(), "gm") ||
                    TextUtils.equals(branch.getMater().getSingleUnit(), "G") || TextUtils.equals(branch.getMater().getSingleUnit(), "g")) {
                totalWeightTextView.setText(new BigDecimal(Double.toString(single)).multiply(new BigDecimal(Double.toString(amount))).divide(new BigDecimal(Double.toString(1000d))).toString() + "KG");
                return;
            } else if (TextUtils.equals(branch.getMater().getSingleUnit(), "KG") || TextUtils.equals(branch.getMater().getSingleUnit(), "kg")) {
                totalWeightTextView.setText(new BigDecimal(Double.toString(single)).multiply(new BigDecimal(Double.toString(amount))).toString() + "KG");
                return;
            }
        }
        totalWeightTextView.setText("0KG");
    }

    @Override
    protected void handleScanCode(String code) {
        if (TextUtils.isEmpty(code))
            return;
        boolean state = mInquireButton.getTag() == null ? false : (boolean) mInquireButton.getTag();
        if (state) {//当前按钮状态为“清除”
            handleScanBranch(code);
        } else {
            handleScanMater(code);
        }
    }

    private void handleScanMater(String code) {
        String mater = CommonTools.decodeScanString(PropertiesUtil.getInstance(getApplicationContext()).getValue(PropertiesUtil.BARCODE_PREFIX_MATER, ""), code);
        if (TextUtils.isEmpty(mater)) {
            ShowToast("无效物料标签");
            return;
        }
        materEditText.setText(mater);
        handleScanBranch(code);
        handleInquireMater(mater, mBranchEditText.getText().toString().toUpperCase());
    }

    private void handleScanBranch(String code) {
        String branch = CommonTools.decodeScanString(PropertiesUtil.getInstance(getApplicationContext()).getValue(PropertiesUtil.BARCODE_PREFIX_BRANCH, ""), code);
        if (!branch.isEmpty()) {
            mBranchEditText.setText(branch);
        }
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
        mBranchedTextView.setText(branch.getMater().getBranchControl() == Mater.BRANCH_CONTROL ? "是" :
                branch.getMater().getBranchControl() == Mater.BRANCH_NO_CONTROL ? "否" : "");
        unitTextView.setText(branch.getMater().getUnit());
        singleUnitTextView.setText(branch.getMater().getSingleUnit());

        if (branch.getMater().getBranchControl() == Mater.BRANCH_CONTROL) {
            mBranchEditText.setEnabled(true);
            mBranchEditText.setText(branch.getPo());
        } else {
            mBranchEditText.setEnabled(false);
            mBranchEditText.setText("");
        }

        amountEditText.setText(branch.getMater().getQuantity() + "");
        singleEditText.setText(branch.getMater().getSingle() + "");

        updateReceiptTotalWeight();

        if (TextUtils.isEmpty(branch.getMater().getNumber())) {
            mInquireButton.setTag(false);
            mInquireButton.setText("查询");
            materEditText.getText().clear();
            materEditText.setHint("输入领料单号");
            mDateEditText.setText("");
            firmEditText.getText().clear();
            collapseButton();
        } else {
            mInquireButton.setText("清除");
            mInquireButton.setTag(true);
            materEditText.setText(branch.getMater().getNumber());
            popUpButton();
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
                    actionBarRight.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_printer_disconnected));
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
        if (mPrintButton.getVisibility() == View.GONE)
            return;
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPrintButton.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mPrintButton.startAnimation(animation);
    }

    private void popUpButton() {
        if (mPrintButton.getVisibility() == View.VISIBLE)
            return;
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
        animation.setDuration(300);
        mPrintButton.setVisibility(View.VISIBLE);
        mPrintButton.startAnimation(animation);
    }

    private void showSetDatePicker(int year, int month, int day, int hour, int minute) {

        final WheelMain wheelMain;

        LayoutInflater inflater = LayoutInflater.from(this);
        View timepickerview = inflater.inflate(R.layout.timepicker, null);
        ScreenInfo screenInfo = new ScreenInfo(this);
        wheelMain = new WheelMain(timepickerview, true);
        wheelMain.screenheight = screenInfo.getHeight();
        wheelMain.initDateTimePicker(year, month, day, hour, minute);
        new AlertDialog.Builder(this)
                .setTitle("选择日期")
                .setView(timepickerview)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mDateEditText.setText(wheelMain.getTime());
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
                            actionBarRight.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_printer_connected));
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
                    actionBarRight.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_printer_disconnected));
                    con_dev = null;
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:     //无法连接设备
                    ShowToast("无法连接设备");
                    actionBarRight.setText("点击此处连接打印机");
                    actionBarRight.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_printer_disconnected));
                    con_dev = null;
                    break;
            }
        }
    }
}

