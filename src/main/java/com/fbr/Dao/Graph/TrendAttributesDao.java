package com.fbr.Dao.Graph;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Graph.Entities.TrendAttributesDbType;
import com.fbr.Dao.Graph.Entities.TrendAttributesPrimaryKey;
import com.fbr.Dao.ProjectDaoImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository("trendAttributesDao")
public class TrendAttributesDao extends ProjectDaoImpl<TrendAttributesDbType, TrendAttributesPrimaryKey> {
    public TrendAttributesDao() {
        this.entityClass = TrendAttributesDbType.class;
    }

    public List<TrendAttributesDbType> getTrendAttributes(List<String> trendIdList) {
        String hql = "select e from " + entityClass.getName() + " e where e.id.trendId in (?1)";
        TypedQuery<TrendAttributesDbType> query = entityManager.createQuery(hql, entityClass);
        query.setParameter(1, trendIdList);

        return query.getResultList();
    }

    public List<TrendAttributesDbType> getTrendAttributes(String trendId) {
        String hql = "select e from " + entityClass.getName() + " e where e.id.trendId = ?1";
        TypedQuery<TrendAttributesDbType> query = entityManager.createQuery(hql, entityClass);
        query.setParameter(1, trendId);

        return query.getResultList();
    }

    @Transactional
    public void deleteTrendAttributes(String trendId) {
        Query q = entityManager.createQuery("delete from " + entityClass.getName() + " a where a.id.trendId = ?1");
        q.setParameter(1, trendId);
        q.executeUpdate();
    }
}
