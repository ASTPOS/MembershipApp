package com.astpos.membershipapp.util;

/**
 * Created by Iskren Iliev on 11/20/17.
 */

public class Constants {
    //tags
    public static final String TAG = "ASTPOS";

    // Socket variables
    public static final int TIMEOUT_INTERVAL = 10000;

    //error message tags and labels
    public static final String APPROVED_MSG = "error_msg";
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR_TYPE = "error_type";
    public static final int TRANSFER_SUCCESS = 100;
    public static final int TIMEOUT_ERR    = 101;
    public static final int WRONG_PHONE   = 102;
    public static final int INCOMPLETE_ERR = 103;
    public static final int CONNECTION_ERR = 105;
    public static final int COLLECT_EMAIL  = 106;


    // labels for response map
    public static final String AUTH_CODE = "AUTH_CODE";
    public static final String AUTH_CODE_PAX = "authCode";
    public static final String AUTH_RESP = "AUTH_RESP";
    public static final String ENTRY_MOD_NAB = "CARD_ENT_METH";
    public static final String ENTRY_MOD_PAX = "entryMode";
    public static final String AUTH_RESP_TEXT = "AUTH_RESP_TEXT";
    public static final String RESP_MSG = "responseMessage";
    public static final String RESP_CODE = "responseCode";
    public static final String HOST_RESP_CODE = "hostResponseCode";
    public static final String HOST_RESP_MSG = "hostResponseMesg";
    public static final String AUTH_ACCOUNT_NBR = "AUTH_MASKED_ACCOUNT_NBR";
    public static final String AUTH_ACCOUNT_PAX = "account";


    // labels for preferences constants
    public static final String BUSINESS_NAME = "business_name";
    public static final String SERVER_IP = "server_ip";
    public static final String SERVER_PASS = "server_pass";
    public static final String TRANSACTION_NAME = "transaction_name";
    public static final String TRANSACTION_ID = "transaction_id";
    public static final String USER_EMAIL_ID = "user_email_id";
    public static final String USER_EMAIL_PASS = "user_email_pass";
    public static final String USER_SIGN_PATH = "user_signature_PATH";


    //extras for PaxPinpadActivity
    public static final String TRANS_AMOUNT = "trans_amount";
    public static final String TIP_AMOUNT = "tip_amount";
    public static final String TOTAL_AMOUNT = "total_amount";
    public static final String FROM_SIGN = "from_sign";
    public static final String IMAGE_PATH = "image_path";
    public static final String PROCESSOR_TYPE = "processor_type";
    public static final String NAB = "nab";
    public static final String PAX = "pax";



    // constants used with transaction type menu in activity_paxmain.xml
    public static final int SALE    = 0;
    public static final int AUTH    = 1;
    public static final int ADJ_TIP = 2;
    public static final int ADJ_REF = 3;
    public static final int CAPTURE = 4;
    public static final int VOID    = 5;
    public static final int RETURN  = 6;
    public static final int CLOSE_BATCH = 7;
    public static final int HISTORY_REPORT = 8;
    public static final int INIT = 9;
    public static final int REBOOT = 10;

}
