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
import com.fbr.Utilities.Comparators;
import com.fbr.domain.Company.Branch;
import com.fbr.domain.Company.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CompanyService {
    @Autowired
    private CompanyDao companyDao;
    @Autowired
    private BranchDao branchDao;

    @Transactional
    public Company addCompanyAndBranches(Company company) {
        int id = companyDao.getMaxCompanyIdValue() + 1;
        companyDao.add(Conversions.getCompanyDbEntry(id, company));
        company.setId(id);

        addBranches(id, company.getBranches());
        return company;
    }

    @Transactional
    public Company updateCompanyAndBranches(int companyId, Company company) {
        CompanyDbType companyDbType = companyDao.find(companyId);
        List<BranchDbType> listBranchDb = branchDao.getBranchesByCompany(companyId);

        updateCompanyDbEntry(companyDbType, company);
        updateBranchDbEntries(companyId, listBranchDb, company.getBranches());

        return company;
    }

    public List<Company> getCompanies() {
        List<CompanyDbType> listDbCompanies = companyDao.findAll();
        List<BranchDbType> listDbBranches = branchDao.findAll();

        return matchCompanyAndBranches(listDbCompanies, listDbBranches);
    }

    public Company getCompany(int companyId) {
        CompanyDbType companyDbType = companyDao.find(companyId);
        List<BranchDbType> listDbBranches = branchDao.getBranchesByCompany(companyId);

        return matchCompanyAndBranches(companyDbType, listDbBranches);
    }

    private Company matchCompanyAndBranches(CompanyDbType companyDbType, List<BranchDbType> listDbBranches) {
        List<CompanyDbType> list = new ArrayList<CompanyDbType>(1);
        list.add(companyDbType);
        return matchCompanyAndBranches(list, listDbBranches).get(0);
    }

    private List<Company> matchCompanyAndBranches(List<CompanyDbType> listDbCompanies, List<BranchDbType> listDbBranches) {
        List<Company> out = new ArrayList<Company>(listDbCompanies.size());

        Collections.sort(listDbBranches, Comparators.COMPARE_DB_BRANCHES);
        Collections.sort(listDbCompanies, Comparators.COMPARE_DB_COMPANIES);

        int cIndex = 0, bIndex = 0;
        while (cIndex < listDbCompanies.size()) {
            Company company = Conversions.getCompany(listDbCompanies.get(cIndex));
            List<Branch> listBranches = new ArrayList<Branch>();
            company.setBranches(listBranches);

            while (bIndex < listDbBranches.size() && company.getId() == listDbBranches.get(bIndex).getId().getCompanyId()) {
                listBranches.add(Conversions.getBranch(listDbBranches.get(bIndex)));
                bIndex++;
            }

            out.add(company);
            cIndex++;
        }
        return out;
    }

    private void updateCompanyDbEntry(CompanyDbType companyDbEntry, Company company) {
        boolean updated = false;
        if (!company.getInfo().equals(companyDbEntry.getInfo())) {
            companyDbEntry.setInfo(company.getInfo());
            updated = true;
        }
        if (!company.getName().equals(companyDbEntry.getName())) {
            companyDbEntry.setName(company.getName());
            updated = true;
        }
        if (updated)
            companyDao.update(companyDbEntry);
    }

    private void updateBranchDbEntries(int companyId, List<BranchDbType> listBranchDb, List<Branch> inputBranches) {
        Collections.sort(listBranchDb, Comparators.COMPARE_DB_BRANCHES);
        Collections.sort(inputBranches, Comparators.COMPARE_BRANCHES);

        int dbIndex = 0, inputIndex = 0;
        while (dbIndex < listBranchDb.size() && inputIndex < inputBranches.size()) {
            BranchDbType branchDbEntry = listBranchDb.get(dbIndex);
            Branch inputBranch = inputBranches.get(inputIndex);

            if (inputBranch.getId() == branchDbEntry.getId().getBranchId()) {
                updateBranchDbEntry(branchDbEntry, inputBranch);
                dbIndex++;
                inputIndex++;
            } else if (inputBranches.get(inputIndex).getId() < branchDbEntry.getId().getBranchId()) {
                addBranch(companyId, inputBranch);
                inputIndex++;
            } else {
                deleteBranchDbEntry(branchDbEntry);
                dbIndex++;
            }
        }
        while (dbIndex < listBranchDb.size()) {
            deleteBranchDbEntry(listBranchDb.get(dbIndex));
            dbIndex++;
        }
        while (inputIndex < inputBranches.size()) {
            addBranch(companyId, inputBranches.get(inputIndex));
            inputIndex++;
        }

    }

    private void updateBranchDbEntry(BranchDbType branchDbEntry, Branch inputBranch) {
        if (!branchDbEntry.getInfo().equals(inputBranch.getInfo())) {
            branchDbEntry.setInfo(inputBranch.getInfo());
            branchDao.update(branchDbEntry);
        }
    }

    private void deleteBranchDbEntry(BranchDbType branchDbType) {
        branchDao.delete(branchDbType);
    }

    public List<BranchDbType> getDbBranches(int companyId) {
        return branchDao.getBranchesByCompany(companyId);
    }


    private void addBranches(int companyId, List<Branch> branches) {
        for (Branch branch : branches) {
            addBranch(companyId, branch);
        }
    }

    private void addBranch(int companyId, Branch branch) {
        int id = branchDao.getMaxBranchIdValue(companyId) + 1;
        branchDao.add(Conversions.getBranchDbEntry(companyId, id, branch));
    }

    public static class Conversions {
        public static CompanyDbType getCompanyDbEntry(int companyId, Company company) {
            CompanyDbType companyDbEntry = new CompanyDbType();
            companyDbEntry.setCompanyId(companyId);
            companyDbEntry.setInfo(company.getInfo());

            return companyDbEntry;
        }

        public static BranchDbType getBranchDbEntry(int companyId, int branchId, Branch branch) {
            BranchDbType branchDbEntry = new BranchDbType();
            BranchPrimaryKey key = new BranchPrimaryKey();

            key.setCompanyId(companyId);
            key.setBranchId(branchId);
            branchDbEntry.setId(key);
            branchDbEntry.setInfo(branch.getInfo());

            return branchDbEntry;
        }

        public static Company getCompany(CompanyDbType companyDbEntry) {
            Company company = new Company();
            company.setId(companyDbEntry.getId());
            company.setInfo(companyDbEntry.getInfo());
            company.setName(companyDbEntry.getName());

            return company;
        }

        public static Branch getBranch(BranchDbType branchDbEntry) {
            Branch branch = new Branch();

            branch.setId(branchDbEntry.getId().getBranchId());
            branch.setInfo(branchDbEntry.getInfo());

            return branch;
        }
    }
}
