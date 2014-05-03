package com.fbr.Dao.Question.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectEntity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "answers")
public class AnswerDbType implements Serializable, ProjectEntity<AnswerPrimaryKey> {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "companyId", column = @Column(name = "company_id", nullable = false)),
            @AttributeOverride(name = "questionId", column = @Column(name = "question_id", nullable = false)),
            @AttributeOverride(name = "answerId", column = @Column(name = "answer_id", nullable = false))})
    AnswerPrimaryKey id;
    @Column(name = "answer_string", nullable = false)
    String answerString;
    @Column(name = "attribute_id", nullable = false)
    int attributeId;
    @Column(name = "max_value", nullable = false)
    int maxValue;
    @Column(name = "attainable_value", nullable = false)
    int attainableValue;


    public String getAnswerString() {
        return answerString;
    }

    public void setAnswerString(String answerString) {
        this.answerString = answerString;
    }

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getAttainableValue() {
        return attainableValue;
    }

    public void setAttainableValue(int attainableValue) {
        this.attainableValue = attainableValue;
    }

    @Override
    @Transient
    public void setId(AnswerPrimaryKey s) {
        this.id = s;
    }

    @Override
    public AnswerPrimaryKey getId() {
        return this.id;
    }
}

