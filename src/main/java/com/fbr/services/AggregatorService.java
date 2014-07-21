package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Cache.CacheDbEntry;
import com.fbr.Dao.Cache.CacheJdbcClient;
import com.fbr.Dao.Company.Entities.CompanyDbType;
import com.fbr.Dao.Response.CustomerResponseDao;
import com.fbr.Dao.Response.Entities.CustomerResponseValuesDbType;
import com.fbr.Utilities.FeedbackUtilities;
import com.fbr.domain.Attribute.Attribute;
import com.fbr.domain.Response.AttributeTuple;
import com.fbr.domain.Response.Response;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("aggregatorService")
public class AggregatorService {
    private static final Logger logger = Logger.getLogger(AggregatorService.class);
    @Autowired
    private AttributeService attributeService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private CacheJdbcClient cacheJdbcClient;
    @Autowired
    private CustomerResponseDao customerResponseDao;

    Map<Integer, List<Attribute>> companyCacheExistsMap;

    @PostConstruct
    public void init() {
        List<CompanyDbType> companies = companyService.getCompanyDbEntries();
        companyCacheExistsMap = new HashMap<Integer, List<Attribute>>(companies.size());//random initial value

        for (CompanyDbType company : companies) {
            try {
                List<Attribute> filterAttributes = attributeService.getFilterAttributes(company.getCompanyId());
                createCompanyCache(company.getCompanyId(), filterAttributes);
                companyCacheExistsMap.put(company.getCompanyId(), filterAttributes);

            } catch (Exception e) {
                logger.error("error initialising info for : " + company.getCompanyId());
            }
        }
    }

    public void addResponses(int companyId, int branchId, List<Response> responseList) {
        try {
            logger.info("adding customer responses for aggregator service : (" + companyId + "," + branchId + ")");
            List<Attribute> filterAttributes = attributeService.getFilterAttributes(companyId);

            if (!companyCacheExistsMap.containsKey(companyId)) {
                createCompanyCache(companyId, filterAttributes);
                companyCacheExistsMap.put(companyId, filterAttributes);
            } else {
                //exists : check if the filter attributes changed
                if (checkChangeFilterAttributes(companyCacheExistsMap.get(companyId), filterAttributes)) {
                    cacheJdbcClient.updateTable(companyId, companyCacheExistsMap.get(companyId), filterAttributes);
                }
            }

            for (Response response : responseList) {
                processSingleResponse(companyId, branchId, response, filterAttributes);
            }
        } catch (Exception e) {
            logger.error("error processing responses for aggregator service : " + companyId + "," + branchId);
        }
    }

    public void addResponses(int companyId, List<CustomerResponseDao.CustomerResponseAndValues> listResponse) {
        try {
            logger.info("adding customer responses for aggregator service : (" + companyId + ")");
            List<Attribute> filterAttributes = attributeService.getFilterAttributes(companyId);

            for (CustomerResponseDao.CustomerResponseAndValues response : listResponse) {
                processSingleResponse(companyId, response.getResponse().getBranchId(), response, filterAttributes);
            }
        } catch (Exception e) {
            logger.error("error processing responses for aggregator service : " + companyId);
        }
    }

    private void createCompanyCache(int companyId, List<Attribute> filterAttributes) {
        if (cacheJdbcClient.checkCacheExists(companyId)) return;

        cacheJdbcClient.createTable(companyId, filterAttributes);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -364);//>365
        List<CustomerResponseDao.CustomerResponseAndValues> listResponse = customerResponseDao.getResponses(companyId, cal.getTime());

        addResponses(companyId, listResponse);
    }

    @Transactional
    private void processSingleResponse(int companyId, int branchId, Response response, List<Attribute> filterAttributes) {
        int date = FeedbackUtilities.dateFromCal(response.getDate());
        Map<Integer, Integer> map = new HashMap<Integer, Integer>(filterAttributes.size());
        for (Attribute attribute : filterAttributes) {
            map.put(attribute.getAttributeId(), -1);
        }

        for (AttributeTuple attributeTuple : response.getAttributeTuples()) {
            if (checkFilter(attributeTuple.getAttributeId(), filterAttributes))
                map.put(attributeTuple.getAttributeId(), attributeTuple.getObtainedValue());
        }

        for (AttributeTuple attributeTuple : response.getAttributeTuples()) {
            if (!checkFilter(attributeTuple.getAttributeId(), filterAttributes)) {
                CacheDbEntry key = getDbClientKey(branchId, date, map, attributeTuple.getAttributeId());
                CacheDbEntry cacheDbEntry = cacheJdbcClient.getEntry(companyId, key, filterAttributes);
                if (cacheDbEntry == null) {
                    cacheDbEntry = getNewCacheDbEntry(branchId, date, map, attributeTuple.getAttributeId(), attributeTuple.getObtainedValue());
                    cacheJdbcClient.addEntry(companyId, cacheDbEntry);
                } else {
                    updateCacheDbEntry(cacheDbEntry, attributeTuple.getAttributeId(), attributeTuple.getObtainedValue());
                    cacheJdbcClient.updateEntry(companyId, cacheDbEntry);
                }
            }
        }
    }

    @Transactional
    private void processSingleResponse(int companyId, int branchId, CustomerResponseDao.CustomerResponseAndValues response, List<Attribute> filterAttributes) {
        int date = FeedbackUtilities.dateFromCal(response.getResponse().getTimestamp());
        Map<Integer, Integer> map = new HashMap<Integer, Integer>(filterAttributes.size());
        for (Attribute attribute : filterAttributes) {
            map.put(attribute.getAttributeId(), -1);
        }

        for (CustomerResponseValuesDbType responseValue : response.getResponseValues()) {
            if (checkFilter(responseValue.getId().getAttributeId(), filterAttributes))
                map.put(responseValue.getId().getAttributeId(), responseValue.getObtainedValue());
        }

        for (CustomerResponseValuesDbType responseValue : response.getResponseValues()) {
            if (!checkFilter(responseValue.getId().getAttributeId(), filterAttributes)) {
                CacheDbEntry key = getDbClientKey(branchId, date, map, responseValue.getId().getAttributeId());
                CacheDbEntry cacheDbEntry = cacheJdbcClient.getEntry(companyId, key, filterAttributes);
                if (cacheDbEntry == null) {
                    cacheDbEntry = getNewCacheDbEntry(branchId, date, map, responseValue.getId().getAttributeId(), responseValue.getObtainedValue());
                    cacheJdbcClient.addEntry(companyId, cacheDbEntry);
                } else {
                    updateCacheDbEntry(cacheDbEntry, responseValue.getId().getAttributeId(), responseValue.getObtainedValue());
                    cacheJdbcClient.updateEntry(companyId, cacheDbEntry);
                }
            }
        }
    }

    private CacheDbEntry getDbClientKey(int branchId, int date, Map<Integer, Integer> map, int attributeId) {
        CacheDbEntry dbEntry = new CacheDbEntry();
        dbEntry.setBranchId(branchId);
        dbEntry.setMapOfFilters(map);
        dbEntry.setWeightedAttributeId(attributeId);
        dbEntry.setDate(date);

        return dbEntry;
    }

    private CacheDbEntry getNewCacheDbEntry(int branchId, int date, Map<Integer, Integer> map, int attributeId, int attributeValue) {
        CacheDbEntry key = getDbClientKey(branchId, date, map, attributeId);
        key.setCount_1(0);
        key.setCount_2(0);
        key.setCount_3(0);
        key.setCount_4(0);
        key.setCount_5(0);

        switch (attributeValue) {
            case 1:
                key.setCount_1(1);
            case 2:
                key.setCount_2(1);
            case 3:
                key.setCount_3(1);
            case 4:
                key.setCount_4(1);
            case 5:
                key.setCount_5(1);
        }

        return key;
    }

    private void updateCacheDbEntry(CacheDbEntry cacheDbEntry, int attributeId, int attributeValue) {
        switch (attributeValue) {
            case 1:
                cacheDbEntry.setCount_1(cacheDbEntry.getCount_1() + 1);
            case 2:
                cacheDbEntry.setCount_2(cacheDbEntry.getCount_2() + 1);
            case 3:
                cacheDbEntry.setCount_3(cacheDbEntry.getCount_3() + 1);
            case 4:
                cacheDbEntry.setCount_4(cacheDbEntry.getCount_4() + 1);
            case 5:
                cacheDbEntry.setCount_5(cacheDbEntry.getCount_5() + 1);
        }
    }

    private boolean checkFilter(int attrId, List<Attribute> filterAttributes) {
        for (Attribute attribute : filterAttributes) {
            if (attrId == attribute.getAttributeId())
                return true;
        }
        return false;
    }

    private boolean checkChangeFilterAttributes(List<Attribute> oldFilters, List<Attribute> newFilters) {
        if (oldFilters.size() != newFilters.size())
            return true;

        int oldIndex = 0, newIndex = 0;
        while (oldIndex < oldFilters.size() && newIndex < newFilters.size()) {
            if (oldFilters.get(oldIndex).getAttributeId() == newFilters.get(newIndex).getAttributeId()) {
                oldIndex++;
                newIndex++;
            } else {
                return true;
            }
        }
        return false;
    }
}
