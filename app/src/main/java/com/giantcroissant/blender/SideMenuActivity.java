package com.giantcroissant.blender;

import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;


public class SideMenuActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        CookBooksFragment.OnCookBooksFragmentInteractionListener,
        UserDataFragment.OnUserDataFragmentInteractionListener,
        AutoTestFragment.OnAutoTestFragmentInteractionListener,
        AboutCompanyFragment.OnAboutCompanyFragmentInteractionListener,
        View.OnClickListener
{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CookBooksFragment cookBooksFragment;
    private UserDataFragment userDataFragment;
    private AutoTestFragment autoTestFragment;
    private AboutCompanyFragment aboutCompanyFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_menu);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            switch (position) {
                case 0:
                    if (cookBooksFragment == null)
                    {
                        cookBooksFragment = new CookBooksFragment();
                    }
//                    fragmentTransaction.replace(R.id.container, cookBooksFragment.newInstance(position + 1));
                    fragmentTransaction.commit();
                    break;
                case 1:
                    if (userDataFragment == null)
                    {
                        userDataFragment = new UserDataFragment();
                    }
//                    fragmentTransaction.replace(R.id.container, userDataFragment.newInstance(position + 1));
                    fragmentTransaction.commit();
                    break;
                case 2:
                    if (autoTestFragment == null)
                    {
                        autoTestFragment = new AutoTestFragment();
                    }
                    fragmentTransaction.replace(R.id.container, autoTestFragment.newInstance(position + 1));
                    fragmentTransaction.commit();
                    break;
                case 3:
                    if (aboutCompanyFragment == null)
                    {
                        aboutCompanyFragment = new AboutCompanyFragment();
                    }
                    fragmentTransaction.replace(R.id.container, aboutCompanyFragment.newInstance(position + 1));
                    fragmentTransaction.commit();
                    break;
            }

//        fragmentManager.beginTransaction()
//                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
//                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
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
            CookBooksFragment fragment = (CookBooksFragment)fm.findFragmentById(R.id.container);
//            fragment.setCurrentCookBooks(0);
//            Log.e("XXX","OOO");
        }
        else if (view.getId() == R.id.hotCookBookButton) {
            FragmentManager fm = getSupportFragmentManager();//if added by xml
            CookBooksFragment fragment = (CookBooksFragment)fm.findFragmentById(R.id.container);
//            fragment.setCurrentCookBooks(1);
//            Log.e("OOO","XXX");
        }
        else if (view.getId() == R.id.userRecordCookBookButton) {
            FragmentManager fm = getSupportFragmentManager();//if added by xml
            UserDataFragment fragment = (UserDataFragment)fm.findFragmentById(R.id.container);
//            fragment.setCurrentCookBooks(0);
//            Log.e("XXX","OOO");
        }
        else if (view.getId() == R.id.userLikeCookBookButton) {
            FragmentManager fm = getSupportFragmentManager();//if added by xml
            UserDataFragment fragment = (UserDataFragment)fm.findFragmentById(R.id.container);
//            fragment.setCurrentCookBooks(1);
//            Log.e("OOO","XXX");

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.side_menu, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCookBookFragmentInteraction(String string) {

    }

    @Override
    public void onAboutCompanyFragmentInteraction(String String) {

    }

    @Override
    public void onAutoTestFragmentInteraction(String String) {

    }

    @Override
    public void onUserDataFragmentInteraction(String String) {

    }
//
//    /**
//     * A placeholder fragment containing a simple view.
//     */
//    public static class PlaceholderFragment extends Fragment {
//        /**
//         * The fragment argument representing the section number for this
//         * fragment.
//         */
//        private static final String ARG_SECTION_NUMBER = "section_number";
//
//        /**
//         * Returns a new instance of this fragment for the given section
//         * number.
//         */
//        public static PlaceholderFragment newInstance(int sectionNumber) {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        public PlaceholderFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_side_menu, container, false);
//            return rootView;
//        }
//
//        @Override
//        public void onAttach(Activity activity) {
//            super.onAttach(activity);
//            ((SideMenuActivity) activity).onSectionAttached(
//                    getArguments().getInt(ARG_SECTION_NUMBER));
//        }
//    }

}
