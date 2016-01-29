package com.giantcroissant.blender.jsonModel;

/**
 * Created by apprentice on 1/29/16.
 */
public class RecipesIngredientJsonObject {
    private String name;
    private Boolean exactMeasurement;
    private String amount;
    private String unit;
    private String suggestedMeasurement;

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }

    public Boolean getExactMeasurement() { return exactMeasurement; }
    public void setExactMeasurement(Boolean exactMeasurement) {
        this.exactMeasurement = exactMeasurement;
    }

    public String getAmount() { return amount; }
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUnit() { return unit; }
    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSuggestedMeasurement() { return suggestedMeasurement; }
    public void setSuggestedMeasurement(String suggestedMeasurement) {
        this.suggestedMeasurement = suggestedMeasurement;
    }

}
