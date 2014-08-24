package com.fbr.Dao.Question;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectDaoImpl;
import com.fbr.Dao.Question.Entities.AnswerAttributeDbType;
import com.fbr.Dao.Question.Entities.AnswerAttributePrimaryKey;
import com.fbr.domain.Attribute.Attribute;
import com.fbr.domain.Attribute.AttributeValue;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Repository("answerAttributeDao")
public class AnswerAttributeDao extends ProjectDaoImpl<AnswerAttributeDbType, AnswerAttributePrimaryKey> {
    public AnswerAttributeDao() {
        this.entityClass = AnswerAttributeDbType.class;
    }

    public List<Attribute> getCompanyAttributesAndValues(int companyId) {
        String hql = "select distinct e.id.attributeId, e.value, e.maxValue from " + entityClass.getName() + " e where e.id.companyId = ?1 order by e.id.attributeId";
        Query q = entityManager.createQuery(hql);
        q.setParameter(1, companyId);

        return getAttributes(q.getResultList());
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

    public void delete(int companyId, int questionId, int answerGroupId, int answerId) {
        Query q = entityManager.createQuery("delete from " + entityClass.getName() + " a where a.id.companyId = ?1 and a.id.questionId = ?2 and a.id.answerGroupId = ?3 and a.id.answerId = ?4");
        q.setParameter(1, companyId);
        q.setParameter(2, questionId);
        q.setParameter(3, answerGroupId);
        q.setParameter(4, answerId);

        q.executeUpdate();
    }

    private List<Attribute> getAttributes(List<Object[]> resultSet) {
        List<Value> input = new ArrayList<Value>();
        for (Object[] obj : resultSet) {
            Value value = new Value();
            value.attributeId = ((Number) (obj[0])).intValue();
            value.value = ((Number) (obj[1])).intValue();
            value.maxValue = ((Number) (obj[2])).intValue();

            input.add(value);
        }

        List<Attribute> out = new ArrayList<Attribute>();
        int i = 0;

        while (i < input.size()) {
            Value val = input.get(i);
            Attribute attribute = new Attribute();
            attribute.setAttributeId(val.attributeId);
            List<AttributeValue> values = new ArrayList<AttributeValue>();
            attribute.setAttributeValues(values);


            int j = i;
            while (j < input.size() && input.get(j).attributeId == attribute.getAttributeId()) {
                AttributeValue attributeValue = new AttributeValue();
                attributeValue.setValue(val.value);
                attributeValue.setMaxValue(val.maxValue);

                values.add(attributeValue);
                j++;
            }
            out.add(attribute);
            i = j;
        }

        return out;
    }

    class Value {
        int attributeId;
        int value;
        int maxValue;
    }
}

/*
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
 */
