package com.giantcroissant.blender;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity
        implements OnItemClickListener,
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        CookBooksFragment.OnCookBooksFragmentInteractionListener,
        UserDataFragment.OnUserDataFragmentInteractionListener,
        AutoTestFragment.OnAutoTestFragmentInteractionListener,
        AboutCompanyFragment.OnAboutCompanyFragmentInteractionListener,
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

    private ArrayList<CookBook> cookBooks;

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

//        onItemClick(null, null, fragmentId, 0);
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
//        if((CookBooksDataFragment)fm.findFragmentById(R.id.main_content) != null)
//        {
            CookBooksDataFragment fragment = (CookBooksDataFragment)fm.findFragmentById(R.id.main_content);
            fragment.upDateListView(realm);
//        }

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

//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
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
        tempNavigationMenuItemList.add(new NavigationMenuItem(getString(R.string.title_section3),R.drawable.icon_machine_test));
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

        String newStep = "步驟1;步驟2;步驟3;步驟4;步驟5;";


        cookBooks = new ArrayList<CookBook>();
        cookBooks.add(new CookBook(UUID.randomUUID().toString(), "檸檬葡萄汁", "很好喝", "Http://xd.com", "https://www.dropbox.com/s/xm0pw3kw78orzg5/pictures_01.png?dl=0", "葡萄、蜂蜜、檸檬",newSteps, 20, 100, true));
        cookBooks.add(new CookBook(UUID.randomUUID().toString(), "草莓葡萄汁", "超好喝", "Http://xd.com", "https://www.dropbox.com/s/6r3vdhrxqvot47d/pictures_02.png?dl=0", "葡萄、蜂蜜、草莓",newSteps, 40, 80, true));
        cookBooks.add(new CookBook(UUID.randomUUID().toString(), "水蜜桃芒果汁", "非常好喝", "Http://xd.com", "https://www.dropbox.com/s/pw4fyjhfs1kqsxa/pictures_03.png?dl=0", "水蜜桃、蜂蜜、芒果",newSteps, 60, 60, true));
        cookBooks.add(new CookBook(UUID.randomUUID().toString(), "水蜜桃汁", "好喝到不行", "Http://xd.com", "https://www.dropbox.com/s/1u136gj6nvu8mjw/pictures_04.png?dl=0", "水蜜桃、蜂蜜",newSteps, 80, 40, true));
        cookBooks.add(new CookBook(UUID.randomUUID().toString(), "芒果汁", "好好喝", "Http://xd.com", "https://www.dropbox.com/s/gqvvaquqaqs978s/pictures_05.png?dl=0", "芒果、蜂蜜",newSteps, 100, 20, true));

        for (CookBook cookBook : cookBooks) {
            String tmpStep = "";
            for (String s : cookBook.getSteps()) {
                tmpStep += s;
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

            realm.commitTransaction();
        }

    }

    private void getCookBooks()
    {
        cookBooks = new ArrayList<CookBook>();
        for (CookBookRealm cookBookRealm : cookBookRealmResult) {
            ArrayList<String> tmpSteps = new ArrayList<String>();
            String[] tmpStepParts = cookBookRealm.getSteps().split("\\;");
            for (String tmpStepPart : tmpStepParts) {
                tmpSteps.add(tmpStepPart);
//            Log.e("XXX", tmpStepPart);
            }
            CookBook newCookBook = new CookBook(cookBookRealm.getId(), cookBookRealm.getName(), cookBookRealm.getDescription(), cookBookRealm.getUrl(), cookBookRealm.getImageUrl(), cookBookRealm.getIngredient(), tmpSteps, cookBookRealm.getViewedPeopleCount(), cookBookRealm.getCollectedPeopleCount(), cookBookRealm.getBeCollected());
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
            fragment.setCurrentCookBooks(realm,0);

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
