package com.fbr.Dao.Question;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectDaoImpl;
import com.fbr.Dao.Question.Entities.AnswerGroupDbType;
import com.fbr.Dao.Question.Entities.AnswerGroupPrimaryKey;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository("answerGroupDao")
public class AnswerGroupDao extends ProjectDaoImpl<AnswerGroupDbType, AnswerGroupPrimaryKey> {
    public AnswerGroupDao() {
        this.entityClass = AnswerGroupDbType.class;
    }

    public void delete(int companyId, int questionId) {
        Query q = entityManager.createQuery("delete from " + entityClass.getName() + " a where a.id.companyId = ?1 and a.id.questionId = ?2");
        q.setParameter(1, companyId);
        q.setParameter(2, questionId);
        q.executeUpdate();
    }

    public int getMaxQuestionIdValue(int companyId, int questionId) {
        Query q = entityManager.createQuery("select max(e.id.answerGroupId) from " + entityClass.getName() + " e where e.id.companyId = ?1 and e.id.questionId = ?2");
        q.setParameter(1, companyId);
        q.setParameter(2, questionId);
        List x = q.getResultList();
        if (x.get(0) != null) {
            return ((Number) x.get(0)).intValue();
        }
        return 0;
    }
}
