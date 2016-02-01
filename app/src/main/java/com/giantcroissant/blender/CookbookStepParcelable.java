package com.giantcroissant.blender;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by apprentice on 1/29/16.
 */
public class CookbookStepParcelable implements Parcelable {
    public String stepDesc;
    public String stepSpeed;
    public String stepTime;

    public CookbookStepParcelable(String stepDesc, String stepSpeed, String stepTime) {
        this.stepDesc = stepDesc;
        this.stepSpeed= stepSpeed;
        this.stepTime = stepTime;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stepDesc);
        dest.writeString(stepSpeed);
        dest.writeString(stepTime);
    }

    public CookbookStepParcelable(Parcel in) {
        stepDesc = in.readString();
        stepSpeed = in.readString();
        stepTime = in.readString();
    }

    public static final Parcelable.Creator<CookbookStepParcelable> CREATOR = new Parcelable.Creator<CookbookStepParcelable>() {
        public CookbookStepParcelable createFromParcel(Parcel in) {
            return new CookbookStepParcelable(in);
        }

        public CookbookStepParcelable[] newArray(int size) {
            return new CookbookStepParcelable[size];
        }
    };
}
