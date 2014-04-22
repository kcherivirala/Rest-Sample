package com.fbr.Dao;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Entities.TrendDbType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("trendsDao")
public class TrendsDao extends ProjectDaoImpl<TrendDbType, String> {
    public TrendsDao() {
        this.entityClass = TrendDbType.class;
    }

    @Override
    @Transactional
    public void add(TrendDbType entity) {
        super.add(entity);
    }
}
