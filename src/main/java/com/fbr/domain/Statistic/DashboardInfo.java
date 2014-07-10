package com.fbr.domain.Statistic;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

public class DashboardInfo {
    int npsPositive;
    int npsNegative;
    int npsPassive;


    int countResponsesTotal;
    int countResponsesNegativeTotal;
    int countResponsesPositiveTotal;

    int countResponsesToday;
    int countResponsesNegativeToday;
    int countResponsesPositiveToday;

    double avgRating;

    public int getCountResponsesPositiveTotal() {
        return countResponsesPositiveTotal;
    }

    public void setCountResponsesPositiveTotal(int countResponsesPositiveTotal) {
        this.countResponsesPositiveTotal = countResponsesPositiveTotal;
    }

    public int getCountResponsesPositiveToday() {
        return countResponsesPositiveToday;
    }

    public void setCountResponsesPositiveToday(int countResponsesPositiveToday) {
        this.countResponsesPositiveToday = countResponsesPositiveToday;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public int getNpsPositive() {
        return npsPositive;
    }

    public void setNpsPositive(int npsPositive) {
        this.npsPositive = npsPositive;
    }

    public int getNpsNegative() {
        return npsNegative;
    }

    public void setNpsNegative(int npsNegative) {
        this.npsNegative = npsNegative;
    }

    public int getNpsPassive() {
        return npsPassive;
    }

    public void setNpsPassive(int npsPassive) {
        this.npsPassive = npsPassive;
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
