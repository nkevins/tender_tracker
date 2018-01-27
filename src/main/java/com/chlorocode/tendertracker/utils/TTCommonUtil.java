package com.chlorocode.tendertracker.utils;

import com.chlorocode.tendertracker.constants.TTConstants;

/**
 * Utility for common usage.
 */
public class TTCommonUtil {
    /**
     * This method is use to get convert the String of the status from status code.
     *
     * @param statusCode
     * @return String
     */
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

    /**
     * This method is used to create the like query pattern.(for eg- '%searchTerm%'
     *
     * @param searchTerm search free text
     * @return String
     */
    public static String getContainsLikePattern(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return "%";
        }
        else {
            return "%" + searchTerm.toLowerCase() + "%";
        }
    }
}
