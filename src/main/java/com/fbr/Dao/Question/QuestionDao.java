package com.fbr.Dao.Question;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectDaoImpl;
import com.fbr.Dao.Question.Entities.QuestionDbType;
import com.fbr.Dao.Question.Entities.QuestionPrimaryKey;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository("questionDao")
public class QuestionDao extends ProjectDaoImpl<QuestionDbType, QuestionPrimaryKey> {
    public QuestionDao() {
        this.entityClass = QuestionDbType.class;
    }

    public List<QuestionDbType> getQuestions(int companyId) {
        String hql = "select e from " + entityClass.getName() + " e where e.id.companyId = ?1";
        TypedQuery<QuestionDbType> query = entityManager.createQuery(hql, entityClass);
        query.setParameter(1, companyId);

        return query.getResultList();
    }

    public List<QuestionDbType> getQuestions(int companyId, int questionId) {
        String hql = "select e from " + entityClass.getName() + " e where e.id.companyId = ?1 and e.id.questionId = ?2";
        TypedQuery<QuestionDbType> query = entityManager.createQuery(hql, entityClass);
        query.setParameter(1, companyId);
        query.setParameter(2, questionId);

        return query.getResultList();
    }

    public int getMaxQuestionIdValue(int companyId) {
        Query q = entityManager.createQuery("select max(e.id.questionId) from " + entityClass.getName() + " e where e.id.companyId = ?1");
        q.setParameter(1, companyId);
        List x = q.getResultList();
        if (x.get(0) != null) {
            return ((Number) x.get(0)).intValue();
        }
        return -1;
    }
}

