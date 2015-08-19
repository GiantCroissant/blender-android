package com.giantcroissant.blender;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class CookBookDetailActivity extends AppCompatActivity
        implements CookBookDetailInfoFragment.OnCookBookDetailInfoFragmentInteractionListener,
        CookBookDetailToDoFragment.OnCookBookDetailVideoFragmentInteractionListener,
        View.OnClickListener {

    private ActionBar actionBar;                              // Declaring the Toolbar Object
    private CookBook cookBook;
    private CookBookDetailInfoFragment cookBookDetailInfoFragment;
    private CookBookDetailToDoFragment cookBookDetailToDoFragment;
    private Realm realm;
    private RealmQuery<CookBookRealm> cookBookRealmQuery;
    private RealmResults<CookBookRealm> cookBookRealmResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_book_detial);

        Intent intent = getIntent();
        getView();

        cookBook = new CookBook();
        cookBook.setId(intent.getStringExtra("cookBookListViewID"));
        cookBook.setName(intent.getStringExtra("cookBookListViewName"));
        cookBook.setDescription(intent.getStringExtra("cookBookListViewDescription"));
        cookBook.setUrl(intent.getStringExtra("cookBookListViewUrl"));
        cookBook.setImageUrl(intent.getStringExtra("cookBookListViewImageUrl"));
        cookBook.setIngredient(intent.getStringExtra("cookBookListViewIngredient"));
        cookBook.setStep(intent.getStringArrayListExtra("cookBookListViewSteps"));
        cookBook.setViewedPeopleCount(intent.getIntExtra("cookBookListViewViewPeople", 0));
        cookBook.setCollectedPeopleCount(intent.getIntExtra("cookBookListViewCollectedPeople", 0));
        cookBook.setIsCollected(intent.getBooleanExtra("cookBookListIsCollected", false));

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        actionBar.setTitle(cookBook.getName());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setValueToView();
        setDefaultFragment();
        getRealm();
        setRealmData();
    }

    private void getRealm()
    {
        realm = Realm.getInstance(this);
        cookBookRealmQuery = realm.where(CookBookRealm.class);
        cookBookRealmQuery = cookBookRealmQuery.contains("Id", cookBook.getId());
        cookBookRealmResult = cookBookRealmQuery.findAll();
    }

    private void setRealmData()
    {
        realm.beginTransaction();
        CookBookRealm cookBookRealm = cookBookRealmResult.first();
        cookBookRealm.setUploadTimestamp(new Date(System.currentTimeMillis()));
        cookBook.setUploadTimestamp(new Date(System.currentTimeMillis()));
        cookBookRealm.setViewedPeopleCount(cookBook.getViewedPeopleCount() + 1);
        cookBook.setViewedPeopleCount(cookBook.getViewedPeopleCount() + 1);
        realm.commitTransaction();
    }

    private void setDefaultFragment()
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.contentfragment, new CookBookDetailInfoFragment().newInstance(cookBook));
        fragmentTransaction.commit();

        ImageButton infoButton = (ImageButton) findViewById(R.id.cook_book_Info_button_SelectColor);
        infoButton.setImageResource(R.color.TabSelectColor);

        ImageButton toDoButton = (ImageButton) findViewById(R.id.cook_book_to_do_button_SelectColor);
        toDoButton.setImageResource(R.color.TabNoSelectColor);
    }

    private void getView()
    {
    }



    private void setValueToView()
    {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cook_book_detial, menu);
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
    public void onClick(View view)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (view.getId() == R.id.cook_book_Info_button) {

            if (cookBookDetailInfoFragment == null)
            {
                cookBookDetailInfoFragment = new CookBookDetailInfoFragment().newInstance(cookBook);
            }
            fragmentTransaction.replace(R.id.contentfragment, cookBookDetailInfoFragment);

            ImageButton infoButton = (ImageButton) findViewById(R.id.cook_book_Info_button_SelectColor);
            infoButton.setImageResource(R.color.TabSelectColor);

//            ImageButton videoButton = (ImageButton) findViewById(R.id.cook_book_video_button_SelectColor);
//            videoButton.setImageResource(R.drawable.hotcookbook_false);

            ImageButton toDoButton = (ImageButton) findViewById(R.id.cook_book_to_do_button_SelectColor);
            toDoButton.setImageResource(R.color.TabNoSelectColor);

        }

        else if (view.getId() == R.id.cook_book_video_button) {

            Intent intent = new Intent(this, CookBookDetailVideoActivity.class);
            intent.putExtra("cookBookListViewID", cookBook.getId());
            intent.putExtra("cookBookListViewName", cookBook.getName());
            intent.putExtra("cookBookListViewDescription", cookBook.getDescription());
            intent.putExtra("cookBookListViewUrl", cookBook.getUrl());
            intent.putExtra("cookBookListViewImageUrl", cookBook.getImageUrl());
            intent.putExtra("cookBookListViewIngredient", cookBook.getIngredient());
            intent.putExtra("cookBookListViewSteps", cookBook.getSteps());
            intent.putExtra("cookBookListViewViewPeople", cookBook.getViewedPeopleCount());
            intent.putExtra("cookBookListViewCollectedPeople", cookBook.getCollectedPeopleCount());
            intent.putExtra("cookBookListIsCollected", cookBook.getIsCollected());

            startActivityForResult(intent, 0);
        }

        else if (view.getId() == R.id.cook_book_to_do_button) {

            if (cookBookDetailToDoFragment == null)
            {
                cookBookDetailToDoFragment = new CookBookDetailToDoFragment().newInstance(cookBook);
            }

            fragmentTransaction.replace(R.id.contentfragment, cookBookDetailToDoFragment);

            ImageButton infoButton = (ImageButton) findViewById(R.id.cook_book_Info_button_SelectColor);
            infoButton.setImageResource(R.color.TabNoSelectColor);

//            ImageButton videoButton = (ImageButton) findViewById(R.id.cook_book_video_button_SelectColor);
//            videoButton.setImageResource(R.drawable.hotcookbook_false);

            ImageButton toDoButton = (ImageButton) findViewById(R.id.cook_book_to_do_button_SelectColor);
            toDoButton.setImageResource(R.color.TabSelectColor);

        }
        else if (view.getId() == R.id.likeCookBookButton) {

            realm.beginTransaction();
            CookBookRealm cookBookRealm = cookBookRealmResult.first();

            cookBookRealm.setBeCollected(!cookBook.getIsCollected());
            cookBook.setIsCollected(!cookBook.getIsCollected());
            if(cookBook.getIsCollected())
            {
                cookBookRealm.setCollectedPeopleCount(cookBook.getCollectedPeopleCount() + 1);
                cookBook.setCollectedPeopleCount(cookBook.getCollectedPeopleCount() + 1);
            }
            else
            {
                cookBookRealm.setCollectedPeopleCount(cookBook.getCollectedPeopleCount() - 1);
                cookBook.setCollectedPeopleCount(cookBook.getCollectedPeopleCount() - 1);
            }
            realm.commitTransaction();

            setLikeCookBookButton();

        }

        fragmentTransaction.commit();

    }

    private void setLikeCookBookButton()
    {
        ImageButton likeCookbookButton = (ImageButton) findViewById(R.id.likeCookBookButton);
        if(cookBook.getIsCollected())
        {
            likeCookbookButton.setImageResource(R.drawable.icon_collect_y) ;
        }
        else
        {

            likeCookbookButton.setImageResource(R.drawable.icon_collect_n) ;
        }
    }

    @Override
    public void onCookBookDetailInfoFragmentInteraction(String string) {

    }

    @Override
    public void onCookBookDetailVideoFragmentInteraction(String string) {

    }
}
