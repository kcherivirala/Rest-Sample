package com.fbr.Dao;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Entities.AttributeValuesDbType;
import com.fbr.Dao.Entities.AttributeValuesPrimaryKey;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

@Repository("attributeValuesDao")
public class AttributeValuesDao extends ProjectDaoImpl<AttributeValuesDbType, AttributeValuesPrimaryKey> {
    public AttributeValuesDao() {
        this.entityClass = AttributeValuesDbType.class;
    }

    @Override
    @Transactional
    public void add(AttributeValuesDbType entity) {
        super.add(entity);
    }

    public List<AttributeValuesDbType>  getAttributeValuesByCompany(int companyId){
        Query q = entityManager.createQuery("select distinct a from AttributeValuesDbType  a, AnswerDbType b  where b.id.companyId = ?1 and a.id.attributeId = b.attributeId", entityClass);
        q.setParameter(1, companyId);
        return q.getResultList();
    }

    public List<AttributeValuesDbType>  getAttributeValues(int attributeId){
        Query q = entityManager.createQuery("select distinct a from AttributeValuesDbType a  where a.id.attributeId= ?1 ", entityClass);
        q.setParameter(1, attributeId);
        return q.getResultList();
    }

    @Transactional
    public void deleteAttributeValues(int attributeId){
        Query q = entityManager.createQuery("delete from " + entityClass.getName() + " a where a.id.attributId = ?1");
        q.setParameter(1, attributeId);
        q.executeUpdate();
    }
}