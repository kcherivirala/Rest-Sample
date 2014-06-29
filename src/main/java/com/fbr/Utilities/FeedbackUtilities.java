package com.fbr.Utilities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import java.util.Calendar;
import java.util.Date;

public class FeedbackUtilities {
    public static int dateFromCal(Calendar cal) {
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int date = cal.get(Calendar.DATE);

        return year * 10000 + month * 100 + date;
    }

    public static int dateFromCal(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return dateFromCal(cal);
    }

    public static int nextDate(int date) {
        return addToDate(date, 1);
    }

    public static int addToDate(int date, int number) {
        int day = date % 100;
        int month = (date / 100) % 100 - 1;
        int year = date / 10000;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);

        cal.add(Calendar.DAY_OF_MONTH, number);
        int x = dateFromCal(cal);

        return x;
    }

    public static int addToMonth(int month, int number) {
        int monthVal = month % 100 - 1;
        int year = month / 100;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, monthVal);
        cal.set(Calendar.YEAR, year);

        cal.add(Calendar.MONTH, number);

        return cal.get(Calendar.YEAR) * 100 + cal.get(Calendar.MONTH) + 1;
    }

    public static int nextMonth(int month) {
        return addToMonth(month, 1);
    }

    public static int monthFromDate(int date) {
        return date / 100;
    }

    public static long differenceInDates(int date1, int date2) {
        int day = date1 % 100;
        int month = (date1 / 100) % 100 - 1;
        int year = date1 / 10000;

        Calendar cal1 = Calendar.getInstance();
        cal1.set(Calendar.DAY_OF_MONTH, day);
        cal1.set(Calendar.MONTH, month);
        cal1.set(Calendar.YEAR, year);

        cal1.set(Calendar.HOUR, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);

        day = date2 % 100;
        month = (date2 / 100) % 100 - 1;
        year = date2 / 10000;

        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.DAY_OF_MONTH, day);
        cal2.set(Calendar.MONTH, month);
        cal2.set(Calendar.YEAR, year);

        cal2.set(Calendar.HOUR, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);

        return (cal1.getTimeInMillis() - cal2.getTimeInMillis()) / (24 * 60 * 60 * 1000);
    }
}
