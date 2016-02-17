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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.giantcroissant.blender.realm.CookBookRealm;

import io.realm.RealmQuery;
import io.realm.RealmResults;


public class CookBookDetailActivity extends AppCompatActivity
    implements CookBookDetailInfoFragment.OnCookBookDetailInfoFragmentInteractionListener,
    CookBookDetailToDoFragment.OnCookBookDetailToDoFragmentInteractionListener,
    View.OnClickListener {

    private ActionBar actionBar;
    private Cookbook cookBook;
    private CookBookDetailInfoFragment cookBookDetailInfoFragment;
    private CookBookDetailToDoFragment cookBookDetailToDoFragment;
    private RealmQuery<CookBookRealm> cookBookRealmQuery;
    private RealmResults<CookBookRealm> cookBookRealmResult;
    private boolean isNeedStartBlender = false;
    private boolean isFinished = false;
    public int currentFragmentIndex = 0;

    Button connectBlueToothButton;
    Button confirmButton;
    Button startBlenderButton;
    Button skipBlenderButton;
    Button finishButton;
    TextView isConnectedBlueToothTextView;

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

        requestCode = intent.getIntExtra("requestCode", 0);
        position = intent.getIntExtra("position", 0);
        currentFragmentIndex = intent.getIntExtra("currentFragmentIndex", 0);
        CookbookParcelable cp = intent.getParcelableExtra("cookbook");
        cookBook = ConvertToCookbook.convertFromParcelable(cp);

        if (intent.getIntExtra("requestCode", 0) != -1) {
            requestCode = intent.getIntExtra("requestCode", 0);
        }

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        actionBar.setTitle(cookBook.getName());

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mHandler = new Handler();
    }

    private void setDefaultFragment() {
        if (requestCode == 2) {

            currentFragmentIndex = 1;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (cookBookDetailToDoFragment == null) {
                cookBookDetailToDoFragment = new CookBookDetailToDoFragment().newInstance(2, cookBook);
            }

            fragmentTransaction.replace(R.id.content_fragment, cookBookDetailToDoFragment);


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

        } else {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            cookBookDetailInfoFragment = new CookBookDetailInfoFragment().newInstance(1, cookBook);

            fragmentTransaction.replace(R.id.content_fragment, cookBookDetailInfoFragment);

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
                if (resultCode == 0 && cookToDoData.getInstance().doing) {
                    Intent messageIntent = new Intent(this, MessageActivity.class);
                    messageIntent.putExtra("currentStateIndex", cookToDoData.getInstance().currentStateIndex);
                    startActivityForResult(messageIntent, 101);

                } else {
                    if (requestCode == 103) {
                        setResult(requestCode);

                    } else if (requestCode == 104) {
                        setResult(requestCode);
                    }
                    setResult(requestCode);
                    finish();
                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                }

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.resultCode = requestCode;
        this.requestCode = requestCode;
        if (requestCode == 103 || requestCode == 104) {
            this.requestCode = requestCode;
        }

        if (data != null) {
            cookToDoData.getInstance().currentStateIndex = data.getIntExtra("currentStateIndex", 0);
        }

        if (resultCode == 101) {
            this.resultCode = 0;
            setResult(requestCode);
            finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.cook_book_Info_button) {
            currentFragmentIndex = 0;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (cookBookDetailInfoFragment == null) {
                cookBookDetailInfoFragment = new CookBookDetailInfoFragment().newInstance(1, cookBook);
            }
            fragmentTransaction.replace(R.id.content_fragment, cookBookDetailInfoFragment);

            ImageButton infoButtonColor = (ImageButton) findViewById(R.id.cook_book_Info_button_SelectColor);
            ImageButton toDoButtonColor = (ImageButton) findViewById(R.id.cook_book_to_do_button_SelectColor);

            Button videoButton = (Button) findViewById(R.id.cook_book_video_button);
            Button infoButton = (Button) findViewById(R.id.cook_book_Info_button);
            Button toDoButton = (Button) findViewById(R.id.cook_book_to_do_button);
            infoButton.setTextColor(getResources().getColor(R.color.White));
            videoButton.setTextColor(getResources().getColor(R.color.c70White));
            toDoButton.setTextColor(getResources().getColor(R.color.c70White));
            infoButtonColor.setImageResource(R.color.TabSelectColor);
            toDoButtonColor.setImageResource(R.color.TabNoSelectColor);
            fragmentTransaction.commit();

        } else if (view.getId() == R.id.cook_book_video_button) {
            Intent intent = new Intent(this, CookBookDetailVideoActivity.class);
            intent.putExtra("cookbook", ConvertToCookbook.convertToParcelable(cookBook));
            startActivityForResult(intent, 0);

        } else if (view.getId() == R.id.cook_book_to_do_button) {
            currentFragmentIndex = 1;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (cookBookDetailToDoFragment == null) {
                cookBookDetailToDoFragment = new CookBookDetailToDoFragment().newInstance(2, cookBook);
            }

            fragmentTransaction.replace(R.id.content_fragment, cookBookDetailToDoFragment);


            ImageButton infoButtonColor = (ImageButton) findViewById(R.id.cook_book_Info_button_SelectColor);
            ImageButton toDoButtonColor = (ImageButton) findViewById(R.id.cook_book_to_do_button_SelectColor);

            Button videoButton = (Button) findViewById(R.id.cook_book_video_button);
            Button infoButton = (Button) findViewById(R.id.cook_book_Info_button);
            Button toDoButton = (Button) findViewById(R.id.cook_book_to_do_button);
            infoButton.setTextColor(getResources().getColor(R.color.c70White));
            videoButton.setTextColor(getResources().getColor(R.color.c70White));
            toDoButton.setTextColor(getResources().getColor(R.color.White));
            infoButtonColor.setImageResource(R.color.TabNoSelectColor);
            toDoButtonColor.setImageResource(R.color.TabSelectColor);
            fragmentTransaction.commit();

        } else if (view.getId() == R.id.likeCookBookButton) {



            setLikeCookBookButton();

        } else if (view.getId() == R.id.connectBlueToothButton) {
            connectBlueToothButton = (Button) findViewById(R.id.connectBlueToothButton);
            confirmButton = (Button) findViewById(R.id.ConfrimButton);
            startBlenderButton = (Button) findViewById(R.id.StartBlenderButton);
            skipBlenderButton = (Button) findViewById(R.id.SkipBlenderButton);
            finishButton = (Button) findViewById(R.id.FinishButton);
            isConnectedBlueToothTextView = (TextView) findViewById(R.id.IsConnectedBlueToothText);
            BlenderBluetoothManager.getInstance().getConnected();

            if (BlenderBluetoothManager.getInstance().getConnected() == false) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("cookBookListViewID", cookBook.getId());
                setResult(100, intent);
                finish();
            }
            checkButtonState();
            cookToDoData.getInstance().currentStateIndex = cookBookDetailToDoFragment.getCurrentIndex();

        } else if (view.getId() == R.id.ConfrimButton) {
            if (!cookToDoData.getInstance().doing) {
                cookToDoData.getInstance().doing = true;
            }
            confirmButton = (Button) findViewById(R.id.ConfrimButton);
            cookBookDetailToDoFragment.setConfrim();
            isNeedStartBlender = cookBookDetailToDoFragment.getIsNeedStartBlender();
            isFinished = cookBookDetailToDoFragment.getFinished();
            checkButtonState();
            cookToDoData.getInstance().currentStateIndex = cookBookDetailToDoFragment.getCurrentIndex();

        } else if (view.getId() == R.id.StartBlenderButton) {
            if (!cookToDoData.getInstance().doing) {
                cookToDoData.getInstance().doing = true;
            }
            startBlenderButton = (Button) findViewById(R.id.StartBlenderButton);
            cookBookDetailToDoFragment.setConfrim();
            isNeedStartBlender = cookBookDetailToDoFragment.getIsNeedStartBlender();
            isFinished = cookBookDetailToDoFragment.getFinished();
            checkButtonState();
            cookToDoData.getInstance().currentStateIndex = cookBookDetailToDoFragment.getCurrentIndex();
            BlenderBluetoothManager.getInstance().startBlending(
                cookBookDetailToDoFragment.getCookBoookTimeOfSteps(),
                cookBookDetailToDoFragment.getCookBoookSpeedOfSteps()
            );

        } else if (view.getId() == R.id.SkipBlenderButton) {
            if (!cookToDoData.getInstance().doing) {
                cookToDoData.getInstance().doing = true;
            }
            skipBlenderButton = (Button) findViewById(R.id.SkipBlenderButton);
            cookBookDetailToDoFragment.setConfrim();
            isNeedStartBlender = cookBookDetailToDoFragment.getIsNeedStartBlender();
            isFinished = cookBookDetailToDoFragment.getFinished();
            checkButtonState();
            cookToDoData.getInstance().currentStateIndex = cookBookDetailToDoFragment.getCurrentIndex();

        } else if (view.getId() == R.id.FinishButton) {
            if (cookToDoData.getInstance().doing) {
                cookToDoData.getInstance().doing = false;
            }
            finishButton = (Button) findViewById(R.id.FinishButton);
            BlenderBluetoothManager.getInstance().getConnected();
            cookBookDetailToDoFragment.setReStart();
            isNeedStartBlender = cookBookDetailToDoFragment.getIsNeedStartBlender();
            isFinished = cookBookDetailToDoFragment.getFinished();
            checkButtonState();
            cookToDoData.getInstance().currentStateIndex = cookBookDetailToDoFragment.getCurrentIndex();
        }
    }

    void checkButtonState() {
        if (currentFragmentIndex == 1 && resultCode != 102) {
            if (isFinished == true) {
                connectBlueToothButton.setVisibility(View.INVISIBLE);
                confirmButton.setVisibility(View.INVISIBLE);
                startBlenderButton.setVisibility(View.INVISIBLE);
                skipBlenderButton.setVisibility(View.INVISIBLE);
                finishButton.setVisibility(View.VISIBLE);

            } else {
                if (BlenderBluetoothManager.getInstance().getConnected() == false) {
                    connectBlueToothButton.setVisibility(View.VISIBLE);
                    confirmButton.setVisibility(View.INVISIBLE);
                    startBlenderButton.setVisibility(View.INVISIBLE);
                    skipBlenderButton.setVisibility(View.INVISIBLE);
                    finishButton.setVisibility(View.INVISIBLE);
                    if (isConnectedBlueToothTextView != null) {
                        isConnectedBlueToothTextView.setText(R.string.disconnected);
                    }

                } else {
                    if (isConnectedBlueToothTextView != null) {
                        isConnectedBlueToothTextView.setText(R.string.connected);
                    }

                    if (isNeedStartBlender == true) {
                        connectBlueToothButton.setVisibility(View.INVISIBLE);
                        confirmButton.setVisibility(View.INVISIBLE);
                        startBlenderButton.setVisibility(View.VISIBLE);
                        skipBlenderButton.setVisibility(View.VISIBLE);
                        finishButton.setVisibility(View.INVISIBLE);

                    } else {
                        connectBlueToothButton.setVisibility(View.INVISIBLE);
                        confirmButton.setVisibility(View.VISIBLE);
                        startBlenderButton.setVisibility(View.INVISIBLE);
                        skipBlenderButton.setVisibility(View.INVISIBLE);
                        finishButton.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }


    }

    private void setLikeCookBookButton() {
        ImageButton likeCookbookButton = (ImageButton) findViewById(R.id.likeCookBookButton);
        if (cookBook.getIsCollected()) {
            likeCookbookButton.setImageResource(R.drawable.icon_collect_y);

        } else {
            likeCookbookButton.setImageResource(R.drawable.icon_collect_n);
        }
    }

    @Override
    public void onCookBookDetailInfoFragmentInteraction(String string) {
    }

    @Override
    public void onCookBookDetailToDoFragmentInteraction(String string) {
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
            return BlenderBluetoothManager.getInstance().getConnected();
        }

        // UI
        protected void onPostExecute(Boolean mConnected) {
            if (mConnected == true) {
                connectBlueToothButton = (Button) findViewById(R.id.connectBlueToothButton);
                confirmButton = (Button) findViewById(R.id.ConfrimButton);
                startBlenderButton = (Button) findViewById(R.id.StartBlenderButton);
                skipBlenderButton = (Button) findViewById(R.id.SkipBlenderButton);
                finishButton = (Button) findViewById(R.id.FinishButton);
                isConnectedBlueToothTextView = (TextView) findViewById(R.id.IsConnectedBlueToothText);

                if (connectBlueToothButton != null) {
                    checkButtonState();
                }

            } else {
                mHandler.postDelayed(checkIsConnected, 5000);
            }
        }
    }


    @Override
    public void onPostResume() {
        super.onPostResume();
        BlenderBluetoothManager.getInstance().getConnected();
        if (currentFragmentIndex == 1 && resultCode != 102) {
            connectBlueToothButton = (Button) findViewById(R.id.connectBlueToothButton);
            confirmButton = (Button) findViewById(R.id.ConfrimButton);
            startBlenderButton = (Button) findViewById(R.id.StartBlenderButton);
            skipBlenderButton = (Button) findViewById(R.id.SkipBlenderButton);
            finishButton = (Button) findViewById(R.id.FinishButton);
            isConnectedBlueToothTextView = (TextView) findViewById(R.id.IsConnectedBlueToothText);
            if (connectBlueToothButton != null) {
                checkButtonState();
            }
        }
        resultCode = 0;
    }

    public void onFragmentAttached(int number) {
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

        connectBlueToothButton = null;
        confirmButton = null;
        startBlenderButton = null;
        skipBlenderButton = null;
        finishButton = null;
        isConnectedBlueToothTextView = null;
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isConnectedBlueToothTextView != null) {
                    isConnectedBlueToothTextView.setText(resourceId);
                }
                BlenderBluetoothManager.getInstance().getConnected();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

}