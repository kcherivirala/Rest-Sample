package com.fbr.Dao;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Entities.AlertDbType;
import org.springframework.stereotype.Repository;

@Repository("alertDao")
public class AlertDao extends ProjectDaoImpl<AlertDbType, String> {
    public AlertDao() {
        this.entityClass = AlertDbType.class;
    }
}

