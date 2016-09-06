package com.amphenol.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.amphenol.adapter.DeviceListAdapter;
import com.amphenol.amphenol.R;
import com.hoin.btsdk.BluetoothService;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Carl on 2016-09-06 006.
 */
public class DeviceListActivity extends BaseActivity {
    public static final String EXTRA_DEVICE_ADDRESS = "device_address";
    private Button mButton;
    private TextView mTitleTextView;
    private ListView mPairedListView;
    private ListView mAvailableListView;

    private View.OnClickListener mOnClickListener;
    private AdapterView.OnItemClickListener mOnItemClickListener;

    BluetoothService mService = null;
    private DeviceListAdapter mPairedDeviceSimpleAdapter, mAvailableDeviceSimpleAdapter;
    private ArrayList<BluetoothDevice> mPairedDeviceArrayList, mAvailableDeviceArrayList;

    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("wyj", "onCreate");
        super.onCreate(savedInstanceState);
        setResult(Activity.RESULT_CANCELED);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_device_list);
    }

    @Override
    public void initViews() {
        mTitleTextView = (TextView) findViewById(R.id.activity_device_title);
        mButton = (Button) findViewById(R.id.activity_device_bt);
        mButton.setOnClickListener(mOnClickListener);
        mPairedListView = (ListView) findViewById(R.id.activity_device_list_paired);
        mAvailableListView = (ListView) findViewById(R.id.activity_device_list_available);
        mPairedListView.setAdapter(mPairedDeviceSimpleAdapter);
        mPairedListView.setOnItemClickListener(mOnItemClickListener);
        mAvailableListView.setAdapter(mAvailableDeviceSimpleAdapter);
        mAvailableListView.setOnItemClickListener(mOnItemClickListener);
    }

    @Override
    public void initListeners() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDiscovery();
            }
        };
        mOnItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mService.cancelDiscovery();

                BluetoothDevice device = (BluetoothDevice) parent.getItemAtPosition(position);
                Intent intent = new Intent();
                intent.putExtra(EXTRA_DEVICE_ADDRESS, device.getAddress());
                Log.d("连接地址", device.getAddress());

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        };


    }

    @Override
    public void initData() {

        mService = new BluetoothService(this, null);
        mPairedDeviceArrayList = new ArrayList<>();
        mAvailableDeviceArrayList = new ArrayList<>();
        mPairedDeviceSimpleAdapter = new DeviceListAdapter(mPairedDeviceArrayList, this);

        mAvailableDeviceSimpleAdapter = new DeviceListAdapter(mAvailableDeviceArrayList, this);


        Set<BluetoothDevice> pairedDevices = mService.getPairedDev();

        for (BluetoothDevice device : pairedDevices) {
            mPairedDeviceArrayList.add(device);
        }

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                Log.d("wyj", "onReceive:" + action);
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                        mAvailableDeviceSimpleAdapter.add(device);
                    }
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    mTitleTextView.setText("选择连接的蓝牙设备");
                    mButton.setText("立即搜索");
                    if (mAvailableDeviceSimpleAdapter.getCount() == 0 && mPairedDeviceSimpleAdapter.getCount() == 0) {
                        ShowToast("未发现新的设备");
                    }
                }
            }
        };
    }

    private void doDiscovery() {
        if (mService.isDiscovering()) {
            mService.cancelDiscovery();
            mTitleTextView.setText("选择连接的蓝牙设备");
            mButton.setText("立即搜索");
        } else {
            mService.startDiscovery();
            mTitleTextView.setText("搜寻中...");
            mButton.setText("停止搜索");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            mService.cancelDiscovery();
        }
        mService = null;
        unregisterReceiver(mReceiver);
    }
}
