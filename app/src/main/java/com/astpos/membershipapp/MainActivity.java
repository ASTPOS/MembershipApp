package com.astpos.membershipapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.astpos.membershipapp.util.Constants;


public class MainActivity extends AppCompatActivity {

    //tags and flags
    private static final String TAG = Constants.TAG;
    private static boolean pictureButtonClicked = false;
    private static boolean signatureButtonClicked = false;
    private int ERROR_TYPE;
    private String ERROR_MSG;

    // pop up message
    private AstDialogFragment dialogFragment;

    //users variables
    private String userName;
    private EditText phoneEditTex;
    private ConstraintLayout mainLayout;
    private RelativeLayout mainRelativeLayout;
    private ScrollView mainScrollLayout;
    private LinearLayout mainLinearLayout;
    private Button pictureBtn, settingsBtn;
    private ImageView pictureView, signatureView;

    // remote server
    private List<String> ipAddressList = new ArrayList<String>();
    private static String[] fileNamesList = {"user_pic.jpg", "user_sig.png"};
    private static String serverUser = "astpos";
    private static String serverPass = "Amb88er275!!";
    private static final String serverStorageLocation = "/usr/local/akashi/bin/data";

    // Shared Preferences
    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEditor;

    private File mainPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init all
        setContentView(R.layout.activity_main);

        this.preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.preferencesEditor = this.preferences.edit();

        //initialize fragment for any popup msgs that may appear
        dialogFragment = new AstDialogFragment();

//        updatePreferences();

        mainPath = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        Log.d(TAG, "main path: " + mainPath);

        // set listener so KB hides when click anywhere else but EditText
        mainLinearLayout = (LinearLayout) findViewById(R.id.mainLinearLayout);
        mainLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
            }
        });

        // set TextWatcher to format phone number text
        phoneEditTex = (EditText) findViewById(R.id.editTextPhone);
        CustomTextWatcher phoneTextWatcher = new CustomTextWatcher(phoneEditTex);
//        phoneEditTex.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        phoneEditTex.addTextChangedListener(phoneTextWatcher);
        phoneEditTex.clearFocus();

        // picture button
//        settingsBtn = (Button) findViewById(R.id.buttonSettings);
//        pictureBtn = (Button) findViewById(R.id.buttonPic);
        pictureView = (ImageView) findViewById(R.id.imageViewPic);
        signatureView = (ImageView) findViewById(R.id.imageViewSign);


        updateImage();
        hideKeyboard(getCurrentFocus());
    }


    private void updatePreferences() {
        ipAddressList.clear();
        //        ipAddressList.add("192.168.1.238");
        ipAddressList.add(preferences.getString(Constants.SERVER_IP, ""));
        serverPass = preferences.getString(Constants.SERVER_PASS, "");

        Log.d(TAG, "IP ARR Size: " + ipAddressList.size());
    }


    private void updateImage() {
        Bitmap bitmap;

        //set picture and signature views
        if(!pictureButtonClicked && !signatureButtonClicked) {
            signatureView.setVisibility(View.GONE);
            pictureView.setVisibility(View.GONE);
        } else {
            signatureView.setVisibility(View.INVISIBLE);
            pictureView.setVisibility(View.INVISIBLE);
        }

        if(pictureButtonClicked) {
            Log.d(TAG, "Update picture");
            File picture = new File(mainPath+"/user_pic.jpg");

            if(picture.exists()) {
                bitmap = BitmapFactory.decodeFile(picture.getAbsolutePath());
                pictureView.setImageBitmap(bitmap);
                pictureView.setVisibility(View.VISIBLE);
//                pictureBtn.setBackground(new BitmapDrawable(getResources(), myBitmap));
            }
        }

        if(signatureButtonClicked) {
            Log.d(TAG, "Update signature");
            File signature = new File(mainPath + "/user_sig.png");

            if(signature.exists()) {
                bitmap = BitmapFactory.decodeFile(signature.getAbsolutePath());
                signatureView.setImageBitmap(bitmap);
                signatureView.setVisibility(View.VISIBLE);
            }
        }

        //set text field for phone
        if(signatureButtonClicked && pictureButtonClicked) {
            Log.d(TAG, "set focusable the phone!!!");
            phoneEditTex.setFocusableInTouchMode(true);
            phoneEditTex.setFocusable(true);
            pictureButtonClicked = false;
            signatureButtonClicked = false;
        } else {
            phoneEditTex.setText("");
            hideKeyboard(getCurrentFocus());
            phoneEditTex.setFocusableInTouchMode(false);
            phoneEditTex.setFocusable(false);

        }
    }


    /* ************************** TOOLBAR MENU START  ******************************* */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_settings:
                View view = getWindow().getCurrentFocus();
                useSettingsButton(view);
                return true;
            case R.id.action_refresh:
                pictureButtonClicked = false;
                signatureButtonClicked = false;
                phoneEditTex.setText("");
                updateImage();
                return true;
            case R.id.action_exit:
                closeActivity();
                return true;
//            case R.id.help:
//                _aboutWindow.show(getFragmentManager(), "AST INFO");
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /* ************************** TOOLBAR MENU END  ******************************* */


    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "MainActivity  resumed");
        updateImage();
        hideKeyboard(null);
        updatePreferences();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.d(TAG, "MainActivity  stopped");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Log.d(TAG, "MainActivity  stopped");
    }


    public void useSignButton(View view) {
        Log.i(TAG, "Sign button clicked");

        Intent i;
        i = new Intent(this, SignatureActivity.class);
        startActivity(i);

        signatureButtonClicked = true;
    }

    public void usePicButton(View view) {
        Log.i(TAG, "Pic button clicked");

        Intent i;
        i = new Intent(this, PhotoIntentActivity.class);
        startActivity(i);

        pictureButtonClicked = true;
    }

    public void useSettingsButton(View view) {
        Log.i(TAG, "Settings button clicked");

        Intent i;
        i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    public void useSubmitButton(View view) {
        Log.i(TAG, "Transfer button clicked");

        userName = phoneEditTex.getText().toString();
        if(userName.length() != 12){
            ERROR_TYPE = Constants.WRONG_PHONE;
            ERROR_MSG = userName;
            createDialog(ERROR_MSG, ERROR_TYPE, "wrongPhoneMsg");
            return;
        }

        new Thread(new Runnable() {
            public void run() {
            TransferAsyncTask transferTask;

            for(String ipAddress : ipAddressList) {
//                Log.d(TAG, "Start transferring to IP: " + ipAddress);
                for(String name : fileNamesList) {
//                    //using async
//                    transferTask = new TransferAsyncTask();
//                    transferTask.execute(ipAddress, name);

                    //using runnable
                    String fromPath = mainPath+"/"+name;
                    String toPath = serverStorageLocation;
                    try {
                        transferFile(ipAddress, fromPath, toPath, name);
                        ERROR_MSG = getString(R.string.updated);
                    } catch (JSchException e) {
                        ERROR_TYPE = Constants.CONNECTION_ERR;
                        ERROR_MSG = e.getMessage().toString();
                        e.printStackTrace();
                        Log.e(TAG, "JSch: "+e.toString());
                        break;
                    } catch (SftpException e) {
                        ERROR_TYPE = Constants.TIMEOUT_ERR;
                        e.printStackTrace();
                        Log.e(TAG, "Sftp: "+e.toString());
                        break;
                    }

                    // //////delete a file if exists //////
//                    File file = new File(fromPath);
//                    if(file.exists()) {
//                        Log.d(TAG, "file: " + file.getPath() + " exists");
//                        if(file.delete()) {
//                            Log.d(TAG, "file: " + file.getName() + " deleted");
//                        }
//                    }
                }
            }
            createDialog(ERROR_MSG, ERROR_TYPE, "successfulTransferMsg");

            }
        }).start();


        updateImage();
    }



    /**
     * Builds a dialog fragment and displays it in the current activity
     * @param errorMessage to be displayed in the dialog
     * @param errorType chosen from Constants triggers different dialog
     * @param errorId standard required char ID for the Dialog
     */
    public void createDialog(String errorMessage, int errorType, String errorId){
        Bundle args = new Bundle();
        args.putString(Constants.ERROR_MSG, errorMessage);
        args.putInt(Constants.ERROR_TYPE, errorType);
        if((dialogFragment.getDialog() != null) && (dialogFragment.getDialog().isShowing())) {
            dialogFragment.dismiss();
            dialogFragment = new AstDialogFragment();
        }
        dialogFragment.setArguments(args);
        dialogFragment.setCancelable(false);
        dialogFragment.show(getFragmentManager(), errorId);
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
            Log.d(TAG, "VIEW IS NULL");
            view = new View(this);
        }

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /**
     * closes current activity
     */
    public void closeActivity(){
        finish();
    }


//do not need it
    private class TransferAsyncTask extends AsyncTask<String, Void, String>{

        private String errorMessage;
        private int errorType;


        @Override
        protected String doInBackground(String... params) {
            String ipAddress = params[0];
            String name = params[1];
            String fromPath = mainPath+"/"+name;
            String toPath = serverStorageLocation;
            try {
                transferFile(ipAddress, fromPath, toPath, name);
                errorMessage = getString(R.string.updated);

            } catch (JSchException e) {
                ERROR_TYPE = Constants.CONNECTION_ERR;
                errorMessage = e.toString();
                e.printStackTrace();
                Log.e(TAG, e.toString());
            } catch (SftpException e) {
                ERROR_TYPE = Constants.TIMEOUT_ERR;
                errorMessage = e.toString();
                e.printStackTrace();
                Log.e(TAG, e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String string){
            createDialog(errorMessage, ERROR_TYPE, "transferErrorMsg");

        }
    }


    /**
     * Transfers a specified file to a specific location on requested IP address
     * @param ipAddress IP address where to send file
     * @param from String path of location to copy on local host
     * @param to String path of location to store on remote host
     * @param fileName name of file to copy
     */
    private void transferFile(String ipAddress, String from, String to, String fileName)
            throws JSchException, SftpException {
        // find file
        File file = new File(from);

        Log.d(TAG, " ========= TRANSFER LOG ============");
        Log.d(TAG, "ip address    : " + ipAddress);
        Log.d(TAG, "file name     : " + file.getName());
        Log.d(TAG, "file local path   : " + file.getPath());

        JSch jsch = new JSch();
        Session session;
//        try {
            session = jsch.getSession(serverUser, ipAddress, 22);
            session.setTimeout(5000);

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

            ERROR_TYPE = Constants.TRANSFER_SUCCESS;
//        }

//        catch (JSchException e) {
//            ERROR_TYPE = Constants.CONNECTION_ERR;
//            e.printStackTrace();
//            Log.e(TAG, e.toString());
//        } catch (SftpException e) {
//            ERROR_TYPE = Constants.TIMEOUT_ERR;
//            e.printStackTrace();
//            Log.e(TAG, e.toString());
//        }
    }




}


