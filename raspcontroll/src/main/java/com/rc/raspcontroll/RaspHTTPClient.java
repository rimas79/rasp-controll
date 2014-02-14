package com.rc.raspcontroll;

import android.os.AsyncTask;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Dennis.Erokhin on 13.02.14.
 */
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
            return downloadUrl(urls[0]);
        }
        catch (IOException e) {
            return "Unable to retrieve web page. URL may be invalid.";
        }
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);

//        MainActivity act = get
// Just dummy message -- real implementation will put some meaningful data in it
        Message msg = Message.obtain();

        if (result.equals("OK")) {
//            MainActivity.setConnected(true);
            msg.what = 999;
            MainActivity.this._handler.sendMessage(msg);

        }
        else if (result.startsWith("TOKEN")) {
            String[] res_tok = result.split("=", 2);
            MainActivity.setRaps_token(res_tok[1]);
        }
        else if (result.equals("SW_ON")){
            MainActivity.setSwitch(true);
        }
        else if (result.equals("SW_OFF")){
            MainActivity.setSwitch(false);
        }
        else if (result.equals("LED_ON")){
            MainActivity.setLED(true);
        }
        else if (result.equals("LED_OFF")){
            MainActivity.setLED(false);
        }
        else {
            MainActivity.setConnected(false);
        }
    }

}