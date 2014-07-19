package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Cache.CacheDbEntry;
import com.fbr.Dao.Cache.CacheJdbcClient;
import com.fbr.Utilities.FeedbackUtilities;
import com.fbr.domain.Attribute.Attribute;
import com.fbr.domain.Response.AttributeTuple;
import com.fbr.domain.Response.Response;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("aggregatorService")
public class AggregatorService {
    private static final Logger logger = Logger.getLogger(AggregatorService.class);
    @Autowired
    private AttributeService attributeService;
    @Autowired
    private CacheJdbcClient cacheJdbcClient;

    @Transactional
    public void addResponses(int companyId, int branchId, List<Response> responseList) {
        try {
            logger.info("adding customer responses for aggregator service : (" + companyId + "," + branchId + ")");
            List<Attribute> filterAttributes = attributeService.getFilterAttributes(companyId);

            for (Response response : responseList) {
                List<CacheDbEntry> dbEntry = getCacheDbEntry(branchId, response, filterAttributes);
            }
        } catch (Exception e) {
            logger.error("error processing responses for aggregator service : " + companyId + "," + branchId);
        }
    }


    private List<CacheDbEntry> getCacheDbEntry(int branchId, Response response, List<Attribute> filterAttributes) {
        List<CacheDbEntry> listDb = new ArrayList<CacheDbEntry>();

        int date = FeedbackUtilities.dateFromCal(response.getDate());
        Map<Integer, Integer> map = new HashMap<Integer, Integer>(filterAttributes.size());
        for (Attribute attribute : filterAttributes) {
            map.put(attribute.getAttributeId(), -1);
        }


        for (AttributeTuple attributeTuple : response.getAttributeTuples()) {
            if (checkFilter(attributeTuple.getAttributeId(), filterAttributes))
                map.put(attributeTuple.getAttributeId(), attributeTuple.getObtainedValue());
        }


        return listDb;
    }


    private boolean checkFilter(int attrId, List<Attribute> filterAttributes) {
        for (Attribute attribute : filterAttributes) {
            if (attrId == attribute.getAttributeId())
                return true;
        }
        return false;
    }

}
