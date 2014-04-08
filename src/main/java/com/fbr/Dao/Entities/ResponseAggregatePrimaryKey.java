package com.fbr.Dao.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ResponseAggregatePrimaryKey implements Serializable{
    @Column(name = "attribute_id", nullable = false)
    String attributeId;
    @Column(name = "company_id", nullable = false)
    String companyId;
    @Column(name = "branch_id", nullable = false)
    int branchId;
    @Column(name = "date", nullable = false)
    int date;

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }
}
