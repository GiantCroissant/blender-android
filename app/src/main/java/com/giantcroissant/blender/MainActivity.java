package com.giantcroissant.blender;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static android.bluetooth.BluetoothAdapter.*;

public class MainActivity extends AppCompatActivity
        implements OnItemClickListener,
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        CookBooksFragment.OnCookBooksFragmentInteractionListener,
        UserDataFragment.OnUserDataFragmentInteractionListener,
        AutoTestFragment.OnAutoTestFragmentInteractionListener,
        AboutCompanyFragment.OnAboutCompanyFragmentInteractionListener,
        DeviceControlFragment.OnFragmentInteractionListener,
        DeviceScanFragment.OnFragmentInteractionListener,
        View.OnClickListener
{
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    private int mTimeOutMills = 5000;
    private int mRefreshRateMills = 500;
    private ActionBar actionBar;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private View mFragmentContainerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private int fragmentId;
    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private Bitmap tmpBitmap;

    private CookBooksFragment cookBooksFragment;
    private UserDataFragment userDataFragment;
    private AutoTestFragment autoTestFragment;
    private AboutCompanyFragment aboutCompanyFragment;
    private NavigationDrawerFragment.NavigationDrawerCallbacks mCallbacks;
    private CharSequence mTitle;
    private SharedPreferences sp;

    private Realm realm;
    private RealmQuery<CookBookRealm> cookBookRealmQuery;
    private RealmResults<CookBookRealm> cookBookRealmResult;

    private Intent searchIntent;

    private ArrayList<Cookbook> cookBooks;

    private Switch blueToothSwitch;
    private TextView blueToothHint;
    private TextView blenderHint;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private ListView mGattServicesList;
    private boolean mScanning;
    private Handler bluetoothHandler;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        moveDrawerToTop();
        mTitle = getTitle();

        initActionBar() ;
        initDrawer();
        getRealm();

        fragmentId = 0;

        mFragmentContainerView = findViewById(fragmentId);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
//            mDrawerLayout.openDrawer(mDrawerLayout);
        }

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mCallbacks = (NavigationDrawerFragment.NavigationDrawerCallbacks) this;
        selectItem(mCurrentSelectedPosition);

        bluetoothHandler = new Handler();
        final android.bluetooth.BluetoothManager bluetoothManager =
                (android.bluetooth.BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        cookBookRealmQuery = realm.where(CookBookRealm.class);
        getCookBooks();

        FragmentManager fm = getSupportFragmentManager();//if added by xml
        if((CookBooksDataFragment)fm.findFragmentById(R.id.main_content) != null)
        {
            CookBooksDataFragment fragment = (CookBooksDataFragment)fm.findFragmentById(R.id.main_content);
            fragment.upDateListView(realm);
        }

        if(blueToothSwitch != null)
        {
            enableBlueToothIntent();
        }

    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        cookBookRealmQuery = realm.where(CookBookRealm.class);
        getCookBooks();

        FragmentManager fm = getSupportFragmentManager();//if added by xml
        if((CookBooksDataFragment)fm.findFragmentById(R.id.main_content) != null)
        {
            CookBooksDataFragment fragment = (CookBooksDataFragment)fm.findFragmentById(R.id.main_content);
            fragment.upDateListView(realm);
        }
    }

    private void moveDrawerToTop() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DrawerLayout drawer  = (DrawerLayout) inflater.inflate(R.layout.decor, null); // "null" is important.

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

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mDrawerLayout);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private int getContentIdResource() {
        return getResources().getIdentifier("content", "id", "android");
    }

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
            if(searchIntent == null)
            {
                searchIntent  = new Intent(this, SearchActivity.class);
            }
            startActivity(searchIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setElevation(0);
    }

    private void initDrawer() {
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawerlayout);
        mDrawerList = (ListView)findViewById(R.id.navigationlistView);
        mDrawerLayout.setDrawerListener(createDrawerToggle());

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        ArrayList<NavigationMenuItem> tempNavigationMenuItemList = new ArrayList<NavigationMenuItem>();
        tempNavigationMenuItemList.add(new NavigationMenuItem(getString(R.string.title_section1),R.drawable.icon_recipe));
        tempNavigationMenuItemList.add(new NavigationMenuItem(getString(R.string.title_section2),R.drawable.icon_favorit));
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
//        Log.e("XXXX",String.valueOf(position));
        if (mDrawerList != null) {
            mDrawerList.setItemChecked(position, true);
//            Log.e("XXXX", "xxx2");

        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer((RelativeLayout)findViewById(R.id.drawer));

//            mDrawerLayout.closeDrawer(mDrawerLayout);
//            Log.e("XXXX","xxx3");

        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
//            Log.e("XXXX", "xxx4");
        }
    }

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
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
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
//        mDrawerLayout.closeDrawer(mFragmentContainerView);
        mDrawerLayout.closeDrawer((RelativeLayout)findViewById(R.id.drawer));
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        switch (position) {
            case 0:
                if (cookBooksFragment == null)
                {
                    cookBooksFragment = new CookBooksFragment();
                }

                cookBookRealmQuery = realm.where(CookBookRealm.class);
                fragmentTransaction.replace(R.id.main_content, cookBooksFragment.newInstance(position + 1, realm));
                fragmentTransaction.commit();
                break;
            case 1:
                if (userDataFragment == null)
                {
                    userDataFragment = new UserDataFragment();
                }

                cookBookRealmQuery = realm.where(CookBookRealm.class);
                fragmentTransaction.replace(R.id.main_content, userDataFragment.newInstance(position + 1, realm));
                fragmentTransaction.commit();
                break;
            case 2:
                if (autoTestFragment == null)
                {
                    autoTestFragment = new AutoTestFragment();
                }

                cookBookRealmQuery = realm.where(CookBookRealm.class);
                fragmentTransaction.replace(R.id.main_content, autoTestFragment.newInstance(position + 1));
                fragmentTransaction.commit();

                checkSupportBLE();
                checkSupportBlueTooth();
                enableBlueToothIntent();

                break;
            case 3:
                if (aboutCompanyFragment == null)
                {
                    aboutCompanyFragment = new AboutCompanyFragment();
                }

                cookBookRealmQuery = realm.where(CookBookRealm.class);
                fragmentTransaction.replace(R.id.main_content, aboutCompanyFragment.newInstance(position + 1));
                fragmentTransaction.commit();
                break;
        }
//        ftx.commit();
//          Log.e("XXXX", "xxx5");

    }

    public void onSectionAttached(int number) {
        if(actionBar == null)
        {
            actionBar = getSupportActionBar();
        }
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
//                restoreActionBar();
                actionBar.setTitle(mTitle);

                break;
            case 2:
                mTitle = getString(R.string.title_section2);
//                restoreActionBar();
                actionBar.setTitle(mTitle);

                break;
            case 3:
                mTitle = getString(R.string.title_section3);
//                restoreActionBar();
                actionBar.setTitle(mTitle);

                break;
            case 4:
                mTitle = getString(R.string.title_section4);
//                restoreActionBar();
                actionBar.setTitle(mTitle);

                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
//        actionBar.show();
//        actionBar.hide();
//        actionBar.setShowHideAnimationEnabled(false);
    }

    private void getRealm()
    {
        realm = Realm.getInstance(this);
        cookBookRealmQuery = realm.where(CookBookRealm.class);
        cookBookRealmResult = cookBookRealmQuery.findAll();

        if(cookBookRealmResult.size() < 1)
        {
            createFakeData();
        }
        else
        {
            getCookBooks();
        }
    }

    private void createFakeData()
    {
        ArrayList<String> newSteps = new ArrayList<String>();
        newSteps.add("步驟1;");
        newSteps.add("步驟2;");
        newSteps.add("步驟3;");
        newSteps.add("步驟4;");
        newSteps.add("步驟5;");

        ArrayList<String> newTimeOfSteps = new ArrayList<String>();
        newTimeOfSteps.add("0;");
        newTimeOfSteps.add("0;");
        newTimeOfSteps.add("1;");
        newTimeOfSteps.add("0;");
        newTimeOfSteps.add("2;");

        ArrayList<String> newSpeedOfSteps = new ArrayList<String>();
        newSpeedOfSteps.add("0;");
        newSpeedOfSteps.add("0;");
        newSpeedOfSteps.add("1;");
        newSpeedOfSteps.add("0;");
        newSpeedOfSteps.add("1;");


        cookBooks = new ArrayList<Cookbook>();
        cookBooks.add(new Cookbook(UUID.randomUUID().toString(), "檸檬葡萄汁", "很好喝", "Http://xd.com", "https://www.dropbox.com/s/xm0pw3kw78orzg5/pictures_01.png?dl=0", "葡萄、蜂蜜、檸檬",newSteps, 20, 100, true,newTimeOfSteps,newSpeedOfSteps));
        cookBooks.add(new Cookbook(UUID.randomUUID().toString(), "草莓葡萄汁", "超好喝", "Http://xd.com", "https://www.dropbox.com/s/6r3vdhrxqvot47d/pictures_02.png?dl=0", "葡萄、蜂蜜、草莓",newSteps, 40, 80, true,newTimeOfSteps,newSpeedOfSteps));
        cookBooks.add(new Cookbook(UUID.randomUUID().toString(), "水蜜桃芒果汁", "非常好喝", "Http://xd.com", "https://www.dropbox.com/s/pw4fyjhfs1kqsxa/pictures_03.png?dl=0", "水蜜桃、蜂蜜、芒果",newSteps, 60, 60, true,newTimeOfSteps,newSpeedOfSteps));
        cookBooks.add(new Cookbook(UUID.randomUUID().toString(), "水蜜桃汁", "好喝到不行", "Http://xd.com", "https://www.dropbox.com/s/1u136gj6nvu8mjw/pictures_04.png?dl=0", "水蜜桃、蜂蜜",newSteps, 80, 40, true,newTimeOfSteps,newSpeedOfSteps));
        cookBooks.add(new Cookbook(UUID.randomUUID().toString(), "芒果汁", "好好喝", "Http://xd.com", "https://www.dropbox.com/s/gqvvaquqaqs978s/pictures_05.png?dl=0", "芒果、蜂蜜",newSteps, 100, 20, true,newTimeOfSteps,newSpeedOfSteps));

        for (Cookbook cookBook : cookBooks) {
            String tmpStep = "";
            for (String s : cookBook.getSteps()) {
                tmpStep += s;
            }


            String tmpTimeOfStep = "";
            for (String timeofstep : cookBook.getTimeOfSteps()) {
                tmpTimeOfStep = tmpTimeOfStep + timeofstep;
            }

            String tmpSpeedOfStep = "";
            for (String speedofstep : cookBook.getSpeedOfSteps()) {
                tmpSpeedOfStep = tmpSpeedOfStep + speedofstep;
            }

            realm.beginTransaction();
            CookBookRealm cookBookRealm = realm.createObject(CookBookRealm.class);
            cookBookRealm.setId(cookBook.getId());
            cookBookRealm.setName(cookBook.getName());
            cookBookRealm.setIngredient(cookBook.getIngredient());
            cookBookRealm.setDescription(cookBook.getDescription());
            cookBookRealm.setUrl(cookBook.getUrl());
            cookBookRealm.setImageUrl(cookBook.getImageUrl());
            cookBookRealm.setSteps(tmpStep);
            cookBookRealm.setViewedPeopleCount(cookBook.getViewedPeopleCount());
            cookBookRealm.setCollectedPeopleCount(cookBook.getCollectedPeopleCount());
            cookBookRealm.setBeCollected(cookBook.getIsCollected());
            cookBookRealm.setUploadTimestamp(cookBook.getUploadTimestamp());
            cookBookRealm.setCreateTime(cookBook.getCreateTime());
            cookBookRealm.setTimeOfSteps(tmpTimeOfStep);
            cookBookRealm.setSpeedOfSteps(tmpSpeedOfStep);

            realm.commitTransaction();
        }

    }

    private void getCookBooks()
    {
        cookBooks = new ArrayList<Cookbook>();
        for (CookBookRealm cookBookRealm : cookBookRealmResult) {
            ArrayList<String> tmpSteps = new ArrayList<String>();
            String[] tmpStepParts = cookBookRealm.getSteps().split("\\;");
            for (String tmpStepPart : tmpStepParts) {
                tmpSteps.add(tmpStepPart);
//            Log.e("XXX", tmpStepPart);
            }
            ArrayList<String> tmpTimeOfSteps = new ArrayList<String>();
            String[] tmpTimeOfStepParts = cookBookRealm.getTimeOfSteps().split("\\;");
            for (String tmpTimeOfStepPart : tmpTimeOfStepParts) {
                tmpTimeOfSteps.add(tmpTimeOfStepPart);
//            Log.e("XXX", tmpStepPart);
            }
            ArrayList<String> tmpSpeedOfSteps = new ArrayList<String>();
            String[] tmpSpeedOfStepParts = cookBookRealm.getSpeedOfSteps().split("\\;");
            for (String tmpSpeedOfStepPart : tmpSpeedOfStepParts) {
                tmpSpeedOfSteps.add(tmpSpeedOfStepPart);
//            Log.e("XXX", tmpStepPart);
            }
            Cookbook newCookBook = new Cookbook(cookBookRealm.getId(), cookBookRealm.getName(), cookBookRealm.getDescription(), cookBookRealm.getUrl(), cookBookRealm.getImageUrl(), cookBookRealm.getIngredient(), tmpSteps, cookBookRealm.getViewedPeopleCount(), cookBookRealm.getCollectedPeopleCount(), cookBookRealm.getBeCollected() , tmpTimeOfSteps, tmpSpeedOfSteps);
            newCookBook.setUploadTimestamp(cookBookRealm.getUploadTimestamp());
            cookBooks.add(newCookBook);
        }
    }


    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.newCookBookButton) {
            FragmentManager fm = getSupportFragmentManager();//if added by xml
            CookBooksFragment fragment = (CookBooksFragment)fm.findFragmentById(R.id.main_content);
            fragment.setCurrentCookBooks(realm, 0);

//            Log.e("XXX","OOO");
        }
        else if (view.getId() == R.id.hotCookBookButton) {
            FragmentManager fm = getSupportFragmentManager();//if added by xml
            CookBooksFragment fragment = (CookBooksFragment)fm.findFragmentById(R.id.main_content);
            fragment.setCurrentCookBooks(realm,1);
//            Log.e("OOO","XXX");
        }
        else if (view.getId() == R.id.userRecordCookBookButton) {
            FragmentManager fm = getSupportFragmentManager();//if added by xml
            UserDataFragment fragment = (UserDataFragment)fm.findFragmentById(R.id.main_content);
            fragment.setCurrentCookBooks(realm,0);
//            Log.e("XXX","OOO");
        }
        else if (view.getId() == R.id.userLikeCookBookButton) {
            FragmentManager fm = getSupportFragmentManager();//if added by xml
            UserDataFragment fragment = (UserDataFragment)fm.findFragmentById(R.id.main_content);
            fragment.setCurrentCookBooks(realm,1);
//            Log.e("OOO","XXX");

        }
        else if (view.getId() == R.id.BlenderSettingButton) {

            FragmentManager fm = getSupportFragmentManager();//if added by xml
            AutoTestFragment fragment = (AutoTestFragment)fm.findFragmentById(R.id.main_content);
            fragment.setCurrentTab(0);
//            Intent intent = new Intent(this, DeviceScanActivity.class);
//            intent.putExtra("Name", "BlueToothTest");
//            startActivity(intent);

        }
        else if (view.getId() == R.id.BlenderControlButton) {

            FragmentManager fm = getSupportFragmentManager();//if added by xml
            AutoTestFragment fragment = (AutoTestFragment)fm.findFragmentById(R.id.main_content);
            fragment.setCurrentTab(1);

        }
        else if (view.getId() == R.id.AboutCompanyButton) {

            FragmentManager fm = getSupportFragmentManager();//if added by xml
            AboutCompanyFragment fragment = (AboutCompanyFragment)fm.findFragmentById(R.id.main_content);
            fragment.setCurrentTab(0);

        }
        else if (view.getId() == R.id.AboutGoodButton) {
            FragmentManager fm = getSupportFragmentManager();//if added by xml
            AboutCompanyFragment fragment = (AboutCompanyFragment)fm.findFragmentById(R.id.main_content);
            fragment.setCurrentTab(1);
        }
        else if (view.getId() == R.id.blueToothSwitch) {
            blueToothSwitch = (Switch)findViewById(R.id.blueToothSwitch);
            blueToothHint = (TextView) findViewById(R.id.blueToothHint);
            blenderHint = (TextView) findViewById(R.id.blenderHint);
//            Log.e("XXX",String.valueOf(blueToothSwitch.isChecked()));
            if(blueToothSwitch.isChecked())
            {
                initializesListViewAdapter();
                enableBlueToothIntent();
                if(mBluetoothAdapter.isEnabled())
                {
                    blueToothHint.setText("");
                    blenderHint.setText(R.string.hint_blender);

                    mLeDeviceListAdapter.clear();
                    scanLeDevice(true);
                }
            }
            else
            {
                stopConnectBlueTooth();
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {

        }
        if(blueToothSwitch != null)
        {
            blueToothSwitch.setChecked(false);
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public void onFragmentInteraction(String string) {

    }

    void checkSupportBLE()
    {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            if(blueToothSwitch != null)
            {
                blueToothSwitch.setChecked(false);
            }
        }
    }

    void checkSupportBlueTooth()
    {
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            if(blueToothSwitch != null)
            {
                blueToothSwitch.setChecked(false);
            }
        }
    }

    void enableBlueToothIntent()
    {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    void initializesListViewAdapter()
    {
        mLeDeviceListAdapter = new LeDeviceListAdapter(MainActivity.this.getLayoutInflater());
        mGattServicesList = (ListView) findViewById(R.id.gattServicesList);

        mGattServicesList.setAdapter(mLeDeviceListAdapter);
        mGattServicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                selectService(position);
            }
        });
    }

    private void selectService(int position) {

        if(mBluetoothAdapter.isEnabled())
        {
            final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
            if (device == null) return;
            final Intent intent = new Intent(this, DeviceControlActivity.class);
            intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
            intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
            if (mScanning) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mScanning = false;
            }

            BlueToothData.getInstance().mDeviceName = device.getName();
            BlueToothData.getInstance().mDeviceAddress = device.getAddress();
        }
        else
        {
            blueToothSwitch.setChecked(false);
            stopConnectBlueTooth();
            enableBlueToothIntent();
        }

        mLeDeviceListAdapter.notifyDataSetChanged();
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            bluetoothHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

    }

    void stopConnectBlueTooth()
    {
        blenderHint.setText("");
        blueToothHint.setText(R.string.hint_to_connect_bluetooth);
        mLeDeviceListAdapter.clear();
        scanLeDevice(false);
        mLeDeviceListAdapter.notifyDataSetChanged();
    }

    // Device scan callback.
    private LeScanCallback mLeScanCallback =
            new LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLeDeviceListAdapter.addDevice(device);
                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };

//
//    private Runnable getImage = new Runnable() {
//        @Override
//        public void run() {
//            new LoadImage().execute(cookbook.imageUrl);
//        }
//    };
//
//    private class LoadImage extends AsyncTask<String, String, Bitmap> {
//
//        // Background
//        protected Bitmap doInBackground(String... args) {
//            try {
//                URLConnection url = new URL(args[0]).openConnection();
//                url.setConnectTimeout(mTimeOutMills);
//                url.setReadTimeout(mTimeOutMills);
//                cookbook.image = BitmapFactory.decodeStream(url.getInputStream());
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return cookbook.image;
//        }
//
//        // UI
//        protected void onPostExecute(Bitmap aboutEventImage) {
//            if (aboutEventImage != null) {
//
//            }
//            mHandler.postDelayed(getImage, mRefreshRateMills);
//        }
//    }

}
