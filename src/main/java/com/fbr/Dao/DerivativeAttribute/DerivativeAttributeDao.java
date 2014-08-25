package com.fbr.Dao.DerivativeAttribute;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.DerivativeAttribute.Entities.DerivativeAttributeDbType;
import com.fbr.Dao.DerivativeAttribute.Entities.DerivativeAttributePrimaryKey;
import com.fbr.Dao.ProjectDaoImpl;

import javax.persistence.Query;
import java.util.List;

public class DerivativeAttributeDao extends ProjectDaoImpl<DerivativeAttributeDbType, DerivativeAttributePrimaryKey> {
    public DerivativeAttributeDao() {
        this.entityClass = DerivativeAttributeDbType.class;
    }

    public List<DerivativeAttributeDbType> getAttributesByCompany(int companyId) {
        Query q = entityManager.createQuery("select distinct a from " + entityClass.getName() + " a where a.id.companyId = ?1", entityClass);
        q.setParameter(1, companyId);
        return q.getResultList();
    }
}