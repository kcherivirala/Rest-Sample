package com.fbr.Dao.Graph.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectEntity;

import javax.persistence.*;

@Entity
@Table(name = "graphs")
public class GraphDbType implements ProjectEntity<String> {
    @Column(name = "graph_id", unique = true, nullable = false)
    @Id
    String graphId;
    @Column(name = "company_id", nullable = false)
    int companyId;
    @Column(name = "name", nullable = false)
    String name;

    public String getGraphId() {
        return graphId;
    }

    public void setGraphId(String graphId) {
        this.graphId = graphId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Transient
    public void setId(String s) {
        this.graphId = s;
    }

    @Override
    public String getId() {
        return this.graphId;
    }
}
