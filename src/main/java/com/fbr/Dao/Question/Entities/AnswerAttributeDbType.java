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
@Table(name = "answer_attributes")
public class AnswerAttributeDbType implements Serializable, ProjectEntity<AnswerAttributePrimaryKey> {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "companyId", column = @Column(name = "company_id", nullable = false)),
            @AttributeOverride(name = "questionId", column = @Column(name = "question_id", nullable = false)),
            @AttributeOverride(name = "attributeId", column = @Column(name = "attribute_id", nullable = false)),
            @AttributeOverride(name = "answerId", column = @Column(name = "answer_id", nullable = false))})
    AnswerAttributePrimaryKey id;

    @Column(name = "value", nullable = false)
    int value;
    @Column(name = "max_value", nullable = false)
    int maxValue;


    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    @Transient
    public void setId(AnswerAttributePrimaryKey s) {
        this.id = s;
    }

    @Override
    public AnswerAttributePrimaryKey getId() {
        return this.id;
    }
}
