package com.fbr.Dao;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Entities.TrendAttributesDbType;
import com.fbr.Dao.Entities.TrendAttributesPrimaryKey;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("trendAttributesDao")
public class TrendAttributesDao extends ProjectDaoImpl<TrendAttributesDbType, TrendAttributesPrimaryKey> {
    public TrendAttributesDao() {
        this.entityClass = TrendAttributesDbType.class;
    }
}
