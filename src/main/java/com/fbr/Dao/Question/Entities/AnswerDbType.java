package com.fbr.Dao.Question.Entities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.ProjectEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "answers")
public class AnswerDbType implements Serializable, ProjectEntity<AnswerPrimaryKey> {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "companyId", column = @Column(name = "company_id", nullable = false)),
            @AttributeOverride(name = "questionId", column = @Column(name = "question_id", nullable = false)),
            @AttributeOverride(name = "answerGroupId", column = @Column(name = "answer_group_id", nullable = false)),
            @AttributeOverride(name = "answerId", column = @Column(name = "answer_id", nullable = false))})
    AnswerPrimaryKey id;
    @Column(name = "answer_string", nullable = false)
    String answerString;
    @Column(name = "link")
    String link;


    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "company_id", referencedColumnName = "company_id"),
            @JoinColumn(name = "question_id", referencedColumnName = "question_id"),
            @JoinColumn(name = "answer_group_id", referencedColumnName = "answer_group_id"),
            @JoinColumn(name = "answer_id", referencedColumnName = "answer_id")
    })
    List<AnswerAttributeDbType> answerAttributes = new ArrayList<AnswerAttributeDbType>();

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

    public List<AnswerAttributeDbType> getAnswerAttributes() {
        return answerAttributes;
    }

    public void setAnswerAttributes(List<AnswerAttributeDbType> answerAttributes) {
        this.answerAttributes = answerAttributes;
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

