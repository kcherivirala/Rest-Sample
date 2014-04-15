package com.fbr.Dao;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Entities.ResponseAggregateDbType;
import com.fbr.Dao.Entities.ResponseAggregatePrimaryKey;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository("responseAggregateDao")
public class ResponseAggregateDao  extends ProjectDaoImpl<ResponseAggregateDbType, ResponseAggregatePrimaryKey> {
    public ResponseAggregateDao() {
        this.entityClass = ResponseAggregateDbType.class;
    }

    @Override
    @Transactional
    public void add(ResponseAggregateDbType entity) {
        super.add(entity);
    }

    public List<ResponseAggregateDbType> getAggregateInfo(int companyId){
        String hql = "select e from " + entityClass.getName() + " e where e.id.companyId = ?1" ;
        TypedQuery<ResponseAggregateDbType> query = entityManager.createQuery(hql, entityClass);
        query.setParameter(1, companyId);
        return query.getResultList();
    }

    @Transactional
    public void deleteOldEntries(int date){
        String hql = "delete from " + entityClass.getName() + " e where e.id.date < ?1" ;
        javax.persistence.Query query = entityManager.createQuery(hql);
        query.setParameter(1, date);

        query.executeUpdate();

    }
}