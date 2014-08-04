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
    @Column(name = "link", nullable = false)
    String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAnswerString() {
        return answerString;
    }

    public void setAnswerString(String answerString) {
        this.answerString = answerString;
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

