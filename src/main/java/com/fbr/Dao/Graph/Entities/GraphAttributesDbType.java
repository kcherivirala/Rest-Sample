package com.fbr.Dao.Graph.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectEntity;

import javax.persistence.*;

@Entity
@Table(name = "graph_attributes")
public class GraphAttributesDbType implements ProjectEntity<GraphAttributesPrimaryKey> {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "graphId", column = @Column(name = "graph_id", nullable = false)),
            @AttributeOverride(name = "attributeId", column = @Column(name = "attribute_id", nullable = false))})
    GraphAttributesPrimaryKey id;

    @Override
    public GraphAttributesPrimaryKey getId() {
        return id;
    }

    @Override
    public void setId(GraphAttributesPrimaryKey id) {
        this.id = id;
    }
}
