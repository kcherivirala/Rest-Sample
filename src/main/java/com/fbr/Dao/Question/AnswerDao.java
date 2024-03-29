package com.fbr.Dao.Question;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectDaoImpl;
import com.fbr.Dao.Question.Entities.AnswerDbType;
import com.fbr.Dao.Question.Entities.AnswerPrimaryKey;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository("answerDao")
public class AnswerDao extends ProjectDaoImpl<AnswerDbType, AnswerPrimaryKey> {
    public AnswerDao() {
        this.entityClass = AnswerDbType.class;
    }

    public void delete(int companyId, int questionId) {
        Query q = entityManager.createQuery("delete from " + entityClass.getName() + " a where a.id.companyId = ?1 and a.id.questionId = ?2");
        q.setParameter(1, companyId);
        q.setParameter(2, questionId);
        q.executeUpdate();
    }

    public void delete(int companyId, int questionId, int answerGroupId) {
        Query q = entityManager.createQuery("delete from " + entityClass.getName() + " a where a.id.companyId = ?1 and a.id.questionId = ?2 and a.id.answerGroupId = ?3");
        q.setParameter(1, companyId);
        q.setParameter(2, questionId);
        q.setParameter(3, answerGroupId);
        q.executeUpdate();
    }

    public int getMaxQuestionIdValue(int companyId, int questionId, int answerGroupId) {
        Query q = entityManager.createQuery("select max(e.id.answerId) from " + entityClass.getName() + " e where e.id.companyId = ?1 and e.id.questionId = ?2 and e.id.answerGroupId = ?3");
        q.setParameter(1, companyId);
        q.setParameter(2, questionId);
        q.setParameter(3, answerGroupId);
        List x = q.getResultList();
        if (x.get(0) != null) {
            return ((Number) x.get(0)).intValue();
        }
        return 0;
    }
}
/*

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
 */
