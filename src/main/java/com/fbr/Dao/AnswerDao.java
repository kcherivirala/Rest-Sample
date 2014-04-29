package com.fbr.Dao;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Entities.AnswerDbType;
import com.fbr.Dao.Entities.AnswerPrimaryKey;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository("answerDao")
public class AnswerDao extends ProjectDaoImpl<AnswerDbType, AnswerPrimaryKey> {
    public AnswerDao() {
        this.entityClass = AnswerDbType.class;
    }

    public List<AnswerDbType> getAnswers(int companyId) {
        String hql = "select e from " + entityClass.getName() + " e where e.id.companyId = ?1";
        TypedQuery<AnswerDbType> query = entityManager.createQuery(hql, entityClass);
        query.setParameter(1, companyId);

        return query.getResultList();
    }

    public List<AnswerDbType> getAnswers(int companyId, int questionId) {
        String hql = "select e from " + entityClass.getName() + " e where e.id.companyId = ?1 and e.id.questionId = ?2";
        TypedQuery<AnswerDbType> query = entityManager.createQuery(hql, entityClass);
        query.setParameter(1, companyId);
        query.setParameter(2, questionId);

        return query.getResultList();
    }

    @Transactional
    public void deleteAnswersOfQuestion(int companyId, int questionId) {

    }
}
