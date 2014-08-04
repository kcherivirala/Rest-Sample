package com.fbr.Dao.Attribute;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Attribute.Entities.AttributeValuesDbType;
import com.fbr.Dao.Attribute.Entities.AttributeValuesPrimaryKey;
import com.fbr.Dao.ProjectDaoImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository("attributeValuesDao")
public class AttributeValuesDao extends ProjectDaoImpl<AttributeValuesDbType, AttributeValuesPrimaryKey> {
    public AttributeValuesDao() {
        this.entityClass = AttributeValuesDbType.class;
    }

    public List<AttributeValuesDbType> getAttributeValuesByCompany(int companyId) {
        Query q = entityManager.createQuery("select distinct a from AttributeValuesDbType  a, AnswerAttributeDbType b  where b.id.companyId = ?1 and a.id.attributeId = b.id.attributeId", entityClass);
        q.setParameter(1, companyId);
        return q.getResultList();
    }

    public List<AttributeValuesDbType> getAttributeValues(int attributeId) {
        Query q = entityManager.createQuery("select distinct a from AttributeValuesDbType a  where a.id.attributeId= ?1 ", entityClass);
        q.setParameter(1, attributeId);
        return q.getResultList();
    }

    public void deleteAttributeValues(int attributeId) {
        Query q = entityManager.createQuery("delete from " + entityClass.getName() + " a where a.id.attributeId = ?1");
        q.setParameter(1, attributeId);
        q.executeUpdate();
    }
}