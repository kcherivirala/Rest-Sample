package com.fbr.domain.Question;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import java.util.List;

public class AnswerGroup {
    int answerGroupId;
    List<Answer> answers;
    String answerGroup;
    String link;
    int displayEnum;

    public int getAnswerGroupId() {
        return answerGroupId;
    }

    public void setAnswerGroupId(int answerGroupId) {
        this.answerGroupId = answerGroupId;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAnswerGroup() {
        return answerGroup;
    }

    public void setAnswerGroup(String answerGroup) {
        this.answerGroup = answerGroup;
    }

    public int getDisplayEnum() {
        return displayEnum;
    }

    public void setDisplayEnum(int displayEnum) {
        this.displayEnum = displayEnum;
    }
}
