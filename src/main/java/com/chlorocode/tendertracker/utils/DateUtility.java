package com.chlorocode.tendertracker.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Utility class for Date.
 */
public class DateUtility {
    /**
     * This method is used to get current date with 00 hour 00 minute 00 second.
     * @return Date
     */
    public static final Date getCurrentDate() {
        Date date = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        return date;
    }

    /**
     * This method is used to get current date.
     * @return Date
     */
    public static final Date getCurrentDateTime() {
        return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * This method used to get result date after add or subtract the params(years or days or hour or minutes)
     *
     * @param years desired +/-years
     * @param days desired +/-days
     * @param hours desired +/-hours
     * @param minutes desired +/-minutes
     * @return Date
     */
    public static final Date getFutureDateTime(int years, int days, int hours, int minutes) {
        LocalDateTime tokenExpirationDate = LocalDateTime.now();
        if (years > 0) {
            tokenExpirationDate = tokenExpirationDate.plusYears(years);
        }
        if (days > 0) {
            tokenExpirationDate = tokenExpirationDate.plusDays(days);
        }
        if (hours > 0) {
            tokenExpirationDate = tokenExpirationDate.plusHours(hours);
        }
        if (minutes > 0) {
            tokenExpirationDate = tokenExpirationDate.plusMinutes(minutes);
        }
        return Date.from(tokenExpirationDate.atZone(ZoneId.systemDefault()).toInstant());
    }
}
