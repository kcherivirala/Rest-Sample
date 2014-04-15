package com.fbr.domain;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

public class AttributeTuple {
    int attributeId;
    int maxValue;
    int obtainedValue;
    String responseString;

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }

    public int getObtainedValue() {
        return obtainedValue;
    }

    public void setObtainedValue(int obtainedValue) {
        this.obtainedValue = obtainedValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }
}
