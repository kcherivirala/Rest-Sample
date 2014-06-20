package com.fbr.Dao.Customer;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Customer.Entities.CustomerDbType;
import com.fbr.Dao.ProjectDaoImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository("customerDao")
public class CustomerDao extends ProjectDaoImpl<CustomerDbType, String> {
    public CustomerDao() {
        this.entityClass = CustomerDbType.class;
    }

    public CustomerDbType getCustomerWithMail(String mail) {
        String hql = "select e from " + entityClass.getName() + " e where e.mail = ?1";
        TypedQuery<CustomerDbType> query = entityManager.createQuery(hql, entityClass);
        query.setParameter(1, mail);
        List<CustomerDbType> list = query.getResultList();
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
