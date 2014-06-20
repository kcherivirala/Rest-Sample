package com.fbr.domain.Company;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

public class Branch {
    int id;
    String name;

    String branchManagerName;
    boolean branchManagerSex;

    String mail;
    String contact;

    int latitude;
    int longitude;

    int country;
    int state;
    int city;
    int region;

    int budgetCategory;
    String industryType;
    String subIndustryType;
    String operationalTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
