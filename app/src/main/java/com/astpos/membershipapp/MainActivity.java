package com.astpos.membershipapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Layout;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    //tags
    public static final String TAG = "ASTPOS";
    public static final String USER_SIGN = "user_email";

    //users variables
    private String userName;
    private EditText phoneEditTex;
    private ConstraintLayout mainLayout;
    private RelativeLayout mainRelativeLayout;
    private ScrollView mainScrollLayout;
    private LinearLayout mainLinearLayout;

    // remote server
    private static final String[] ipAddressList = {"192.168.1.238"};
    private static final String[] fileNamesList = {"user_pic.jpg"};
    private static final String serverUser = "astpos";
    private static final String serverPass = "Amb88er275!!";
    private static final String serverStorageLocation = "/usr/local/akashi/bin/data";

    private File mainPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainPath = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        Log.d(TAG, "main path: " + mainPath);

//        mainRelativeLayout = (RelativeLayout) findViewById(R.id.mainRelativeLayout);
//        mainRelativeLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hideKeyboard(v);
//            }
//        });

//        mainScrollLayout = (ScrollView) findViewById(R.id.mainScrallView);
////        mainLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
////            @Override
////            public void onFocusChange(View v, boolean hasFocus) {
////                if (!hasFocus) {
////                    hideKeyboard(v);
////                }
////            }
////        });
//        mainScrollLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hideKeyboard(v);
//            }
//        });
//
        mainLinearLayout = (LinearLayout) findViewById(R.id.mainLinearLayout);
        mainLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
            }
        });


        phoneEditTex = (EditText) findViewById(R.id.editTextPhone);
        CustomTextWatcher phoneTextWatcher = new CustomTextWatcher(phoneEditTex);
//        phoneEditTex.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        phoneEditTex.addTextChangedListener(phoneTextWatcher);
        phoneEditTex.clearFocus();

    }



    public void useSignButton(View view) {
        Log.i(TAG, "Sign button clicked");

        Intent i;
        i = new Intent(this, SignatureActivity.class);
        startActivity(i);
    }

    public void usePicButton(View view) {
        Log.i(TAG, "Pic button clicked");

        Intent i;
        i = new Intent(this, PhotoIntentActivity.class);
        startActivity(i);
    }

    public void useTransferButton(View view) {
        Log.i(TAG, "Transfer button clicked");

        userName = phoneEditTex.getText().toString();

        new Thread(new Runnable() {
            public void run() {
            for(String ipAddress : ipAddressList) {
//                Log.d(TAG, "Start transferring to IP: " + ipAddress);
                for(String name : fileNamesList) {
                    String fromPath = mainPath+"/"+name;
                    String toPath = serverStorageLocation;
                    transferFile(ipAddress, fromPath, toPath, name);
                }
            }
            }
        }).start();
    }


    /**
     * hides keyboard on current page
     */
    private void hideKeyboard(View view) {
        Log.d(TAG, "Hide soft KB" ) ;

        View viewCurrent = this.getCurrentFocus();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if ((view == null) && (viewCurrent != null)) {
            view = viewCurrent;
        } else if(viewCurrent == null){ // exit if both views are null
            return;
        }

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /**
     * Transfers a specified file to a specific location on requested IP address
     * @param ipAddress IP address where to send file
     * @param from String path of location to copy on local host
     * @param to String path of location to store on remote host
     * @param fileName name of file to copy
     */
    private void transferFile(String ipAddress, String from, String to, String fileName) {
        // find file
        File file = new File(from);

        Log.d(TAG, " ========= TRANSFER LOG ============");
        Log.d(TAG, "file name     : " + file.getName());
        Log.d(TAG, "file local path   : " + file.getPath());

        JSch jsch = new JSch();
        Session session;
        try {
            session = jsch.getSession(serverUser, ipAddress, 22);

            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(serverPass);
            session.connect();
            Log.d(TAG, "Connected to " + ipAddress + "!");

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp channelSftp = (ChannelSftp) channel;

            SftpATTRS attrs;

            //change current location to Vigore App /bin if exists or exist
            try {
                attrs = channelSftp.stat(to);
                if(attrs.isDir()) {
                    channelSftp.cd(to);
                }
            } catch (Exception e) {
                Log.d(TAG, to +" not found! Exiting transfer...");
                return;
            }

            String currentDirectory = channelSftp.pwd();
            Log.d(TAG, "Current Dir on remote host: "+currentDirectory);

            String subDir = userName;

            // check if sub directory exists
            try {
                attrs = channelSftp.stat(currentDirectory+"/"+subDir);
                // create a sub directory if needed
                Log.d(TAG, "Directory \'"+subDir+"\' exists, IsDir="+attrs.isDir());
            } catch (Exception e) {
                Log.d(TAG, currentDirectory+"/"+subDir+" not found");
                Log.d(TAG, "Creating dir "+subDir);
                channelSftp.mkdir(subDir);
            }

            //upload file using the same folder structure
            String destinationPath = currentDirectory+"/"+subDir+"/"+fileName;
            Log.d(TAG, "dest path: " + destinationPath);
            channelSftp.put(file.getPath(), destinationPath); // src -> dst

            channelSftp.exit();
            session.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        } catch (SftpException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }

    }




}


