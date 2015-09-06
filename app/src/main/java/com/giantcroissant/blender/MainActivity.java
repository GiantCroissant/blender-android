package com.giantcroissant.blender;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        DeviceControlFragment.OnDeviceControlFragmentInteractionListener,
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
//scan bluetooth
    private boolean switchIsChecked = false;
    private Switch blueToothSwitch;
    private TextView blueToothHint;
    private TextView blenderHint;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private ListView mGattServicesList;
    private boolean mScanning;
    private Handler bluetoothHandler;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_COOKBOOK = 2;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 1000;
    private int resultCode = 0;
    private int resultPosition = 0;
    private String resultCookBookListViewID = "";

//cotrol blender
//    private boolean isConnected = false;
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private final static String TAG = CookBookDetailActivity.class.getSimpleName();
    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private BluetoothGattCharacteristic mClickCharacteristic;
    private boolean mConnected = false;
    private BluetoothManager mblueToothManager;
    private TextView IsConnectedBlueToothText;
//    private int connectStateId = R.string.disconnected;

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
        mblueToothManager = bluetoothManager;
        mBluetoothAdapter = bluetoothManager.getAdapter();

//        ReplaceFont.replaceDefaultFont(this, "DEFAULT", "fonts/NotoSansCJKjp-Medium.otf");
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

        if((Button)findViewById(R.id.startBlenderButton) != null)
        {
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
            if (mBluetoothLeService != null) {
                final boolean result = mBluetoothLeService.connect(mDeviceAddress);
                Log.d(TAG, "Connect request result=" + result);

            }
            else
            {

                mDeviceName = BlueToothData.getInstance().mDeviceName;
                mDeviceAddress = BlueToothData.getInstance().mDeviceAddress;

                if(mDeviceName != null && mDeviceAddress != null )
                {
                    Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
                    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
                }

                mClickCharacteristic = BlueToothData.getInstance().mClickCharacteristic;

            }

//            isConnected = mBluetoothLeService != null && mClickCharacteristic != null;
        }

    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();
        if(resultCode == 100)
        {
            if (autoTestFragment == null)
            {
                autoTestFragment = new AutoTestFragment();
            }

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            cookBookRealmQuery = realm.where(CookBookRealm.class);
            fragmentTransaction.replace(R.id.main_content, autoTestFragment.newInstance(2 + 1, switchIsChecked));
            fragmentTransaction.commit();

            checkSupportBLE();
            checkSupportBlueTooth();
            enableBlueToothIntent();
        }
//        Log.e("XXX",String.valueOf(switchIsChecked));

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
                RealmData.getInstance().realm = realm;
                fragmentTransaction.commit();
                break;
            case 1:
                if (userDataFragment == null)
                {
                    userDataFragment = new UserDataFragment();
                }

                cookBookRealmQuery = realm.where(CookBookRealm.class);
                fragmentTransaction.replace(R.id.main_content, userDataFragment.newInstance(position + 1, realm));
                RealmData.getInstance().realm = realm;

                fragmentTransaction.commit();
                break;
            case 2:
                if (autoTestFragment == null)
                {
                    autoTestFragment = new AutoTestFragment();
                }

                cookBookRealmQuery = realm.where(CookBookRealm.class);
                fragmentTransaction.replace(R.id.main_content, autoTestFragment.newInstance(position + 1, switchIsChecked));
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
                if(actionBar != null)
                actionBar.setTitle(mTitle);

                break;
            case 2:
                mTitle = getString(R.string.title_section2);
//                restoreActionBar();
                if(actionBar != null)
                    actionBar.setTitle(mTitle);

                break;
            case 3:
                mTitle = getString(R.string.title_section3);
//                restoreActionBar();
                if(actionBar != null)
                    actionBar.setTitle(mTitle);

                break;
            case 4:
                mTitle = getString(R.string.title_section4);
//                restoreActionBar();
                if(actionBar != null)
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
        newSteps.add("雪梨洗淨去皮，切成可放入榨汁機內 雪梨洗淨去皮，切成可放入榨汁機內。;");
        newSteps.add("香蕉去皮切成數段。;");
        newSteps.add("啟動果汁機。;");
        newSteps.add("檸檬連皮對切為四份去核。;");
        newSteps.add("將所有材料順序放入榨汁機內壓榨成汁榨成汁。;");
        newSteps.add("啟動果汁機。;");

        ArrayList<String> newTimeOfSteps = new ArrayList<String>();
        newTimeOfSteps.add("0;");
        newTimeOfSteps.add("0;");
        newTimeOfSteps.add("10;");
        newTimeOfSteps.add("0;");
        newTimeOfSteps.add("0;");
        newTimeOfSteps.add("7;");


        ArrayList<String> newSpeedOfSteps = new ArrayList<String>();
        newSpeedOfSteps.add("0;");
        newSpeedOfSteps.add("0;");
        newSpeedOfSteps.add("2;");
        newSpeedOfSteps.add("0;");
        newSpeedOfSteps.add("0;");
        newSpeedOfSteps.add("3;");


        cookBooks = new ArrayList<Cookbook>();
        cookBooks.add(new Cookbook(UUID.randomUUID().toString(), "檸檬葡萄汁", "美味的檸檬和葡萄。好吃好吃。做成果汁也很棒喔。", "Http://xd.com", "https://www.dropbox.com/s/xm0pw3kw78orzg5/pictures_01.png?dl=0", "葡萄、蜂蜜、檸檬",newSteps, 20, 100, true,newTimeOfSteps,newSpeedOfSteps));
        cookBooks.add(new Cookbook(UUID.randomUUID().toString(), "草莓葡萄汁", "美味的草莓和葡萄。好吃好吃。做成果汁也很棒喔。", "Http://xd.com", "https://www.dropbox.com/s/6r3vdhrxqvot47d/pictures_02.png?dl=0", "葡萄、蜂蜜、草莓",newSteps, 40, 80, true,newTimeOfSteps,newSpeedOfSteps));
        cookBooks.add(new Cookbook(UUID.randomUUID().toString(), "水蜜桃芒果汁", "美味的水蜜桃和芒果。好吃好吃。做成果汁也很棒喔。", "Http://xd.com", "https://www.dropbox.com/s/pw4fyjhfs1kqsxa/pictures_03.png?dl=0", "水蜜桃、蜂蜜、芒果",newSteps, 60, 60, true,newTimeOfSteps,newSpeedOfSteps));
        cookBooks.add(new Cookbook(UUID.randomUUID().toString(), "水蜜桃汁", "美味的水蜜桃。好吃好吃。做成果汁也很棒喔。", "Http://xd.com", "https://www.dropbox.com/s/1u136gj6nvu8mjw/pictures_04.png?dl=0", "水蜜桃、蜂蜜",newSteps, 80, 40, true,newTimeOfSteps,newSpeedOfSteps));
        cookBooks.add(new Cookbook(UUID.randomUUID().toString(), "雪梨香蕉生菜汁", "美味的雪梨和香蕉。好吃好吃。做成果汁也很棒喔。", "Http://xd.com", "https://www.dropbox.com/s/gqvvaquqaqs978s/pictures_05.png?dl=0", "芒果、蜂蜜",newSteps, 100, 20, true,newTimeOfSteps,newSpeedOfSteps));

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
        FragmentManager fm = getSupportFragmentManager();//if added by xml
        if (view.getId() == R.id.newCookBookButton) {
            CookBooksFragment fragment = (CookBooksFragment)fm.findFragmentById(R.id.main_content);
            fragment.setCurrentCookBooks(realm, 0);

//            Log.e("XXX","OOO");
        }
        else if (view.getId() == R.id.hotCookBookButton) {
            CookBooksFragment fragment = (CookBooksFragment)fm.findFragmentById(R.id.main_content);
            fragment.setCurrentCookBooks(realm,1);
//            Log.e("OOO","XXX");
        }
        else if (view.getId() == R.id.userRecordCookBookButton) {
            UserDataFragment fragment = (UserDataFragment)fm.findFragmentById(R.id.main_content);
            fragment.setCurrentCookBooks(realm,0);
//            Log.e("XXX","OOO");
        }
        else if (view.getId() == R.id.userLikeCookBookButton) {
            UserDataFragment fragment = (UserDataFragment)fm.findFragmentById(R.id.main_content);
            fragment.setCurrentCookBooks(realm,1);
//            Log.e("OOO","XXX");

        }
        else if (view.getId() == R.id.BlenderSettingButton)
        {
            AutoTestFragment fragment = (AutoTestFragment)fm.findFragmentById(R.id.main_content);
            fragment.setSwitchChecked(switchIsChecked);
            fragment.setCurrentTab(0);
//            Intent intent = new Intent(this, DeviceScanActivity.class);
//            intent.putExtra("Name", "BlueToothTest");
//            startActivity(intent);

        }
        else if (view.getId() == R.id.BlenderControlButton)
        {
            AutoTestFragment fragment = (AutoTestFragment)fm.findFragmentById(R.id.main_content);
            fragment.setCurrentTab(1);
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

            if (mBluetoothLeService != null) {
                final boolean result = mBluetoothLeService.connect(mDeviceAddress);
                Log.d(TAG, "Connect request result=" + result);

            }
            else
            {

                mDeviceName = BlueToothData.getInstance().mDeviceName;
                mDeviceAddress = BlueToothData.getInstance().mDeviceAddress;

                if(mDeviceName != null && mDeviceAddress != null )
                {
                    Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
                    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
                }

                mClickCharacteristic = BlueToothData.getInstance().mClickCharacteristic;

            }

//            isConnected = mBluetoothLeService != null && mClickCharacteristic != null;

        }
        else if (view.getId() == R.id.AboutCompanyButton)
        {
            AboutCompanyFragment fragment = (AboutCompanyFragment)fm.findFragmentById(R.id.main_content);
            fragment.setCurrentTab(0);
        }
        else if (view.getId() == R.id.AboutGoodButton) {
            AboutCompanyFragment fragment = (AboutCompanyFragment)fm.findFragmentById(R.id.main_content);
            fragment.setCurrentTab(1);
        }
        else if (view.getId() == R.id.blueToothSwitch) {

            switchConnectBlueTooth();

        }
        else if (view.getId() == R.id.startBlenderButton)
        {
            AutoTestFragment fragment = (AutoTestFragment)fm.findFragmentById(R.id.main_content);
//            isConnected = mBluetoothLeService != null && mClickCharacteristic != null;
            if(mConnected == false)
            {
                fragment.setSwitchChecked(switchIsChecked);
                fragment.setCurrentTab(0);
            }

            EditText setTimeEditText = (EditText) findViewById(R.id.setTimeEditText);
            EditText setSpeedEditText = (EditText) findViewById(R.id.setSpeedEditText);
            byte[] sendmsg = new byte[10];
            sendmsg[0] = (byte) 0xA5;
            sendmsg[1] = (byte) 0x5A;
            sendmsg[9] = (byte) 0xB3;
            sendmsg[2] = (byte) 0x07;
            sendmsg[3] = (byte) 0x01;
            sendmsg[4] = (byte) (Integer.parseInt(setTimeEditText.getText().toString()) - 1 % 256);//((npTime.getValue()+1)*5 % 256);
            sendmsg[5] = (byte) (Integer.parseInt(setTimeEditText.getText().toString()) - 1 / 256);//((npTime.getValue()+1)*5 / 256);
            sendmsg[6] = (byte) (Integer.parseInt(setSpeedEditText.getText().toString()) % 256);//(npSpeed.getValue() % 256);
            sendmsg[7] = (byte) (Integer.parseInt(setSpeedEditText.getText().toString()) / 256);//(npSpeed.getValue() / 256);
            sendmsg[8] = (byte) 0x01;

            if(mClickCharacteristic != null && mBluetoothLeService != null)
            {

                mClickCharacteristic.setValue(sendmsg);
                mClickCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                mBluetoothLeService.writeCharacteristic(mClickCharacteristic);
            }

        }
        else if (view.getId() == R.id.stopBlenderButton)
        {
            AutoTestFragment fragment = (AutoTestFragment)fm.findFragmentById(R.id.main_content);
//            isConnected = mBluetoothLeService != null && mClickCharacteristic != null;
            if(mConnected == false)
            {
                fragment.setSwitchChecked(switchIsChecked);
                fragment.setCurrentTab(0);
            }

            EditText setTimeEditText = (EditText) findViewById(R.id.setTimeEditText);
            EditText setSpeedEditText = (EditText) findViewById(R.id.setSpeedEditText);

            byte[] sendmsg = new byte[10];
            sendmsg[0] = (byte) 0xA5;
            sendmsg[1] = (byte) 0x5A;
            sendmsg[9] = (byte) 0xB3;
            sendmsg[2] = (byte) 0x07;
            sendmsg[3] = (byte) 0x01;
            sendmsg[4] = (byte) 0x00;
            sendmsg[5] = (byte) 0x00;
            sendmsg[6] = (byte) 0x00;
            sendmsg[7] = (byte) 0x00;
            sendmsg[8] = (byte) 0x00;

            if(mClickCharacteristic != null && mBluetoothLeService != null)
            {

                mClickCharacteristic.setValue(sendmsg);
                mClickCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                mBluetoothLeService.writeCharacteristic(mClickCharacteristic);
            }

        }
        else if (view.getId() == R.id.cancelSettingButton)
        {
            AutoTestFragment fragment = (AutoTestFragment)fm.findFragmentById(R.id.main_content);
            fragment.setBlenderSettingView(false);
        }
        else if (view.getId() == R.id.confrimSettingButton)
        {
            AutoTestFragment fragment = (AutoTestFragment)fm.findFragmentById(R.id.main_content);
            fragment.setValue();
            fragment.setBlenderSettingView(false);
        }
        else if (view.getId() == R.id.setTimeEditText)
        {
            AutoTestFragment fragment = (AutoTestFragment)fm.findFragmentById(R.id.main_content);
            fragment.setBlenderSettingView(true);
            fragment.setTimeView();
        }
        else if (view.getId() == R.id.setSpeedEditText)
        {
            AutoTestFragment fragment = (AutoTestFragment)fm.findFragmentById(R.id.main_content);
            fragment.setBlenderSettingView(true);
            fragment.setSpeedView();
        }


    }

    public void switchConnectBlueTooth()
    {
        blueToothSwitch = (Switch)findViewById(R.id.blueToothSwitch);
        blueToothHint = (TextView) findViewById(R.id.blueToothHint);
        blenderHint = (TextView) findViewById(R.id.blenderHint);
//            Log.e("XXX",String.valueOf(blueToothSwitch.isChecked()));
        if(blueToothSwitch != null)
        {
            initializesListViewAdapter();
            enableBlueToothIntent();
            switchIsChecked = blueToothSwitch.isChecked();

//        Log.e("XXX",String.valueOf(switchIsChecked));
//        Log.e("XXX", String.valueOf(mBluetoothAdapter.isEnabled()));

            if(switchIsChecked)
            {
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
        if (requestCode == REQUEST_ENABLE_BT )//&& resultCode == Activity.RESULT_CANCELED) {
        {
            if(blueToothSwitch != null)
            {
                switchIsChecked = false;
                blueToothSwitch.setChecked(false);
            }
        }

        if(resultCode == 100)
        {
            this.resultCode = resultCode;
            this.resultPosition = data.getIntExtra("posistion",0);
            this.resultCookBookListViewID = data.getStringExtra("cookBookListViewID");
//            Log.e("XXX","100");
        }

        if(requestCode == REQUEST_COOKBOOK)
        {

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
    public void onDeviceControlFragmentInteraction(String string) {

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
//        Log.e("XXX",string);
        if(string.compareTo("Ok") == 0)
        {
//            updateConnectionState(connectStateId);
            switchConnectBlueTooth();
//            Log.e("XXX",string);
        }
        else if(string.compareTo("Exit") == 0)
        {

            stopConnectBlueTooth();
        }
    }

    void checkSupportBLE()
    {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            if(blueToothSwitch != null)
            {
                switchIsChecked = false;
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
                switchIsChecked = false;
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
        if(mLeDeviceListAdapter == null)
        {
            mLeDeviceListAdapter = new LeDeviceListAdapter(MainActivity.this.getLayoutInflater());
        }
        mGattServicesList = (ListView) findViewById(R.id.gattServicesList);
        if(mGattServicesList != null)
        {
            mGattServicesList.setAdapter(mLeDeviceListAdapter);
            mGattServicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                    selectService(position, view);
                }
            });
        }

    }

    private void selectService(int position, View view) {
        TextView textView = (TextView)view.findViewById(R.id.device_name);

        if(textView.getText().toString().compareTo("ITRI_JUICER_v1.0") != 0) return;
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

            if(resultCode == 100)
            {
                Intent cookbookDetailIntent = new Intent(this, CookBookDetailActivity.class);

                RealmQuery<CookBookRealm> tmpCookBookRealmQuery = realm.where(CookBookRealm.class);
                RealmResults<CookBookRealm> tempCookBookRealmResult = tmpCookBookRealmQuery.contains("Id",resultCookBookListViewID).findAll();
                ArrayList<Cookbook> tmpCookBooks = new ArrayList<Cookbook>();

                for (CookBookRealm cookBookRealm : tempCookBookRealmResult) {
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
                    tmpCookBooks.add(newCookBook);
                }
                cookbookDetailIntent.putExtra("position", resultPosition);
//                cookbookDetailIntent.putExtra("currentFragmentIndex", 1);

                cookbookDetailIntent.putExtra("cookBookListViewID", tmpCookBooks.get(0).getId());
                cookbookDetailIntent.putExtra("cookBookListViewName", tmpCookBooks.get(0).getName());
                cookbookDetailIntent.putExtra("cookBookListViewDescription", tmpCookBooks.get(0).getDescription());
                cookbookDetailIntent.putExtra("cookBookListViewUrl", tmpCookBooks.get(0).getUrl());
                cookbookDetailIntent.putExtra("cookBookListViewImageUrl", tmpCookBooks.get(0).getImageUrl());
                cookbookDetailIntent.putExtra("cookBookListViewIngredient", tmpCookBooks.get(0).getIngredient());
                cookbookDetailIntent.putExtra("cookBookListViewSteps", tmpCookBooks.get(0).getSteps());
                cookbookDetailIntent.putExtra("cookBookListViewViewPeople", tmpCookBooks.get(0).getViewedPeopleCount());
                cookbookDetailIntent.putExtra("cookBookListViewCollectedPeople", tmpCookBooks.get(0).getCollectedPeopleCount());
                cookbookDetailIntent.putExtra("cookBookListIsCollected", tmpCookBooks.get(0).getIsCollected());
                cookbookDetailIntent.putExtra("cookBookListViewTimeOfSteps", tmpCookBooks.get(0).getTimeOfSteps());
                cookbookDetailIntent.putExtra("cookBookListViewSpeedOfSteps", tmpCookBooks.get(0).getSpeedOfSteps());

//                Log.e("getCookBoookTimeOfSteps", String.valueOf(tmpCookBooks.get(0).getTimeOfSteps()));
//                Log.e("getCookBoookSpeedOfSteps", String.valueOf(tmpCookBooks.get(0).getSpeedOfSteps()));
                startActivityForResult(cookbookDetailIntent, REQUEST_COOKBOOK);
                resultCode = 0;
            }
            else
            {
                startActivity(intent);
            }
        }
        else
        {
            switchIsChecked = false;
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
            for (BluetoothDevice bluetoothDevice : mblueToothManager.getConnectedDevices(BluetoothProfile.GATT_SERVER)) {
                mLeDeviceListAdapter.addDevice(bluetoothDevice);
            }

        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

    }

    void stopConnectBlueTooth()
    {
        blenderHint.setText("");
        blueToothHint.setText(R.string.hint_to_connect_bluetooth);
        if(mLeDeviceListAdapter != null)
        {
            mLeDeviceListAdapter.clear();
            scanLeDevice(false);
            mLeDeviceListAdapter.notifyDataSetChanged();
        }
    }

    // Device scan callback.
    private LeScanCallback mLeScanCallback =
            new LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Log.e("XXX",device.getName());

                            mLeDeviceListAdapter.addDevice(device);
                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };




    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

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
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
//                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
//                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        registerReceiver(mGattUpdateReceiver,makeGattUpdateIntentFilter());
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mConnected)
        {
            unbindService(mServiceConnection);
            mBluetoothLeService = null;
        }
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
//                isConnected = mBluetoothLeService != null && mClickCharacteristic != null;
//                connectStateId = resourceId;
                if (IsConnectedBlueToothText != null)
                {
//                    IsConnectedBlueToothText.setText(resourceId);
                }
            }
        });
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);

            if(mGattCharacteristics.size() == 3)
            {
                BlueToothData.getInstance().mClickCharacteristic = mGattCharacteristics.get(2).get(0);
                mClickCharacteristic =  BlueToothData.getInstance().mClickCharacteristic;

            }
//            Log.e("XXX", String.valueOf(mGattCharacteristics.size()));
        }
    }


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
