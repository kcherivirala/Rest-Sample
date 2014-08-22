package com.fbr.Dao.Graph.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    @Column(name = "type", nullable = false)
    String type;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "graph_id", referencedColumnName = "graph_id")
    List<GraphAttributesDbType> graphAttributes = new ArrayList<GraphAttributesDbType>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "graph_id", referencedColumnName = "graph_id")
    List<GraphFiltersDbType> graphFilters = new ArrayList<GraphFiltersDbType>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public List<GraphAttributesDbType> getGraphAttributes() {
        return graphAttributes;
    }

    public void setGraphAttributes(List<GraphAttributesDbType> graphAttributes) {
        this.graphAttributes = graphAttributes;
    }

    public List<GraphFiltersDbType> getGraphFilters() {
        return graphFilters;
    }

    public void setGraphFilters(List<GraphFiltersDbType> graphFilters) {
        this.graphFilters = graphFilters;
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
