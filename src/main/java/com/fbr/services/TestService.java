package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Cache.CacheJdbcClient;
import com.fbr.Dao.Question.AnswerAttributeDao;
import com.fbr.Dao.Question.AnswerDao;
import com.fbr.Dao.Question.AnswerGroupDao;
import com.fbr.Dao.Question.Entities.*;
import com.fbr.Dao.Question.QuestionDao;
import com.fbr.Dao.Response.CustomerResponseDao;
import com.fbr.domain.Attribute.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service("testService")
public class TestService {
    @Autowired
    private CacheJdbcClient cacheJdbcClient;

    @Autowired
    private CustomerResponseDao customerResponseDao;
    @Autowired
    private AggregatorService aggregatorService;
    @Autowired
    private AnswerDao answerDao;
    @Autowired
    private AnswerAttributeDao answerAttributeDao;
    @Autowired
    private QuestionDao questionDao;
    @Autowired
    private AnswerGroupDao answerGroupDao;

    @Transactional
    public Attribute test() {
        AnswerAttributePrimaryKey id1 = new AnswerAttributePrimaryKey();
        id1.setCompanyId(1);
        id1.setQuestionId(1);
        id1.setAnswerGroupId(1);
        id1.setAnswerId(1);
        id1.setAttributeId(1);

        AnswerAttributeDbType answerAttributeDbType1 = new AnswerAttributeDbType();
        answerAttributeDbType1.setId(id1);
        answerAttributeDbType1.setMaxValue(5);
        answerAttributeDbType1.setValue(1);

        AnswerAttributePrimaryKey id2 = new AnswerAttributePrimaryKey();
        id2.setCompanyId(1);
        id2.setQuestionId(1);
        id2.setAnswerGroupId(1);
        id2.setAnswerId(1);
        id2.setAttributeId(2);

        AnswerAttributeDbType answerAttributeDbType2 = new AnswerAttributeDbType();
        answerAttributeDbType2.setId(id2);
        answerAttributeDbType2.setMaxValue(5);
        answerAttributeDbType2.setValue(2);

        Set<AnswerAttributeDbType> set = new HashSet<AnswerAttributeDbType>(2);
        set.add(answerAttributeDbType1);
        set.add(answerAttributeDbType2);

        AnswerPrimaryKey id3 = new AnswerPrimaryKey();
        id3.setCompanyId(1);
        id3.setQuestionId(1);
        id3.setAnswerGroupId(1);
        id3.setAnswerId(1);

        AnswerDbType dbEntry = new AnswerDbType();
        dbEntry.setId(id3);

        dbEntry.setAnswerString("abc");
        dbEntry.setLink("abc");

        try {
            QuestionPrimaryKey key = new QuestionPrimaryKey();
            key.setCompanyId(1);
            key.setQuestionId(1);

            QuestionDbType db = questionDao.find(key);
            System.out.println(db.getAnswerGroups().size());

        } catch (Exception e) {
            System.out.print("");
        }

        System.out.print(dbEntry.getAnswerString());


        return null;
    }


}
/*
QuestionPrimaryKey key = new QuestionPrimaryKey();
            key.setCompanyId(1);
            key.setQuestionId(1);

            QuestionDbType db = questionDao.find(key);
            System.out.println(db.getAnswerGroups().size());

            AnswerGroupPrimaryKey key = new AnswerGroupPrimaryKey();
            key.setCompanyId(1);
            key.setQuestionId(1);
            key.setAnswerGroupId(1);

            AnswerGroupDbType db = answerGroupDao.find(key);
            System.out.println(db.getAnswers().size());
 */
