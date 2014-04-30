package com.fbr.Dao;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Entities.GraphDbType;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository("graphsDao")
public class GraphsDao extends ProjectDaoImpl<GraphDbType, String> {
    public GraphsDao() {
        this.entityClass = GraphDbType.class;
    }

    public List<GraphDbType> getGraphs(int companyId) {
        String hql = "select e from " + entityClass.getName() + " e where e.companyId = ?1";
        TypedQuery<GraphDbType> query = entityManager.createQuery(hql, entityClass);
        query.setParameter(1, companyId);

        return query.getResultList();
    }
}
