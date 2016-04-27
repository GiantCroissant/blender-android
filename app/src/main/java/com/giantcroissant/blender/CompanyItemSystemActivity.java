package com.giantcroissant.blender;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class CompanyItemSystemActivity extends AppCompatActivity {

    private int category;
    private int position;
    private String itemID;
    private String itemTitle;
    private String itemContent;
    private String itemIconUrl;
    private ActionBar actionBar;                              // Declaring the Toolbar Object
    private ListView listview;
    AboutCompanyItemInfoAdapter aboutCompanyItemInfoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_item_system);


        Intent intent = getIntent();
        getView();

        category = intent.getIntExtra("category", 0);
        position = intent.getIntExtra("position", 0);
        itemID = intent.getStringExtra("itemListViewID");
        itemTitle = intent.getStringExtra("itemListViewTitle");
        itemContent = intent.getStringExtra("itemListViewContent");
        itemIconUrl = intent.getStringExtra("itemListViewIconUrl");

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        actionBar.setTitle(itemTitle);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        aboutCompanyItemInfoAdapter = new AboutCompanyItemInfoAdapter(this , R.layout.about_company_item_info, CompanyData.getInstance().companyItemSystems.get(position).contentIds);
        listview.setAdapter(aboutCompanyItemInfoAdapter);
        aboutCompanyItemInfoAdapter.notifyDataSetChanged();
    }

    private void selectItem(int position) {

        Intent intent = new Intent(this, CompanyItemActivity.class);

        intent.putExtra("position", position);
        intent.putExtra("itemListViewID", CompanyData.getInstance().companyItemSystems.get(category).contentIds.get(position).getId());
        intent.putExtra("itemListViewTitle", CompanyData.getInstance().companyItemSystems.get(category).contentIds.get(position).getTitle());
        intent.putExtra("itemListViewName", CompanyData.getInstance().companyItemSystems.get(category).contentIds.get(position).getName());
        intent.putExtra("itemListViewContent", CompanyData.getInstance().companyItemSystems.get(category).contentIds.get(position).getContents());
        intent.putExtra("itemListViewIconUrl", CompanyData.getInstance().companyItemSystems.get(category).contentIds.get(position).getIconUrl());
        byte[] tmpIconByteArray = Bitmap2Bytes(CompanyData.getInstance().companyItemSystems.get(category).contentIds.get(position).getIcon());
        intent.putExtra("itemListViewIcon", tmpIconByteArray);
        CompanyData.getInstance().currentCompanyItem = CompanyData.getInstance().companyItemSystems.get(category).contentIds.get(position);
        startActivityForResult(intent, 0);

    }

    private byte[] Bitmap2Bytes(Bitmap bm){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);

        return baos.toByteArray();

    }

    private void getView()
    {
        listview = (ListView) findViewById(R.id.companyItemSystem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_company_item_system, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        Log.e("XXX",String.valueOf(id));
//        Log.e("XXX",String.valueOf(android.R.id.home));
        switch (id) {
            case android.R.id.home:
                finish();
//                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
