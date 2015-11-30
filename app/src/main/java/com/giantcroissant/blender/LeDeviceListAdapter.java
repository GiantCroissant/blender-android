package com.giantcroissant.blender;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by liyihao on 15/9/3.
 */
// Adapter for holding devices found through scanning.
public class LeDeviceListAdapter extends BaseAdapter {
    private ArrayList<BluetoothDevice> mLeDevices;
    ArrayList<BluetoothDevice> tmpLeDevices;
    private LayoutInflater mInflator;

    public LeDeviceListAdapter(LayoutInflater mInflator) {
        super();
        mLeDevices = new ArrayList<BluetoothDevice>();
        tmpLeDevices = new ArrayList<BluetoothDevice>();
        this.mInflator = mInflator;//DeviceScanActivity.this.getLayoutInflater();
    }

    public void addDevice(BluetoothDevice device) {
        if(!mLeDevices.contains(device)) {
            mLeDevices.add(device);
        }
    }

    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
//        for (BluetoothDevice mLeDevice : mLeDevices) {
//            Log.e("XXX",mLeDevice.getName());
//            if(BlueToothData.getInstance().mDeviceAddress.compareTo(mLeDevice.getAddress()) == 0)
//            {
//                tmpLeDevices.add(mLeDevice);
//                Log.e("XXX",mLeDevice.getName());
//            }
//        }

        mLeDevices.clear();

//        for (BluetoothDevice tmpLeDevice : tmpLeDevices) {
//            mLeDevices.add(tmpLeDevice);
//        }
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        BluetoothViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflator.inflate(R.layout.listitem_device, null);
            viewHolder = new BluetoothViewHolder();
            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
            viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (BluetoothViewHolder) view.getTag();
        }

        BluetoothDevice device = mLeDevices.get(i);
        final String deviceName = device.getName();

        if (deviceName != null && deviceName.length() > 0)
            viewHolder.deviceName.setText(deviceName);
        else
            viewHolder.deviceName.setText(R.string.unknown_device);

//        device.getBondState()
        viewHolder.deviceAddress.setText(R.string.disconnected);
        ImageView contectImage = (ImageView) view.findViewById(R.id.contectImage);
        contectImage.setVisibility(View.VISIBLE);
        if(BlenderBluetoothManager.getInstance().mDeviceAddress != null)
        {
            if(BlenderBluetoothManager.getInstance().mDeviceAddress.compareTo(device.getAddress()) == 0)
            {
                viewHolder.deviceAddress.setText(R.string.connected);
                contectImage.setVisibility(View.INVISIBLE);
            }
        }

        return view;
    }
}

