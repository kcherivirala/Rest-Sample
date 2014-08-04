package com.fbr.Dao.Attribute;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Attribute.Entities.AttributeDbType;
import com.fbr.Dao.ProjectDaoImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

@Repository("attributeDao")
public class AttributeDao extends ProjectDaoImpl<AttributeDbType, Integer> {
    public AttributeDao() {
        this.entityClass = AttributeDbType.class;
    }

    @Transactional
    public void delete(Integer attrId) {
        AttributeDbType entity = find(attrId);
        super.delete(entity);
    }

    public List<AttributeDbType> getAttributesByCompany(int companyId) {
        Query q = entityManager.createQuery("select distinct a from AttributeDbType  a, AnswerAttributeDbType b  where b.id.companyId = ?1 and a.attributeId = b.id.attributeId", entityClass);
        q.setParameter(1, companyId);
        return q.getResultList();
    }

    public int getMaxAttributeIdValue() {
        Query q = entityManager.createQuery("select max(e.attributeId) from " + entityClass.getName() + " e");
        List x = q.getResultList();
        if (x.get(0) != null) {
            return ((Number) x.get(0)).intValue();
        }
        return 0;
    }

}