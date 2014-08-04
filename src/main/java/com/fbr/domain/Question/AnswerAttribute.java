package com.fbr.domain.Question;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

public class AnswerAttribute {
    int attributeId;
    int maxValue;
    int attainedValue;


    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getAttainedValue() {
        return attainedValue;
    }

    public void setAttainedValue(int attainedValue) {
        this.attainedValue = attainedValue;
    }
}
