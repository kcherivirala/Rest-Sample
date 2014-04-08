package com.fbr.Dao.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectEntity;

import javax.persistence.*;

@Entity
@Table(name = "customer_response")
public class CustomerResponseDbType implements ProjectEntity<CustomerResponsePrimaryKey>{
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "customerId", column = @Column(name = "customer_id", nullable = false)),
            @AttributeOverride(name = "attributeId", column = @Column(name = "attribute_id", nullable = false)),
            @AttributeOverride(name = "companyId", column = @Column(name = "company_id", nullable = false)),
            @AttributeOverride(name = "branchId", column = @Column(name = "branch_id", nullable = false)),
            @AttributeOverride(name = "timestamp", column = @Column(name = "timestamp", nullable = false))
    })
    CustomerResponsePrimaryKey id;
    @Column(name = "max_value")
    int maxValue;
    @Column(name = "obtained_value")
    int obtainedValue;
    @Column(name = "response")
    String response;

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getObtainedValue() {
        return obtainedValue;
    }

    public void setObtainedValue(int obtainedValue) {
        this.obtainedValue = obtainedValue;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    @Transient
    public CustomerResponsePrimaryKey getId() {
        return this.id;
    }

    @Override
    public void setId(CustomerResponsePrimaryKey customerResponsePrimaryKey) {
        this.id = customerResponsePrimaryKey;
    }
}
