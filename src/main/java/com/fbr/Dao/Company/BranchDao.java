package com.fbr.Dao.Company;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Company.Entities.BranchDbType;
import com.fbr.Dao.Company.Entities.BranchPrimaryKey;
import com.fbr.Dao.ProjectDaoImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository("branchDao")
public class BranchDao extends ProjectDaoImpl<BranchDbType, BranchPrimaryKey> {
    public BranchDao() {
        this.entityClass = BranchDbType.class;
    }

    public int getMaxBranchIdValue(int companyId) {
        Query q = entityManager.createQuery("select max(e.id.branchId) from " + entityClass.getName() + " e where e.id.companyId = ?1");
        q.setParameter(1, companyId);
        List x = q.getResultList();
        if (x.get(0) != null) {
            return ((Number) x.get(0)).intValue();
        }
        return -1;
    }

    public List<BranchDbType> getBranchesByCompany(int companyId) {
        Query q = entityManager.createQuery("select a from " + entityClass.getName() + " a where a.id.companyId = ?1", entityClass);
        q.setParameter(1, companyId);
        return q.getResultList();
    }

    public void deleteBranches(int companyId) {
        Query q = entityManager.createQuery("delete from " + entityClass.getName() + " a where a.id.companyId = ?1");
        q.setParameter(1, companyId);
        q.executeUpdate();
    }
}
