package com.giantcroissant.blender;

import io.realm.Realm;

/**
 * Created by liyihao on 15/8/18.
 */
public interface CookBooksDataFragment {
    /**
     * Called when an item in the navigation drawer is selected.
     */
    void upDateListView(Realm realm);
}
