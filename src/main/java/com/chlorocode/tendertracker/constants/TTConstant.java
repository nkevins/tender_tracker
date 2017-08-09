package com.chlorocode.tendertracker.constants;

/**
 * Created by Kyaw Min Thu on 3/7/2017.
 */
public class TTConstant {
    public static final int OTP_VALID_DAYS = 1;
    public static final int OTP_LENGTH = 6;
    public static String OTP_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    public static final String PASSWORD_COMPLEXITY = "^[\\S]{6,}$";
    public static final String EMPTY = "";

    public static final int ACCOUNT_STATIC_ACTIVE = 1;
    public static final int ACCOUNT_STATIC_EXPIRE = 2;
    public static final int ACCOUNT_STATIC_LOCK = 3;

    public static final int MAX_ATTEMPT = 3;
}
