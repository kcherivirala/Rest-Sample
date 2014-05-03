package com.fbr.Dao.Company.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectEntity;

import javax.persistence.*;

@Entity
@Table(name = "branches")
public class BranchDbType implements ProjectEntity<BranchPrimaryKey> {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "companyId", column = @Column(name = "company_id", nullable = false)),
            @AttributeOverride(name = "branchId", column = @Column(name = "branch_id", nullable = false))})
    BranchPrimaryKey id;

    @Column(name = "info")
    String info;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    @Transient
    public void setId(BranchPrimaryKey s) {
        this.id = s;
    }

    @Override
    public BranchPrimaryKey getId() {
        return this.id;
    }
}
