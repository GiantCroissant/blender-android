package com.giantcroissant.blender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class CookBookDetailActivity extends AppCompatActivity
        implements CookBookDetailInfoFragment.OnCookBookDetailInfoFragmentInteractionListener,
        CookBookDetailToDoFragment.OnCookBookDetailToDoFragmentInteractionListener,
        View.OnClickListener {

    private ActionBar actionBar;                              // Declaring the Toolbar Object
    private Cookbook cookBook;
    private CookBookDetailInfoFragment cookBookDetailInfoFragment;
    private CookBookDetailToDoFragment cookBookDetailToDoFragment;
    private Realm realm;
    private RealmQuery<CookBookRealm> cookBookRealmQuery;
    private RealmResults<CookBookRealm> cookBookRealmResult;
//    private boolean isConnected = false;
    private boolean isNeedStartBlender = false;
    private boolean isFinished = false;
    public int currentFragmentIndex = 0;

//    private final String LIST_NAME = "NAME";
//    private final String LIST_UUID = "UUID";
//    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
//            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
//    private final static String TAG = CookBookDetailActivity.class.getSimpleName();
//    private String mDeviceName;
//    private String mDeviceAddress;
//    private BluetoothLeService mBluetoothLeService;
//    private BluetoothGattCharacteristic mClickCharacteristic;
//    private boolean mConnected = false;
    Button connectBlueToothbutton;
    Button confrimhbutton;
    Button startBlenderbutton;
    Button skipBlenderhbutton;
    Button finishhbutton;
    TextView IsConnectedBlueToothText;
    private int position;
    private int requestCode = 0;
    private int resultCode = 0;
    private boolean doing = false;
    private int currentStateIndex = 0;
    private Handler mHandler;
    private CookToDoData cookToDoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_book_detial);

        Intent intent = getIntent();
        getView();
        requestCode = intent.getIntExtra("requestCode",0);
        position = intent.getIntExtra("position",0);
        currentFragmentIndex = intent.getIntExtra("currentFragmentIndex",0);

        CookbookParcelable cp = (CookbookParcelable)intent.getParcelableExtra("cookbook");
        cookBook = ConvertToCookbook.convertFromParceable(cp);

//        cookBook = new Cookbook();
//        cookBook.setId(intent.getStringExtra("cookBookListViewID"));
//        cookBook.setName(intent.getStringExtra("cookBookListViewName"));
//        cookBook.setDescription(intent.getStringExtra("cookBookListViewDescription"));
//        cookBook.setUrl(intent.getStringExtra("cookBookListViewUrl"));
//        cookBook.setImageUrl(intent.getStringExtra("cookBookListViewImageUrl"));
//        cookBook.setIngredient(intent.getStringExtra("cookBookListViewIngredient"));
//        cookBook.setStep(intent.getStringArrayListExtra("cookBookListViewSteps"));
//        cookBook.setViewedPeopleCount(intent.getIntExtra("cookBookListViewViewPeople", 0));
//        cookBook.setCollectedPeopleCount(intent.getIntExtra("cookBookListViewCollectedPeople", 0));
//        cookBook.setIsCollected(intent.getBooleanExtra("cookBookListIsCollected", false));
//        cookBook.setTimeOfStep(intent.getStringArrayListExtra("cookBookListViewTimeOfSteps"));
//        cookBook.setSpeedOfStep(intent.getStringArrayListExtra("cookBookListViewSpeedOfSteps"));
//        cookBook.setImageID(intent.getIntExtra("cookBookImageId", 0));
//        cookBook.setImage(BitmapFactory.decodeResource(getResources(),cookBook.getImageID()));

        if(intent.getIntExtra("requestCode",0) != -1)
        {
            requestCode = intent.getIntExtra("requestCode",0);

        }

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        actionBar.setTitle(cookBook.getName());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setValueToView();
        getRealm();
        setRealmData();
        mHandler = new Handler();
//        ReplaceFont.replaceDefaultFont(this, "DEFAULT", "fonts/NotoSansCJKjp-Medium.otf");

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
        if(requestCode == 2)
        {
            currentFragmentIndex = 1;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (cookBookDetailToDoFragment == null)
            {
                cookBookDetailToDoFragment = new CookBookDetailToDoFragment().newInstance(2,cookBook);
            }

            fragmentTransaction.replace(R.id.contentfragment, cookBookDetailToDoFragment);


            ImageButton infoButtonColor = (ImageButton) findViewById(R.id.cook_book_Info_button_SelectColor);
            ImageButton toDoButtonColor = (ImageButton) findViewById(R.id.cook_book_to_do_button_SelectColor);

            Button videoButton = (Button) findViewById(R.id.cook_book_video_button);
            Button infoButton = (Button) findViewById(R.id.cook_book_Info_button);
            Button toDoButton = (Button) findViewById(R.id.cook_book_to_do_button);
            infoButton.setTextColor(getResources().getColor(R.color.c70White));
            videoButton.setTextColor(getResources().getColor(R.color.c70White));
            toDoButton.setTextColor(getResources().getColor(R.color.White));

//            videoButton.setImageResource(R.drawable.hotcookbook_false);
            infoButtonColor.setImageResource(R.color.TabNoSelectColor);
            toDoButtonColor.setImageResource(R.color.TabSelectColor);

            fragmentTransaction.commit();
        }
        else
        {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            cookBookDetailInfoFragment = new CookBookDetailInfoFragment().newInstance(1,cookBook);

            fragmentTransaction.replace(R.id.contentfragment, cookBookDetailInfoFragment);

            ImageButton infoButtonColor = (ImageButton) findViewById(R.id.cook_book_Info_button_SelectColor);
            ImageButton toDoButtonColor = (ImageButton) findViewById(R.id.cook_book_to_do_button_SelectColor);

            Button videoButton = (Button) findViewById(R.id.cook_book_video_button);
            Button infoButton = (Button) findViewById(R.id.cook_book_Info_button);
            Button toDoButton = (Button) findViewById(R.id.cook_book_to_do_button);
            infoButton.setTextColor(getResources().getColor(R.color.White));
            videoButton.setTextColor(getResources().getColor(R.color.c70White));
            toDoButton.setTextColor(getResources().getColor(R.color.c70White));

//            videoButton.setImageResource(R.drawable.hotcookbook_false);
            infoButtonColor.setImageResource(R.color.TabSelectColor);
            toDoButtonColor.setImageResource(R.color.TabNoSelectColor);

            fragmentTransaction.commit();
        }

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

            if(resultCode == 0 && cookToDoData.getInstance().doing)
            {
                Intent messageIntent = new Intent(this, MessageActivity.class);
                messageIntent.putExtra("currentStateIndex", cookToDoData.getInstance().currentStateIndex);
                startActivityForResult(messageIntent, 101);
            }
            else
            {
                if(requestCode == 103)
                {
                    setResult(requestCode);

                }
                else if(requestCode == 104)
                {
                    setResult(requestCode);
                }
                setResult(requestCode);
                Log.e("XXX2", String.valueOf(this.requestCode));

                finish();
            }

//                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.resultCode = requestCode;
        this.requestCode = requestCode;

//        Log.e("XXX3",String.valueOf(resultCode));

        if(requestCode == 103 || requestCode == 104)
        {
            this.requestCode = requestCode;
        }
        if(data != null)
        {
            cookToDoData.getInstance().currentStateIndex = data.getIntExtra("currentStateIndex",0);
        }
        if(resultCode == 101)
        {
            this.resultCode = 0;
            setResult(requestCode);

//            Log.e("XXX3", String.valueOf(resultCode)+"XD");
            finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view)
    {
//        Log.e("currentIndex","XXX");
        if (view.getId() == R.id.cook_book_Info_button) {
            currentFragmentIndex = 0;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (cookBookDetailInfoFragment == null)
            {
                cookBookDetailInfoFragment = new CookBookDetailInfoFragment().newInstance(1,cookBook);
            }
            fragmentTransaction.replace(R.id.contentfragment, cookBookDetailInfoFragment);

            ImageButton infoButtonColor = (ImageButton) findViewById(R.id.cook_book_Info_button_SelectColor);
            ImageButton toDoButtonColor = (ImageButton) findViewById(R.id.cook_book_to_do_button_SelectColor);

            Button videoButton = (Button) findViewById(R.id.cook_book_video_button);
            Button infoButton = (Button) findViewById(R.id.cook_book_Info_button);
            Button toDoButton = (Button) findViewById(R.id.cook_book_to_do_button);
            infoButton.setTextColor(getResources().getColor(R.color.White));
            videoButton.setTextColor(getResources().getColor(R.color.c70White));
            toDoButton.setTextColor(getResources().getColor(R.color.c70White));

//            videoButton.setImageResource(R.drawable.hotcookbook_false);
            infoButtonColor.setImageResource(R.color.TabSelectColor);
            toDoButtonColor.setImageResource(R.color.TabNoSelectColor);

            fragmentTransaction.commit();
        }

        else if (view.getId() == R.id.cook_book_video_button) {

            Intent intent = new Intent(this, CookBookDetailVideoActivity.class);

            intent.putExtra("cookbook", ConvertToCookbook.convertToParceable(cookBook));

//            intent.putExtra("cookBookListViewID", cookBook.getId());
//            intent.putExtra("cookBookListViewName", cookBook.getName());
//            intent.putExtra("cookBookListViewDescription", cookBook.getDescription());
//            intent.putExtra("cookBookListViewUrl", cookBook.getUrl());
//            intent.putExtra("cookBookListViewImageUrl", cookBook.getImageUrl());
//            intent.putExtra("cookBookListViewIngredient", cookBook.getIngredient());
//            intent.putExtra("cookBookListViewSteps", cookBook.getSteps());
//            intent.putExtra("cookBookListViewViewPeople", cookBook.getViewedPeopleCount());
//            intent.putExtra("cookBookListViewCollectedPeople", cookBook.getCollectedPeopleCount());
//            intent.putExtra("cookBookListIsCollected", cookBook.getIsCollected());
//            intent.putExtra("cookBookListViewTimeOfSteps", cookBook.getTimeOfSteps());
//            intent.putExtra("cookBookListViewSpeedOfSteps", cookBook.getSpeedOfSteps());

            startActivityForResult(intent, 0);
        }

        else if (view.getId() == R.id.cook_book_to_do_button) {
            currentFragmentIndex = 1;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (cookBookDetailToDoFragment == null)
            {
                cookBookDetailToDoFragment = new CookBookDetailToDoFragment().newInstance(2,cookBook);
            }

            fragmentTransaction.replace(R.id.contentfragment, cookBookDetailToDoFragment);


            ImageButton infoButtonColor = (ImageButton) findViewById(R.id.cook_book_Info_button_SelectColor);
            ImageButton toDoButtonColor = (ImageButton) findViewById(R.id.cook_book_to_do_button_SelectColor);

            Button videoButton = (Button) findViewById(R.id.cook_book_video_button);
            Button infoButton = (Button) findViewById(R.id.cook_book_Info_button);
            Button toDoButton = (Button) findViewById(R.id.cook_book_to_do_button);
            infoButton.setTextColor(getResources().getColor(R.color.c70White));
            videoButton.setTextColor(getResources().getColor(R.color.c70White));
            toDoButton.setTextColor(getResources().getColor(R.color.White));

//            videoButton.setImageResource(R.drawable.hotcookbook_false);
            infoButtonColor.setImageResource(R.color.TabNoSelectColor);
            toDoButtonColor.setImageResource(R.color.TabSelectColor);

            fragmentTransaction.commit();


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
        else if(view.getId() == R.id.connectBlueToothButton)
        {
            connectBlueToothbutton = (Button) findViewById(R.id.connectBlueToothButton);
            confrimhbutton = (Button)findViewById(R.id.ConfrimButton);
            startBlenderbutton = (Button) findViewById(R.id.StartBlenderButton);
            skipBlenderhbutton = (Button) findViewById(R.id.SkipBlenderButton);
            finishhbutton = (Button) findViewById(R.id.FinishButton);
            IsConnectedBlueToothText = (TextView) findViewById(R.id.IsConnectedBlueToothText);

//            Log.e("xxx", String.valueOf(connectBlueToothbutton == null));
            BlenderBluetoothManager.getInstance().getConnected();

//            BlueToothData.getInstance().mConnected = BlueToothData.getInstance().mBluetoothLeService != null && BlueToothData.getInstance().mClickCharacteristic != null;
            if(BlenderBluetoothManager.getInstance().getConnected() == false)
            {
//                Intent intent = new Intent(this, DeviceScanActivity.class);
//                intent.putExtra("Name", "ToDoList");
//                startActivity(intent);
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("cookBookListViewID", cookBook.getId());

                setResult(100, intent);
                finish();
            }
            checkButtonState();
            cookToDoData.getInstance().currentStateIndex = cookBookDetailToDoFragment.getCurrentIndex();

        }
        else if(view.getId() == R.id.ConfrimButton)
        {
            if(!cookToDoData.getInstance().doing)
            {
                cookToDoData.getInstance().doing = true;
            }
            confrimhbutton = (Button)findViewById(R.id.ConfrimButton);
            cookBookDetailToDoFragment.setConfrim();
            isNeedStartBlender = cookBookDetailToDoFragment.getIsNeedStartBlender();
            isFinished = cookBookDetailToDoFragment.getFinished();
            checkButtonState();
            cookToDoData.getInstance().currentStateIndex = cookBookDetailToDoFragment.getCurrentIndex();
        }
        else if(view.getId() == R.id.StartBlenderButton)
        {
            if(!cookToDoData.getInstance().doing)
            {
                cookToDoData.getInstance().doing = true;
            }

//            byte[] sendmsg = new byte[10];
//            sendmsg[0] = (byte) 0xA5;
//            sendmsg[1] = (byte) 0x5A;
//            sendmsg[9] = (byte) 0xB3;
//            sendmsg[2] = (byte) 0x07;
//            sendmsg[3] = (byte) 0x01;
//            sendmsg[4] = (byte) (cookBookDetailToDoFragment.getCookBoookTimeOfSteps()-1 % 256);//((npTime.getValue()+1)*5 % 256);
//            sendmsg[5] = (byte) (cookBookDetailToDoFragment.getCookBoookTimeOfSteps()-1 / 256);//((npTime.getValue()+1)*5 / 256);
//            sendmsg[6] = (byte) (cookBookDetailToDoFragment.getCookBoookSpeedOfSteps() % 256);//(npSpeed.getValue() % 256);
//            sendmsg[7] = (byte) (cookBookDetailToDoFragment.getCookBoookSpeedOfSteps() / 256);//(npSpeed.getValue() / 256);
//            sendmsg[8] = (byte) 0x01;

//            Log.e("getCookBoookSpeedOfSteps", String.valueOf(cookBookDetailToDoFragment.getCookBoookSpeedOfSteps()));
//            Log.e("getCookBoookTimeOfSteps", String.valueOf(cookBookDetailToDoFragment.getCookBoookTimeOfSteps()));

            startBlenderbutton = (Button) findViewById(R.id.StartBlenderButton);
            cookBookDetailToDoFragment.setConfrim();
            isNeedStartBlender = cookBookDetailToDoFragment.getIsNeedStartBlender();
            isFinished = cookBookDetailToDoFragment.getFinished();
            checkButtonState();
            cookToDoData.getInstance().currentStateIndex = cookBookDetailToDoFragment.getCurrentIndex();



//            mDeviceName = BlueToothData.getInstance().mDeviceName;
//            mDeviceAddress = BlueToothData.getInstance().mDeviceAddress;
//            mClickCharacteristic = BlueToothData.getInstance().mClickCharacteristic;
//
//            Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
//            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
//            Log.e("XXX", String.valueOf(mClickCharacteristic != null));
//            Log.e("XXX", String.valueOf(mBluetoothLeService != null));
            BlenderBluetoothManager.getInstance().startBlending(cookBookDetailToDoFragment.getCookBoookTimeOfSteps(),cookBookDetailToDoFragment.getCookBoookSpeedOfSteps());
//            if(BlenderBluetoothManager.getInstance().mClickCharacteristic != null && BlenderBluetoothManager.getInstance().mBluetoothLeService != null)
//            {
//
//                BlenderBluetoothManager.getInstance().mClickCharacteristic.setValue(sendmsg);
//                BlenderBluetoothManager.getInstance().mClickCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
//                BlenderBluetoothManager.getInstance().mBluetoothLeService.writeCharacteristic(BlenderBluetoothManager.getInstance().mClickCharacteristic);
//            }

        }
        else if(view.getId() == R.id.SkipBlenderButton)
        {
            if(!cookToDoData.getInstance().doing)
            {
                cookToDoData.getInstance().doing = true;
            }
            skipBlenderhbutton = (Button) findViewById(R.id.SkipBlenderButton);
            cookBookDetailToDoFragment.setConfrim();
            isNeedStartBlender = cookBookDetailToDoFragment.getIsNeedStartBlender();
            isFinished = cookBookDetailToDoFragment.getFinished();
            checkButtonState();
            cookToDoData.getInstance().currentStateIndex = cookBookDetailToDoFragment.getCurrentIndex();

        }
        else if(view.getId() == R.id.FinishButton)
        {
            if(cookToDoData.getInstance().doing)
            {
                cookToDoData.getInstance().doing = false;
            }
            finishhbutton = (Button) findViewById(R.id.FinishButton);
            BlenderBluetoothManager.getInstance().getConnected();
            cookBookDetailToDoFragment.setReStart();
            isNeedStartBlender = cookBookDetailToDoFragment.getIsNeedStartBlender();
            isFinished = cookBookDetailToDoFragment.getFinished();
            checkButtonState();
            cookToDoData.getInstance().currentStateIndex = cookBookDetailToDoFragment.getCurrentIndex();

        }


    }

    void checkButtonState()
    {
        if(currentFragmentIndex == 1 && resultCode != 102)
        {
            if(isFinished == true)
            {
                connectBlueToothbutton.setVisibility(View.INVISIBLE);
                confrimhbutton.setVisibility(View.INVISIBLE);
                startBlenderbutton.setVisibility(View.INVISIBLE);
                skipBlenderhbutton.setVisibility(View.INVISIBLE);
                finishhbutton.setVisibility(View.VISIBLE);
            }
            else
            {
                if(BlenderBluetoothManager.getInstance().getConnected() == false)
                {
                    connectBlueToothbutton.setVisibility(View.VISIBLE);
                    confrimhbutton.setVisibility(View.INVISIBLE);
                    startBlenderbutton.setVisibility(View.INVISIBLE);
                    skipBlenderhbutton.setVisibility(View.INVISIBLE);
                    finishhbutton.setVisibility(View.INVISIBLE);
                    if(IsConnectedBlueToothText != null)
                    {
                        IsConnectedBlueToothText.setText(R.string.disconnected);
                    }
                }
                else
                {
                    if(IsConnectedBlueToothText != null)
                    {
                        IsConnectedBlueToothText.setText(R.string.connected);

                    }

                    if(isNeedStartBlender == true)
                    {
                        connectBlueToothbutton.setVisibility(View.INVISIBLE);
                        confrimhbutton.setVisibility(View.INVISIBLE);
                        startBlenderbutton.setVisibility(View.VISIBLE);
                        skipBlenderhbutton.setVisibility(View.VISIBLE);
                        finishhbutton.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        connectBlueToothbutton.setVisibility(View.INVISIBLE);
                        confrimhbutton.setVisibility(View.VISIBLE);
                        startBlenderbutton.setVisibility(View.INVISIBLE);
                        skipBlenderhbutton.setVisibility(View.INVISIBLE);
                        finishhbutton.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }


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
    public void onCookBookDetailToDoFragmentInteraction(String string)
    {
        cookBookDetailToDoFragment.setIsConnected(BlenderBluetoothManager.getInstance().getConnected());
        cookBookDetailToDoFragment.setCurrentIndex(cookToDoData.getInstance().currentStateIndex);

        mHandler.post(checkIsConnected);
    }


    private Runnable checkIsConnected = new Runnable() {
        @Override
        public void run() {
            new checkConnected().execute(String.valueOf(BlenderBluetoothManager.getInstance().getConnected()));
        }
    };

    private class checkConnected extends AsyncTask<String, String, Boolean> {

        // Background
        protected Boolean doInBackground(String... args) {

//            BlueToothData.getInstance().mConnected = BlueToothData.getInstance().mBluetoothLeService != null && BlueToothData.getInstance().mClickCharacteristic != null;
            BlenderBluetoothManager.getInstance().getConnected();
            return BlenderBluetoothManager.getInstance().getConnected();
        }

        // UI
        protected void onPostExecute(Boolean mConnected) {
            if (mConnected == true) {
                connectBlueToothbutton = (Button) findViewById(R.id.connectBlueToothButton);
                confrimhbutton = (Button)findViewById(R.id.ConfrimButton);
                startBlenderbutton = (Button) findViewById(R.id.StartBlenderButton);
                skipBlenderhbutton = (Button) findViewById(R.id.SkipBlenderButton);
                finishhbutton = (Button) findViewById(R.id.FinishButton);
                IsConnectedBlueToothText = (TextView) findViewById(R.id.IsConnectedBlueToothText);

                if(connectBlueToothbutton != null)
                {
                    checkButtonState();
                }
            }
            else
            {
                mHandler.postDelayed(checkIsConnected, 5000);
            }
        }
    }


    @Override
    public void onPostResume() {
        super.onPostResume();

//        BlueToothData.getInstance().mConnected = BlueToothData.getInstance().mBluetoothLeService != null && BlueToothData.getInstance().mClickCharacteristic != null;
        BlenderBluetoothManager.getInstance().getConnected();
        if(currentFragmentIndex == 1 && resultCode != 102)
        {
            connectBlueToothbutton = (Button) findViewById(R.id.connectBlueToothButton);
            confrimhbutton = (Button)findViewById(R.id.ConfrimButton);
            startBlenderbutton = (Button) findViewById(R.id.StartBlenderButton);
            skipBlenderhbutton = (Button) findViewById(R.id.SkipBlenderButton);
            finishhbutton = (Button) findViewById(R.id.FinishButton);
            IsConnectedBlueToothText = (TextView) findViewById(R.id.IsConnectedBlueToothText);

            if(connectBlueToothbutton != null)
            {
                checkButtonState();
            }

        }
        resultCode = 0;
    }

    public void onFragmentAttached(int number)
    {
        switch (number) {
        case 1:

        break;

        case 2:
        break;
        }
    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
//            Log.e("onReceive", action.toString() + " by CookBookDetailActivity");
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {

//                BlueToothData.getInstance().mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {

//                BlueToothData.getInstance().mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
//                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                BlenderBluetoothManager.getInstance().displayGattServices(BlenderBluetoothManager.getInstance().mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
//                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        setDefaultFragment();
        BlenderBluetoothManager.getInstance().connectBlender(this, mGattUpdateReceiver);
        BlenderBluetoothManager.getInstance().getConnected();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        connectBlueToothbutton = null;
        confrimhbutton = null;
        startBlenderbutton = null;
        skipBlenderhbutton = null;
        finishhbutton = null;
        IsConnectedBlueToothText = null;

//        if(BlueToothData.getInstance().mConnected)
//        {
//            unbindService(mServiceConnection);
//            BlueToothData.getInstance().mBluetoothLeService = null;
//        }
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(IsConnectedBlueToothText != null)
                {
                    IsConnectedBlueToothText.setText(resourceId);
                }
                BlenderBluetoothManager.getInstance().getConnected();

//                BlueToothData.getInstance().mConnected = BlueToothData.getInstance().mBluetoothLeService != null && BlueToothData.getInstance().mClickCharacteristic != null;
            }
        });
    }
}
