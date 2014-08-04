package com.fbr.Dao.Question;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectDaoImpl;
import com.fbr.Dao.Question.Entities.AnswerAttributeDbType;
import com.fbr.Dao.Question.Entities.AnswerAttributePrimaryKey;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository("answerAttributeDao")
public class AnswerAttributeDao extends ProjectDaoImpl<AnswerAttributeDbType, AnswerAttributePrimaryKey> {
    public AnswerAttributeDao() {
        this.entityClass = AnswerAttributeDbType.class;
    }

    public List<AnswerAttributeDbType> getAnswerAttributes(int companyId) {
        String hql = "select e from " + entityClass.getName() + " e where e.id.companyId = ?1";
        TypedQuery<AnswerAttributeDbType> query = entityManager.createQuery(hql, entityClass);
        query.setParameter(1, companyId);

        return query.getResultList();
    }

    public List<AnswerAttributeDbType> getAnswerAttributes(int companyId, int questionId) {
        String hql = "select e from " + entityClass.getName() + " e where e.id.companyId = ?1 and e.id.questionId = ?2";
        TypedQuery<AnswerAttributeDbType> query = entityManager.createQuery(hql, entityClass);
        query.setParameter(1, companyId);
        query.setParameter(2, questionId);

        return query.getResultList();
    }

    public List<AnswerAttributeDbType> getAnswerAttributes(int companyId, int questionId, int answerId) {
        String hql = "select e from " + entityClass.getName() + " e where e.id.companyId = ?1 and e.id.questionId = ?2 and e.id.answerId = ?3";
        TypedQuery<AnswerAttributeDbType> query = entityManager.createQuery(hql, entityClass);
        query.setParameter(1, companyId);
        query.setParameter(2, questionId);
        query.setParameter(3, answerId);

        return query.getResultList();
    }


    public void deleteAnswerAttributesOfQuestion(int companyId, int questionId) {
        Query q = entityManager.createQuery("delete from " + entityClass.getName() + " a where a.id.companyId = ?1 and a.id.questionId = ?2");
        q.setParameter(1, companyId);
        q.setParameter(2, questionId);

        q.executeUpdate();
    }

    public void deleteAnswerAttributesOfQuestion(int companyId, int questionId, int answerId) {
        Query q = entityManager.createQuery("delete from " + entityClass.getName() + " a where a.id.companyId = ?1 and a.id.questionId = ?2 and a.id.answerId = ?3");
        q.setParameter(1, companyId);
        q.setParameter(2, questionId);
        q.setParameter(3, answerId);

        q.executeUpdate();
    }
}
