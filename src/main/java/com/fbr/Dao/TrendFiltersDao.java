package com.fbr.Dao;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Entities.TrendFiltersDbType;
import com.fbr.Dao.Entities.TrendFiltersPrimaryKey;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("trendFiltersDao")
public class TrendFiltersDao extends ProjectDaoImpl<TrendFiltersDbType, TrendFiltersPrimaryKey> {
    public TrendFiltersDao() {
        this.entityClass = TrendFiltersDbType.class;
    }
}