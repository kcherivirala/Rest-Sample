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

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository("trendFiltersDao")
public class TrendFiltersDao extends ProjectDaoImpl<TrendFiltersDbType, TrendFiltersPrimaryKey> {
    public TrendFiltersDao() {
        this.entityClass = TrendFiltersDbType.class;
    }

    public List<TrendFiltersDbType> getTrendFilters(List<String> trendIdList) {
        String hql = "select e from " + entityClass.getName() + " e where e.id.trendId in (?1)";
        TypedQuery<TrendFiltersDbType> query = entityManager.createQuery(hql, entityClass);
        query.setParameter(1, trendIdList);

        return query.getResultList();
    }

    public List<TrendFiltersDbType> getTrendFilters(String trendId) {
        String hql = "select e from " + entityClass.getName() + " e where e.id.trendId = ?1";
        TypedQuery<TrendFiltersDbType> query = entityManager.createQuery(hql, entityClass);
        query.setParameter(1, trendId);

        return query.getResultList();
    }

    @Transactional
    public void deleteTrendFilters(String trendId) {
        Query q = entityManager.createQuery("delete from " + entityClass.getName() + " a where a.id.trendId = ?1");
        q.setParameter(1, trendId);
        q.executeUpdate();
    }
}