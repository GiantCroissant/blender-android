package com.giantcroissant.blender.jsonModel;

/**
 * Created by apprentice on 1/29/16.
 */
public class RecipesStepJsonObject {
    public String action;
    public RecipesMachineAction machineAction;
    public String note;

    public String getAction() { return action; }
    public void setAction(String action) {
        this.action = action;
    }

    public RecipesMachineAction getMachineAction() { return machineAction; }
    public void setAction(RecipesMachineAction machineAction) {
        this.machineAction = machineAction;
    }

    public String getNote() { return note; }
    public void setNote(String note) {
        this.note = note;
    }
}
