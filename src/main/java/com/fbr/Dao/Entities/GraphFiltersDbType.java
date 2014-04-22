package com.fbr.Dao.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectEntity;

import javax.persistence.*;

@Entity
@Table(name = "graph_filters")
public class GraphFiltersDbType implements ProjectEntity<GraphFiltersPrimaryKey> {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "graphId", column = @Column(name = "graph_id", nullable = false)),
            @AttributeOverride(name = "attributeId", column = @Column(name = "attribute_id", nullable = false))})
    GraphFiltersPrimaryKey id;

    @Override
    public GraphFiltersPrimaryKey getId() {
        return id;
    }

    @Override
    public void setId(GraphFiltersPrimaryKey id) {
        this.id = id;
    }
}
