package com.fbr.Dao;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Entities.QuestionDbType;
import com.fbr.Dao.Entities.QuestionPrimaryKey;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository("questionDao")
public class QuestionDao extends ProjectDaoImpl<QuestionDbType, QuestionPrimaryKey> {
    public QuestionDao() {
        this.entityClass = QuestionDbType.class;
    }

    @Override
    @Transactional
    public void add(QuestionDbType entity) {
        super.add(entity);
    }

    public List<QuestionDbType> getQuestions(int companyId){
        String hql = "select e from " + entityClass.getName() + " e where e.id.companyId = ?1";
        TypedQuery<QuestionDbType> query = entityManager.createQuery(hql, entityClass);
        query.setParameter(1, companyId);

        return query.getResultList();
    }

    public List<QuestionDbType> getQuestions(int companyId, int questionId){
        String hql = "select e from " + entityClass.getName() + " e where e.id.companyId = ?1 and e.id.questionId = ?2";
        TypedQuery<QuestionDbType> query = entityManager.createQuery(hql, entityClass);
        query.setParameter(1, companyId);
        query.setParameter(2, questionId);

        return query.getResultList();
    }
}

