package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Customer.Entities.CustomerDbType;
import com.fbr.Dao.Response.CustomerResponseDao;
import com.fbr.Dao.Response.CustomerResponseValuesDao;
import com.fbr.Dao.Response.Entities.CustomerResponseDbType;
import com.fbr.Dao.Response.Entities.CustomerResponseValuesDbType;
import com.fbr.Dao.Response.Entities.CustomerResponseValuesPrimaryKey;
import com.fbr.domain.Attribute.Attribute;
import com.fbr.domain.Attribute.AttributeValue;
import com.fbr.domain.Response.AttributeTuple;
import com.fbr.domain.Response.Response;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.UUID;

@Service
public class ResponseService {
    private static final Logger logger = Logger.getLogger(ResponseService.class);
    @Autowired
    private CustomerResponseDao customerResponseDao;
    @Autowired
    private CustomerResponseValuesDao customerResponseValuesDao;
    @Autowired
    private AlertService alertService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private AttributeService attributeService;

    private Timer timer;
    long AGGREGATE_TIME_INTERVAL = 24 * 3600 * 1000; // 24 hours

    @Transactional
    public void processResponse(int companyId, int branchId, List<Response> responseList) {
        //processes teh list of responses from various customer for the given company and branch.
        try {
            logger.info("adding customer responses for : (" + companyId + "," + branchId + ")");
            List<Attribute> attributeList = attributeService.getAttributesByCompany(companyId);
            for (Response response : responseList) {
                CustomerDbType customerDbEntry = customerService.addCustomerInfo(response.getEmail(), response.getPhone(), response.getName());

                String customerId;
                if (customerDbEntry != null) customerId = customerDbEntry.getCustomerId();
                else customerId = UUID.randomUUID().toString();

                if (check(companyId, response)) {
                    addToResponseDb(companyId, branchId, customerId, response.getAttributeTuples());

                    if (customerDbEntry != null)
                        alertService.addToAlertDb(companyId, branchId, customerDbEntry, response.getAttributeTuples(), attributeList);
                }
            }
            logger.info("done adding customer responses for : (" + companyId + "," + branchId + ")");
        } catch (Exception e) {
            logger.error("error processing responses : " + companyId + "," + branchId);
        }
    }

    private void addToResponseDb(int companyId, int branchId, String customerId, List<AttributeTuple> attributeResponseTuples) {
        //adds the feedback of one customer into the DB.
        String responseId = UUID.randomUUID().toString();
        customerResponseDao.add(getCustomerResponseDbEntry(responseId, companyId, branchId, customerId));
        for (AttributeTuple attributeTuple : attributeResponseTuples) {
            customerResponseValuesDao.add(getCustomerResponseValuesDbEntry(responseId, attributeTuple));
        }
    }

    private CustomerResponseDbType getCustomerResponseDbEntry(String responseId, int companyId, int branchId, String customerId) {
        CustomerResponseDbType customerResponseDbEntry = new CustomerResponseDbType();

        customerResponseDbEntry.setResponseId(responseId);
        customerResponseDbEntry.setCompanyId(companyId);
        customerResponseDbEntry.setBranchId(branchId);
        customerResponseDbEntry.setCustomerId(customerId);
        customerResponseDbEntry.setTimestamp(new Date());

        return customerResponseDbEntry;
    }

    private CustomerResponseValuesDbType getCustomerResponseValuesDbEntry(String responseId, AttributeTuple attributeTuple) {
        CustomerResponseValuesDbType dbEntry = new CustomerResponseValuesDbType();
        CustomerResponseValuesPrimaryKey key = new CustomerResponseValuesPrimaryKey();

        key.setResponseId(responseId);
        key.setAttributeId(attributeTuple.getAttributeId());

        dbEntry.setId(key);
        dbEntry.setMaxValue(attributeTuple.getMaxValue());
        dbEntry.setObtainedValue(attributeTuple.getObtainedValue());
        dbEntry.setResponse(attributeTuple.getResponseString());

        return dbEntry;
    }

    private boolean check(int companyId, Response response) {
        logger.info("check response for company" + companyId + " responses : " + response.getAttributeTuples().size());
        List<Attribute> attributeList = attributeService.getAttributesByCompany(companyId);
        for (AttributeTuple attributeTuple : response.getAttributeTuples()) {
            logger.debug("checking for company " + companyId + " attribute : " + attributeTuple.getAttributeId() + " and value : " + attributeTuple.getObtainedValue());
            int attributeId = attributeTuple.getAttributeId();
            int obtainedVal = attributeTuple.getObtainedValue();

            boolean flag = false;
            for (Attribute attribute : attributeList) {
                if (attribute.getAttributeId() == attributeId) {
                    for (AttributeValue attributeValue : attribute.getAttributeValues()) {
                        if (obtainedVal == attributeValue.getValue()) {
                            flag = true;
                            break;
                        }
                    }
                }
            }
            if (flag == false) return false;
        }
        return true;
    }
}
