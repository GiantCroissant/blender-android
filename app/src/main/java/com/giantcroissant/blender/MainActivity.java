package com.giantcroissant.blender;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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

    private ActionBar actionBar;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private View mFragmentContainerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private int fragmentId;
    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    private CookBooksFragment cookBooksFragment;
    private UserDataFragment userDataFragment;
    private AutoTestFragment autoTestFragment;
    private AboutCompanyFragment aboutCompanyFragment;
    private NavigationDrawerFragment.NavigationDrawerCallbacks mCallbacks;
    private CharSequence mTitle;
    SharedPreferences sp;

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

        fragmentId = 0;

        mFragmentContainerView = findViewById(fragmentId);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mDrawerLayout);
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
        mDrawerList.setAdapter(new ArrayAdapter<String>(
                this,
                R.layout.navigation_menu_item,
                R.id.menu_text,
                new String[]{
                        getString(R.string.title_section1),
                        getString(R.string.title_section2),
                        getString(R.string.title_section3),
                        getString(R.string.title_section4)
                }));
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
                fragmentTransaction.replace(R.id.main_content, cookBooksFragment.newInstance(position + 1));
                fragmentTransaction.commit();
                break;
            case 1:
                if (userDataFragment == null)
                {
                    userDataFragment = new UserDataFragment();
                }
                fragmentTransaction.replace(R.id.main_content, userDataFragment.newInstance(position + 1));
                fragmentTransaction.commit();
                break;
            case 2:
                if (autoTestFragment == null)
                {
                    autoTestFragment = new AutoTestFragment();
                }
                fragmentTransaction.replace(R.id.main_content, autoTestFragment.newInstance(position + 1));
                fragmentTransaction.commit();
                break;
            case 3:
                if (aboutCompanyFragment == null)
                {
                    aboutCompanyFragment = new AboutCompanyFragment();
                }
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
                restoreActionBar();

                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                restoreActionBar();

                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                restoreActionBar();

                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                restoreActionBar();

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

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.newCookBookButton) {
            FragmentManager fm = getSupportFragmentManager();//if added by xml
            CookBooksFragment fragment = (CookBooksFragment)fm.findFragmentById(R.id.main_content);
            fragment.setCurrentCookBooks(0);
//            Log.e("XXX","OOO");
        }
        else if (view.getId() == R.id.hotCookBookButton) {
            FragmentManager fm = getSupportFragmentManager();//if added by xml
            CookBooksFragment fragment = (CookBooksFragment)fm.findFragmentById(R.id.main_content);
            fragment.setCurrentCookBooks(1);
//            Log.e("OOO","XXX");
        }
        else if (view.getId() == R.id.userRecordCookBookButton) {
            FragmentManager fm = getSupportFragmentManager();//if added by xml
            UserDataFragment fragment = (UserDataFragment)fm.findFragmentById(R.id.main_content);
            fragment.setCurrentCookBooks(0);
//            Log.e("XXX","OOO");
        }
        else if (view.getId() == R.id.userLikeCookBookButton) {
            FragmentManager fm = getSupportFragmentManager();//if added by xml
            UserDataFragment fragment = (UserDataFragment)fm.findFragmentById(R.id.main_content);
            fragment.setCurrentCookBooks(1);
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
}
