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

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository("graphFilters™Dao")
public class GraphFiltersDao extends ProjectDaoImpl<GraphFiltersDbType, GraphFiltersPrimaryKey> {
    public GraphFiltersDao() {
        this.entityClass = GraphFiltersDbType.class;
    }

    public List<GraphFiltersDbType> getGraphFilters(List<String> graphIdList) {
        String hql = "select e from " + entityClass.getName() + " e where e.id.graphId in (?1)";
        TypedQuery<GraphFiltersDbType> query = entityManager.createQuery(hql, entityClass);
        query.setParameter(1, graphIdList);

        return query.getResultList();
    }

    public List<GraphFiltersDbType> getGraphFilters(String graphId) {
        String hql = "select e from " + entityClass.getName() + " e where e.id.graphId = ?1";
        TypedQuery<GraphFiltersDbType> query = entityManager.createQuery(hql, entityClass);
        query.setParameter(1, graphId);

        return query.getResultList();
    }

    @Transactional
    public void deleteGraphFilters(String graphId) {
        Query q = entityManager.createQuery("delete from " + entityClass.getName() + " a where a.id.graphId = ?1");
        q.setParameter(1, graphId);
        q.executeUpdate();
    }
}