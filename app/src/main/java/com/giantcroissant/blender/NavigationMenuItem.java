package com.giantcroissant.blender;

import java.util.Date;
import java.util.Locale;

/**
 * Created by liyihao on 15/8/17.
 */
public class NavigationMenuItem {


    private String id;
    private String title;
    private int iconID;

    public NavigationMenuItem(String title,int iconID)
    {
        this.title = title;
        this.iconID = iconID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIconID() {
        return iconID;
    }

    public void setIconUrl(int iconID) {
        this.iconID = iconID;
    }


}
