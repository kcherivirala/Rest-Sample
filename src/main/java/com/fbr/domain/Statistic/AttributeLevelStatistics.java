package com.fbr.domain.Statistic;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */



import com.fbr.domain.Attribute.AttributeValue;

import java.util.List;

public class AttributeLevelStatistics {
    int attributeId;
    List<AttributeValue> listAttributeValue;
    List<DailyAttributeStatisticValues> listDailyAttributeStatisticValues;

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }

    public List<AttributeValue> getListAttributeValue() {
        return listAttributeValue;
    }

    public void setListAttributeValue(List<AttributeValue> listAttributeValue) {
        this.listAttributeValue = listAttributeValue;
    }

    public List<DailyAttributeStatisticValues> getListDailyAttributeStatisticValues() {
        return listDailyAttributeStatisticValues;
    }

    public void setListDailyAttributeStatisticValues(List<DailyAttributeStatisticValues> listDailyAttributeStatisticValues) {
        this.listDailyAttributeStatisticValues = listDailyAttributeStatisticValues;
    }
}
