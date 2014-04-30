package com.fbr.Dao;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Entities.CustomerResponseValuesDbType;
import com.fbr.Dao.Entities.CustomerResponseValuesPrimaryKey;
import org.springframework.stereotype.Repository;

@Repository("customerResponseValuesDao")
public class CustomerResponseValuesDao extends ProjectDaoImpl<CustomerResponseValuesDbType, CustomerResponseValuesPrimaryKey> {
    public CustomerResponseValuesDao() {
        this.entityClass = CustomerResponseValuesDbType.class;
    }
}