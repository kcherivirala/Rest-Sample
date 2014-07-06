package com.fbr.Dao.Offers_Info;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Offers_Info.Entities.OfferInfoDbType;
import com.fbr.Dao.Offers_Info.Entities.OfferInfoPrimaryKey;
import com.fbr.Dao.ProjectDaoImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository("offerInfoDao")
public class OfferInfoDao extends ProjectDaoImpl<OfferInfoDbType, OfferInfoPrimaryKey> {
    public OfferInfoDao() {
        this.entityClass = OfferInfoDbType.class;
    }

    public int getMaxIdValue(int companyId) {
        Query q = entityManager.createQuery("select max(e.id.offerId) from " + entityClass.getName() + " e where e.id.companyId = ?1");
        q.setParameter(1, companyId);

        List x = q.getResultList();
        if (x.get(0) != null) {
            return ((Number) x.get(0)).intValue();
        }
        return -1;
    }

    public List<OfferInfoDbType> getOffersAndInfo(int companyId) {
        Query q = entityManager.createQuery("select e from " + entityClass.getName() + " e where e.id.companyId = ?1", entityClass);
        q.setParameter(1, companyId);
        return q.getResultList();
    }

    public List<OfferInfoDbType> getOffersAndInfo(int companyId, int branchId) {
        Query q = entityManager.createQuery("select e from " + entityClass.getName() + " e where e.id.companyId = ?1 and e.branchId = ?2", entityClass);
        q.setParameter(1, companyId);
        q.setParameter(2, branchId);
        return q.getResultList();
    }
}