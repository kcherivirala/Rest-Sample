package com.fbr.Dao.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "alerts")
public class AlertDbType implements ProjectEntity<String> {
    @Id
    @Column(name = "alert_id", nullable = false)
    String alertId;
    @Column(name = "company_id", nullable = false)
    String companyId;
    @Column(name = "branch_id", nullable = false)
    int branchId;
    @Column(name = "timestamp", nullable = false)
    Date timestamp;


    @Column(name = "alert_string")
    String alertString;
    @Column(name = "info")
    String info;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public String getAlertString() {
        return alertString;
    }

    public void setAlertString(String alertString) {
        this.alertString = alertString;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }


    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    @Override
    @Transient
    public String getId() {
        return this.alertId;
    }

    @Override
    public void setId(String id) {
        this.alertId = id;
    }
}
