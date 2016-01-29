package com.giantcroissant.blender.jsonModel;

import java.util.List;

/**
 * Created by apprentice on 1/29/16.
 */
public class RecipesCollectionDataJsonObject {
    private List<RecipesJsonObject> recipesCollection;

    public List<RecipesJsonObject> getRecipesCollection() { return recipesCollection; }
    public void setRecipesCollection(List<RecipesJsonObject> recipesCollection) {
        this.recipesCollection = recipesCollection;
    }
}
