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
        /*
        boolean flag = false;
        int day = date % 100;
        date = date / 100;
        int month = date % 100;
        date = date / 100;
        int year = date;

        day++;
        if (day == 30 && month == 2 && year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) {
            //leap year
            flag = true;
        } else if (day == 29 && month == 2 && year % 4 != 0) {
            //not a leap year
            flag = true;
        } else if (day == 31 && (month == 4 || month == 6 || month == 9 || month == 11)) {
            flag = true;
        } else if (day == 32) {
            flag = true;
        }

        if (flag == true) {
            day = 1;
            month++;
            if (month == 13) {
                month = 1;
                year++;
            }
        }
        return year * 10000 + month * 100 + day;
        */
        return addToDate(date, 1);
    }

    public static int addToDate(int date, int number){
        int day = date %100;
        int month = (date/100)%100;
        int year = date/10000;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, day);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);

        cal.add(Calendar.DATE, number);
        return dateFromCal(cal);
    }

    public static int addToMonth(int month, int number){
        int monthVal = month %100;
        int year = month /100;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, monthVal);
        cal.set(Calendar.YEAR, year);

        cal.add(Calendar.MONTH, number);

        return cal.get(Calendar.YEAR) * 100 + cal.get(Calendar.MONTH);
    }

    public static int nextMonth(int month){
        return addToMonth(month, 1);
    }

    public static int monthFromDate(int date){
        return date % 10000;
    }
}
