package com.fbr.domain.Graph;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import java.util.List;

public class Graph {
    String graphId;
    String name;
    List<Integer> attributeList;
    List<Integer> filterList;

    public String getGraphId() {
        return graphId;
    }

    public void setGraphId(String graphId) {
        this.graphId = graphId;
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
