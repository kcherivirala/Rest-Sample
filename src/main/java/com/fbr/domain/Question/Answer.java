package com.fbr.domain.Question;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import java.util.List;

public class Answer {
    int answerId;
    String answer;
    String link;

    List<AnswerAttribute> answerAttributeList;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<AnswerAttribute> getAnswerAttributeList() {
        return answerAttributeList;
    }

    public void setAnswerAttributeList(List<AnswerAttribute> answerAttributeList) {
        this.answerAttributeList = answerAttributeList;
    }
}
