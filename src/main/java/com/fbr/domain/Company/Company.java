package com.fbr.domain.Company;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import java.util.List;

public class Company {
    int id;
    String name;

    String ownerName;
    int ownerAge;
    boolean ownerSex;

    String mail;
    String contact;

    int country;
    int state;
    int city;
    int region;

    boolean competitiveAnalysisFlag;
    String industryType;
    String subIndustryType;

    List<Branch> branches;

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public int getOwnerAge() {
        return ownerAge;
    }

    public void setOwnerAge(int ownerAge) {
        this.ownerAge = ownerAge;
    }

    public boolean isOwnerSex() {
        return ownerSex;
    }

    public void setOwnerSex(boolean ownerSex) {
        this.ownerSex = ownerSex;
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

    public boolean isCompetitiveAnalysisFlag() {
        return competitiveAnalysisFlag;
    }

    public void setCompetitiveAnalysisFlag(boolean competitiveAnalysisFlag) {
        this.competitiveAnalysisFlag = competitiveAnalysisFlag;
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
}
