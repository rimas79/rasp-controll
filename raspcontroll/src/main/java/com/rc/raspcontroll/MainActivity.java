package com.rc.raspcontroll;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener, Spinner.OnItemSelectedListener {

    static final String APP_PREFERENCES = "mysettings";
    static final String APP_PREFERENCES_URL = "rasp_url";

    private static String androidID;

/*
    команды
*/

    static final String get_token = "get_token";
    static final String get_sw = "get_sw";
    static final String set_led = "set_led";
// led bar
    static final String LED_BAR = "led_bar";
    static final String LED_BAR_DYN = "dynamic";
    static final String LED_BAR_RED = "red";
    static final String LED_BAR_GREEN = "green";
    static final String LED_BAR_BLUE = "blue";
    static final String LED_BAR_BRIGHT = "bright";
    static final String LED_BAR_SPEED = "speed";
    static final String LED_BAR_DYN_MODE = "dyn_mode";
    static final String LED_BAR_DYN_EFF = "dyn_effect";
//

    SeekBar seekBarRed;
    SeekBar seekBarGreen;
    SeekBar seekBarBlue;
    SeekBar seekBarBright;

    Spinner spMode;
    Spinner spSpeed;
    Spinner spEffect;

    SharedPreferences mSettings;

    static private String raps_token = "";

    private String uri;

    private EditText edURL;

    static public void setRaps_token(String token) {
        MainActivity.raps_token = token;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        RaspHTTPClient rasp;
        rasp = new RaspHTTPClient();
        String command = uri;
        command += "/"+"LED_BAR?";
        command += LED_BAR_DYN+"=false&";
        command += LED_BAR_RED+"="+seekBarRed.getProgress()+"&";
        command += LED_BAR_GREEN+"="+seekBarGreen.getProgress()+"&";
        command += LED_BAR_BLUE+"="+seekBarBlue.getProgress()+"&";
        command += LED_BAR_BRIGHT+"="+seekBarBright.getProgress();
        rasp.execute(command);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int position, long id) {
        // показываем позиция нажатого элемента
        RaspHTTPClient rasp;
        rasp = new RaspHTTPClient();
        String command = uri+"/"+"LED_BAR?"+LED_BAR_DYN+"=true&"+LED_BAR_SPEED+"="+(spSpeed.getSelectedItemPosition()+1)+"&"+LED_BAR_DYN_MODE+"="+(spMode.getSelectedItemPosition()+1)+"&"+LED_BAR_DYN_EFF+"="+(spEffect.getSelectedItemPosition()+1);
     //   command = command + LED_BAR_RED+"="+seekBarRed.getProgress()+"&"+LED_BAR_GREEN+"="+seekBarGreen.getProgress()+"&"+LED_BAR_BLUE+"="+seekBarBlue.getProgress();
        rasp.execute(command);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    void setConnected(Boolean status){
        Switch sw = (Switch) findViewById(R.id.swConnect);
        sw.setChecked(status);
        if (!status){
//            ImageView iv = (ImageView) findViewById(R.id.ivSwStatus);
//            iv.setBackgroundColor(0xaaa);
        }

    }

    void setSwitch(Boolean status){
/*        ImageView iv = (ImageView) findViewById(R.id.ivSwStatus);
        if (status){
            iv.setBackgroundColor(0xff99cc00);
        }
        else {
            iv.setBackgroundColor(0xffff4444);
        }
*/
    }

    void setLED(Boolean status){
/*        ToggleButton tb = (ToggleButton) findViewById(R.id.tbLED);
        tb.setChecked(status);
*/
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

        androidID = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();

        seekBarRed = (SeekBar)findViewById(R.id.seekBarRed);
        seekBarRed.setOnSeekBarChangeListener(this);
        seekBarGreen = (SeekBar)findViewById(R.id.seekBarGreen);
        seekBarGreen.setOnSeekBarChangeListener(this);
        seekBarBlue = (SeekBar)findViewById(R.id.seekBarBlue);
        seekBarBlue.setOnSeekBarChangeListener(this);
        seekBarBright = (SeekBar)findViewById(R.id.seekBarBright);
        seekBarBright.setOnSeekBarChangeListener(this);

        spMode = (Spinner)findViewById(R.id.spinnerModes);
        spMode.setOnItemSelectedListener(this);
        spSpeed = (Spinner)findViewById(R.id.spinnerSpeeds);
        spSpeed.setOnItemSelectedListener(this);
        spEffect = (Spinner)findViewById(R.id.spinnerEffects);
        spEffect.setOnItemSelectedListener(this);

        edURL = (EditText) findViewById(R.id.raspURL);

        edURL.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){}

            @Override
            public void afterTextChanged(Editable editable) {
                uri = editable.toString();
            }
        });

        if (mSettings.contains(APP_PREFERENCES_URL)) {
            uri = mSettings.getString(APP_PREFERENCES_URL, "");
            edURL.setText(uri);
        }

    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_URL, edURL.getText().toString());
        editor.apply();
    }

    public void tbConn_Click(View view) {
        RaspHTTPClient rasp;
        rasp = new RaspHTTPClient();
        rasp.execute(uri+"/"+get_token+"?andr_id="+androidID);
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

    public class RaspHTTPClient extends AsyncTask<String, Void, String> {

        // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream, int len) throws IOException {
            Reader reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, len);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        @Override
        protected String doInBackground(String... urls){
            try {
                String res = downloadUrl(urls[0]);
                return res;
            }
            catch (IOException e) {
                return "Unable to retrieve web page:"+e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            if (result.equals("OK")) {
                setConnected(true);
            }
            else if (result.startsWith("TOKEN")) {
                String[] res_tok = result.split("=", 2);
                setRaps_token(res_tok[1]);
                setConnected(true);
            }
            else if (result.equals("SW_ON")){
                setSwitch(true);
            }
            else if (result.equals("SW_OFF")){
                setSwitch(false);
            }
            else if (result.equals("LED_ON")){
                setLED(true);
            }
            else if (result.equals("LED_OFF")){
                setLED(false);
            }
            else if (result.equals("LED_BAR_OK")){
//                setLED(false);
            }
            else {
                setConnected(false);
            }
        }

    }


}
