package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Attribute.Entities.AttributeDbType;
import com.fbr.Dao.CustomerDbType;
import com.fbr.Dao.Response.AlertDao;
import com.fbr.Dao.Response.Entities.AlertDbType;
import com.fbr.domain.Response.AttributeTuple;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AlertService {
    private static final Logger logger = Logger.getLogger(AlertService.class);
    @Autowired
    private AlertDao alertDao;

    public void addToAlertDb(int companyId, int branchId, CustomerDbType customerDbEntry,
                             List<AttributeTuple> attributeResponseTuples, List<AttributeDbType> attributeDbEntries) {
        /*
        adds the response of the customer in the alerts Db for the whom the feedback is really low;
        so that the company can take a corrective action.
         */
        String info = getCustomerInfo(customerDbEntry);
        String alertString = getAlertString(attributeResponseTuples, attributeDbEntries);
        if (!alertString.equals(""))
            alertDao.add(getAlertDbEntry(companyId, branchId, customerDbEntry.getCustomerId(), alertString, info));
    }

    private AlertDbType getAlertDbEntry(int companyId, int branchId, String customerId, String alertString, String info) {
        AlertDbType alertDbEntry = new AlertDbType();

        alertDbEntry.setAlertId(UUID.randomUUID().toString());
        alertDbEntry.setCompanyId(companyId);
        alertDbEntry.setBranchId(branchId);
        alertDbEntry.setTimestamp(new Date());

        alertDbEntry.setAlertString(alertString);
        alertDbEntry.setInfo(info);
        return alertDbEntry;
    }

    private String getCustomerInfo(CustomerDbType customerDbEntry) {
        String info = customerDbEntry.getMail();
        if (customerDbEntry.getPhone() != null) info += " , " + customerDbEntry.getPhone();
        if (customerDbEntry.getName() != null) info += " , " + customerDbEntry.getName();
        return info;
    }

    private String getAlertString(List<AttributeTuple> attributeResponseTuples, List<AttributeDbType> attributeDbEntries) {
        List<String> attributeAlerts = new ArrayList<String>(attributeDbEntries.size());
        for (int i = 0; i < attributeDbEntries.size(); i++) attributeAlerts.add("");

        for (AttributeTuple attributeTuple : attributeResponseTuples) {
            if (attributeTuple.getObtainedValue() <= 1 && attributeTuple.getMaxValue() != 0)
                setAlertStringInRootAttribute(attributeTuple.getAttributeId(), attributeDbEntries, attributeAlerts);
        }

        String alert = "";
        for (int i = 0; i < attributeAlerts.size(); i++) {
            String attributeAlert = attributeAlerts.get(i);
            if (!attributeAlert.equals("")) {
                alert += "(" + attributeDbEntries.get(i).getAttributeString() + "{" + attributeAlert + "})";
            }
        }
        return alert;
    }

    private void setAlertStringInRootAttribute(int attributeId, List<AttributeDbType> attributeDbEntries, List<String> attributeAlerts) {
        //sets the string of the attribute to the string of the root attribute.
        if (attributeId == -1) return;

        int index = getIndex(attributeId, attributeDbEntries);
        AttributeDbType attributeDbEntry = attributeDbEntries.get(index);
        setStringInRoot(index, attributeDbEntry.getAttributeString(), attributeDbEntry.getParentId(), attributeDbEntries, attributeAlerts);
    }

    private void setStringInRoot(int index, String baseAttributeString, int parentId,
                                 List<AttributeDbType> attributeDbEntries, List<String> attributeAlerts) {
        if (parentId == -1) {
            if (baseAttributeString.equals(attributeDbEntries.get(index).getAttributeString()))
                attributeAlerts.set(index, attributeAlerts.get(index) + " ");
            else
                attributeAlerts.set(index, attributeAlerts.get(index) + baseAttributeString);
            return;
        }
        int tempIndex = getIndex(parentId, attributeDbEntries);
        setStringInRoot(tempIndex, baseAttributeString, attributeDbEntries.get(tempIndex).getParentId(), attributeDbEntries, attributeAlerts);
    }

    private int getIndex(int attributeId, List<AttributeDbType> attributeDbEntries) {
        for (int i = 0; i < attributeDbEntries.size(); i++) {
            AttributeDbType attributeDbEntry = attributeDbEntries.get(i);
            if (attributeId == attributeDbEntry.getAttributeId()) {
                return i;
            }
        }
        return -1;
    }
}
