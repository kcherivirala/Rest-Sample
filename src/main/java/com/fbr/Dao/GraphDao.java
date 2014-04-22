package com.fbr.Dao;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Entities.GraphDbType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("graphsDao")
public class GraphDao extends ProjectDaoImpl<GraphDbType, String> {
    public GraphDao() {
        this.entityClass = GraphDbType.class;
    }

    @Override
    @Transactional
    public void add(GraphDbType entity) {
        super.add(entity);
    }
}
