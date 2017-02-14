package com.gmail.huashadow.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.gmail.huashadow.myapplication.ticwear.R;


public class DisplayMessageActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        if (null == message) {
            message = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        //for test SharedPreferences
        SharedPreferences sharedPref = getPreferences(MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_message), message);
        editor.commit();

        //test get SharedPreferences MainActivity
//        SharedPreferences prefFromMainActivity = getSharedPreferences("MainActivity", MODE_PRIVATE);
//        String messageFromMainActivity = prefFromMainActivity.getString(getString(R.string.saved_message), "null");
//        message += "\n From MainActivity SharedPreferences: " + messageFromMainActivity;

        TextView textView = new TextView(this);
        textView.setTextSize(18);
        textView.setText(message);

        setContentView(textView);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
}
