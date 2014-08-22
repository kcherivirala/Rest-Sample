package com.fbr.Dao.Company.Entities;

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
@Table(name = "companies")
public class CompanyDbType implements ProjectEntity<Integer> {
    @Column(name = "company_id", unique = true, nullable = false)
    @Id
    int companyId;
    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "owner_name", nullable = false)
    String ownerName;
    @Column(name = "owner_age", nullable = false)
    int ownerAge;
    @Column(name = "owner_sex", nullable = false)
    boolean ownerSex;

    @Column(name = "mail", nullable = false)
    String mail;
    @Column(name = "contact", nullable = false)
    String contact;

    @Column(name = "country", nullable = false)
    int country;
    @Column(name = "state", nullable = false)
    int state;
    @Column(name = "city", nullable = false)
    int city;
    @Column(name = "region", nullable = false)
    int region;

    @Column(name = "competitive_analysis_flag", nullable = false)
    boolean competitiveAnalysisFlag;
    @Column(name = "industry_type", nullable = false)
    String industryType;
    @Column(name = "sub_industry_type", nullable = false)
    String subIndustryType;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "company_id", referencedColumnName = "company_id")
    List<BranchDbType> branches = new ArrayList<BranchDbType>();


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

    public List<BranchDbType> getBranches() {
        return branches;
    }

    public void setBranches(List<BranchDbType> branches) {
        this.branches = branches;
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
