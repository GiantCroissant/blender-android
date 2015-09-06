package com.giantcroissant.blender;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MessageActivity extends Activity implements View.OnClickListener
{

    private int requestCode = 0;
    private int currentStateIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
//        ReplaceFont.replaceDefaultFont(this, "DEFAULT", "fonts/NotoSansCJKjp-Medium.otf");
        Intent intent = getIntent();
        currentStateIndex = intent.getIntExtra("currentStateIndex",0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 101)
        {
            this.requestCode = requestCode;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.messageCancelButton)
        {
            Intent intent = new Intent(this, CookBookDetailActivity.class);
            intent.putExtra("currentStateIndex", currentStateIndex);
            setResult(102,intent);
            finish();
        }
        else if (view.getId() == R.id.messageConfrimButton)
        {
            setResult(101);
            finish();
        }
     }
}
