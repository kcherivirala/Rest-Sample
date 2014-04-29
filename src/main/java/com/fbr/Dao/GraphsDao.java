package com.fbr.Dao;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Entities.GraphDbType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

@Repository("graphsDao")
public class GraphsDao extends ProjectDaoImpl<GraphDbType, String> {
    public GraphsDao() {
        this.entityClass = GraphDbType.class;
    }
}
