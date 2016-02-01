package com.giantcroissant.blender;

import java.util.ArrayList;

/**
 * Created by apprentice on 1/29/16.
 */
public class ConvertToCookbook {

//    public static CookbookParcelable ConvertFromRealmToParceable(CookBookRealm cookbookRealm) {
//
//    }

    public static CookbookParcelable convertToParceable(Cookbook cookbook) {

        ArrayList<CookbookStepParcelable> cookbookStepParcelables = new ArrayList<CookbookStepParcelable>();
        for(CookbookStep cookbookStep : cookbook.getSteps1()) {
            CookbookStepParcelable csp = new CookbookStepParcelable(
                    cookbookStep.getStepDesc(),
                    cookbookStep.getStepSpeed(),
                    cookbookStep.getStepTime()
            );
            cookbookStepParcelables.add(csp);
        }

        CookbookParcelable cp = new CookbookParcelable(
                cookbook.getId(),
                cookbook.getUrl(),
                cookbook.getImageUrl(),
                cookbook.getName(),
                cookbook.getDescription(),
                cookbook.getIngredient(),
                cookbookStepParcelables,
                cookbook.getViewedPeopleCount(),
                cookbook.getCollectedPeopleCount(),
                cookbook.getIsCollected()
        );

        return cp;
    }

    public static Cookbook convertFromParceable(CookbookParcelable cookbookParcelable) {
        ArrayList<CookbookStep> cookbookSteps = new ArrayList<CookbookStep>();
        for(CookbookStepParcelable csp : cookbookParcelable.steps) {
            CookbookStep cs = new CookbookStep();
            cs.setStepDesc(csp.stepDesc);
            cs.setStepSpeed(csp.stepSpeed);
            cs.setStepTime(csp.stepTime);

            cookbookSteps.add(cs);
        }

        Cookbook c = new Cookbook(
                cookbookParcelable.id,
                cookbookParcelable.url,
                cookbookParcelable.imageUrl,
                cookbookParcelable.name,
                cookbookParcelable.description,
                cookbookParcelable.ingredient,
                cookbookSteps,
                cookbookParcelable.viewedPeople,
                cookbookParcelable.collectedPeople,
                cookbookParcelable.beCollected
        );

        return c;
    }
}
