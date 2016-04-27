package com.giantcroissant.blender;

import java.util.ArrayList;

/**
 * Created by apprentice on 1/29/16.
 */
public class ConvertToCookbook {

    public static CookbookParcelable convertToParcelable(Cookbook cookbook) {

        ArrayList<CookbookStepParcelable> cookbookStepParcelables = new ArrayList<>();
        for (CookbookStep cookbookStep : cookbook.getSteps1()) {
            CookbookStepParcelable csp = new CookbookStepParcelable(
                cookbookStep.getStepDesc(),
                cookbookStep.getStepSpeed(),
                cookbookStep.getStepTime()
            );
            cookbookStepParcelables.add(csp);
        }

        return new CookbookParcelable(
            cookbook.getId(),
            cookbook.getCategory(),
            cookbook.getUrl(),
            cookbook.getImageUrl(),
            cookbook.getName(),
            cookbook.getDescription(),
            cookbook.getIngredient(),
            cookbookStepParcelables,
            cookbook.getViewedPeopleCount(),
            cookbook.getCollectedPeopleCount(),
            cookbook.getIsCollected(),
            cookbook.getImageName(),
            cookbook.getVideoCode()
        );
    }

    public static Cookbook convertFromParcelable(CookbookParcelable cookbookParcelable) {
        ArrayList<CookbookStep> cookbookSteps = new ArrayList<>();
        for (CookbookStepParcelable csp : cookbookParcelable.steps) {
            CookbookStep cs = new CookbookStep();
            cs.setStepDesc(csp.stepDesc);
            cs.setStepSpeed(csp.stepSpeed);
            cs.setStepTime(csp.stepTime);
            cookbookSteps.add(cs);
        }

        /*
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
         */

        return new Cookbook(
            cookbookParcelable.id,
            cookbookParcelable.category,
            cookbookParcelable.name,
            cookbookParcelable.description,
            cookbookParcelable.url,
            cookbookParcelable.imageUrl,
            cookbookParcelable.ingredient,
            cookbookSteps,
            cookbookParcelable.viewedPeople,
            cookbookParcelable.collectedPeople,
            cookbookParcelable.beCollected,
            cookbookParcelable.imageName,
            cookbookParcelable.videoCode
        );
    }
}
