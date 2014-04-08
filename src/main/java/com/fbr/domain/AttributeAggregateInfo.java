package com.fbr.domain;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import java.util.List;

public class AttributeAggregateInfo {
    String attributeId;
    List<Integer> dates;
    List<Integer> obtainedValues;
    List<Integer> totalValues;

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public List<Integer> getDates() {
        return dates;
    }

    public void setDates(List<Integer> dates) {
        this.dates = dates;
    }

    public List<Integer> getObtainedValues() {
        return obtainedValues;
    }

    public void setObtainedValues(List<Integer> obtainedValues) {
        this.obtainedValues = obtainedValues;
    }

    public List<Integer> getTotalValues() {
        return totalValues;
    }

    public void setTotalValues(List<Integer> totalValues) {
        this.totalValues = totalValues;
    }
}
