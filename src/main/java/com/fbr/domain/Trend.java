package com.fbr.domain;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import java.util.List;

public class Trend {
    String name;
    String trendId;
    List<Integer> attributeList;
    List<Integer> filterList;

    public String getTrendId() {
        return trendId;
    }

    public void setTrendId(String trendId) {
        this.trendId = trendId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<Integer> attributeList) {
        this.attributeList = attributeList;
    }

    public List<Integer> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<Integer> filterList) {
        this.filterList = filterList;
    }
}
