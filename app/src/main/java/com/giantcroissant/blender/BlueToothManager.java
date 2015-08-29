package com.giantcroissant.blender;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;

/**
 * Created by liyihao on 15/8/29.
 */
public class BlueToothManager {

//    public String mJsonText;
//    public String CurrentCameraName;
//    public String CurrentCameraURL;
    public Intent gattServiceIntent;
    public BluetoothLeService bluetoothLeService;
    public BluetoothGattCharacteristic mClickCharacteristic;

    private static BlueToothManager uniqueInstance;
    private BlueToothManager(){} // 使用Private 建構子, 確保類別CameraManager 的物件化只能透過 API:getInstance()
    public static synchronized BlueToothManager getInstance() { // 使用 synchronized 關鍵字避免同時兩支Thread 進入函數
        if(uniqueInstance == null ) {uniqueInstance = new BlueToothManager();}
        return uniqueInstance;
    }
}
