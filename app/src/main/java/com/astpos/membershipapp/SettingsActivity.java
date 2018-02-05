package com.astpos.membershipapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.astpos.membershipapp.util.Constants;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.astpos.membershipapp.util.Constants.TAG;


/**
 * Created by Iskren Iliev on 11/17/17.
 */


public class SettingsActivity extends Activity {

    private LinearLayout mainLayout;
    private Button saveButton, cancelButton, pingButton;
    private CheckBox checkBox;
    private Spinner spinnerTransName;
    private EditText editBusinessName, editServerIp, editServerPass, editPingData,
            editUserEmail, editUserPass;
    private ProgressBar progressBar;

    // Shared Preferences
    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEditor;

    private String transactionName = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init shared preferences
        this.preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.preferencesEditor = this.preferences.edit();
        this.printSharedPreferences();

        this.setLayout();
        this.setListeners();

    }


    private void setLayout(){
        setContentView(R.layout.activity_settings);

        mainLayout = (LinearLayout)findViewById(R.id.linearLayoutSettingsMain);

        progressBar = (ProgressBar)findViewById(R.id.indeterminateBar);

        saveButton = (Button)findViewById(R.id.save_button);
        saveButton.setOnClickListener(onSaveButtonClick);

        cancelButton = (Button)findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(onCancelButtonClick);

//        pingButton = (Button)findViewById(R.id.ping_button);
//        pingButton.setOnClickListener(onPingButtonClick);

        checkBox = (CheckBox) findViewById(R.id.checkbox);
        //disable button if checkbox is not checked else enable button
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( isChecked ) {
                    saveButton.setEnabled(true);
                }
                else {
                    saveButton.setEnabled(false);
                }
            }
        });

        editBusinessName = (EditText)findViewById(R.id.business_name_data);
        editServerIp = (EditText)findViewById(R.id.server_ip_data);
        editServerPass = (EditText) findViewById(R.id.server_pass_data);
//        editPingData = (EditText)findViewById(R.id.ping_data);
        editUserEmail = (EditText)findViewById(R.id.user_email);
        editUserPass = (EditText)findViewById(R.id.user_pass);

        spinnerTransName = (Spinner)findViewById(R.id.transaction_name);


        if(!preferences.getString(Constants.BUSINESS_NAME, "").isEmpty()) {
            editBusinessName.setText(preferences.getString(Constants.BUSINESS_NAME, ""));
        }
        if(!preferences.getString(Constants.SERVER_IP, "").isEmpty()) {
            editServerIp.setText(preferences.getString(Constants.SERVER_IP, ""));
        }
        if(!preferences.getString(Constants.SERVER_PASS, "").isEmpty()) {
            editServerPass.setText(preferences.getString(Constants.SERVER_PASS, ""));
        }
        if(!preferences.getString(Constants.USER_EMAIL_ID, "").isEmpty()) {
            editUserEmail.setText(preferences.getString(Constants.USER_EMAIL_ID, ""));
        }
        if(!preferences.getString(Constants.USER_EMAIL_PASS, "").isEmpty()) {
            editUserPass.setText(preferences.getString(Constants.USER_EMAIL_PASS, ""));
        }

        // preset with previously chosen transaction or INIT/0 by default
        spinnerTransName.setSelection(preferences.getInt(Constants.TRANSACTION_ID, 0));
    }


    private void setListeners(){
        //here implement spinner for transaction name drop down.
        spinnerTransName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                saveTransactionName(position);
                Log.i(TAG, "position: "+position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });
    }




    /**
     * This method used for save the position and name corresponding to
     * the selected spinner for transaction name.
     * @param position
     */
    public void saveTransactionName(int position){
        String name = spinnerTransName.getSelectedItem().toString();
        this.transactionName = name;
    }


    Button.OnClickListener onSaveButtonClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Save button clicked");
            if (checkBox.isChecked()){
                savePreferences();
//                printSharedPreferences();
                finish();
            }
            else {
                saveButton.setEnabled(false);
            }
        }
    };


    Button.OnClickListener onCancelButtonClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Cancel button clicked");
//            printSharedPreferences();
            // not opening new activity but going back to parent activity
            finish();
        }
    };




    private class PingAsyncTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String[] params) {
            // do above Server call here
//            Log.i(TAG, "ping IP: "+ params[0]);
            if(isPingable(params[0])) {
                return true;//;
            } else {
                return false;//;
            }
        }

        @Override
        protected void onPostExecute(Boolean pingIsOK) {
            //process message
            if(pingIsOK){
                editPingData.setText("Response OK");
                editPingData.setBackgroundColor(Color.GREEN);
                editPingData.setTextColor(Color.WHITE);
            } else {
                editPingData.setText("Unreachable");
                editPingData.setBackgroundColor(Color.RED);
                editPingData.setTextColor(Color.WHITE);
            }

            progressBar.setVisibility(View.GONE);
            mainLayout.setAlpha(1.0f);
            Log.i(TAG, "onPostExecute Ping: " + pingIsOK);
        }
    }


    public static boolean isPingable(String ipAddress) {
        InetAddress in;
        in = null;
        try {
            in = InetAddress.getByName(ipAddress);
            Log.d(TAG, "pinging " + ipAddress);

        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }

        try {
            if (in.isReachable(Constants.TIMEOUT_INTERVAL)) { //Response OK
                return true;
            } else { //Time out
                return false;
            }
        } catch (IOException e) {
            Log.d(TAG, e.toString());
            return false;
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Settings activity destroyed");
//        printSharedPreferences();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "Settings activity stopped");
        printSharedPreferences();
    }


    /**
     * hides keyboard on current page
     */
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }




    /**
     * Saves all user preferences (user defaults) from this activity
     */
    public void savePreferences() {
        this.preferencesEditor.putString(Constants.TRANSACTION_NAME, transactionName);
        this.preferencesEditor.putString(Constants.BUSINESS_NAME, editBusinessName.getText().toString());
        this.preferencesEditor.putString(Constants.SERVER_IP, editServerIp.getText().toString());
        this.preferencesEditor.putString(Constants.SERVER_PASS, editServerPass.getText().toString());
        this.preferencesEditor.putString(Constants.USER_EMAIL_ID, editUserEmail.getText().toString());
        this.preferencesEditor.putString(Constants.USER_EMAIL_PASS, editUserPass.getText().toString());

        this.preferencesEditor.apply();
    }

    private void printSharedPreferences() {
        Log.i(TAG, "============ PREFERENCES ===========");
        Log.i(TAG, Constants.BUSINESS_NAME + ": " + preferences.getString(Constants.BUSINESS_NAME, ""));
        Log.i(TAG, Constants.SERVER_IP + ": " + preferences.getString(Constants.SERVER_IP, ""));
        Log.i(TAG, Constants.SERVER_PASS + ": " + preferences.getString(Constants.SERVER_PASS, ""));
        Log.i(TAG, Constants.TRANSACTION_NAME + ": " + preferences.getString(Constants.TRANSACTION_NAME, ""));
        Log.i(TAG, "====================================");
    }
}

