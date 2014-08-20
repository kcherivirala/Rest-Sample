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
@Table(name = "answer_groups")
public class AnswerGroupDbType implements Serializable, ProjectEntity<AnswerGroupPrimaryKey> {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "companyId", column = @Column(name = "company_id", nullable = false)),
            @AttributeOverride(name = "questionId", column = @Column(name = "question_id", nullable = false)),
            @AttributeOverride(name = "answerGroupId", column = @Column(name = "answer_group_id", nullable = false))})
    AnswerGroupPrimaryKey id;
    @Column(name = "answer_group_string", nullable = false)
    String answerGroupString;
    @Column(name = "link")
    String link;
    @Column(name = "display_enum")
    int displayEnum;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "company_id", referencedColumnName = "company_id"),
            @JoinColumn(name = "question_id", referencedColumnName = "question_id"),
            @JoinColumn(name = "answer_group_id", referencedColumnName = "answer_group_id")
    })
    List<AnswerDbType> answers = new ArrayList<AnswerDbType>();

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAnswerGroupString() {
        return answerGroupString;
    }

    public void setAnswerGroupString(String answerGroupString) {
        this.answerGroupString = answerGroupString;
    }

    public List<AnswerDbType> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerDbType> answers) {
        this.answers = answers;
    }

    public int getDisplayEnum() {
        return displayEnum;
    }

    public void setDisplayEnum(int displayEnum) {
        this.displayEnum = displayEnum;
    }

    @Override
    @Transient
    public void setId(AnswerGroupPrimaryKey s) {
        this.id = s;
    }

    @Override
    public AnswerGroupPrimaryKey getId() {
        return this.id;
    }
}

