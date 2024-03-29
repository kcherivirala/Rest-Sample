package com.fbr.Dao.Response.Entities;

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
@Table(name = "customer_responses")
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
    @Column(name = "start_timestamp", nullable = false)
    Date startTimestamp;
    @Column(name = "end_timestamp", nullable = false)
    Date endTimestamp;
    @Column(name = "server_timestamp", nullable = false)
    Date serverTimestamp;

    public Date getServerTimestamp() {
        return serverTimestamp;
    }

    public void setServerTimestamp(Date serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }

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

    public Date getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Date endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public Date getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Date startTimestamp) {
        this.startTimestamp = startTimestamp;
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
