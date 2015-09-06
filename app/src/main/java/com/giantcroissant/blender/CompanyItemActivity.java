package com.giantcroissant.blender;

import android.content.Intent;
import android.os.Debug;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


public class CompanyItemActivity extends AppCompatActivity {

    private String itemID;
    private String itemTitle;
    private String itemContent;
    private String itemIconUrl;
    private ActionBar actionBar;                              // Declaring the Toolbar Object
    private TextView titelText;
    private TextView contentText;
    private ImageView iconImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_item);

        Intent intent = getIntent();
        getView();

        itemID = intent.getStringExtra("itemListViewID");
        itemTitle = intent.getStringExtra("itemListViewTitle");
        itemContent = intent.getStringExtra("itemListViewContent");
        itemIconUrl = intent.getStringExtra("itemListViewIconUrl");

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        setValueToView();
//        ReplaceFont.replaceDefaultFont(this, "DEFAULT", "fonts/NotoSansCJKjp-Medium.otf");

    }

    private void getView()
    {
        titelText = (TextView) findViewById(R.id.item_title_text);
        contentText = (TextView) findViewById(R.id.item_content_text);
        iconImage = (ImageView) findViewById(R.id.item_icon);
    }



    private void setValueToView()
    {
        titelText.setText(itemTitle);
        contentText.setText(itemContent);

//        Spannable span = (Spannable) contentText.getText();
////        span.setSpan(new RelativeSizeSpan(0.8f), 0, questions.getOwnerName().length(), 0);
//        span.setSpan(new ForegroundColorSpan(0xFFFF5522), 0, question.getOwnerName().length(),
//                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//
//        createTimeText.setText(DateDistance.twoDateDistance(question.getCreateTime(), new Date(System.currentTimeMillis())));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_company_item, menu);
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
