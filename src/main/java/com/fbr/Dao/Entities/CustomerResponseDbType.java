package com.fbr.Dao.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "customer_response")
public class CustomerResponseDbType implements ProjectEntity<String> {

    @Column(name = "response_id", nullable = false)
    @Id
    String responseId;
    @Column(name = "customer_id", nullable = false)
    String customerId;
    @Column(name = "company_id", nullable = false)
    int companyId;
    @Column(name = "branch_id", nullable = false)
    int branchId;
    @Column(name = "timestamp", nullable = false)
    Date timestamp;

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String getId() {
        return responseId;
    }

    @Override
    public void setId(String s) {
        this.responseId = s;
    }
}
