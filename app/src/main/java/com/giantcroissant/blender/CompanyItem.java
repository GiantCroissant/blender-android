package com.giantcroissant.blender;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by liyihao on 15/7/14.
 */
public class CompanyItem {

    private String id;
    private String title;
    private String name;
    private ArrayList<String> contents;
    private String iconUrl;
    private Date createTime;
    private String systemId;
    private Bitmap icon;

    public CompanyItem(String id, String title, String name, ArrayList<String> contents, String iconUrl, String systemId)
    {
        this.id = id;
        this.title = title;
        this.name = name;
        this.contents = contents;
        this.iconUrl = iconUrl;
        this.createTime = new Date(System.currentTimeMillis());
        this.systemId = systemId;
    }

    public CompanyItem(String id, String title, String name, ArrayList<String> contents, Bitmap icon, String systemId)
    {
        this.id = id;
        this.title = title;
        this.name = name;
        this.contents = contents;
        this.icon = icon;
        this.createTime = new Date(System.currentTimeMillis());
        this.systemId = systemId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public String getLocaleDatetime() {
        return String.format(Locale.getDefault(), "%tF  %<tR", createTime);
    }

    // 裝置區域的日期
    public String getLocaleDate() {
        return String.format(Locale.getDefault(), "%tF", createTime);
    }

    // 裝置區域的時間
    public String getLocaleTime() {
        return String.format(Locale.getDefault(), "%tR", createTime);
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getContents() {
        return contents;
    }

    public void setContents( ArrayList<String> contents) {
        this.contents = contents;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }


    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }


    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }


}
