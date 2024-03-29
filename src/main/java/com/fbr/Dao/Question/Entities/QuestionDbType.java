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
@Table(name = "questions")
public class QuestionDbType implements Serializable, ProjectEntity<QuestionPrimaryKey> {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "companyId", column = @Column(name = "company_id", nullable = false)),
            @AttributeOverride(name = "questionId", column = @Column(name = "question_id", nullable = false))})
    QuestionPrimaryKey id;
    @Column(name = "question_string", nullable = false)
    String questionString;
    @Column(name = "parent_id")
    int parentId;
    @Column(name = "function")
    String function;
    @Column(name = "enabled")
    boolean enabled;
    @Column(name = "placement")
    int placement;
    @Column(name = "link")
    String link;
    @Column(name = "category")
    String category;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "company_id", referencedColumnName = "company_id"),
            @JoinColumn(name = "question_id", referencedColumnName = "question_id")
    })
    List<AnswerGroupDbType> answerGroups = new ArrayList<AnswerGroupDbType>();

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getPlacement() {
        return placement;
    }

    public void setPlacement(int placement) {
        this.placement = placement;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getQuestionString() {
        return questionString;
    }

    public void setQuestionString(String questionString) {
        this.questionString = questionString;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public List<AnswerGroupDbType> getAnswerGroups() {
        return answerGroups;
    }

    public void setAnswerGroups(List<AnswerGroupDbType> answerGroups) {
        this.answerGroups = answerGroups;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    @Transient
    public void setId(QuestionPrimaryKey s) {
        this.id = s;
    }

    @Override
    public QuestionPrimaryKey getId() {
        return this.id;
    }
}
