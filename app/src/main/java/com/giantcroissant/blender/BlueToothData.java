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
public class BlueToothData {

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

    private static BlueToothData uniqueInstance;
    private BlueToothData(){} // 使用Private 建構子, 確保類別CameraManager 的物件化只能透過 API:getInstance()
    public static synchronized BlueToothData getInstance() { // 使用 synchronized 關鍵字避免同時兩支Thread 進入函數
        if(uniqueInstance == null ) {uniqueInstance = new BlueToothData();}
        return uniqueInstance;
    }

    public boolean getConnected()
    {
        return BlueToothData.getInstance().mBluetoothLeService != null && BlueToothData.getInstance().mClickCharacteristic != null;

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
                    Log.e("Unable to initialize Bluetooth", "Unable to initialize Bluetooth");
                    if(currentActicity != null)
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

    public void startBlender(AppCompatActivity activity,BroadcastReceiver mGattUpdateReceiver)
    {
        currentActicity = activity;

        activity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d("Connect request result", "Connect request result=" + result);

        }
        else
        {

            mDeviceName = BlueToothData.getInstance().mDeviceName;
            mDeviceAddress = BlueToothData.getInstance().mDeviceAddress;

            if(mDeviceName != null && mDeviceAddress != null )
            {
                Intent gattServiceIntent = new Intent(activity, BluetoothLeService.class);
                activity.bindService(gattServiceIntent, mServiceConnection, currentActicity.BIND_AUTO_CREATE);
            }

            mClickCharacteristic = BlueToothData.getInstance().mClickCharacteristic;

        }
    }

    public void scanLeDevice(final boolean enable) {
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
        BlueToothData.getInstance().mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

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
