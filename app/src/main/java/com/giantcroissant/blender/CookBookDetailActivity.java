package com.giantcroissant.blender;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class CookBookDetailActivity extends AppCompatActivity
        implements CookBookDetailInfoFragment.OnCookBookDetailInfoFragmentInteractionListener,
        CookBookDetailToDoFragment.OnCookBookDetailVideoFragmentInteractionListener,
        View.OnClickListener {

    private ActionBar actionBar;                              // Declaring the Toolbar Object
    private Cookbook cookBook;
    private CookBookDetailInfoFragment cookBookDetailInfoFragment;
    private CookBookDetailToDoFragment cookBookDetailToDoFragment;
    private Realm realm;
    private RealmQuery<CookBookRealm> cookBookRealmQuery;
    private RealmResults<CookBookRealm> cookBookRealmResult;
    private boolean isConnected = false;
    private boolean isNeedStartBlender = false;
    private boolean isFinished = false;
    public int currentFragmentIndex = 0;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private final static String TAG = CookBookDetailActivity.class.getSimpleName();
    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private BluetoothGattCharacteristic mClickCharacteristic;
    private boolean mConnected = false;
    Button connectBlueToothbutton;
    Button confrimhbutton;
    Button startBlenderbutton;
    Button skipBlenderhbutton;
    Button finishhbutton;
    TextView IsConnectedBlueToothText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_book_detial);

        Intent intent = getIntent();
        getView();

        cookBook = new Cookbook();
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
        cookBook.setTimeOfStep(intent.getStringArrayListExtra("cookBookListViewTimeOfSteps"));
        cookBook.setSpeedOfStep(intent.getStringArrayListExtra("cookBookListViewSpeedOfSteps"));

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
        currentFragmentIndex = 0;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.contentfragment, new CookBookDetailInfoFragment().newInstance(1,cookBook));
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
        if (view.getId() == R.id.cook_book_Info_button) {
            currentFragmentIndex = 0;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (cookBookDetailInfoFragment == null)
            {
                cookBookDetailInfoFragment = new CookBookDetailInfoFragment().newInstance(1,cookBook);
            }
            fragmentTransaction.replace(R.id.contentfragment, cookBookDetailInfoFragment);

            ImageButton infoButton = (ImageButton) findViewById(R.id.cook_book_Info_button_SelectColor);
            infoButton.setImageResource(R.color.TabSelectColor);

//            ImageButton videoButton = (ImageButton) findViewById(R.id.cook_book_video_button_SelectColor);
//            videoButton.setImageResource(R.drawable.hotcookbook_false);

            ImageButton toDoButton = (ImageButton) findViewById(R.id.cook_book_to_do_button_SelectColor);
            toDoButton.setImageResource(R.color.TabNoSelectColor);

            fragmentTransaction.commit();
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
            intent.putExtra("cookBookListViewTimeOfSteps", cookBook.getTimeOfSteps());
            intent.putExtra("cookBookListViewSpeedOfSteps", cookBook.getSpeedOfSteps());

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

            ImageButton infoButton = (ImageButton) findViewById(R.id.cook_book_Info_button_SelectColor);
            infoButton.setImageResource(R.color.TabNoSelectColor);

//            ImageButton videoButton = (ImageButton) findViewById(R.id.cook_book_video_button_SelectColor);
//            videoButton.setImageResource(R.drawable.hotcookbook_false);

            ImageButton toDoButton = (ImageButton) findViewById(R.id.cook_book_to_do_button_SelectColor);
            toDoButton.setImageResource(R.color.TabSelectColor);

            fragmentTransaction.commit();
//            mClickCharacteristic = BlueToothManager.getInstance().mClickCharacteristic;


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
            isConnected = mBluetoothLeService != null && mClickCharacteristic != null;
            if(isConnected == false)
            {
                Intent intent = new Intent(this, DeviceScanActivity.class);
                intent.putExtra("Name", "ToDoList");
                startActivity(intent);
            }
            checkButtonState();

        }
        else if(view.getId() == R.id.ConfrimButton)
        {
            confrimhbutton = (Button)findViewById(R.id.ConfrimButton);
            cookBookDetailToDoFragment.setConfrim();
            isNeedStartBlender = cookBookDetailToDoFragment.getIsNeedStartBlender();
            isFinished = cookBookDetailToDoFragment.getFinished();
            checkButtonState();
        }
        else if(view.getId() == R.id.StartBlenderButton)
        {
            startBlenderbutton = (Button) findViewById(R.id.StartBlenderButton);
            cookBookDetailToDoFragment.setConfrim();
            isNeedStartBlender = cookBookDetailToDoFragment.getIsNeedStartBlender();
            isFinished = cookBookDetailToDoFragment.getFinished();
            checkButtonState();

            byte[] sendmsg = new byte[10];
            sendmsg[0] = (byte) 0xA5;
            sendmsg[1] = (byte) 0x5A;
            sendmsg[9] = (byte) 0xB3;
            sendmsg[2] = (byte) 0x07;
            sendmsg[3] = (byte) 0x01;
            sendmsg[4] = (byte) ((cookBookDetailToDoFragment.getCookBoookTimeOfSteps()+1)*5 % 256);//((npTime.getValue()+1)*5 % 256);
            sendmsg[5] = (byte) ((cookBookDetailToDoFragment.getCookBoookTimeOfSteps()+1)*5 / 256);//((npTime.getValue()+1)*5 / 256);
            sendmsg[6] = (byte) (cookBookDetailToDoFragment.getCookBoookSpeedOfSteps() % 256);//(npSpeed.getValue() % 256);
            sendmsg[7] = (byte) (cookBookDetailToDoFragment.getCookBoookSpeedOfSteps() / 256);//(npSpeed.getValue() / 256);
            sendmsg[8] = (byte) 0x01;

//            mDeviceName = BlueToothData.getInstance().mDeviceName;
//            mDeviceAddress = BlueToothData.getInstance().mDeviceAddress;
//            mClickCharacteristic = BlueToothData.getInstance().mClickCharacteristic;
//
//            Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
//            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
//            Log.e("XXX", String.valueOf(mClickCharacteristic != null));
//            Log.e("XXX", String.valueOf(mBluetoothLeService != null));
            if(mClickCharacteristic != null && mBluetoothLeService != null)
            {

                mClickCharacteristic.setValue(sendmsg);
                mClickCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                mBluetoothLeService.writeCharacteristic(mClickCharacteristic);
            }

        }
        else if(view.getId() == R.id.SkipBlenderButton)
        {
            skipBlenderhbutton = (Button) findViewById(R.id.SkipBlenderButton);
            isFinished = cookBookDetailToDoFragment.getFinished();
            cookBookDetailToDoFragment.setConfrim();
            isNeedStartBlender = cookBookDetailToDoFragment.getIsNeedStartBlender();
            checkButtonState();

        }
        else if(view.getId() == R.id.FinishButton)
        {
            finishhbutton = (Button) findViewById(R.id.FinishButton);
            isConnected = mBluetoothLeService != null && mClickCharacteristic != null;
            cookBookDetailToDoFragment.setReStart();
            isNeedStartBlender = cookBookDetailToDoFragment.getIsNeedStartBlender();
            isFinished = cookBookDetailToDoFragment.getFinished();
            checkButtonState();

        }


    }

    void checkButtonState()
    {
        if(currentFragmentIndex == 1)
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
                if(isConnected == false)
                {
                    connectBlueToothbutton.setVisibility(View.VISIBLE);
                    confrimhbutton.setVisibility(View.INVISIBLE);
                    startBlenderbutton.setVisibility(View.INVISIBLE);
                    skipBlenderhbutton.setVisibility(View.INVISIBLE);
                    finishhbutton.setVisibility(View.INVISIBLE);

                    IsConnectedBlueToothText.setText("未連接果汁機");
                }
                else
                {
                    IsConnectedBlueToothText.setText("已連接果汁機");
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
    public void onCookBookDetailVideoFragmentInteraction(String string) {

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

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

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
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
//                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
//                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);

        }
        else
        {

            mDeviceName = BlueToothData.getInstance().mDeviceName;
            mDeviceAddress = BlueToothData.getInstance().mDeviceAddress;

            if(mDeviceName != null && mDeviceAddress != null )
            {
                Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
                bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
            }

            mClickCharacteristic = BlueToothData.getInstance().mClickCharacteristic;

        }

        isConnected = mBluetoothLeService != null && mClickCharacteristic != null;
        checkButtonState();

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

        if(isConnected)
        {
            unbindService(mServiceConnection);
            mBluetoothLeService = null;
        }
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isConnected = mBluetoothLeService != null && mClickCharacteristic != null;

                if(IsConnectedBlueToothText != null)
                {
                    if(isConnected == false)
                    {
                        IsConnectedBlueToothText.setText("未連接果汁機");
                    }
                    else
                    {
                        IsConnectedBlueToothText.setText("已連接果汁機");
                    }
                }

            }
        });
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);

            if(mGattCharacteristics.size() == 3)
            {
                BlueToothData.getInstance().mClickCharacteristic = mGattCharacteristics.get(2).get(0);
                mClickCharacteristic =  BlueToothData.getInstance().mClickCharacteristic;
                checkButtonState();

            }
//            Log.e("XXX", String.valueOf(mGattCharacteristics.size()));
        }
    }
}
