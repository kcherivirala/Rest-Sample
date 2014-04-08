package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ResponseAggregateDao;

import java.util.Calendar;
import java.util.TimerTask;

public class UpdateAggregateTask extends TimerTask {
    private ResponseAggregateDao responseAggregateDao;

    public UpdateAggregateTask(ResponseAggregateDao responseAggregateDao) {
        this.responseAggregateDao = responseAggregateDao;
    }

    @Override
    public void run() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, -90);

        int date = FeedbackUtilities.dateFromCal(cal);
        date = 20140308;
        responseAggregateDao.deleteOldEntries(date);
    }
}
