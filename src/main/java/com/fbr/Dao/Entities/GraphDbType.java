package com.fbr.Dao.Entities;

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