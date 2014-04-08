package com.fbr.Dao;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Entities.AttributeDbType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

@Repository("attributeDao")
public class AttributeDao extends ProjectDaoImpl<AttributeDbType, String> {
    public AttributeDao() {
        this.entityClass = AttributeDbType.class;
    }

    @Override
    @Transactional
    public void add(AttributeDbType entity) {
        super.add(entity);
    }

    @Override
    @Transactional
    public AttributeDbType update(AttributeDbType entity) {
        return super.update(entity);
    }

    @Transactional
    public void delete(String attrId){
        AttributeDbType entity = find(attrId);
        super.delete(entity);
    }

    public List<AttributeDbType> getAttributesByCompany(String companyId){
        Query q =  entityManager.createQuery("select distinct a from AttributeDbType  a, AnswerDbType b  where b.id.companyId = ?1 and a.attributeId = b.attributeId", entityClass);
        q.setParameter(1, companyId);
        return  q.getResultList();
    }

}