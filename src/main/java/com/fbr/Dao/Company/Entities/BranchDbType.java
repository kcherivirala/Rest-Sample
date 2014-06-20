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

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "branch_manager_name", nullable = false)
    String branchManagerName;
    @Column(name = "branch_manager_sex", nullable = false)
    boolean branchManagerSex;

    @Column(name = "mail", nullable = false)
    String mail;
    @Column(name = "contact", nullable = false)
    String contact;

    @Column(name = "latitude", nullable = false)
    int latitude;
    @Column(name = "longitude", nullable = false)
    int longitude;

    @Column(name = "country", nullable = false)
    int country;
    @Column(name = "state", nullable = false)
    int state;
    @Column(name = "city", nullable = false)
    int city;
    @Column(name = "region", nullable = false)
    int region;

    @Column(name = "budget_category", nullable = false)
    int budgetCategory;
    @Column(name = "industry_type", nullable = false)
    String industryType;
    @Column(name = "sub_industry_type", nullable = false)
    String subIndustryType;
    @Column(name = "operational_time", nullable = false)
    String operationalTime;

    @Override
    @Transient
    public void setId(BranchPrimaryKey s) {
        this.id = s;
    }

    @Override
    public BranchPrimaryKey getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBranchManagerName() {
        return branchManagerName;
    }

    public void setBranchManagerName(String branchManagerName) {
        this.branchManagerName = branchManagerName;
    }

    public boolean isBranchManagerSex() {
        return branchManagerSex;
    }

    public void setBranchManagerSex(boolean branchManagerSex) {
        this.branchManagerSex = branchManagerSex;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public int getCountry() {
        return country;
    }

    public void setCountry(int country) {
        this.country = country;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    public int getBudgetCategory() {
        return budgetCategory;
    }

    public void setBudgetCategory(int budgetCategory) {
        this.budgetCategory = budgetCategory;
    }

    public String getIndustryType() {
        return industryType;
    }

    public void setIndustryType(String industryType) {
        this.industryType = industryType;
    }

    public String getSubIndustryType() {
        return subIndustryType;
    }

    public void setSubIndustryType(String subIndustryType) {
        this.subIndustryType = subIndustryType;
    }

    public String getOperationalTime() {
        return operationalTime;
    }

    public void setOperationalTime(String operationalTime) {
        this.operationalTime = operationalTime;
    }
}
