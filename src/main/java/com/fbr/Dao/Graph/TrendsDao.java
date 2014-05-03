package com.fbr.Dao.Graph;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Graph.Entities.TrendDbType;
import com.fbr.Dao.ProjectDaoImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository("trendsDao")
public class TrendsDao extends ProjectDaoImpl<TrendDbType, String> {
    public TrendsDao() {
        this.entityClass = TrendDbType.class;
    }

    public List<TrendDbType> getTrends(int companyId) {
        String hql = "select e from " + entityClass.getName() + " e where e.companyId = ?1";
        TypedQuery<TrendDbType> query = entityManager.createQuery(hql, entityClass);
        query.setParameter(1, companyId);

        return query.getResultList();
    }
}
