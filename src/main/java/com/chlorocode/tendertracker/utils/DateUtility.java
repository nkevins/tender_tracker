package com.chlorocode.tendertracker.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtility {
    public static final Date getCurrentDate() {
        Date date = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        return date;
    }

    public static final Date getCurrentDateTime() {
        return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }

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
