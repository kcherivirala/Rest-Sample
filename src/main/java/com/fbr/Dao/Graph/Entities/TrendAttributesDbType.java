package com.fbr.Dao.Graph.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectEntity;

import javax.persistence.*;

@Entity
@Table(name = "trend_attributes")
public class TrendAttributesDbType implements ProjectEntity<TrendAttributesPrimaryKey> {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "trendId", column = @Column(name = "trend_id", nullable = false)),
            @AttributeOverride(name = "attributeId", column = @Column(name = "attribute_id", nullable = false))})
    TrendAttributesPrimaryKey id;

    @Override
    public TrendAttributesPrimaryKey getId() {
        return id;
    }

    @Override
    public void setId(TrendAttributesPrimaryKey id) {
        this.id = id;
    }
}