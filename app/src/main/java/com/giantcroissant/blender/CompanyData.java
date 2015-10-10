package com.giantcroissant.blender;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.ArrayList;

/**
 * Created by liyihao on 15/8/29.
 */
public class CompanyData {

    public ArrayList<CompanyItemSystem> companyItemSystems;
    public ArrayList<CompanyItem> companyItems;


    private static CompanyData uniqueInstance;
    private CompanyData(){} // 使用Private 建構子, 確保類別CameraManager 的物件化只能透過 API:getInstance()
    public static synchronized CompanyData getInstance() { // 使用 synchronized 關鍵字避免同時兩支Thread 進入函數
        if(uniqueInstance == null ) {uniqueInstance = new CompanyData();}
        return uniqueInstance;
    }
}
