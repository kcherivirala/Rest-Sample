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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CompanyService {
    private static final Logger logger = Logger.getLogger(CompanyService.class);
    @Autowired
    private CompanyDao companyDao;
    @Autowired
    private BranchDao branchDao;

    @Transactional
    public Company addCompanyAndBranches(Company company) throws Exception {
        logger.info("adding a company : " + company.getName() + " and branches : " + company.getBranches().size());

        if (company.getBranches() == null || company.getBranches().size() == 0) {
            logger.error("company has to have at least 1 branch : " + company.getName());
            throw new Exception("company has to have at least 1 branch : " + company.getName());
        }

        try {
            int id = companyDao.getMaxCompanyIdValue() + 1;
            companyDao.add(Conversions.getCompanyDbEntry(id, company));
            company.setId(id);

            addBranches(id, company.getBranches());
            logger.info("done adding a company : " + company.getName() + " and branches : " + company.getBranches().size());
            return company;
        } catch (Exception e) {
            logger.error("error creating company : " + company.getName() + " : " + e.getMessage());
            throw new Exception("error creating company : " + company.getName() + " : " + e.getMessage());
        }
    }

    @Transactional
    public Company updateCompanyAndBranches(int companyId, Company company) throws Exception {
        logger.info("updating a company : " + company.getName() + " and branches : " + company.getBranches().size());

        if (company.getBranches() == null || company.getBranches().size() == 0) {
            logger.error("company has to have at least 1 branch : " + company.getName());
            throw new Exception("company has to have at least 1 branch : " + company.getName());
        }

        try {
            CompanyDbType companyDbType = companyDao.find(companyId);
            List<BranchDbType> listBranchDb = branchDao.getBranchesByCompany(companyId);

            updateCompanyDbEntry(companyDbType, company);
            updateBranchDbEntries(companyId, listBranchDb, company.getBranches());

            logger.info("done updating a company : " + company.getName() + " and branches : " + company.getBranches().size());
            return company;
        } catch (Exception e) {
            logger.error("error updating company : " + company.getName() + " : " + e.getMessage());
            throw new Exception("error updating company : " + company.getName() + " : " + e.getMessage());
        }
    }

    public List<Company> getCompanies() throws Exception {
        try {
            logger.info("getting all the companies");
            List<CompanyDbType> listDbCompanies = companyDao.findAll();
            List<BranchDbType> listDbBranches = branchDao.findAll();

            List<Company> out = matchCompanyAndBranches(listDbCompanies, listDbBranches);
            logger.info("done getting all the companies");
            return out;
        } catch (Exception e) {
            logger.error("error getting the list of companies");
            throw new Exception("error getting the list of companies");
        }
    }

    public Company getCompany(int companyId) throws Exception {
        try {
            logger.info("getting the company : " + companyId);
            CompanyDbType companyDbType = companyDao.find(companyId);
            List<BranchDbType> listDbBranches = branchDao.getBranchesByCompany(companyId);

            Company out = matchCompanyAndBranches(companyDbType, listDbBranches);
            logger.info("done getting the company : " + companyId);
            return out;
        } catch (Exception e) {
            logger.error("error getting company : " + companyId + " : " + e.getMessage());
            throw new Exception("error getting company : " + companyId + " : " + e.getMessage());
        }
    }

    @Transactional
    public void deleteCompanyAndBranches(int companyId) throws Exception {
        try {
            logger.info("deleting the company : " + companyId);
            branchDao.deleteBranches(companyId);
            companyDao.delete(companyDao.find(companyId));
            logger.info("done deleting the company : " + companyId);
        } catch (Exception e) {
            logger.error("error deleting company : " + companyId + " : " + e.getMessage());
            throw new Exception("error deleting company : " + companyId + " : " + e.getMessage());
        }
    }

    public List<CompanyDbType> getCompanyDbEntries() {
        return companyDao.findAll();
    }

    public CompanyDbType getCompanyDbEntry(int companyId) {
        return companyDao.find(companyId);
    }

    /* private functions */

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

            while (bIndex < listDbBranches.size() && company.getId() > listDbBranches.get(bIndex).getId().getCompanyId()) {
                bIndex++;
            }
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
        logger.debug("modifying Company : " + companyDbEntry.getCompanyId());
        boolean updated = false;
        if (!company.getName().equals(companyDbEntry.getName())) {
            companyDbEntry.setName(company.getName());
            updated = true;
        }

        if (!company.getMail().equals(companyDbEntry.getMail())) {
            companyDbEntry.setMail(company.getMail());
            updated = true;
        }
        if (!company.getContact().equals(companyDbEntry.getContact())) {
            companyDbEntry.setContact(company.getContact());
            updated = true;
        }

        if (!company.getOwnerName().equals(companyDbEntry.getOwnerName())) {
            companyDbEntry.setOwnerName(company.getOwnerName());
            updated = true;
        }
        if (company.getOwnerAge() != (companyDbEntry.getOwnerAge())) {
            companyDbEntry.setOwnerAge(company.getOwnerAge());
            updated = true;
        }
        if (company.isOwnerSex() != companyDbEntry.isOwnerSex()) {
            companyDbEntry.setOwnerSex(company.isOwnerSex());
            updated = true;
        }

        if (company.getCountry() != companyDbEntry.getCountry()) {
            companyDbEntry.setCountry(company.getCountry());
            updated = true;
        }
        if (company.getState() != companyDbEntry.getState()) {
            companyDbEntry.setState(company.getState());
            updated = true;
        }
        if (company.getCity() != companyDbEntry.getCity()) {
            companyDbEntry.setCity(company.getCity());
            updated = true;
        }
        if (company.getRegion() != companyDbEntry.getRegion()) {
            companyDbEntry.setRegion(company.getRegion());
            updated = true;
        }

        if (company.isCompetitiveAnalysisFlag() != companyDbEntry.isCompetitiveAnalysisFlag()) {
            companyDbEntry.setCompetitiveAnalysisFlag(company.isCompetitiveAnalysisFlag());
            updated = true;
        }
        if (!company.getIndustryType().equals(companyDbEntry.getIndustryType())) {
            companyDbEntry.setIndustryType(company.getIndustryType());
            updated = true;
        }
        if (!company.getSubIndustryType().equals(companyDbEntry.getSubIndustryType())) {
            companyDbEntry.setSubIndustryType(company.getSubIndustryType());
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
                logger.debug("modifying the branch : (" + companyId + "," + inputBranch.getId() + ")");
                updateBranchDbEntry(branchDbEntry, inputBranch);
                dbIndex++;
                inputIndex++;
            } else if (inputBranches.get(inputIndex).getId() < branchDbEntry.getId().getBranchId()) {
                logger.debug("adding the branch : (" + companyId + "," + inputBranch.getId() + ")");
                addBranch(companyId, inputBranch);
                inputIndex++;
            } else {
                logger.debug("deleting the branch : (" + companyId + "," + branchDbEntry.getId().getBranchId() + ")");
                deleteBranchDbEntry(branchDbEntry);
                dbIndex++;
            }
        }
        while (dbIndex < listBranchDb.size()) {
            logger.debug("deleting the branch : (" + companyId + "," + listBranchDb.get(dbIndex).getId().getBranchId() + ")");
            deleteBranchDbEntry(listBranchDb.get(dbIndex));
            dbIndex++;
        }
        while (inputIndex < inputBranches.size()) {
            logger.debug("adding the branch : (" + companyId + "," + inputBranches.get(inputIndex).getId() + ")");
            addBranch(companyId, inputBranches.get(inputIndex));
            inputIndex++;
        }

    }

    private void updateBranchDbEntry(BranchDbType branchDbEntry, Branch branch) {
        logger.debug("modifying branch : " + branchDbEntry.getId().getBranchId());
        boolean updated = false;

        if (!branch.getName().equals(branchDbEntry.getName())) {
            branchDbEntry.setName(branch.getName());
            updated = true;
        }

        if (!branch.getMail().equals(branchDbEntry.getMail())) {
            branchDbEntry.setMail(branch.getMail());
            updated = true;
        }
        if (!branch.getContact().equals(branchDbEntry.getContact())) {
            branchDbEntry.setContact(branch.getContact());
            updated = true;
        }

        if (!branch.getBranchManagerName().equals(branchDbEntry.getBranchManagerName())) {
            branchDbEntry.setBranchManagerName(branch.getBranchManagerName());
            updated = true;
        }
        if (branch.isBranchManagerSex() != branchDbEntry.isBranchManagerSex()) {
            branchDbEntry.setBranchManagerSex(branch.isBranchManagerSex());
            updated = true;
        }

        if (branch.getLatitude() != branchDbEntry.getLatitude()) {
            branchDbEntry.setLatitude(branch.getLatitude());
            updated = true;
        }
        if (branch.getLongitude() != branchDbEntry.getLongitude()) {
            branchDbEntry.setLongitude(branch.getLongitude());
            updated = true;
        }

        if (branch.getCountry() != branchDbEntry.getCountry()) {
            branchDbEntry.setCountry(branch.getCountry());
            updated = true;
        }
        if (branch.getState() != branchDbEntry.getState()) {
            branchDbEntry.setState(branch.getState());
            updated = true;
        }
        if (branch.getCity() != branchDbEntry.getCity()) {
            branchDbEntry.setCity(branch.getCity());
            updated = true;
        }
        if (branch.getRegion() != branchDbEntry.getRegion()) {
            branchDbEntry.setRegion(branch.getRegion());
            updated = true;
        }

        if (!branch.getIndustryType().equals(branchDbEntry.getIndustryType())) {
            branchDbEntry.setIndustryType(branch.getIndustryType());
            updated = true;
        }
        if (!branch.getSubIndustryType().equals(branchDbEntry.getSubIndustryType())) {
            branchDbEntry.setSubIndustryType(branch.getSubIndustryType());
            updated = true;
        }

        if (branch.getBudgetCategory() != branchDbEntry.getBudgetCategory()) {
            branchDbEntry.setBudgetCategory(branch.getBudgetCategory());
            updated = true;
        }
        if (!branch.getOperationalTime().equals(branchDbEntry.getOperationalTime())) {
            branchDbEntry.setOperationalTime(branch.getOperationalTime());
            updated = true;
        }

        if (updated)
            branchDao.update(branchDbEntry);
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
            companyDbEntry.setName(company.getName());

            companyDbEntry.setMail(company.getMail());
            companyDbEntry.setContact(company.getContact());

            companyDbEntry.setOwnerName(company.getOwnerName());
            companyDbEntry.setOwnerAge(company.getOwnerAge());
            companyDbEntry.setOwnerSex(company.isOwnerSex());

            companyDbEntry.setCountry(company.getCountry());
            companyDbEntry.setState(company.getState());
            companyDbEntry.setCity(company.getCity());
            companyDbEntry.setRegion(company.getRegion());

            companyDbEntry.setCompetitiveAnalysisFlag(company.isCompetitiveAnalysisFlag());
            companyDbEntry.setIndustryType(company.getIndustryType());
            companyDbEntry.setSubIndustryType(company.getSubIndustryType());

            return companyDbEntry;
        }

        public static Company getCompany(CompanyDbType companyDbEntry) {
            Company company = new Company();

            company.setId(companyDbEntry.getId());
            company.setName(companyDbEntry.getName());

            company.setMail(companyDbEntry.getMail());
            company.setContact(companyDbEntry.getContact());

            company.setOwnerName(companyDbEntry.getOwnerName());
            company.setOwnerAge(companyDbEntry.getOwnerAge());
            company.setOwnerSex(companyDbEntry.isOwnerSex());

            company.setCountry(companyDbEntry.getCountry());
            company.setState(companyDbEntry.getState());
            company.setCity(companyDbEntry.getCity());
            company.setRegion(companyDbEntry.getRegion());

            company.setCompetitiveAnalysisFlag(companyDbEntry.isCompetitiveAnalysisFlag());
            company.setIndustryType(companyDbEntry.getIndustryType());
            company.setSubIndustryType(companyDbEntry.getSubIndustryType());

            return company;
        }

        public static BranchDbType getBranchDbEntry(int companyId, int branchId, Branch branch) {
            BranchDbType branchDbEntry = new BranchDbType();
            BranchPrimaryKey key = new BranchPrimaryKey();

            key.setCompanyId(companyId);
            key.setBranchId(branchId);
            branchDbEntry.setId(key);
            branchDbEntry.setName(branch.getName());

            branchDbEntry.setBranchManagerName(branch.getBranchManagerName());
            branchDbEntry.setBranchManagerSex(branch.isBranchManagerSex());

            branchDbEntry.setMail(branch.getMail());
            branchDbEntry.setContact(branch.getContact());

            branchDbEntry.setLatitude(branch.getLatitude());
            branchDbEntry.setLongitude(branch.getLongitude());

            branchDbEntry.setCountry(branch.getCountry());
            branchDbEntry.setState(branch.getState());
            branchDbEntry.setCity(branch.getCity());
            branchDbEntry.setRegion(branch.getRegion());

            branchDbEntry.setIndustryType(branch.getIndustryType());
            branchDbEntry.setSubIndustryType(branch.getSubIndustryType());

            branchDbEntry.setBudgetCategory(branch.getBudgetCategory());
            branchDbEntry.setOperationalTime(branch.getOperationalTime());

            return branchDbEntry;
        }

        public static Branch getBranch(BranchDbType branchDbEntry) {
            Branch branch = new Branch();

            branch.setId(branchDbEntry.getId().getBranchId());

            branch.setName(branchDbEntry.getName());

            branch.setBranchManagerName(branchDbEntry.getBranchManagerName());
            branch.setBranchManagerSex(branchDbEntry.isBranchManagerSex());

            branch.setMail(branchDbEntry.getMail());
            branch.setContact(branchDbEntry.getContact());

            branch.setLatitude(branchDbEntry.getLatitude());
            branch.setLongitude(branchDbEntry.getLongitude());

            branch.setCountry(branchDbEntry.getCountry());
            branch.setState(branchDbEntry.getState());
            branch.setCity(branchDbEntry.getCity());
            branch.setRegion(branchDbEntry.getRegion());

            branch.setIndustryType(branchDbEntry.getIndustryType());
            branch.setSubIndustryType(branchDbEntry.getSubIndustryType());

            branch.setBudgetCategory(branchDbEntry.getBudgetCategory());
            branch.setOperationalTime(branchDbEntry.getOperationalTime());

            return branch;
        }
    }
}
