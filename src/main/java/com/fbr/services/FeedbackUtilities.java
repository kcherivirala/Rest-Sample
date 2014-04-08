package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import java.util.Calendar;

public class FeedbackUtilities {
    public static int dateFromCal(Calendar cal) {
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int date =  cal.get(Calendar.DATE);

        return year*10000 + month * 100 + date;
    }
}
