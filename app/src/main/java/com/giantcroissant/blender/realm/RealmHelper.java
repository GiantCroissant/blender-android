package com.giantcroissant.blender.realm;

import android.content.Context;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by ayo on 2/18/16.
 */
public class RealmHelper {
    private static final int SCHEMA_VERSION = 2;

    private static RealmConfiguration mRealmConfig;

    public static Realm getRealmInstance(Context context) {
        if (mRealmConfig == null) {
            RealmMigration migration = new RealmMigration() {
                @Override
                public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                    RealmSchema schema = realm.getSchema();
                    if (oldVersion == 0) {
                        schema.get("CookBookRealm").addField("imageName", String.class);
                        oldVersion++;
                    }

                    if (oldVersion == 1) {
                        schema.get("CookBookRealm").addField("videoCode", String.class);
                        oldVersion++;
                    }

                    if (oldVersion == newVersion) {
                    }
                }
            };

            mRealmConfig = new RealmConfiguration.Builder(context)
                .schemaVersion(SCHEMA_VERSION)
                .migration(migration)
                .build();
        }
        return Realm.getInstance(mRealmConfig);
    }
}
