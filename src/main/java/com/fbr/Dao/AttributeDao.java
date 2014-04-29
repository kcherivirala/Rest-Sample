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
public class AttributeDao extends ProjectDaoImpl<AttributeDbType, Integer> {
    public AttributeDao() {
        this.entityClass = AttributeDbType.class;
    }

    @Override
    @Transactional
    public AttributeDbType update(AttributeDbType entity) {
        return super.update(entity);
    }

    @Transactional
    public void delete(Integer attrId) {
        AttributeDbType entity = find(attrId);
        super.delete(entity);
    }

    public List<AttributeDbType> getAttributesByCompany(int companyId) {
        Query q = entityManager.createQuery("select distinct a from AttributeDbType  a, AnswerDbType b  where b.id.companyId = ?1 and a.attributeId = b.attributeId", entityClass);
        q.setParameter(1, companyId);
        return q.getResultList();
    }

    public int getMaxAttributeIdValue() {
        Query q = entityManager.createQuery("select max(e.attributeId) from " + entityClass.getName() + " e");
        List x = q.getResultList();
        if (x.get(0) != null) {
            return ((Number) x.get(0)).intValue();
        }
        return -1;
    }

}