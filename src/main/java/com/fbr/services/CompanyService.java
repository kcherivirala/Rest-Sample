package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Company.BranchDao;
import com.fbr.Dao.Company.CompanyDao;
import com.fbr.Dao.Company.Entities.BranchDbType;
import com.fbr.Dao.Company.Entities.BranchPrimaryKey;
import com.fbr.Dao.Company.Entities.CompanyDbType;
import com.fbr.domain.Company.Branch;
import com.fbr.domain.Company.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompanyService {
    @Autowired
    private CompanyDao companyDao;
    @Autowired
    private BranchDao branchDao;

    @Transactional
    public Company addCompany(Company company){
        int id = companyDao.getMaxCompanyIdValue() +1;
        companyDao.add(Conversions.getCompanyDbEntry(id, company));
        company.setId(id);
        return company;
    }

    @Transactional
    public Branch addBranch(int companyId, Branch branch){
        int id = branchDao.getMaxBranchIdValue(companyId);
        branchDao.add(Conversions.getBranchDbEntry(companyId, id, branch));
        branch.setId(id);
        return branch;
    }

    public List<BranchDbType> getDbBranches(int companyId){
        return branchDao.getBranchesByCompany(companyId);
    }

    private static class Conversions{
        public static CompanyDbType getCompanyDbEntry(int companyId, Company company){
            CompanyDbType companyDbEntry = new CompanyDbType();
            companyDbEntry.setCompanyId(companyId);
            companyDbEntry.setInfo(company.getInfo());

            return companyDbEntry;
        }

        public static BranchDbType getBranchDbEntry(int companyId, int branchId, Branch branch){
            BranchDbType branchDbEntry = new BranchDbType();
            BranchPrimaryKey key = new BranchPrimaryKey();

            key.setCompanyId(companyId);
            key.setBranchId(branchId);
            branchDbEntry.setId(key);
            branchDbEntry.setInfo(branch.getInfo());

            return branchDbEntry;
        }
    }

}
