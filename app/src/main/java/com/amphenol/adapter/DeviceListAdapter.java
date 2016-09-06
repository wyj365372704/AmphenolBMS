package com.amphenol.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amphenol.amphenol.R;

import java.util.List;

/**
 * Created by Carl on 2016-09-06 006.
 */
public class DeviceListAdapter extends BaseAdapter {
    private List<BluetoothDevice> data;
    private Context mContext;

    public DeviceListAdapter(List<BluetoothDevice> data, Context context) {
        this.data = data;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.device_list_layout, parent, false);
        TextView nameTextView = (TextView) view.findViewById(R.id.devece_list_layout_name_tv);
        TextView addressTextView = (TextView) view.findViewById(R.id.devece_list_layout_address_tv);
        nameTextView.setText(data.get(position).getName());
        addressTextView.setText(data.get(position).getAddress());
        return view;
    }

    public void add(BluetoothDevice bluetoothDevice) {
        if (data == null) {
            return;
        }
        for (BluetoothDevice device : data) {
            if (TextUtils.equals(device.getAddress(), bluetoothDevice.getAddress()))
                return;
        }
        data.add(bluetoothDevice);
        notifyDataSetChanged();
    }
}
