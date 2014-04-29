package com.fbr.Dao;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */


import com.fbr.Dao.Entities.GraphAttributesDbType;
import com.fbr.Dao.Entities.GraphAttributesPrimaryKey;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("graphAttributesDao")
public class GraphAttributesDao extends ProjectDaoImpl<GraphAttributesDbType, GraphAttributesPrimaryKey> {
    public GraphAttributesDao() {
        this.entityClass = GraphAttributesDbType.class;
    }
}