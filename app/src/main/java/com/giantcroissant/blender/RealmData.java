package com.giantcroissant.blender;


import io.realm.Realm;

/**
 * Created by liyihao on 15/9/7.
 */
public class RealmData {
    public Realm realm;


    private static RealmData uniqueInstance;


    private RealmData(){} // 使用Private 建構子, 確保類別CameraManager 的物件化只能透過 API:getInstance()
    public static synchronized RealmData getInstance() { // 使用 synchronized 關鍵字避免同時兩支Thread 進入函數
        if(uniqueInstance == null ) {uniqueInstance = new RealmData();}
        return uniqueInstance;
    }
}
