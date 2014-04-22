package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.AnswerDao;
import com.fbr.Dao.Entities.AnswerDbType;
import com.fbr.Dao.Entities.AnswerPrimaryKey;
import com.fbr.Dao.Entities.QuestionDbType;
import com.fbr.Dao.Entities.QuestionPrimaryKey;
import com.fbr.Dao.QuestionDao;
import com.fbr.domain.Answer;
import com.fbr.domain.Question;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class QuestionService {
    private static final Logger logger = Logger.getLogger(QuestionService.class);
    @Autowired
    private AnswerDao answerDao;
    @Autowired
    private QuestionDao questionDao;


    @Transactional
    public void addQuestionAndAnswers(int companyId, List<Question> questions) {
        for (Question question : questions) {
            addQuestion(companyId, question);

            for (Answer answer : question.getAnswers()) {
                addAnswer(companyId, question, answer);
            }
        }
    }

    public List<Question> getQuestionAndAnswers(int companyId) {
        List<QuestionDbType> questionDbEntries = questionDao.getQuestions(companyId);
        List<AnswerDbType> answerDbEntries = answerDao.getAnswers(companyId);

        return matchQuestionAndAnswers(questionDbEntries, answerDbEntries);
    }

    public List<Question> getQuestionAndAnswers(int companyId, int questionId) {
        List<QuestionDbType> questionDbEntries = questionDao.getQuestions(companyId, questionId);
        List<AnswerDbType> answerDbEntries = answerDao.getAnswers(companyId, questionId);

        return matchQuestionAndAnswers(questionDbEntries, answerDbEntries);
    }

    @Transactional
    public void updateQuestionAndAnswers(int companyId, int questionId, Question question) {
        QuestionPrimaryKey key = new QuestionPrimaryKey();
        key.setCompanyId(companyId);
        key.setQuestionId(questionId);

        QuestionDbType questionDbEntry = questionDao.find(key);
        List<AnswerDbType> answerDbEntries = answerDao.getAnswers(companyId, questionId);

        updateQuestion(questionDbEntry, question);
        updateAnswers(companyId, answerDbEntries, question);
    }

    /*
    @Transactional
    public void deleteQuestionAndAnswers(String companyId, int questionId, Question question){
        QuestionPrimaryKey key = new QuestionPrimaryKey();
        key.setCompanyId(companyId);
        key.setQuestionId(questionId);

        QuestionDbType questionDbEntry = questionDao.find(key);
        questionDao.delete(questionDbEntry);

        answerDao.de
    }
    */

    private void updateAnswers(int companyId, List<AnswerDbType> answerDbEntries, Question question) {
        List<Answer> inputAnswers = question.getAnswers();
        Collections.sort(answerDbEntries, COMPARE_DB_ANSWERS);
        Collections.sort(inputAnswers, COMPARE_DOMAIN_ANSWERS);

        int dbIndex = 0, inputIndex = 0;
        while (dbIndex < answerDbEntries.size() && inputIndex < inputAnswers.size()) {
            AnswerDbType answerDbEntry = answerDbEntries.get(dbIndex);
            Answer inputAnswer = inputAnswers.get(inputIndex);

            if (inputAnswer.getAnswerId() == answerDbEntry.getId().getAnswerId()) {
                updateAnswer(answerDbEntry, inputAnswer);
                dbIndex++;
                inputIndex++;
            } else if (inputAnswers.get(inputIndex).getAnswerId() < answerDbEntries.get(dbIndex).getId().getAnswerId()) {
                addAnswer(companyId, question, inputAnswer);
                inputIndex++;
            } else {
                deleteAnswer(answerDbEntry);
                dbIndex++;
            }
        }
        while (dbIndex < answerDbEntries.size()) {
            deleteAnswer(answerDbEntries.get(dbIndex));
            dbIndex++;
        }
        while (inputIndex < inputAnswers.size()) {
            addAnswer(companyId, question, inputAnswers.get(inputIndex));
            inputIndex++;
        }
    }

    private List<Question> matchQuestionAndAnswers(List<QuestionDbType> questionDbEntries, List<AnswerDbType> answerDbEntries) {
        List<Question> questionList = new ArrayList<Question>(questionDbEntries.size());

        Collections.sort(questionDbEntries, COMPARE_DB_QUESTIONS);
        Collections.sort(answerDbEntries, COMPARE_DB_ANSWERS);

        int qIndex = 0, aIndex = 0;
        while (qIndex < questionDbEntries.size()) {
            QuestionDbType questionDbentry = questionDbEntries.get(qIndex);
            int questionId = questionDbentry.getId().getQuestionId();

            Question question = getQuestion(questionDbentry);

            while (aIndex < answerDbEntries.size() && answerDbEntries.get(aIndex).getId().getQuestionId() == questionId) {
                addAnswerToQuestion(question, answerDbEntries.get(aIndex));
                aIndex++;
            }
            questionList.add(question);
            qIndex++;
        }
        return questionList;
    }

    private void addQuestion(int companyId, Question inputQuestion) {
        QuestionDbType questionDbEntry = getQuestionDbEntry(companyId, inputQuestion);
        questionDao.add(questionDbEntry);
    }

    private void updateQuestion(QuestionDbType questionDbEntry, Question question) {
        boolean updated = false;
        if (!questionDbEntry.getQuestionString().equals(question.getQuestion())) {
            questionDbEntry.setQuestionString(question.getQuestion());
            updated = true;
        }
        if (questionDbEntry.getParentId() != question.getParentId()) {
            questionDbEntry.setParentId(question.getParentId());
            updated = true;
        }
        if ((questionDbEntry.getFunction() != question.getFunction()) ||
                (questionDbEntry.getFunction() != null && question.getFunction() != null && !questionDbEntry.getFunction().equals(question.getFunction()))) {
            questionDbEntry.setFunction(question.getFunction());
            updated = true;
        }
        if (updated)
            questionDao.update(questionDbEntry);
    }

    private void updateAnswer(AnswerDbType answerDbEntry, Answer inputAnswer) {
        boolean updated = false;
        if (!answerDbEntry.getAnswerString().equals(inputAnswer.getAnswer())) {
            answerDbEntry.setAnswerString(inputAnswer.getAnswer());
            updated = true;
        }
        if (answerDbEntry.getAttributeId() != (inputAnswer.getAttributeId())) {
            answerDbEntry.setAttributeId(inputAnswer.getAttributeId());
            updated = true;
        }
        if (answerDbEntry.getAttainableValue() != inputAnswer.getAttainedValue()) {
            answerDbEntry.setAttainableValue(inputAnswer.getAttainedValue());
            updated = true;
        }
        if (answerDbEntry.getMaxValue() != inputAnswer.getMaxValue()) {
            answerDbEntry.setMaxValue(inputAnswer.getMaxValue());
            updated = true;
        }

        if (updated) {
            answerDao.update(answerDbEntry);
        }
    }

    private void deleteAnswer(AnswerDbType answerDbEntry) {
        answerDao.delete(answerDbEntry);
    }

    private void addAnswer(int companyId, Question inputQuestion, Answer inputAnswer) {
        AnswerDbType answerDbEntry = getAnswerDbEntry(companyId, inputQuestion, inputAnswer);
        answerDao.add(answerDbEntry);
    }

    private AnswerDbType getAnswerDbEntry(int companyId, Question question, Answer answer) {
        AnswerDbType answerDbEntry = new AnswerDbType();
        AnswerPrimaryKey aKey = new AnswerPrimaryKey();
        answerDbEntry.setId(aKey);

        aKey.setCompanyId(companyId);
        aKey.setQuestionId(question.getQuestionId());
        aKey.setAnswerId(answer.getAnswerId());

        answerDbEntry.setAnswerString(answer.getAnswer());
        answerDbEntry.setAttributeId(answer.getAttributeId());
        answerDbEntry.setAttainableValue(answer.getAttainedValue());
        answerDbEntry.setMaxValue(answer.getMaxValue());

        return answerDbEntry;
    }

    private QuestionDbType getQuestionDbEntry(int companyId, Question question) {
        QuestionDbType questionDbEntry = new QuestionDbType();
        QuestionPrimaryKey qKey = new QuestionPrimaryKey();
        questionDbEntry.setId(qKey);

        qKey.setCompanyId(companyId);
        qKey.setQuestionId(question.getQuestionId());

        questionDbEntry.setParentId(question.getParentId());
        questionDbEntry.setFunction(question.getFunction());
        questionDbEntry.setQuestionString(question.getQuestion());

        return questionDbEntry;
    }

    private Question getQuestion(QuestionDbType questionDbEntry) {
        Question question = new Question();

        question.setFunction(questionDbEntry.getFunction());
        question.setParentId(questionDbEntry.getParentId());
        question.setQuestion(questionDbEntry.getQuestionString());
        question.setQuestionId(questionDbEntry.getId().getQuestionId());

        return question;
    }

    private void addAnswerToQuestion(Question question, AnswerDbType answerDbEntry) {
        if (question.getAnswers() == null) {
            question.setAnswers(new ArrayList<Answer>());
        }
        Answer answer = new Answer();

        answer.setAnswer(answerDbEntry.getAnswerString());
        answer.setAnswerId(answerDbEntry.getId().getAnswerId());
        answer.setAttainedValue(answerDbEntry.getAttainableValue());
        answer.setMaxValue(answerDbEntry.getMaxValue());
        answer.setAttributeId(answerDbEntry.getAttributeId());

        question.getAnswers().add(answer);
    }

    private static Comparator<AnswerDbType> COMPARE_DB_ANSWERS = new Comparator<AnswerDbType>() {
        @Override
        public int compare(AnswerDbType first, AnswerDbType second) {
            if (first.getId().getQuestionId() == second.getId().getQuestionId())
                return first.getId().getAnswerId() - second.getId().getAnswerId();
            return first.getId().getQuestionId() - second.getId().getQuestionId();
        }
    };

    private static Comparator<Answer> COMPARE_DOMAIN_ANSWERS = new Comparator<Answer>() {
        @Override
        public int compare(Answer first, Answer second) {
            return first.getAnswerId() - second.getAnswerId();
        }
    };

    private static Comparator<QuestionDbType> COMPARE_DB_QUESTIONS = new Comparator<QuestionDbType>() {
        @Override
        public int compare(QuestionDbType first, QuestionDbType second) {
            return first.getId().getQuestionId() - second.getId().getQuestionId();
        }
    };
}
