package com.giantcroissant.blender.jsonModel;

import java.util.List;

/**
 * Created by apprentice on 1/29/16.
 */
public class RecipesJsonObject {
    private String id;
    private String title;
    private List<RecipesIngredientJsonObject> ingredients;
    private List<RecipesStepJsonObject> steps;
    private String description;
    private String videoCode;

    public String getId() { return id; }
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
    }

    public List<RecipesIngredientJsonObject> getIngredients() { return ingredients; }
    public void setIngredients(List<RecipesIngredientJsonObject> ingredients) {
        this.ingredients = ingredients;
    }

    public List<RecipesStepJsonObject> getSetps() { return steps; }
    public void setSetps(List<RecipesStepJsonObject> steps) {
        this.steps = steps;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoCode() { return videoCode; }
    public void setVideoCode(String videoCode) {
        this.videoCode = videoCode;
    }
}
