package com.chlorocode.tendertracker.constants;

import org.springframework.data.domain.Sort;

/**
 * Created by Kyaw Min Thu on 3/7/2017.
 */
public class TTConstants {
    public static final int OTP_VALID_HOURS = 1;
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
    public static final String PARAM_CHANGE_TYPE = "change_type";
    public static final String PARAM_TOKEN = "confirmation_token";
    public static final String PARAM_TENDER_ID = "tender_id";
    public static final String PARAM_TENDER_TITLE = "tender_title";
    public static final String PARAM_COMPANY = "company";
    public static final String PARAM_COMPANY_ID = "company_id";
    public static final String PARAM_COMPANY_NAME = "company_name";
    public static final String PARAM_TENDER = "tender";
    public static final String PARAM_APPROVAL_ACTION = "approval_action";

    public static final String OPEN_DATE = "openDate";
    public static final String CLOSED_DATE = "closedDate";
    public static final String TITLE = "title";
    public static final String ASC = "asc";
    public static final String DESC = "desc";

    public static final String PUBLISHED_DATE = "publishedDate";
    public static final String CLOSING_DATE = "closingDate";

    public static final String DEFAULT_SORT = OPEN_DATE;
    public static final String DEFAULT_SORT_DIRECTION = ASC;

    public static final String LBL_OPEN_DATE = "Open Date";
    public static final String LBL_CLOSED_DATE = "Closed Date";
    public static final String LBL_TITLE = "Title";

    public static final String LBL_ASC = "Ascending";
    public static final String LBL_DESC = "Descending";

    public static int UPDATE_TENDER = 1;
    public static int ADD_CORRIGENDUM = 2;

    // External tender status.
    public static final String ET_STATUS_OPEN = "OPEN";
    public static final String ET_STATUS_CLOSED = "CLOSED";
    public static final String ET_STATUS_AWARDED = "AWARDED";
    public static final String ET_STATUS_NO_AWARD = "NO_AWARD";
    public static final String ET_STATUS_OTHERS = "OTHERS";
}
