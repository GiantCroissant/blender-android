package com.giantcroissant.blender;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by liyihao on 15/7/14.
 */
public class Cookbook {

    private String id;

    private String url;
    private String image_url;
    private String name;
    private String description;
    private String ingredient;
    private ArrayList<String> steps;
    private Date uploadTimestamp;
    private Date createTime;
    private int viewedPeople;
    private int collectedPeople;
    private boolean beCollected;
    private Bitmap image;

    public Cookbook()
    {
        this.uploadTimestamp = new Date(System.currentTimeMillis());
        this.createTime = new Date(System.currentTimeMillis());
    }

    public Cookbook(String id, String name, String description, String url, String image_url, String ingredient, ArrayList<String> steps, int viewedPeople, int collectedPeople, boolean beCollected)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.image_url = image_url;
        this.ingredient = ingredient;
        this.steps = steps;
        this.uploadTimestamp = new Date(System.currentTimeMillis());
        this.createTime = new Date(System.currentTimeMillis());
        this.viewedPeople = viewedPeople;
        this.collectedPeople = collectedPeople;
        this.beCollected = beCollected;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getUploadTimestamp() {
        return uploadTimestamp;
    }

    public String getLocaleDatetime() {
        return String.format(Locale.getDefault(), "%tF  %<tR", uploadTimestamp);
    }

    // 裝置區域的日期
    public String getLocaleDate() {
        return String.format(Locale.getDefault(), "%tF", uploadTimestamp);
    }

    // 裝置區域的時間
    public String getLocaleTime() {
        return String.format(Locale.getDefault(), "%tR", uploadTimestamp);
    }

    public void setUploadTimestamp(Date uploadTimestamp) {
        this.uploadTimestamp = uploadTimestamp;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() { return image_url;}

    public void setImageUrl(String image_url) {
        this.image_url = image_url;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public ArrayList<String> getSteps() {
        return steps;
    }

    public void setStep(ArrayList<String> steps) {
        this.steps = steps;
    }

    public int getViewedPeopleCount() {
        return viewedPeople;
    }

    public void setViewedPeopleCount(int viewedPeople) {
        this.viewedPeople = viewedPeople;
    }

    public int getCollectedPeopleCount() {
        return collectedPeople;
    }

    public void setCollectedPeopleCount(int collectedPeople) {
        this.collectedPeople = collectedPeople;
    }

    public boolean getIsCollected() {
        return beCollected;
    }

    public void setIsCollected(boolean beCollected) {
        this.beCollected = beCollected;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

}
