package com.giantcroissant.blender;


import java.util.ArrayList;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;


/**
 * Created by liyihao on 15/8/17.
 */
public class CookBookRealm extends RealmObject {
    @PrimaryKey
    private String Id;

    private String url;
    private String imageUrl;
    private String name;
    private String description;
    private String ingredient;
    private String steps;
    private Date uploadTimestamp;
    private Date createTime;
    private int viewedPeopleCount;
    private int collectedPeopleCount;
    private boolean beCollected;

    private String timeOfSteps;
    private String speedOfSteps;
    private int imageID;

    public String getId() { return Id; }
    public void   setId(String Id) { this.Id = Id; }
    public String getUrl() { return url; }
    public void   setUrl(String url) { this.url = url;}
    public String getImageUrl() { return imageUrl; }
    public void   setImageUrl(String imageUrl) { this.imageUrl = imageUrl;}
    public String getName() { return name; }
    public void   setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void   setDescription(String description) { this.description = description; }
    public String getIngredient() { return ingredient; }
    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }
    public String getSteps() {
        return steps;
    }
    public void setSteps(String steps) {
        this.steps = steps;
    }
    public Date getUploadTimestamp() {
        return uploadTimestamp;
    }
    public void setUploadTimestamp(Date uploadTimestamp) {
        this.uploadTimestamp = uploadTimestamp;
    }

    public Date   getCreateTime() { return createTime; }
    public void   setCreateTime(Date createTime) { this.createTime = createTime; }

    public int getViewedPeopleCount() {
        return viewedPeopleCount;
    }
    public void setViewedPeopleCount(int viewedPeopleCount) {
        this.viewedPeopleCount = viewedPeopleCount;
    }

    public int getCollectedPeopleCount() {
        return collectedPeopleCount;
    }

    public void setCollectedPeopleCount(int collectedPeopleCount) {
        this.collectedPeopleCount = collectedPeopleCount;
    }

    public boolean getBeCollected() {
        return beCollected;
    }

    public void setBeCollected(boolean beCollected) {
        this.beCollected = beCollected;
    }

    public String getTimeOfSteps() {
        return timeOfSteps;
    }
    public void setTimeOfSteps(String timeOfSteps) {
        this.timeOfSteps = timeOfSteps;
    }

    public String getSpeedOfSteps() {
        return speedOfSteps;
    }
    public void setSpeedOfSteps(String speedOfSteps) {
        this.speedOfSteps = speedOfSteps;
    }
    public int getImageID() { return imageID; }
    public void   setImageID(int imageID) { this.imageID = imageID;}
}

