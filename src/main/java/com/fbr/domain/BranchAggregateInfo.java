package com.fbr.domain;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import java.util.List;

public class BranchAggregateInfo {
    int branchId;
    List<AttributeAggregateInfo> listAttributeAggregateInfo;

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public List<AttributeAggregateInfo> getListAttributeAggregateInfo() {
        return listAttributeAggregateInfo;
    }

    public void setListAttributeAggregateInfo(List<AttributeAggregateInfo> listAttributeAggregateInfo) {
        this.listAttributeAggregateInfo = listAttributeAggregateInfo;
    }
}
