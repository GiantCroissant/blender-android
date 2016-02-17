package com.giantcroissant.blender;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.List;
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

    private List<CookbookStep> steps1;

    private Date uploadTimestamp;
    private Date createTime;
    private int viewedPeople;
    private int collectedPeople;
    private boolean beCollected;
    private Bitmap image;
    private int imageID;
    private String imageName;

    public Cookbook()
    {
        this.uploadTimestamp = new Date(System.currentTimeMillis());
        this.createTime = new Date(System.currentTimeMillis());
    }

    public Cookbook(
            String id,
            String name,
            String description,
            String url,
            String image_url,
            String ingredient,
            List<CookbookStep> steps1,
            int viewedPeople,
            int collectedPeople,
            boolean beCollected,
            String imageName)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.image_url = image_url;
        this.ingredient = ingredient;

        this.steps1 = steps1;

        this.uploadTimestamp = new Date(System.currentTimeMillis());
        this.createTime = new Date(System.currentTimeMillis());
        this.viewedPeople = viewedPeople;
        this.collectedPeople = collectedPeople;
        this.beCollected = beCollected;
        this.imageName = imageName;
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

    public List<CookbookStep> getSteps1() { return steps1; }
    public void setSteps1(List<CookbookStep> steps1) {
        this.steps1 = steps1;
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

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String name) {
        this.imageName = name;
    }

}
