package com.fbr.Dao.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectEntity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "customer_response_values")
public class CustomerResponseValuesDbType implements Serializable, ProjectEntity<CustomerResponseValuesPrimaryKey> {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "responseId", column = @Column(name = "response_id", nullable = false)),
            @AttributeOverride(name = "attributeId", column = @Column(name = "attribute_id", nullable = false))
    })
    CustomerResponseValuesPrimaryKey id;

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
    public CustomerResponseValuesPrimaryKey getId() {
        return this.id;
    }

    @Override
    public void setId(CustomerResponseValuesPrimaryKey customerResponsePrimaryKey) {
        this.id = customerResponsePrimaryKey;
    }
}