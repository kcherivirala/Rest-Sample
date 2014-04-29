package com.fbr.Dao;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Entities.CustomerResponseDbType;
import com.fbr.Dao.Entities.CustomerResponseValuesDbType;
import com.fbr.domain.AttributeTuple;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository("customerResponseDao")
public class CustomerResponseDao extends ProjectDaoImpl<CustomerResponseDbType, String> {
    public CustomerResponseDao() {
        this.entityClass = CustomerResponseDbType.class;
    }

    public List<CustomerResponse> getResponses(int companyId) {
        Query q = entityManager.createQuery("select a.responseId, a.customerId, a.companyId, a.branchId, b.id.attributeId, b.maxValue, b.obtainedValue, b.response " +
                "from CustomerResponseDbType  a, CustomerResponseValuesDbType b  where a.companyId = ?1 and a.responseId = b.id.responseId", entityClass);
        q.setParameter(1, companyId);
        List<Object[]> listObject =  q.getResultList();
        return processObjectList(listObject);
    }
    public List<CustomerResponse> getResponses() {
        try{
        Query q = entityManager.createQuery("select a, b  from CustomerResponseDbType  a, CustomerResponseValuesDbType b  where a.responseId = b.id.responseId");
        List<Object[]> listObject =  q.getResultList();
        return processObjectList(listObject);
        }catch(Exception e){
            System.out.println("ERROR : " + e.getLocalizedMessage());
            return null;
        }
    }

    private List<CustomerResponse> processObjectList(List<Object[]> listObject){
        List<CustomerResponse> outList = new ArrayList<CustomerResponse>();
        return outList;
    }

    public class CustomerResponse {
        CustomerResponseDbType response;
        List<CustomerResponseValuesDbType> responseValues;
    }
}