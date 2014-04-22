package com.fbr.Dao.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectEntity;

import javax.persistence.*;

@Entity
@Table(name = "trend_filters")
public class TrendFiltersDbType implements ProjectEntity<TrendFiltersPrimaryKey> {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "trendId", column = @Column(name = "trend_id", nullable = false)),
            @AttributeOverride(name = "attributeId", column = @Column(name = "attribute_id", nullable = false))})
    TrendFiltersPrimaryKey id;

    @Override
    public TrendFiltersPrimaryKey getId() {
        return id;
    }

    @Override
    public void setId(TrendFiltersPrimaryKey id) {
        this.id = id;
    }
}
