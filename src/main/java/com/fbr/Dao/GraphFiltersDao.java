package com.fbr.Dao;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Entities.GraphFiltersDbType;
import com.fbr.Dao.Entities.GraphFiltersPrimaryKey;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("graphFilters™Dao")
public class GraphFiltersDao extends ProjectDaoImpl<GraphFiltersDbType, GraphFiltersPrimaryKey> {
    public GraphFiltersDao() {
        this.entityClass = GraphFiltersDbType.class;
    }

    @Override
    @Transactional
    public void add(GraphFiltersDbType entity) {
        super.add(entity);
    }
}