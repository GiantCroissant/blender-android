package com.giantcroissant.blender;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;


public class CookBookDetailActivity extends AppCompatActivity
        implements CookBookDetailInfoFragment.OnCookBookDetailInfoFragmentInteractionListener,
        CookBookDetailVideoFragment.OnCookBookDetailToDoFragmentInteractionListener,
        CookBookDetailToDoFragment.OnCookBookDetailVideoFragmentInteractionListener,
        View.OnClickListener {

    private ActionBar actionBar;                              // Declaring the Toolbar Object
    private CookBook cookBook;
    private CookBookDetailInfoFragment cookBookDetailInfoFragment;
    private CookBookDetailVideoFragment cookBookDetailVideoFragment;
    private CookBookDetailToDoFragment cookBookDetailToDoFragment;

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
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        actionBar.setTitle(cookBook.getName());

        setValueToView();
        setDefaultFragment();
    }


    private void setDefaultFragment()
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.contentfragment, new CookBookDetailInfoFragment());
        fragmentTransaction.commit();
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
                cookBookDetailInfoFragment = new CookBookDetailInfoFragment();
            }
            fragmentTransaction.replace(R.id.contentfragment, cookBookDetailInfoFragment);

            ImageButton infoButton = (ImageButton) findViewById(R.id.cook_book_Info_button);
            infoButton.setImageResource(R.drawable.cook_book_info_true);

            ImageButton videoButton = (ImageButton) findViewById(R.id.cook_book_video_button);
//            videoButton.setImageResource(R.drawable.hotcookbook_false);

            ImageButton toDoButton = (ImageButton) findViewById(R.id.cook_book_to_do_button);
            toDoButton.setImageResource(R.drawable.cook_book_to_do_false);

        }

        else if (view.getId() == R.id.cook_book_video_button) {

            if (cookBookDetailVideoFragment == null)
            {
                cookBookDetailVideoFragment = new CookBookDetailVideoFragment();
            }

            fragmentTransaction.replace(R.id.contentfragment, cookBookDetailVideoFragment);

            ImageButton infoButton = (ImageButton) findViewById(R.id.cook_book_Info_button);
            infoButton.setImageResource(R.drawable.cook_book_info_false);

            ImageButton videoButton = (ImageButton) findViewById(R.id.cook_book_video_button);
//            videoButton.setImageResource(R.drawable.hotcookbook_false);

            ImageButton toDoButton = (ImageButton) findViewById(R.id.cook_book_to_do_button);
            toDoButton.setImageResource(R.drawable.cook_book_to_do_false);
        }

        else if (view.getId() == R.id.cook_book_to_do_button) {

            if (cookBookDetailToDoFragment == null)
            {
                cookBookDetailToDoFragment = new CookBookDetailToDoFragment();
            }

            fragmentTransaction.replace(R.id.contentfragment, cookBookDetailToDoFragment);

            ImageButton infoButton = (ImageButton) findViewById(R.id.cook_book_Info_button);
            infoButton.setImageResource(R.drawable.cook_book_info_false);

            ImageButton videoButton = (ImageButton) findViewById(R.id.cook_book_video_button);
//            videoButton.setImageResource(R.drawable.hotcookbook_false);

            ImageButton toDoButton = (ImageButton) findViewById(R.id.cook_book_to_do_button);
            toDoButton.setImageResource(R.drawable.cook_book_to_do_true);
        }

        fragmentTransaction.commit();

    }

    @Override
    public void onCookBookDetailInfoFragmentInteraction(String string) {

    }

    @Override
    public void onCookBookDetailToDoFragmentInteraction(String string) {

    }

    @Override
    public void onCookBookDetailVideoFragmentInteraction(String string) {

    }
}
