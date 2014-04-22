package com.fbr.Dao.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectEntity;

import javax.persistence.*;

@Entity
@Table(name = "trends")
public class TrendDbType implements ProjectEntity<String> {
    @Column(name = "trend_id", unique = true, nullable = false)
    @Id
    String trendId;
    @Column(name = "company_id", nullable = false)
    int companyId;

    public String getTrendId() {
        return trendId;
    }

    public void setTrendId(String trendId) {
        this.trendId = trendId;
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
        this.trendId = s;
    }

    @Override
    public String getId() {
        return this.trendId;
    }
}
