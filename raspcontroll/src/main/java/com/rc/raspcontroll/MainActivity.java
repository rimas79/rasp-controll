package com.rc.raspcontroll;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.ToggleButton;

public class MainActivity extends ActionBarActivity {

    static final String APP_PREFERENCES = "mysettings";
    static final String APP_PREFERENCES_URL = "rasp_url";
    static final String APP_PREFERENCES_TOK = "rasp_tok";
    private static final String MSG_TAG = "MSG_HANDLER";
    SharedPreferences mSettings;

    public Handler _handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(MSG_TAG, String.format("Handler.handleMessage(): msg=%s", msg));
            // This is where main activity thread receives messages
            // Put here your handling of incoming messages posted by other threads
            super.handleMessage(msg);
        }

    };

    static private String raps_token = "";

    static public void setRaps_token(String token) {
        MainActivity.raps_token = token;
    }

    void setConnected(Boolean status){
        Switch sw = (Switch) findViewById(R.id.swConnect);
        sw.setChecked(status);
        if (!status){
            ImageView iv = (ImageView) findViewById(R.id.ivSwitchStatus);
            iv.setBackgroundColor(0xaaa);
        }
    }

    void setSwitch(Boolean status){
        ImageView iv = (ImageView) findViewById(R.id.ivSwitchStatus);
        if (status){
            iv.setBackgroundColor(0xff99cc00);
        }
        else {
            iv.setBackgroundColor(0xffff4444);
        }
    }

    void setLED(Boolean status){
        ToggleButton tb = (ToggleButton) findViewById(R.id.tbLED);
        tb.setChecked(status);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        if (mSettings.contains(APP_PREFERENCES_URL)) {
            EditText ed = (EditText) findViewById(R.id.raspURL);
            ed.setText(mSettings.getString(APP_PREFERENCES_URL, ""));
        }

        if (mSettings.contains(APP_PREFERENCES_TOK)) {
            raps_token = mSettings.getString(APP_PREFERENCES_TOK, "");
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        EditText ed = (EditText) findViewById(R.id.raspURL);

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_URL, ed.getText().toString());
        editor.putString(APP_PREFERENCES_TOK, raps_token);
        editor.apply();
    }

    public void tbConn_Click(View view) {
        return;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {



        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

    }

}
