package com.giantcroissant.blender;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by liyihao on 15/8/29.
 */
public class BlenderBluetoothManager {

    public String mDeviceName;
    public String mDeviceAddress;
    public BluetoothGattCharacteristic mClickCharacteristic;
    public LeDeviceListAdapter mLeDeviceListAdapter;
    public BluetoothAdapter mBluetoothAdapter;
    public ListView mGattServicesList;
    public boolean mScanning;
    public Handler bluetoothHandler;

    //cotrol blender
    public final String LIST_NAME = "NAME";
    public final String LIST_UUID = "UUID";
    public ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    public BluetoothLeService mBluetoothLeService ;
//    public boolean mConnected = false;
    public BluetoothManager mblueToothManager;
    public AppCompatActivity currentActicity;
    private static final long SCAN_PERIOD = 1000;

    private static BlenderBluetoothManager uniqueInstance;
    private BlenderBluetoothManager(){} // 使用Private 建構子, 確保類別CameraManager 的物件化只能透過 API:getInstance()
    public static synchronized BlenderBluetoothManager getInstance() { // 使用 synchronized 關鍵字避免同時兩支Thread 進入函數
        if(uniqueInstance == null ) {uniqueInstance = new BlenderBluetoothManager();}
        return uniqueInstance;
    }

    public boolean getConnected()
    {
        return mBluetoothLeService != null && mClickCharacteristic != null;

    }

    // Code to manage Service lifecycle.
    public ServiceConnection mServiceConnection;

    public void startBlueTooth(AppCompatActivity activity)
    {
        currentActicity = activity;
        mServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
                if (!mBluetoothLeService.initialize()) {
                    Log.e("BlenderBluetoothManager", "Unable to initialize Bluetooth");
                    if (currentActicity != null)
                    {
                        currentActicity.finish();
                    }
                }
                // Automatically connects to the device upon successful start-up initialization.
                mBluetoothLeService.connect(mDeviceAddress);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mBluetoothLeService = null;
            }
        };
        bluetoothHandler = new Handler();
        final android.bluetooth.BluetoothManager bluetoothManager =
                (android.bluetooth.BluetoothManager) currentActicity.getSystemService(Context.BLUETOOTH_SERVICE);
        mblueToothManager = bluetoothManager;
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    public void connectBlender(AppCompatActivity activity,BroadcastReceiver gattUpdateReceiver)
    {
        currentActicity = activity;
        activity.registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d("Connect request result", "Connect request result=" + result);

        }
        else
        {
            if (mDeviceName != null && mDeviceAddress != null )
            {
                Intent gattServiceIntent = new Intent(activity, BluetoothLeService.class);
                activity.bindService(gattServiceIntent, mServiceConnection, currentActicity.BIND_AUTO_CREATE);
            }
        }
    }

    public void scanLeDevice(final boolean enable) {
        if (mBluetoothAdapter == null) return;

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            bluetoothHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            for (BluetoothDevice bluetoothDevice : mblueToothManager.getConnectedDevices(BluetoothProfile.GATT_SERVER)) {
                mLeDeviceListAdapter.addDevice(bluetoothDevice);
            }

        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

    }

    public void startBlending(int time,int speed)
    {
        byte[] sendmsg = new byte[10];
        sendmsg[0] = (byte) 0xA5;
        sendmsg[1] = (byte) 0x5A;
        sendmsg[9] = (byte) 0xB3;
        sendmsg[2] = (byte) 0x07;
        sendmsg[3] = (byte) 0x01;
        sendmsg[4] = (byte) (time - 1 % 256);//((npTime.getValue()+1)*5 % 256);
        sendmsg[5] = (byte) (time - 1 / 256);//((npTime.getValue()+1)*5 / 256);
        sendmsg[6] = (byte) (speed % 256);//(npSpeed.getValue() % 256);
        sendmsg[7] = (byte) (speed / 256);//(npSpeed.getValue() / 256);
        sendmsg[8] = (byte) 0x01;

        if(mClickCharacteristic != null && mBluetoothLeService != null)
        {
            mClickCharacteristic.setValue(sendmsg);
            mClickCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            mBluetoothLeService.writeCharacteristic(mClickCharacteristic);
        }
    }

    public void stopBlending()
    {
        byte[] sendmsg = new byte[10];
        sendmsg[0] = (byte) 0xA5;
        sendmsg[1] = (byte) 0x5A;
        sendmsg[9] = (byte) 0xB3;
        sendmsg[2] = (byte) 0x07;
        sendmsg[3] = (byte) 0x01;
        sendmsg[4] = (byte) 0x00;
        sendmsg[5] = (byte) 0x00;
        sendmsg[6] = (byte) 0x00;
        sendmsg[7] = (byte) 0x00;
        sendmsg[8] = (byte) 0x00;

        if(mClickCharacteristic != null && mBluetoothLeService != null)
        {
            mClickCharacteristic.setValue(sendmsg);
            mClickCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            mBluetoothLeService.writeCharacteristic(mClickCharacteristic);
        }
    }

    void stopConnectBlueTooth()
    {
        if(mLeDeviceListAdapter != null)
        {
            mLeDeviceListAdapter.clear();
            scanLeDevice(false);
            mLeDeviceListAdapter.notifyDataSetChanged();
        }
    }

    // Device scan callback.
    public BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    currentActicity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Log.e("XXX",device.getName());

                            mLeDeviceListAdapter.addDevice(device);
                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };

    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    public void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = currentActicity.getResources().getString(R.string.unknown_service);
        String unknownCharaString = currentActicity.getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);

            if(mGattCharacteristics.size() == 3)
            {
                mClickCharacteristic = mGattCharacteristics.get(2).get(0);
            }
        }
    }


}
