package com.giantcroissant.blender;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by liyihao on 15/8/29.
 */
public class CookToDoData {

    public boolean doing = false;
    public int currentStateIndex = 0;
    public String cookBookID = "";


    private static CookToDoData uniqueInstance;
    private CookToDoData(){} // 使用Private 建構子, 確保類別CameraManager 的物件化只能透過 API:getInstance()
    public static synchronized CookToDoData getInstance() { // 使用 synchronized 關鍵字避免同時兩支Thread 進入函數
        if(uniqueInstance == null ) {uniqueInstance = new CookToDoData();}
        return uniqueInstance;
    }

    









}
