package com.giantcroissant.blender;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by apprentice on 1/29/16.
 */
public class CookbookParcelable implements Parcelable {
    public String id;
    public String url;
    public String imageUrl;
    public String name;
    public String description;
    public String ingredient;
    public ArrayList<CookbookStepParcelable> steps = new ArrayList<>();
    public int viewedPeople;
    public int collectedPeople;
    public boolean beCollected;
    public String imageName;

    public CookbookParcelable(
        String id,
        String url,
        String imageUrl,
        String name,
        String description,
        String ingredient,
        ArrayList<CookbookStepParcelable> steps,
        int viewedPeople,
        int collectedPeople,
        boolean beCollected, String imageName)
    {
        this.id = id;
        this.url = url;
        this.imageUrl = imageUrl;
        this.name = name;
        this.description = description;
        this.ingredient = ingredient;
        this.steps = steps;
        this.viewedPeople = viewedPeople;
        this.collectedPeople = collectedPeople;
        this.beCollected = beCollected;
        this.imageName = imageName;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(url);
        dest.writeString(imageUrl);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(ingredient);
        dest.writeTypedList(steps);
        dest.writeInt(viewedPeople);
        dest.writeInt(collectedPeople);
        dest.writeByte((byte) (beCollected ? 1 : 0));
        dest.writeString(imageName);
    }

    public CookbookParcelable(Parcel in) {
        id = in.readString();
        url = in.readString();
        imageUrl = in.readString();
        name = in.readString();
        description = in.readString();
        ingredient = in.readString();
        in.readTypedList(steps, CookbookStepParcelable.CREATOR);
        viewedPeople = in.readInt();
        collectedPeople = in.readInt();
        beCollected = (in.readByte() != 0);
        imageName = in.readString();
    }

    public static final Parcelable.Creator<CookbookParcelable> CREATOR = new Parcelable.Creator<CookbookParcelable>() {
        public CookbookParcelable createFromParcel(Parcel in) {
            return new CookbookParcelable(in);
        }

        public CookbookParcelable[] newArray(int size) {
            return new CookbookParcelable[size];
        }
    };
}
