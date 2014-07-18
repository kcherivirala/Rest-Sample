package com.fbr.Dao.Cache;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import java.util.Map;

public class CacheDbEntry {
    int branchId;

    Map<Integer, Integer> mapOfFilters;
    int date;

    int weightedAttributeId;
    int count_1, count_2, count_3, count_4, count_5;

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public Map<Integer, Integer> getMapOfFilters() {
        return mapOfFilters;
    }

    public void setMapOfFilters(Map<Integer, Integer> mapOfFilters) {
        this.mapOfFilters = mapOfFilters;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getWeightedAttributeId() {
        return weightedAttributeId;
    }

    public void setWeightedAttributeId(int weightedAttributeId) {
        this.weightedAttributeId = weightedAttributeId;
    }

    public int getCount_1() {
        return count_1;
    }

    public void setCount_1(int count_1) {
        this.count_1 = count_1;
    }

    public int getCount_2() {
        return count_2;
    }

    public void setCount_2(int count_2) {
        this.count_2 = count_2;
    }

    public int getCount_3() {
        return count_3;
    }

    public void setCount_3(int count_3) {
        this.count_3 = count_3;
    }

    public int getCount_4() {
        return count_4;
    }

    public void setCount_4(int count_4) {
        this.count_4 = count_4;
    }

    public int getCount_5() {
        return count_5;
    }

    public void setCount_5(int count_5) {
        this.count_5 = count_5;
    }
}

