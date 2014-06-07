package com.fbr.domain.Statistic;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import java.util.List;

public class MonthlyAttributeLevelStatisticValues {
    int month;
    List<Integer> listCountPPL;

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public List<Integer> getListCountPPL() {
        return listCountPPL;
    }

    public void setListCountPPL(List<Integer> listCountPPL) {
        this.listCountPPL = listCountPPL;
    }
}
