package com.astpos.membershipapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

import com.astpos.membershipapp.util.Constants;

import static com.astpos.membershipapp.util.Constants.TAG;

/**
 * Created by Iskren Iliev on 10/14/2016.
 */
public class AstDialogFragment extends DialogFragment {

    private int printOption = 0;
    private EditText editEmail;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;

        int errorType = getArguments().getInt(Constants.ERROR_TYPE);
        String errorMsg = getArguments().getString(Constants.ERROR_MSG);

        Log.d(TAG, "Error Type: " + errorType + " Error Msg: "+ errorMsg);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle(getString(R.string.app_name)+ " Ver: " + versionName + "." + versionCode)
                .setIcon(R.mipmap.ic_launcher)
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // OK to close
                    }
                })
        ;

        if(errorMsg == null) {
            Log.d(TAG, "Error Type: " + errorType + " Error Msg is null: "+ errorMsg);

            errorMsg = "";
        }

        if(errorType == Constants.TRANSFER_SUCCESS) {
            Log.d(TAG, "Error Type: " + errorType + " Error Msg is Success: " + errorMsg);

            builder.setTitle(getString(R.string.status) + ": " + errorMsg);
//            final CharSequence[] receiptOptions = {"Receipt via Print", "Receipt via Email", "Receipt via Both", "No Receipt"};
//            builder.setSingleChoiceItems(receiptOptions, 0, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int item) {
//                    switch (item) {
//                        case 0:
//                            setPrintOption(0);
//                            Log.d(Constants.TAG, "Item 0 clicked / print");
//                            break;
//                        case 1:
//                            setPrintOption(1);
//                            Log.d(Constants.TAG, "Item 1 clicked / email");
//                            break;
//                        case 2:
//                            setPrintOption(2);
//                            Log.d(Constants.TAG, "Item 2 clicked / both");
//                            break;
//                        case 3:
//                            setPrintOption(3);
//                            Log.d(Constants.TAG, "Item 3 clicked / none");
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            });

            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //to close parent activity
//                    if(getPrintOption() == 0){
//                        //TODO: implement Print Receipt feature
//                        ((PinpadActivity) getActivity()).closeActivityWithDelay(Constants.TIMEOUT_INTERVAL);
//                    } else if(getPrintOption() == 1){
//                        //TODO: implement Email Receipt feature
//                        prepEmail();
//                    } else if(getPrintOption() == 2) {
//                        //TODO: implement Print and Email Receipt feature
//                        prepEmail();
//                        //prepPrint()
//                    } else if (getPrintOption() == 3) {
//                        //TODO: implement No Receipt feature
//                        ((PinpadActivity) getActivity()).closeActivityWithDelay(Constants.TIMEOUT_INTERVAL);
//                    }

                }
            });
        }

//        if(errorType == Constants.COLLECT_EMAIL) {
//            editEmail = new EditText(getActivity());
//            editEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
//
//            builder.setMessage(errorMsg);
//            builder.setView(editEmail);
//            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    String email = editEmail.getText().toString();
//                    ((PinpadActivity)getActivity()).setEmailAddress(email);
//                    ((PinpadActivity) getActivity()).closeActivityWithDelay(Constants.TIMEOUT_INTERVAL);
//                }
//            });
//        }


        if(errorType == Constants.TIMEOUT_ERR) {
            builder.setMessage(getString(R.string.time_out_prompt) + ": \n" + errorMsg);
        }


        if(errorType == Constants.CONNECTION_ERR) {
            errorMsg = getString(R.string.time_out_prompt);
            builder.setMessage(getString(R.string.connection_err) + ": \n" + errorMsg);
//            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    // OK to close parent activity
//                    ((MainActivity)getActivity()).closeActivity();
//                }
//            });
        }

        if(errorType == Constants.WRONG_PHONE) {
            builder.setMessage("Wrong phone: " + errorMsg +
                    "\n"+ getString(R.string.reenter_phone) );
        }


        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        // Create the AlertDialog object and return it
        return dialog;
    }


    private void setPrintOption(int option){
        this.printOption = option;
    }
//
//    private int getPrintOption(){
//        return printOption;
//    }
//
//    private void prepEmail() {
//        ((PinpadActivity)getActivity()).getEmailAddress();
//    }


//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//
//        try {
//            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
//            childFragmentManager.setAccessible(true);
//            childFragmentManager.set(this, null);
//
//        } catch (NoSuchFieldException e) {
//            throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }

}