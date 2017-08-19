package com.chlorocode.tendertracker.constants;

/**
 * Created by Kyaw Min Thu on 3/7/2017.
 */
public class TTConstants {
    public static final int OTP_VALID_DAYS = 1;
    public static final int OTP_LENGTH = 6;
    public static String OTP_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    public static final String PASSWORD_COMPLEXITY = "^[\\S]{6,}$";
    public static final String EMPTY = "";
    public static final String APPROVED = "approved";
    public static final String REJECTED = "rejected";

    public static final int ACCOUNT_STATIC_ACTIVE = 1;
    public static final int ACCOUNT_STATIC_EXPIRE = 2;
    public static final int ACCOUNT_STATIC_LOCK = 3;

    public static final int MAX_ATTEMPT = 3;

    public static final int BUTTONS_TO_SHOW = 5;
    public static final int INITIAL_PAGE = 0;
    public static final int INITIAL_PAGE_SIZE = 5;
//    public static final int[] PAGE_SIZES = { 5, 10, 20 };
    public static final String PARAM_NAME = "name";
    public static final String PARAM_EMAIL = "email";
    public static final String PARAM_EMAILS = "emails";
    public static final String PARAM_TOKEN = "confirmation_token";
    public static final String PARAM_TENDER_ID = "tender_id";
    public static final String PARAM_TENDER_TITLE = "tender_title";
    public static final String PARAM_COMPANY_ID = "company_id";
    public static final String PARAM_COMPANY_NAME = "company_name";
    public static final String PARAM_TENDER = "tender";
    public static final String PARAM_APPROVAL_ACTION = "approval_action";
}