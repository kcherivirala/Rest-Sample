package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.CustomerDao;
import com.fbr.Dao.CustomerResponseDao;
import com.fbr.Dao.CustomerResponseValuesDao;
import com.fbr.Dao.Entities.*;
import com.fbr.Dao.ResponseAggregateDao;
import com.fbr.domain.AttributeAggregateInfo;
import com.fbr.domain.AttributeTuple;
import com.fbr.domain.BranchAggregateInfo;
import com.fbr.domain.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class ResponseService {
    @Autowired
    private CustomerResponseDao customerResponseDao;
    @Autowired
    private CustomerResponseValuesDao customerResponseValuesDao;
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private AlertService alertService;
    @Autowired
    private AttributeService attributeService;
    @Autowired
    private ResponseAggregateDao responseAggregateDao;

    private Timer timer;
    long AGGREGATE_TIME_INTERVAL = 24 * 3600 * 1000; // 24 hours


    @PostConstruct
    public void init() {
        /*
        timer = new Timer();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        UpdateAggregateTask task = new UpdateAggregateTask(responseAggregateDao);
        timer.schedule(task, cal.getTime(), AGGREGATE_TIME_INTERVAL);
        */
    }

    @Transactional
    public void processResponse(int companyId, int branchId, List<Response> responseList) {
        //processes teh list of responses from various customer for the given company and branch.
        List<AttributeDbType> attributeDbEntries = attributeService.getDbAttributesByCompany(companyId);
        for (Response response : responseList) {
            CustomerDbType customerDbEntry = addCustomerInfo(response.getEmail(), response.getPhone(), response.getName());

            String customerId;
            if (customerDbEntry != null) customerId = customerDbEntry.getCustomerId();
            else customerId = UUID.randomUUID().toString();

            addToResponseDb(companyId, branchId, customerId, response.getAttributeTuples());

            if (customerDbEntry != null)
                alertService.addToAlertDb(companyId, branchId, customerDbEntry, response.getAttributeTuples(), attributeDbEntries);

            addToAggregateDb(companyId, branchId, response.getAttributeTuples());
        }
    }

    private CustomerDbType addCustomerInfo(String mail, String phone, String name) {
        //creates a new customer entry; if already exists it returns the old one.
        if (mail == null) return null;

        CustomerDbType customerDbEntry = customerDao.getCustomerWithMail(mail);
        if (customerDbEntry != null) {
            return customerDbEntry;
        } else {
            customerDbEntry = new CustomerDbType();
            customerDbEntry.setMail(mail);
            customerDbEntry.setCustomerId(UUID.randomUUID().toString());
            customerDbEntry.setPhone(phone);
            customerDbEntry.setName(name);

            customerDao.add(customerDbEntry);
            return customerDbEntry;
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

    private void addToAggregateDb(int companyId, int branchId, List<AttributeTuple> attributeResponseTuples) {
        for (AttributeTuple attributeTuple : attributeResponseTuples) {
            ResponseAggregatePrimaryKey key = getResponseAggregatePrKey(companyId, branchId, attributeTuple);
            ResponseAggregateDbType responseAggregateDbEntry = responseAggregateDao.find(key);

            if (responseAggregateDbEntry == null) {
                responseAggregateDao.add(getResponseAggreagteDbEntry(companyId, branchId, attributeTuple));
            } else {
                updateResponseAggreagteDbEntry(responseAggregateDbEntry, attributeTuple);
                responseAggregateDao.update(responseAggregateDbEntry);
            }
        }
    }

    private ResponseAggregateDbType getResponseAggreagteDbEntry(int companyId, int branchId, AttributeTuple attributeTuple) {
        ResponseAggregatePrimaryKey key = getResponseAggregatePrKey(companyId, branchId, attributeTuple);
        ResponseAggregateDbType dbentry = new ResponseAggregateDbType();
        dbentry.setId(key);

        dbentry.setObtainedValue(attributeTuple.getObtainedValue());
        dbentry.setTotalValue(attributeTuple.getMaxValue());

        return dbentry;
    }

    private void updateResponseAggreagteDbEntry(ResponseAggregateDbType responseAggregateDbEntry, AttributeTuple attributeTuple) {
        responseAggregateDbEntry.setObtainedValue(responseAggregateDbEntry.getObtainedValue() + attributeTuple.getObtainedValue());
        responseAggregateDbEntry.setTotalValue(responseAggregateDbEntry.getTotalValue() + attributeTuple.getMaxValue());
    }

    private ResponseAggregatePrimaryKey getResponseAggregatePrKey(int companyId, int branchId, AttributeTuple attributeTuple) {
        ResponseAggregatePrimaryKey key = new ResponseAggregatePrimaryKey();
        key.setAttributeId(attributeTuple.getAttributeId());
        key.setBranchId(branchId);
        key.setCompanyId(companyId);
        key.setDate(FeedbackUtilities.dateFromCal(Calendar.getInstance()));
        return key;
    }

    public List<BranchAggregateInfo> getAggregateInfo(int companyId) {
        List<ResponseAggregateDbType> responseAggregateDbTypeList = responseAggregateDao.getAggregateInfo(companyId);
        Collections.sort(responseAggregateDbTypeList, aggregateComparator);

        List<BranchAggregateInfo> listBranchAggregateInfo = new ArrayList<BranchAggregateInfo>();
        int branchId = -1, i = 0;

        while (i < responseAggregateDbTypeList.size()) {
            ResponseAggregateDbType dbentry = responseAggregateDbTypeList.get(i);
            //create a new branch aggregate info
            branchId = dbentry.getId().getBranchId();
            int index = addBranchInfo(branchId, responseAggregateDbTypeList, i, listBranchAggregateInfo);
            i = index;
        }
        return listBranchAggregateInfo;
    }

    private int addBranchInfo(int branchId, List<ResponseAggregateDbType> responseAggregateDbTypeList, int index, List<BranchAggregateInfo> listBranchAggregateInfo) {
        BranchAggregateInfo branchInfo = new BranchAggregateInfo();
        List<AttributeAggregateInfo> listAttributeAggregateInfo = new ArrayList<AttributeAggregateInfo>();
        branchInfo.setListAttributeAggregateInfo(listAttributeAggregateInfo);
        branchInfo.setBranchId(branchId);
        listBranchAggregateInfo.add(branchInfo);


        int i = index;
        while (i < responseAggregateDbTypeList.size()) {
            ResponseAggregateDbType aggregateDbEntry = responseAggregateDbTypeList.get(i);
            if (aggregateDbEntry.getId().getBranchId() != branchId) {
                return i;
            }
            int index2 = addAttributeInfo(branchId, aggregateDbEntry.getId().getAttributeId(), responseAggregateDbTypeList, i, branchInfo);
            i = index2;
        }
        return i;
    }

    private int addAttributeInfo(int branchId, int attributeId,
                                 List<ResponseAggregateDbType> responseAggregateDbTypeList, int index, BranchAggregateInfo branchAggregateInfo) {
        AttributeAggregateInfo attributeInfo = new AttributeAggregateInfo();
        branchAggregateInfo.getListAttributeAggregateInfo().add(attributeInfo);
        attributeInfo.setAttributeId(attributeId);

        List<Integer> dates = new ArrayList<Integer>();
        List<Integer> obtainedValues = new ArrayList<Integer>();
        List<Integer> totalValues = new ArrayList<Integer>();

        attributeInfo.setDates(dates);
        attributeInfo.setObtainedValues(obtainedValues);
        attributeInfo.setTotalValues(totalValues);

        int i = index;
        while (i < responseAggregateDbTypeList.size()) {
            ResponseAggregateDbType aggregateDbEntry = responseAggregateDbTypeList.get(i);
            if (attributeId != (aggregateDbEntry.getId().getAttributeId()) || branchId != aggregateDbEntry.getId().getBranchId()) {
                return i;
            }

            dates.add(aggregateDbEntry.getId().getDate());
            obtainedValues.add(aggregateDbEntry.getObtainedValue());
            totalValues.add(aggregateDbEntry.getTotalValue());
            i++;
        }
        return i;
    }

    private static Comparator<ResponseAggregateDbType> aggregateComparator = new Comparator<ResponseAggregateDbType>() {
        @Override
        public int compare(ResponseAggregateDbType first, ResponseAggregateDbType second) {
            if (first.getId().getBranchId() < second.getId().getBranchId()) return -1;
            else if (first.getId().getBranchId() > second.getId().getBranchId()) return 1;
            else {
                return first.getId().getAttributeId() - (second.getId().getAttributeId());
            }
        }
    };
}
