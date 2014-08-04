package com.fbr.Dao.Company;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Company.Entities.CompanyDbType;
import com.fbr.Dao.ProjectDaoImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository("companyDao")
public class CompanyDao extends ProjectDaoImpl<CompanyDbType, Integer> {
    public CompanyDao() {
        this.entityClass = CompanyDbType.class;
    }

    public int getMaxCompanyIdValue() {
        Query q = entityManager.createQuery("select max(e.companyId) from " + entityClass.getName() + " e");
        List x = q.getResultList();
        if (x.get(0) != null) {
            return ((Number) x.get(0)).intValue();
        }
        return 0;
    }
}
