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
    String name;
    List<AttributeValue> listAttributeValue;

    //for graphs of type 'filter'
    List<DailyAttributeStatisticValues> listDailyAttributeStatisticValues;
    List<MonthlyAttributeLevelStatisticValues> listMonthlyAttributeLevelStatisticValues;

    //for graphs of type 'normal'
    List<Integer> listCountPPl_7Days;
    List<Integer> listCountPPl_30Days;
    List<Integer> getListCountPPl_365Days;

    public List<Integer> getListCountPPl_7Days() {
        return listCountPPl_7Days;
    }

    public void setListCountPPl_7Days(List<Integer> listCountPPl_7Days) {
        this.listCountPPl_7Days = listCountPPl_7Days;
    }

    public List<Integer> getListCountPPl_30Days() {
        return listCountPPl_30Days;
    }

    public void setListCountPPl_30Days(List<Integer> listCountPPl_30Days) {
        this.listCountPPl_30Days = listCountPPl_30Days;
    }

    public List<Integer> getGetListCountPPl_365Days() {
        return getListCountPPl_365Days;
    }

    public void setGetListCountPPl_365Days(List<Integer> getListCountPPl_365Days) {
        this.getListCountPPl_365Days = getListCountPPl_365Days;
    }

    public List<MonthlyAttributeLevelStatisticValues> getListMonthlyAttributeLevelStatisticValues() {
        return listMonthlyAttributeLevelStatisticValues;
    }

    public void setListMonthlyAttributeLevelStatisticValues(List<MonthlyAttributeLevelStatisticValues> listMonthlyAttributeLevelStatisticValues) {
        this.listMonthlyAttributeLevelStatisticValues = listMonthlyAttributeLevelStatisticValues;
    }

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
