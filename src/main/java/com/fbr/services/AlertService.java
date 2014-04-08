package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.AlertDao;
import com.fbr.Dao.Entities.AlertDbType;
import com.fbr.Dao.Entities.AttributeDbType;
import com.fbr.Dao.Entities.CustomerDbType;
import com.fbr.domain.AttributeTuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AlertService {
    @Autowired
    private AlertDao alertDao;

    public void addToAlertDb(String companyId, int branchId, CustomerDbType customerDbEntry,
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

    private AlertDbType getAlertDbEntry(String companyId, int branchId, String customerId, String alertString, String info) {
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

    private void setAlertStringInRootAttribute(String attributeId, List<AttributeDbType> attributeDbEntries, List<String> attributeAlerts) {
        //sets the string of the attribute to the string of the root attribute.
        if (attributeId == null) return;

        int index = getIndex(attributeId, attributeDbEntries);
        AttributeDbType attributeDbEntry = attributeDbEntries.get(index);
        setStringInRoot(index, attributeDbEntry.getAttributeString(), attributeDbEntry.getParentId(), attributeDbEntries, attributeAlerts);
    }

    private void setStringInRoot(int index, String baseAttributeString, String parentId,
                                 List<AttributeDbType> attributeDbEntries, List<String> attributeAlerts) {
        if (parentId == null) {
            if (baseAttributeString.equals(attributeDbEntries.get(index).getAttributeString()))
                attributeAlerts.set(index, attributeAlerts.get(index) + " ");
            else
                attributeAlerts.set(index, attributeAlerts.get(index) + baseAttributeString);
            return;
        }
        int tempIndex = getIndex(parentId, attributeDbEntries);
        setStringInRoot(tempIndex, baseAttributeString, attributeDbEntries.get(tempIndex).getParentId(), attributeDbEntries, attributeAlerts);
    }

    private int getIndex(String attributeId, List<AttributeDbType> attributeDbEntries) {
        for (int i = 0; i < attributeDbEntries.size(); i++) {
            AttributeDbType attributeDbEntry = attributeDbEntries.get(i);
            if (attributeId.equals(attributeDbEntry.getAttributeId())) {
                return i;
            }
        }
        return -1;
    }
}
