package com.fbr.Dao;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import javax.persistence.*;

@Entity
@Table(name = "response_aggregate")
public class ResponseAggregateDbType implements ProjectEntity<ResponseAggregatePrimaryKey> {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "companyId", column = @Column(name = "company_id", nullable = false)),
            @AttributeOverride(name = "branchId", column = @Column(name = "branch_id", nullable = false)),
            @AttributeOverride(name = "date", column = @Column(name = "date", nullable = false)),
            @AttributeOverride(name = "attributeId", column = @Column(name = "attribute_id", nullable = false))
    })
    ResponseAggregatePrimaryKey id;

    @Column(name = "obtained_value")
    int obtainedValue;
    @Column(name = "total_value")
    int totalValue;

    public int getObtainedValue() {
        return obtainedValue;
    }

    public void setObtainedValue(int obtainedValue) {
        this.obtainedValue = obtainedValue;
    }

    public int getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(int totalValue) {
        this.totalValue = totalValue;
    }

    @Override
    @Transient
    public ResponseAggregatePrimaryKey getId() {
        return this.id;
    }

    @Override
    public void setId(ResponseAggregatePrimaryKey key) {
        this.id = key;
    }
}
