package com.fbr.Dao.Company.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectEntity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;

public class CompanyDbType implements ProjectEntity<Integer> {
    @Column(name = "company_id", unique = true, nullable = false)
    @Id
    int companyId;
    @Column(name = "name", nullable = false)
    String name;
    @Column(name = "info", nullable = false)
    String info;

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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    @Transient
    public void setId(Integer s) {
        this.companyId = s;
    }

    @Override
    public Integer getId() {
        return this.companyId;
    }
}
