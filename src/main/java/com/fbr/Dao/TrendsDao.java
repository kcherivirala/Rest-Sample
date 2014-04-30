package com.fbr.Dao;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Entities.TrendDbType;
import org.springframework.stereotype.Repository;

@Repository("trendsDao")
public class TrendsDao extends ProjectDaoImpl<TrendDbType, String> {
    public TrendsDao() {
        this.entityClass = TrendDbType.class;
    }
}
