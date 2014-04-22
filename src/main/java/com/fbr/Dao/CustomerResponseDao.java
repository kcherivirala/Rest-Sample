package com.fbr.Dao;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Entities.CustomerResponseDbType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("customerResponseDao")
public class CustomerResponseDao extends ProjectDaoImpl<CustomerResponseDbType, String> {
    public CustomerResponseDao() {
        this.entityClass = CustomerResponseDbType.class;
    }

    @Override
    @Transactional
    public void add(CustomerResponseDbType entity) {
        super.add(entity);
    }
}