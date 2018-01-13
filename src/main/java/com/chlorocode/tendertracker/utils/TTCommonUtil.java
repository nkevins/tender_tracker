package com.chlorocode.tendertracker.utils;

import com.chlorocode.tendertracker.constants.TTConstants;

public class TTCommonUtil {
    public static String getExternalTenderStatus(int statusCode) {
        switch (statusCode) {
            case 1 : return TTConstants.ET_STATUS_OPEN;
            case 2 : return TTConstants.ET_STATUS_CLOSED;
            case 3 : return TTConstants.ET_STATUS_AWARDED;
            case 4 : return TTConstants.ET_STATUS_NO_AWARD;
            case 5 :
            default: return TTConstants.ET_STATUS_OTHERS;
        }
    }
}
