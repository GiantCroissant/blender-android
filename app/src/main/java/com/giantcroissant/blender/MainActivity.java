package com.giantcroissant.blender;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.giantcroissant.blender.jsonModel.RecipesCollectionDataJsonObject;
import com.giantcroissant.blender.jsonModel.RecipesIngredientJsonObject;
import com.giantcroissant.blender.jsonModel.RecipesJsonObject;
import com.giantcroissant.blender.jsonModel.RecipesStepJsonObject;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmMigration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.RealmSchema;
import rx.Subscriber;
import rx.functions.Func1;

import static android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE;

// adb pull /data/data/com.giantcroissant.blender/files/default.realm
// adb shell rm -r /data/data/com.giantcroissant.blender/files
public class MainActivity extends AppCompatActivity
    implements OnItemClickListener,
    NavigationDrawerFragment.NavigationDrawerCallbacks,
    CookBooksFragment.OnCookBooksFragmentInteractionListener,
    UserDataFragment.OnUserDataFragmentInteractionListener,
    AutoTestFragment.OnAutoTestFragmentInteractionListener,
    AboutCompanyFragment.OnAboutCompanyFragmentInteractionListener,
    DeviceControlFragment.OnDeviceControlFragmentInteractionListener,
    DeviceScanFragment.OnFragmentInteractionListener,
    AboutCompanyInfoFragment.OnAboutCompanyInfoFragmentInteractionListener,
    AboutCompanyItemFragment.OnAboutCompanyItemFragmentInteractionListener,
    View.OnClickListener {
    // Stops scanning after 10 seconds.
    @SuppressWarnings("unused")
    private static final long SCAN_PERIOD = 1000;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_COOKBOOK = 2;
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String TAG = MainActivity.class.getName();
    private int resultCode = 0;
    private int resultPosition = 0;

    @SuppressWarnings("unused")
    private int mTimeOutMills = 5000;

    @SuppressWarnings("unused")
    private int mRefreshRateMills = 500;

    private int mCurrentSelectedPosition = 0;
    private boolean mUserLearnedDrawer;
    private boolean switchIsChecked = false;
    private String resultCookBookListViewID = "";

    private ListView mDrawerList;
    private ActionBar actionBar;
    private DrawerLayout mDrawerLayout;

    @SuppressWarnings("deprecation")
    private ActionBarDrawerToggle mDrawerToggle;

//    private CookBooksFragment cookBooksFragment;
//    private UserDataFragment userDataFragment;
//    private AutoTestFragment autoTestFragment;
//    private AboutCompanyFragment aboutCompanyFragment;

    private NavigationDrawerFragment.NavigationDrawerCallbacks mCallbacks;
    private CharSequence mTitle;
    private Intent searchIntent;
    private Switch blueToothSwitch;

    private SharedPreferences preferences;
    private Realm realm;
    private static final int SCHEMA_VERSION = 1;

    private rx.Observable<String> recipesJsonStringData = rx.Observable.create(new rx.Observable.OnSubscribe<String>() {
        @Override
        public void call(rx.Subscriber<? super String> sub) {
            try {
                InputStream inputStream = getAssets().open("recipes-data.json");
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(inputStreamReader);
                String read = br.readLine();
                while (read != null) {
                    sb.append(read);
                    read = br.readLine();
                }
                sub.onNext(sb.toString());
                sub.onCompleted();
            } catch (Exception e) {
                sub.onError(e);
            }
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = configureRealm();
        loadRecipesFromJson();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserLearnedDrawer = preferences.getBoolean(PREF_USER_LEARNED_DRAWER, false);
        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }

        moveDrawerToTop();
        mTitle = getTitle();

        initActionBar();
        initDrawer();


//        int fragmentId = 0;
//        View mFragmentContainerView = findViewById(fragmentId);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

//        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
////            mDrawerLayout.openDrawer(mDrawerLayout);
//        }

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mCallbacks = this;
        selectItem(mCurrentSelectedPosition);

        BlenderBluetoothManager.getInstance().startBlueTooth(this);
    }

    private void loadRecipesFromJson() {
        recipesJsonStringData
            .map(new Func1<String, RecipesCollectionDataJsonObject>() {
                @Override
                public com.giantcroissant.blender.jsonModel.RecipesCollectionDataJsonObject call(String s) {
                    return new Gson().fromJson(s, com.giantcroissant.blender.jsonModel.RecipesCollectionDataJsonObject.class);
                }
            })
            .map(new Func1<RecipesCollectionDataJsonObject, List<Cookbook>>() {
                @Override
                public List<Cookbook> call(RecipesCollectionDataJsonObject dataJson) {
                    List<Cookbook> cookbooks = new ArrayList<>();
                    for (RecipesJsonObject recipeJson : dataJson.getRecipesCollection()) {

                        // 取得 Ingredient
                        String ingredientDesc = "";
                        for (RecipesIngredientJsonObject ingredientJson : recipeJson.getIngredients()) {
                            ingredientDesc += ingredientJson.getName();
                            if (ingredientJson.getExactMeasurement()) {
                                ingredientDesc += ingredientJson.getAmount() + ingredientJson.getUnit();
                            } else {
                                ingredientDesc += ingredientJson.getSuggestedMeasurement();
                            }
                        }

                        // 取得 Steps
                        List<CookbookStep> cookbookSteps = new ArrayList<>();
                        for (RecipesStepJsonObject stepJson : recipeJson.getSetps()) {
                            CookbookStep cookbookStep = new CookbookStep();
                            cookbookStep.setStepDesc(stepJson.getAction());
                            if (stepJson.getMachineAction() != null) {
                                cookbookStep.setStepSpeed("" + stepJson.getMachineAction().getSpeed());
                                cookbookStep.setStepTime("" + stepJson.getMachineAction().getTime());
                            } else {
                                cookbookStep.setStepSpeed("0");
                                cookbookStep.setStepTime("0");
                            }
                            cookbookSteps.add(cookbookStep);
                        }

                        Cookbook cookbook = new Cookbook(
                            recipeJson.getId(),
                            recipeJson.getTitle(),
                            recipeJson.getDescription(),
                            "",
                            "",
                            ingredientDesc,
                            cookbookSteps,
                            10,
                            20,
                            true);

                        cookbook.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.pictures_01));
                        cookbook.setImageID(R.drawable.pictures_01);
                        cookbook.setImageName(recipeJson.getImage());
                        cookbooks.add(cookbook);
                    }

                    return cookbooks;
                }
            })
            .map(new Func1<List<Cookbook>, List<CookBookRealm>>() {
                @Override
                public List<CookBookRealm> call(List<Cookbook> cookbooks) {
                    List<CookBookRealm> cookbookRealms = new ArrayList<>();

                    for (Cookbook cookbook : cookbooks) {
                        RealmList<CookbookStepRealm> cookbookStepRealms = new RealmList<>();
                        for (CookbookStep cookbookStep : cookbook.getSteps1()) {
                            CookbookStepRealm cookbookStepRealm = new CookbookStepRealm();
                            cookbookStepRealm.setStepDesc(cookbookStep.getStepDesc());
                            cookbookStepRealm.setStepSpeed(cookbookStep.getStepSpeed());
                            cookbookStepRealm.setStepTime(cookbookStep.getStepTime());

                            cookbookStepRealms.add(cookbookStepRealm);
                        }
                        CookBookRealm cookBookRealm = new CookBookRealm();
                        cookBookRealm.setId(cookbook.getId());
                        cookBookRealm.setUrl(cookbook.getUrl());
                        cookBookRealm.setImageUrl(cookbook.getImageUrl());
                        cookBookRealm.setName(cookbook.getName());
                        cookBookRealm.setIngredient(cookbook.getIngredient());
                        cookBookRealm.setDescription(cookbook.getDescription());
                        cookBookRealm.setUrl(cookbook.getUrl());
                        cookBookRealm.setImageUrl(cookbook.getImageUrl());
                        cookBookRealm.setSteps1(cookbookStepRealms);
                        cookBookRealm.setViewedPeopleCount(cookbook.getViewedPeopleCount());
                        cookBookRealm.setCollectedPeopleCount(cookbook.getCollectedPeopleCount());
                        cookBookRealm.setBeCollected(cookbook.getIsCollected());
                        cookBookRealm.setUploadTimestamp(cookbook.getUploadTimestamp());
                        cookBookRealm.setCreateTime(cookbook.getCreateTime());
                        cookBookRealm.setImageID(cookbook.getImageID());
                        cookBookRealm.setImageName(cookbook.getImageName());
                        cookbookRealms.add(cookBookRealm);
                    }
                    return cookbookRealms;
                }
            })
            .subscribe(new Subscriber<List<CookBookRealm>>() {
                @Override
                public void onCompleted() {
                    Log.e(TAG, "onCompleted");
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e(TAG, "onError = " + throwable);
                }

                @Override
                public void onNext(List<CookBookRealm> cookbookRealms) {
                    Log.e(TAG, "onNext cookbookRealms = " + cookbookRealms);
                    realm.beginTransaction();
                    for (CookBookRealm cookBookRealm : cookbookRealms) {
                        Log.e(TAG, "cookBookRealm getImageName = " + cookBookRealm.getImageName());

                        realm.copyToRealmOrUpdate(cookBookRealm);
                    }
                    realm.commitTransaction();
                }
            });
    }

    private Realm configureRealm() {
        RealmMigration migration = new RealmMigration() {
            @Override
            public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                RealmSchema schema = realm.getSchema();
                Log.e(TAG, "oldVersion = " + oldVersion);
                if (oldVersion == 0) {
                    schema.get("CookBookRealm").addField("imageName", String.class);
                    oldVersion++;
                }

                if (oldVersion == newVersion) {
                    Log.e(TAG, "migrate complete");
                }
            }
        };

        RealmConfiguration config = new RealmConfiguration.Builder(this)
            .schemaVersion(SCHEMA_VERSION)
            .migration(migration)
            .build();

        return Realm.getInstance(config);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (blueToothSwitch != null) {
            enableBlueToothIntent();
        }

        if (findViewById(R.id.startBlenderButton) != null) {
            BlenderBluetoothManager.getInstance().connectBlender(this, mGattUpdateReceiver);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (resultCode == 100) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_content, AutoTestFragment.newInstance(2 + 1, switchIsChecked));
            fragmentTransaction.commit();

            checkSupportBLE();
            checkSupportBlueTooth();
            enableBlueToothIntent();

        } else if (resultCode == 103) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            //noinspection PointlessArithmeticExpression
            fragmentTransaction.replace(R.id.main_content, CookBooksFragment.newInstance(0 + 1, realm));
            fragmentTransaction.commit();

        } else if (resultCode == 104) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_content, UserDataFragment.newInstance(1 + 1, realm));
            fragmentTransaction.commit();
        }
    }

    private void moveDrawerToTop() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DrawerLayout drawer = (DrawerLayout) inflater.inflate(R.layout.decor, null); // "null" is important.

        // HACK: "steal" the first child of decor view
        ViewGroup decor = (ViewGroup) getWindow().getDecorView();
        View child = decor.getChildAt(0);
        decor.removeView(child);
        LinearLayout container = (LinearLayout) drawer.findViewById(R.id.drawer_content); // This is the container we defined just now.
        container.addView(child, 0);
        drawer.findViewById(R.id.drawer).setPadding(0, getStatusBarHeight(), 0, 0);

        // Make the drawer replace the first child
        decor.addView(drawer);
    }

//    public boolean isDrawerOpen() {
//        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mDrawerLayout);
//    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

//    private int getContentIdResource() {
//        return getResources().getIdentifier("content", "id", "android");
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        mDrawerToggle.syncState();
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();
        if (id == R.id.action_search) {
            if (searchIntent == null) {
                searchIntent = new Intent(this, SearchActivity.class);
            }
            startActivity(searchIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setElevation(0);
        }
    }

    private void initDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mDrawerList = (ListView) findViewById(R.id.navigationlistView);
        mDrawerLayout.setDrawerListener(createDrawerToggle());

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        ArrayList<NavigationMenuItem> tempNavigationMenuItemList = new ArrayList<>();
        tempNavigationMenuItemList.add(new NavigationMenuItem(getString(R.string.title_section1), R.drawable.icon_recipe));
        tempNavigationMenuItemList.add(new NavigationMenuItem(getString(R.string.title_section2), R.drawable.icon_favorit));
        tempNavigationMenuItemList.add(new NavigationMenuItem(getString(R.string.title_section3), R.drawable.icon_machine_test));
        tempNavigationMenuItemList.add(new NavigationMenuItem(getString(R.string.title_section4), R.drawable.icon_shops));

        mDrawerList.setAdapter(new NavigationMenuItemAdapter(
            this,
            R.layout.navigation_menu_item,
            tempNavigationMenuItemList
        ));
        mDrawerList.setItemChecked(mCurrentSelectedPosition, true);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerList != null) {
            mDrawerList.setItemChecked(position, true);

        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(findViewById(R.id.drawer));

        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);

        }
    }

    @SuppressWarnings("deprecation")
    private DrawerListener createDrawerToggle() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.icon_main_menu, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    preferences.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

            }

            @Override
            public void onDrawerStateChanged(int state) {
            }
        };
        return mDrawerToggle;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        mDrawerLayout.closeDrawer(findViewById(R.id.drawer));
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        switch (position) {
            case 0:
                fragmentTransaction.replace(R.id.main_content, CookBooksFragment.newInstance(position + 1, realm));
                RealmData.getInstance().realm = realm;
                fragmentTransaction.commit();
                break;

            case 1:
                fragmentTransaction.replace(R.id.main_content, UserDataFragment.newInstance(position + 1, realm));
                RealmData.getInstance().realm = realm;
                fragmentTransaction.commit();
                break;

            case 2:
                fragmentTransaction.replace(R.id.main_content, AutoTestFragment.newInstance(position + 1, switchIsChecked));
                fragmentTransaction.commit();

                checkSupportBLE();
                checkSupportBlueTooth();
                enableBlueToothIntent();
                break;

            case 3:
                fragmentTransaction.replace(R.id.main_content, AboutCompanyFragment.newInstance(position + 1));
                fragmentTransaction.commit();
                break;
        }
    }

    public void onSectionAttached(int number) {
        if (actionBar == null) {
            actionBar = getSupportActionBar();
        }
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                if (actionBar != null)
                    actionBar.setTitle(mTitle);

                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                if (actionBar != null)
                    actionBar.setTitle(mTitle);

                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                if (actionBar != null)
                    actionBar.setTitle(mTitle);

                break;
            case 4:
                mTitle = getString(R.string.title_section4);
//                restoreActionBar();
                if (actionBar != null)
                    actionBar.setTitle(mTitle);

                break;
        }
    }

    @SuppressWarnings("deprecation")
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }

    @Override
    public void onClick(View view) {
        FragmentManager fm = getSupportFragmentManager();//if added by xml
        if (view.getId() == R.id.newCookBookButton) {
            CookBooksFragment fragment = (CookBooksFragment) fm.findFragmentById(R.id.main_content);
            fragment.setCurrentCookBooks(realm, 0);

        } else if (view.getId() == R.id.hotCookBookButton) {
            CookBooksFragment fragment = (CookBooksFragment) fm.findFragmentById(R.id.main_content);
            fragment.setCurrentCookBooks(realm, 1);

        } else if (view.getId() == R.id.userRecordCookBookButton) {
            UserDataFragment fragment = (UserDataFragment) fm.findFragmentById(R.id.main_content);
            fragment.setCurrentCookBooks(realm, 0);

        } else if (view.getId() == R.id.userLikeCookBookButton) {
            UserDataFragment fragment = (UserDataFragment) fm.findFragmentById(R.id.main_content);
            fragment.setCurrentCookBooks(realm, 1);

        } else if (view.getId() == R.id.BlenderSettingButton) {
            AutoTestFragment fragment = (AutoTestFragment) fm.findFragmentById(R.id.main_content);
            fragment.setSwitchChecked(switchIsChecked);
            fragment.setCurrentTab(0);

        } else if (view.getId() == R.id.BlenderControlButton) {
            AutoTestFragment fragment = (AutoTestFragment) fm.findFragmentById(R.id.main_content);
            fragment.setCurrentTab(1);
            BlenderBluetoothManager.getInstance().connectBlender(this, mGattUpdateReceiver);

        } else if (view.getId() == R.id.AboutCompanyButton) {
            AboutCompanyFragment fragment = (AboutCompanyFragment) fm.findFragmentById(R.id.main_content);
            fragment.setCurrentTab(0);

        } else if (view.getId() == R.id.AboutGoodButton) {
            AboutCompanyFragment fragment = (AboutCompanyFragment) fm.findFragmentById(R.id.main_content);
            fragment.setCurrentTab(1);

        } else if (view.getId() == R.id.blueToothSwitch) {
            switchConnectBlueTooth();

        } else if (view.getId() == R.id.startBlenderButton) {
            AutoTestFragment fragment = (AutoTestFragment) fm.findFragmentById(R.id.main_content);
            if (!BlenderBluetoothManager.getInstance().getConnected()) {
                fragment.setSwitchChecked(switchIsChecked);
                fragment.setCurrentTab(0);
            }

            EditText setTimeEditText = (EditText) findViewById(R.id.setTimeEditText);
            EditText setSpeedEditText = (EditText) findViewById(R.id.setSpeedEditText);
            BlenderBluetoothManager.getInstance().startBlending(Integer.parseInt(setTimeEditText.getText().toString()), Integer.parseInt(setSpeedEditText.getText().toString()));

        } else if (view.getId() == R.id.stopBlenderButton) {
            AutoTestFragment fragment = (AutoTestFragment) fm.findFragmentById(R.id.main_content);
            if (!BlenderBluetoothManager.getInstance().getConnected()) {
                fragment.setSwitchChecked(switchIsChecked);
                fragment.setCurrentTab(0);
            }
            BlenderBluetoothManager.getInstance().stopBlending();

        } else if (view.getId() == R.id.cancelSettingButton) {
            AutoTestFragment fragment = (AutoTestFragment) fm.findFragmentById(R.id.main_content);
            fragment.setBlenderSettingView(false);

        } else if (view.getId() == R.id.confrimSettingButton) {
            AutoTestFragment fragment = (AutoTestFragment) fm.findFragmentById(R.id.main_content);
            fragment.setValue();
            fragment.setBlenderSettingView(false);

        } else if (view.getId() == R.id.setTimeEditText) {
            AutoTestFragment fragment = (AutoTestFragment) fm.findFragmentById(R.id.main_content);
            fragment.setBlenderSettingView(true);
            fragment.setTimeView();

        } else if (view.getId() == R.id.setSpeedEditText) {
            AutoTestFragment fragment = (AutoTestFragment) fm.findFragmentById(R.id.main_content);
            fragment.setBlenderSettingView(true);
            fragment.setSpeedView();
        }
    }

    public void switchConnectBlueTooth() {
        blueToothSwitch = (Switch) findViewById(R.id.blueToothSwitch);
        TextView blueToothHint = (TextView) findViewById(R.id.blueToothHint);
        TextView blenderHint = (TextView) findViewById(R.id.blenderHint);

        if (blueToothSwitch != null) {
            initializesListViewAdapter();
            enableBlueToothIntent();
            switchIsChecked = blueToothSwitch.isChecked();
            if (switchIsChecked) {
                if (BlenderBluetoothManager.getInstance().mBluetoothAdapter.isEnabled()) {
                    blueToothHint.setText("");
                    blenderHint.setText(R.string.hint_blender);

                    BlenderBluetoothManager.getInstance().mLeDeviceListAdapter.clear();
                    BlenderBluetoothManager.getInstance().scanLeDevice(true);
                }
            } else {

                blenderHint.setText("");
                blueToothHint.setText(R.string.hint_to_connect_bluetooth);
                BlenderBluetoothManager.getInstance().stopConnectBlueTooth();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (blueToothSwitch != null) {
                switchIsChecked = false;
                blueToothSwitch.setChecked(false);
            }
        }

        this.resultCode = resultCode;
        if (resultCode == 100) {
            this.resultPosition = data.getIntExtra("posistion", 0);
            this.resultCookBookListViewID = data.getStringExtra("cookBookListViewID");
        }

        //noinspection StatementWithEmptyBody
        if (requestCode == REQUEST_COOKBOOK) {
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    void checkSupportBLE() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            if (blueToothSwitch != null) {
                switchIsChecked = false;
                blueToothSwitch.setChecked(false);
            }
        }
    }

    void checkSupportBlueTooth() {
        if (BlenderBluetoothManager.getInstance().mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            if (blueToothSwitch != null) {
                switchIsChecked = false;
                blueToothSwitch.setChecked(false);
            }
        }
    }

    void enableBlueToothIntent() {
        if (BlenderBluetoothManager.getInstance().mBluetoothAdapter == null) {
            return;
        }

        if (!BlenderBluetoothManager.getInstance().mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    void initializesListViewAdapter() {
        if (BlenderBluetoothManager.getInstance().mLeDeviceListAdapter == null) {
            BlenderBluetoothManager.getInstance().mLeDeviceListAdapter = new LeDeviceListAdapter(MainActivity.this.getLayoutInflater());
        }
        BlenderBluetoothManager.getInstance().mGattServicesList = (ListView) findViewById(R.id.gattServicesList);
        if (BlenderBluetoothManager.getInstance().mGattServicesList != null) {
            BlenderBluetoothManager.getInstance().mGattServicesList.setAdapter(BlenderBluetoothManager.getInstance().mLeDeviceListAdapter);
            BlenderBluetoothManager.getInstance().mGattServicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                    selectService(position, view);
                }
            });
        }
    }

    private void selectService(int position, View view) {
        TextView textView = (TextView) view.findViewById(R.id.device_name);

        if (textView.getText().toString().compareTo("ITRI_JUICER_v1.0") != 0) return;
        if (BlenderBluetoothManager.getInstance().mBluetoothAdapter.isEnabled()) {
            final BluetoothDevice device = BlenderBluetoothManager.getInstance().mLeDeviceListAdapter.getDevice(position);
            if (device == null) return;

            final Intent intent = new Intent(this, DeviceControlActivity.class);
            intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
            intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());

            if (BlenderBluetoothManager.getInstance().mScanning) {
                //noinspection deprecation
                BlenderBluetoothManager.getInstance().mBluetoothAdapter.stopLeScan(BlenderBluetoothManager.getInstance().mLeScanCallback);
                BlenderBluetoothManager.getInstance().mScanning = false;
            }

            BlenderBluetoothManager.getInstance().mDeviceName = device.getName();
            BlenderBluetoothManager.getInstance().mDeviceAddress = device.getAddress();

            if (resultCode == 100) {
                openCookBookDetail();
            } else {
                startActivity(intent);
            }
        } else {
            switchIsChecked = false;
            blueToothSwitch.setChecked(false);
            BlenderBluetoothManager.getInstance().stopConnectBlueTooth();
            enableBlueToothIntent();
        }

        BlenderBluetoothManager.getInstance().mLeDeviceListAdapter.notifyDataSetChanged();
    }

    void openCookBookDetail() {
        Intent cookbookDetailIntent = new Intent(this, CookBookDetailActivity.class);

        RealmQuery<CookBookRealm> tmpCookBookRealmQuery = realm.where(CookBookRealm.class);
        RealmResults<CookBookRealm> tempCookBookRealmResult = tmpCookBookRealmQuery.contains("Id", resultCookBookListViewID).findAll();
        ArrayList<Cookbook> tmpCookBooks = new ArrayList<>();

        for (CookBookRealm cookBookRealm : tempCookBookRealmResult) {
            List<CookbookStep> cookbookSteps = new ArrayList<>();
            for (CookbookStepRealm cookbookStepRealm : cookBookRealm.getSteps1()) {
                CookbookStep cookbookStep = new CookbookStep();
                cookbookStep.setStepDesc(cookbookStepRealm.getStepDesc());
                cookbookStep.setStepSpeed(cookbookStepRealm.getStepSpeed());
                cookbookStep.setStepTime(cookbookStepRealm.getStepTime());

                cookbookSteps.add(cookbookStep);
            }

            Cookbook newCookBook =
                new Cookbook(cookBookRealm.getId(),
                    cookBookRealm.getName(),
                    cookBookRealm.getDescription(),
                    cookBookRealm.getUrl(),
                    cookBookRealm.getImageUrl(),
                    cookBookRealm.getIngredient(),
                    cookbookSteps,
                    cookBookRealm.getViewedPeopleCount(),
                    cookBookRealm.getCollectedPeopleCount(),
                    cookBookRealm.getBeCollected());

            newCookBook.setUploadTimestamp(cookBookRealm.getUploadTimestamp());
            tmpCookBooks.add(newCookBook);
        }
        cookbookDetailIntent.putExtra("requestCode", 2);
        cookbookDetailIntent.putExtra("position", resultPosition);
        cookbookDetailIntent.putExtra("cookbook", ConvertToCookbook.convertToParceable(tmpCookBooks.get(0)));

        startActivityForResult(cookbookDetailIntent, REQUEST_COOKBOOK);
        resultCode = 0;
    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
//                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
//                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                BlenderBluetoothManager.getInstance().displayGattServices(BlenderBluetoothManager.getInstance().mBluetoothLeService.getSupportedGattServices());

            } else //noinspection StatementWithEmptyBody
                if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        registerReceiver(mGattUpdateReceiver, BlenderBluetoothManager.getInstance().makeGattUpdateIntentFilter());
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

//    private void updateConnectionState(final int resourceId) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
////                isConnected = mBluetoothLeService != null && mClickCharacteristic != null;
////                connectStateId = resourceId;
////                if (isConnectedBlueToothTextView != null)
////                {
////                    isConnectedBlueToothTextView.setText(resourceId);
////                }
//            }
//        });
//    }

    @Override
    public void onFragmentInteraction(String string) {
        if (string.compareTo("Ok") == 0) {
            switchConnectBlueTooth();

        } else if (string.compareTo("Exit") == 0) {
            BlenderBluetoothManager.getInstance().stopConnectBlueTooth();

        } else //noinspection SpellCheckingInspection
            if (string.compareTo("blueToothSwitchOnCheckedChangedtrue") == 0) {
                switchConnectBlueTooth();

            } else //noinspection SpellCheckingInspection
                if (string.compareTo("blueToothSwitchOnCheckedChangedfalse") == 0) {
                    BlenderBluetoothManager.getInstance().stopConnectBlueTooth();
                }
    }

    @Override
    public void onAboutCompanyInfoFragmentInteraction(String string) {

    }

    @Override
    public void onAboutCompanyItemFragmentInteraction(String id) {

    }

    @Override
    public void onAboutCompanyFragmentInteraction(String String) {

    }

    @Override
    public void onAutoTestFragmentInteraction(String String) {

    }

    @Override
    public void onCookBookFragmentInteraction(String string) {

    }

    @Override
    public void onUserDataFragmentInteraction(String String) {

    }

    @Override
    public void onDeviceControlFragmentInteraction(String string) {

    }

}
