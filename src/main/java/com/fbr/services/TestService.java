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
import com.fbr.Dao.Question.QuestionDao;
import com.fbr.Dao.Response.CustomerResponseDao;
import com.fbr.domain.Attribute.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        List<Attribute> out = answerAttributeDao.getUniqueAttributesAndValues(1);

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
