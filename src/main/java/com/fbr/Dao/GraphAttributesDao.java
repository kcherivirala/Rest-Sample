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

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository("graphAttributesDao")
public class GraphAttributesDao extends ProjectDaoImpl<GraphAttributesDbType, GraphAttributesPrimaryKey> {
    public GraphAttributesDao() {
        this.entityClass = GraphAttributesDbType.class;
    }

    public List<GraphAttributesDbType> getGraphAttributes(List<String> graphIdList) {
        String hql = "select e from " + entityClass.getName() + " e where e.id.graphId in (?1)";
        TypedQuery<GraphAttributesDbType> query = entityManager.createQuery(hql, entityClass);
        query.setParameter(1, graphIdList);

        return query.getResultList();
    }

    public List<GraphAttributesDbType> getGraphAttributes(String graphId) {
        String hql = "select e from " + entityClass.getName() + " e where e.id.graphId = ?1";
        TypedQuery<GraphAttributesDbType> query = entityManager.createQuery(hql, entityClass);
        query.setParameter(1, graphId);

        return query.getResultList();
    }

    @Transactional
    public void deleteGraphAttributes(String graphId) {
        Query q = entityManager.createQuery("delete from " + entityClass.getName() + " a where a.id.graphId = ?1");
        q.setParameter(1, graphId);
        q.executeUpdate();
    }
}