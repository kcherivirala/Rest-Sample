package com.fbr.Dao.Response;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectDaoImpl;
import com.fbr.Dao.Response.Entities.CustomerResponseDbType;
import com.fbr.Dao.Response.Entities.CustomerResponseValuesDbType;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository("customerResponseDao")
public class CustomerResponseDao extends ProjectDaoImpl<CustomerResponseDbType, String> {
    public CustomerResponseDao() {
        this.entityClass = CustomerResponseDbType.class;
    }

    public List<CustomerResponseAndValues> getResponses(int companyId) {
        try {
            Query q = entityManager.createQuery("select a, b from CustomerResponseDbType  a, CustomerResponseValuesDbType b  where a.companyId = ?1 and a.responseId = b.id.responseId");
            q.setParameter(1, companyId);
            List<Object[]> listObject = q.getResultList();
            return processObjectList(listObject);
        } catch (Exception e) {
            return null;
        }
    }

    public List<CustomerResponseAndValues> getResponses(int companyId, Date timeStamp) {
        try {
            Query q = entityManager.createQuery("select a, b from CustomerResponseDbType  a, CustomerResponseValuesDbType b  where a.companyId = ?1 and a.timeStamp > ?2  a.responseId = b.id.responseId");
            q.setParameter(1, companyId);
            q.setParameter(2, timeStamp);
            List<Object[]> listObject = q.getResultList();
            return processObjectList(listObject);
        } catch (Exception e) {
            return null;
        }
    }

    public List<CustomerResponseAndValues> getResponses() {
        try {
            Query q = entityManager.createQuery("select a, b  from CustomerResponseDbType  a, CustomerResponseValuesDbType b  where a.responseId = b.id.responseId");
            List<Object[]> listObject = q.getResultList();
            return processObjectList(listObject);
        } catch (Exception e) {
            System.out.println("ERROR : " + e.getLocalizedMessage());
            return null;
        }
    }

    private List<CustomerResponseAndValues> processObjectList(List<Object[]> listObject) {
        List<CustomerResponseAndValues> outList = new ArrayList<CustomerResponseAndValues>();

        int i = 0;
        while (i < listObject.size()) {
            Object[] objects = listObject.get(i);
            CustomerResponseDbType responseDbEntry = (CustomerResponseDbType) objects[0];

            CustomerResponseAndValues response = new CustomerResponseAndValues();
            List<CustomerResponseValuesDbType> responseValues = new ArrayList<CustomerResponseValuesDbType>();
            response.response = responseDbEntry;
            response.responseValues = responseValues;

            outList.add(response);

            i = addCustomerResponseValues(i, responseDbEntry.getResponseId(), listObject, responseValues);
        }

        return outList;
    }

    private int addCustomerResponseValues(int startIndex, String responseId, List<Object[]> listObject,
                                          List<CustomerResponseValuesDbType> responseValues) {
        int i = startIndex;
        while (i < listObject.size() && ((CustomerResponseValuesDbType) listObject.get(i)[1]).getId().getResponseId().equals(responseId)) {
            responseValues.add(((CustomerResponseValuesDbType) listObject.get(i)[1]));
            i++;
        }
        return i;
    }

    public class CustomerResponseAndValues {
        CustomerResponseDbType response;
        List<CustomerResponseValuesDbType> responseValues;

        public CustomerResponseDbType getResponse() {
            return response;
        }

        public void setResponse(CustomerResponseDbType response) {
            this.response = response;
        }

        public List<CustomerResponseValuesDbType> getResponseValues() {
            return responseValues;
        }

        public void setResponseValues(List<CustomerResponseValuesDbType> responseValues) {
            this.responseValues = responseValues;
        }
    }
}