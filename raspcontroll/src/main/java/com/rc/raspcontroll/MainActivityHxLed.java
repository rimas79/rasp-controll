package com.rc.raspcontroll;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainActivityHxLed.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainActivityHxLed#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class MainActivityHxLed extends Fragment implements SeekBar.OnSeekBarChangeListener, Spinner.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    static final String APP_PREFERENCES = "mySettings";
    static final String APP_PREFERENCES_URL = "rasp_url";

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
    static final String LED_BAR_SWITCH = "switch";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private EditText edURL;

    SeekBar seekBarRed;
    SeekBar seekBarGreen;
    SeekBar seekBarBlue;
    SeekBar seekBarBright;

    Spinner spMode;
    Spinner spSpeed;
    Spinner spEffect;

    SharedPreferences mSettings;

    private String uri;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainActivityHxLed.
     */
    // TODO: Rename and change types and number of parameters
    public static MainActivityHxLed newInstance(String param1, String param2) {
        MainActivityHxLed fragment = new MainActivityHxLed();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public MainActivityHxLed() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mSettings = this.getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

    }

    @Override
    public void onResume(){
        super.onResume();

        seekBarRed = (SeekBar)getView().findViewById(R.id.seekBarRed);
        seekBarRed.setOnSeekBarChangeListener(this);
        seekBarGreen = (SeekBar)getView().findViewById(R.id.seekBarGreen);
        seekBarGreen.setOnSeekBarChangeListener(this);
        seekBarBlue = (SeekBar)getView().findViewById(R.id.seekBarBlue);
        seekBarBlue.setOnSeekBarChangeListener(this);
        seekBarBright = (SeekBar)getView().findViewById(R.id.seekBarBright);
        seekBarBright.setOnSeekBarChangeListener(this);

        spMode = (Spinner)getView().findViewById(R.id.spinnerModes);
        spMode.setOnItemSelectedListener(this);
        spSpeed = (Spinner)getView().findViewById(R.id.spinnerSpeeds);
        spSpeed.setOnItemSelectedListener(this);
        spEffect = (Spinner)getView().findViewById(R.id.spinnerEffects);
        spEffect.setOnItemSelectedListener(this);

        edURL = (EditText) getView().findViewById(R.id.raspURL);

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_main_activity_hx, container, false);
        final View button = view.findViewById(R.id.tbHXOnOff);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                /* DO SOMETHING UPON THE CLICK */
                        tbHXLed_Click(view);
                    }
                });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    public void tbHXLed_Click(View view) {
        RaspHTTPClient rasp;
        rasp = new RaspHTTPClient();
        String command = uri;
        ToggleButton sw = (ToggleButton) getView().findViewById(R.id.tbHXOnOff);
        command += "/"+"LED_BAR?"+LED_BAR_SWITCH;
        if (sw.isChecked()){
            command += "=true";
        }
        else {
            command += "=false";
        }

        rasp.execute(command);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
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
                return readIt(is, len);

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
                Log.e("HX", e.toString());
                return "Unable to retrieve web page:"+e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            Log.d("HX", result);
        }
    }
}
