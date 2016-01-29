package com.giantcroissant.blender;

import io.realm.RealmObject;

/**
 * Created by apprentice on 1/29/16.
 */
public class CookbookStepRealm extends RealmObject {
    private String stepDesc;
    private String stepSpeed;
    private String stepTime;

    public String getStepDesc() { return stepDesc; }
    public void setStepDesc(String stepDesc) {
        this.stepDesc = stepDesc;
    }

    public String getStepSpeed() { return stepSpeed; }
    public void setStepSpeed(String stepSpeed) {
        this.stepSpeed = stepSpeed;
    }

    public String getStepTime() { return stepTime; }
    public void setStepTime(String stepTime) {
        this.stepTime = stepTime;
    }
}
