package com.giantcroissant.blender;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.util.ArrayList;


public class CompanyItemActivity extends AppCompatActivity {

    private String itemID;
    private String itemTitle;
    private String itemName;
    private ArrayList<String> itemContents;
    private String itemIconUrl;
    private ActionBar actionBar;                              // Declaring the Toolbar Object
    TextView titleView ;
    ImageView companyItemIcon ;
    TextView contentViewWColor;
    TextView contentViewSize;
    TextView contentViewWeight;
    TextView contentViewNation;
    TextView contentViewVoltage;
    TextView contentViewHz;
    TextView contentViewWatt;
    TextView contentViewVolume;
    TextView contentViewDescription;
    private ImageView iconImage;
    Bitmap icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_item);

        Intent intent = getIntent();
        getView();

        itemID = intent.getStringExtra("itemListViewID");
        itemTitle = intent.getStringExtra("itemListViewTitle");
        itemName = intent.getStringExtra("itemListViewName");
        itemContents = intent.getStringArrayListExtra("itemListViewContent");
        itemIconUrl = intent.getStringExtra("itemListViewIconUrl");
        byte[] tmpitemIcon = intent.getByteArrayExtra("itemListViewIcon");
        icon = Bytes2Bimap(tmpitemIcon);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        actionBar.setTitle(itemTitle);
        setValueToView();
//        ReplaceFont.replaceDefaultFont(this, "DEFAULT", "fonts/NotoSansCJKjp-Medium.otf");

    }

    private Bitmap Bytes2Bimap(byte[] b){

        if(b.length!=0){

            return BitmapFactory.decodeByteArray(b, 0, b.length);

        }

        else {

            return null;

        }

    }

    private void getView()
    {
        iconImage = (ImageView) findViewById(R.id.company_item_icon);


        // 讀取記事顏色、已選擇、標題與日期時間元件
        titleView = (TextView) findViewById(R.id.company_item_name);
        contentViewWColor = (TextView) findViewById(R.id.company_item_color);
        contentViewSize = (TextView) findViewById(R.id.company_item_size);
        contentViewWeight = (TextView) findViewById(R.id.company_item_weight);
        contentViewNation = (TextView) findViewById(R.id.company_item_nation);
        //contentViewKG = (TextView) findViewById(R.id.company_item_kg);
        contentViewVoltage = (TextView) findViewById(R.id.company_item_voltage);
        contentViewHz = (TextView) findViewById(R.id.company_item_hz);
        contentViewWatt = (TextView) findViewById(R.id.company_item_watt);
        contentViewVolume = (TextView) findViewById(R.id.company_item_volume);
        contentViewDescription = (TextView) findViewById(R.id.company_item_description);
    }



    private void setValueToView()
    {
        // 設定標題
        titleView.setText(itemName);
        contentViewWColor.setText(itemContents.get(1));
        contentViewSize.setText(itemContents.get(2));
        contentViewWeight.setText(itemContents.get(3));
        contentViewNation.setText(itemContents.get(4));
        //contentViewKG.setText(itemContents.get(5));
        contentViewVoltage.setText(itemContents.get(5));
        contentViewHz.setText(itemContents.get(6));
        contentViewWatt.setText(itemContents.get(7));
        contentViewVolume.setText(itemContents.get(8));
        contentViewDescription.setText(itemContents.get(9));

        iconImage.setImageBitmap(CompanyData.getInstance().currentCompanyItem.getIcon());


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
