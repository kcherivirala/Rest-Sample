package com.fbr.Dao;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Entities.AlertDbType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("alertDao")
public class AlertDao extends ProjectDaoImpl<AlertDbType, String> {
    public AlertDao() {
        this.entityClass = AlertDbType.class;
    }

    @Override
    @Transactional
    public void add(AlertDbType entity) {
        super.add(entity);
    }
}

