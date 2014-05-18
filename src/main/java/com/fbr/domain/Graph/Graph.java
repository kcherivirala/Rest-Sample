package com.fbr.domain.Graph;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.domain.Attribute.Attribute;

import java.util.List;

public class Graph {
    String graphId;
    String name;
    String type;
    List<Attribute> attributeList;
    List<Attribute> filterList;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public List<Attribute> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<Attribute> attributeList) {
        this.attributeList = attributeList;
    }

    public List<Attribute> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<Attribute> filterList) {
        this.filterList = filterList;
    }
}
