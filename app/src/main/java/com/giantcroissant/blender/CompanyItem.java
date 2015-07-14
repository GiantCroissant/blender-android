package com.giantcroissant.blender;

import java.util.Date;
import java.util.Locale;

/**
 * Created by liyihao on 15/7/14.
 */
public class CompanyItem {

    private String id;
    private String title;
    private String content;
    private String iconUrl;
    private Date createTime;

    public CompanyItem(String id,String title,String content,String iconUrl)
    {
        this.id = id;
        this.title = title;
        this.content = content;
        this.iconUrl = iconUrl;
        this.createTime = new Date(System.currentTimeMillis());
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOwnerIconUrl() {
        return iconUrl;
    }

    public void setOwnerIconUrl(String ownerIconUrl) {
        this.iconUrl = iconUrl;
    }


}
