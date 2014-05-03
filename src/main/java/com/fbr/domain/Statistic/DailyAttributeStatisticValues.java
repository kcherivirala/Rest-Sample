package com.fbr.domain.Statistic;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import java.util.List;

public class DailyAttributeStatisticValues {
    int date;
    List<Integer> listCountPPL;

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public List<Integer> getListCountPPL() {
        return listCountPPL;
    }

    public void setListCountPPL(List<Integer> listCountPPL) {
        this.listCountPPL = listCountPPL;
    }
}
