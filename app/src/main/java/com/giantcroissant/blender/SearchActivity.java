package com.giantcroissant.blender;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class SearchActivity extends Activity implements SearchView.OnQueryTextListener {

    private SearchView searchView;

    private Realm realm;
    private RealmQuery<CookBookRealm> cookBookRealmQuery;
    private RealmResults<CookBookRealm> cookBookRealmResult;
    private List<Cookbook> cookbooks;
    private List<Cookbook> newcookbooks;
    private ListView searchListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView=(SearchView)findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search");
//        ReplaceFont.replaceDefaultFont(this, "DEFAULT", "fonts/NotoSansCJKjp-Medium.otf");
        searchListView = (ListView)findViewById(R.id.searchListView);
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        realm = RealmData.getInstance().realm;
        cookBookRealmQuery = realm.where(CookBookRealm.class);
        cookBookRealmResult = cookBookRealmQuery.findAll();
        getCookBooks();
    }

    private void getCookBooks()
    {
        cookbooks = new ArrayList<Cookbook>();
        for (CookBookRealm cookBookRealm : cookBookRealmResult) {
            ArrayList<String> tmpSteps = new ArrayList<String>();
            String[] tmpStepParts = cookBookRealm.getSteps().split("\\;");
            for (String tmpStepPart : tmpStepParts) {
                tmpSteps.add(tmpStepPart);
            }
            ArrayList<String> tmpTimeOfSteps = new ArrayList<String>();
            String[] tmpTimeOfStepParts = cookBookRealm.getTimeOfSteps().split("\\;");
            for (String tmpTimeOfStepPart : tmpTimeOfStepParts) {
                tmpTimeOfSteps.add(tmpTimeOfStepPart);
            }
            ArrayList<String> tmpSpeedOfSteps = new ArrayList<String>();
            String[] tmpSpeedOfStepParts = cookBookRealm.getSpeedOfSteps().split("\\;");
            for (String tmpSpeedOfStepPart : tmpSpeedOfStepParts) {
                tmpSpeedOfSteps.add(tmpSpeedOfStepPart);
            }
            Cookbook newCookBook = new Cookbook(cookBookRealm.getId(), cookBookRealm.getName(), cookBookRealm.getDescription(), cookBookRealm.getUrl(), cookBookRealm.getImageUrl(), cookBookRealm.getIngredient(), tmpSteps, cookBookRealm.getViewedPeopleCount(), cookBookRealm.getCollectedPeopleCount(), cookBookRealm.getBeCollected() , tmpTimeOfSteps, tmpSpeedOfSteps);
            newCookBook.setUploadTimestamp(cookBookRealm.getUploadTimestamp());
            newCookBook.setImage(BitmapFactory.decodeResource(getResources(), cookBookRealm.getImageID()));
            newCookBook.setImageID(cookBookRealm.getImageID());
            cookbooks.add(newCookBook);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view)
    {
//        if (view.getId() == R.id.searchviewBackground) {
//            finish();
//        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        Toast.makeText(this, "您搜尋的是："+query, Toast.LENGTH_SHORT).show();
        newcookbooks = new ArrayList<Cookbook>();

        for (Cookbook cookbook : cookbooks) {
            if(cookbook.getName().contains(query))
            {
                newcookbooks.add(cookbook);
            }
        }
        searchListView.setAdapter(new SearchListAdapter(
                this,
                R.layout.search_list_item,
                newcookbooks));
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newcookbooks = new ArrayList<Cookbook>();

        for (Cookbook cookbook : cookbooks) {
            if(cookbook.getName().contains(newText))
            {
                newcookbooks.add(cookbook);
            }
        }
        searchListView.setAdapter(new SearchListAdapter(
                this,
                R.layout.search_list_item,
                newcookbooks));
        return true;
    }

    private void selectItem(int position) {

        Intent intent = new Intent(this, CookBookDetailActivity.class);

        intent.putExtra("position", position);
        intent.putExtra("cookBookListViewID", newcookbooks.get(position).getId());
        intent.putExtra("cookBookListViewName", newcookbooks.get(position).getName());
        intent.putExtra("cookBookListViewDescription", newcookbooks.get(position).getDescription());
        intent.putExtra("cookBookListViewUrl", newcookbooks.get(position).getUrl());
        intent.putExtra("cookBookListViewImageUrl", newcookbooks.get(position).getImageUrl());
        intent.putExtra("cookBookListViewIngredient", newcookbooks.get(position).getIngredient());
        intent.putExtra("cookBookListViewSteps", newcookbooks.get(position).getSteps());
        intent.putExtra("cookBookListViewViewPeople", newcookbooks.get(position).getViewedPeopleCount());
        intent.putExtra("cookBookListViewCollectedPeople", newcookbooks.get(position).getCollectedPeopleCount());
        intent.putExtra("cookBookListIsCollected", newcookbooks.get(position).getIsCollected());
        intent.putExtra("cookBookListViewTimeOfSteps", newcookbooks.get(position).getTimeOfSteps());
        intent.putExtra("cookBookListViewSpeedOfSteps", newcookbooks.get(position).getSpeedOfSteps());
        intent.putExtra("cookBookImageId",newcookbooks.get(position).getImageID());

        intent.putExtra("requestCode", 104);
        startActivity(intent);
        finish();

    }
}
