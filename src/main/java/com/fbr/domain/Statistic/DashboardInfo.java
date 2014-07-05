package com.fbr.domain.Statistic;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

public class DashboardInfo {
    int nps;
    int countResponsesTotal;
    int countResponsesToday;
    int countResponsesNegativeTotal;
    int countResponsesNegativeToday;

    public int getNps() {
        return nps;
    }

    public void setNps(int nps) {
        this.nps = nps;
    }

    public int getCountResponsesTotal() {
        return countResponsesTotal;
    }

    public void setCountResponsesTotal(int countResponsesTotal) {
        this.countResponsesTotal = countResponsesTotal;
    }

    public int getCountResponsesToday() {
        return countResponsesToday;
    }

    public void setCountResponsesToday(int countResponsesToday) {
        this.countResponsesToday = countResponsesToday;
    }

    public int getCountResponsesNegativeTotal() {
        return countResponsesNegativeTotal;
    }

    public void setCountResponsesNegativeTotal(int countResponsesNegativeTotal) {
        this.countResponsesNegativeTotal = countResponsesNegativeTotal;
    }

    public int getCountResponsesNegativeToday() {
        return countResponsesNegativeToday;
    }

    public void setCountResponsesNegativeToday(int countResponsesNegativeToday) {
        this.countResponsesNegativeToday = countResponsesNegativeToday;
    }
}
