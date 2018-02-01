package com.astpos.membershipapp;

import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.widget.EditText;


/**
 * Created by Iskren Iliev on 1/31/18.
 *
 * Set this TextWatcher to EditText for Phone number formatting.
 *
 * Along with this EditText should have the following values in xml
 * inputType= phone
 * maxLength=12
 */
public class CustomTextWatcher extends PhoneNumberFormattingTextWatcher {

    private EditText editText;

    /**
     * Constructor to format an EditText object to custom phone format ###-###-####
     * @param editText
     */
    public CustomTextWatcher(EditText editText) {
        // TODO Auto-generated constructor stub
        this.editText = editText;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //move cursor to the end of the string
        editText.setSelection(editText.getText().length());
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.i(MainActivity.TAG, "CHAR SEQ: " + s + " start: " + start + " before : " + before +  " Count: " + count);

        //move cursor to the end of the string
        editText.setSelection(editText.getText().length());

        // Unregister self before setText
        editText.removeTextChangedListener(this);

        if(s.length() > 0 ) {//|| (!s.toString().matches( "^((\\d{1,3})?|(\\d+))(\\.\\d{2})?$" ))
            //remove all other characters different from digits
            String userInput= ""+s.toString().replaceAll("[^\\d]", "");
            StringBuilder cashAmountBuilder = new StringBuilder(userInput);

            // add hyphens if string is long enough
            if(cashAmountBuilder.length() > 3){
                cashAmountBuilder.insert(3, '-');
            }
            if(cashAmountBuilder.length() > 6) {
                cashAmountBuilder.insert(6, '-');
            }

            // load the new string to the editText box
            editText.setText(cashAmountBuilder.toString());

            // keeps the cursor always to the right
            Selection.setSelection(editText.getText(), cashAmountBuilder.toString().length());
        }

        // Re-register self after setText
        editText.addTextChangedListener(this);
    }

    @Override
    public void afterTextChanged(Editable s) {
        //move cursor to the end of the string
        editText.setSelection(editText.getText().length());
    }


}