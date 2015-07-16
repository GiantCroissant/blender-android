package com.giantcroissant.blender;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;


public class CookBookDetailVideoActivity extends AppCompatActivity implements CookBookDetailVideoFragment.OnCookBookDetailToDoFragmentInteractionListener, YouTubePlayer.OnInitializedListener {

    CookBookDetailVideoFragment cookBookDetailVideoFragment;

    private String cookBookID;
    public CookBook cookBook;
    private ActionBar actionBar;
    public String YOUTUBE_VIDEO_ID = "pKbac2kh0nM";                            // Declaring the Toolbar Object

//    private Realm realm;
//    private RealmQuery<CookBookRealm> cookBookQuery;
//    private RealmResults<CookBookRealm> cookBookRealmResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_book_detail_video);

        Intent intent = getIntent();
        getView();

        cookBookID = intent.getStringExtra("cookBookID");
        getRealm();


        setActionbar();
        setButtonListener();
        setDefaultFragment();
    }


    private void getView()
    {

    }

    private void setActionbar()
    {
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        actionBar.setDisplayShowTitleEnabled(false);
    }


    private void setButtonListener() {

    }


    private void setDefaultFragment()
    {
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.add(R.id.YOUTUBE_PLAYER, new CookBookDetailVideoFragment().newInstance(cookBook));
//        fragmentTransaction.commit();

        YouTubePlayerSupportFragment youTubeFragment = new YouTubePlayerSupportFragment();
        youTubeFragment.initialize(Config.DEVELOPER_KEY, this);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.YOUTUBE_PLAYER, youTubeFragment);
        fragmentTransaction.commit();
    }

    private void getRealm()
    {
//        realm = Realm.getInstance(this);
//        cookBookQuery = realm.where(CookBookRealm.class);
//        cookBookQuery.equalTo("Id", cookBookID);
//        cookBookRealmResult = cookBookQuery.findAll();
//        getCookBookData();
    }

    private void getCookBookData()
    {
//        for (CookBookRealm cookBookRealm : cookBookRealmResult) {
//            CookBook newCookBook = new CookBook(cookBookRealm.getId(), cookBookRealm.getName(), cookBookRealm.getDescription(), cookBookRealm.getUrl(), cookBookRealm.getImageUrl(), cookBookRealm.getIngredient(), cookBookRealm.getSauce(), cookBookRealm.getStep(), cookBookRealm.getViewedPeopleCount(), cookBookRealm.getCollectedPeopleCount(), cookBookRealm.getIsCollected());
//            newCookBook.setUploadTimestamp(cookBookRealm.getUploadTimestamp());
//
//            cookBook = newCookBook;
//        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cook_book_detail_video, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
//                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCookBookDetailToDoFragmentInteraction(String string) {

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo(YOUTUBE_VIDEO_ID);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Failured to Initialize!", Toast.LENGTH_LONG).show();
    }
}

