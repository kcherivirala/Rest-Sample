package com.fbr.Dao.Question.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import javax.persistence.Column;
import java.io.Serializable;

public class AnswerAttributePrimaryKey implements Serializable {
    @Column(name = "company_id", nullable = false)
    int companyId;
    @Column(name = "question_id", nullable = false)
    int questionId;
    @Column(name = "answer_id", nullable = false)
    int answerId;
    @Column(name = "attribute_id", nullable = false)
    int attributeId;


    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }
}
