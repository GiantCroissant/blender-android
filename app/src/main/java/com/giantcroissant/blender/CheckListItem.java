package com.giantcroissant.blender;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by liyihao on 15/7/14.
 */
public class CheckListItem {

    private String id;

    private String title;
    private boolean finished;

    public CheckListItem(String id, String title, boolean finished)
    {
        this.id = id;
        this.title = title;
        this.finished = finished;
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

    public boolean getIsFinished() {
        return finished;
    }

    public void setIsFinished(boolean finished) {
        this.finished = finished;
    }
}
