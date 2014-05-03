package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Question.AnswerDao;
import com.fbr.Dao.Question.Entities.AnswerDbType;
import com.fbr.Dao.Question.Entities.AnswerPrimaryKey;
import com.fbr.Dao.Question.Entities.QuestionDbType;
import com.fbr.Dao.Question.Entities.QuestionPrimaryKey;
import com.fbr.Dao.Question.QuestionDao;
import com.fbr.domain.Question.Answer;
import com.fbr.domain.Question.Question;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class QuestionService {
    private static final Logger logger = Logger.getLogger(QuestionService.class);
    @Autowired
    private AnswerDao answerDao;
    @Autowired
    private QuestionDao questionDao;

    @Transactional
    public Question addQuestionAndAnswers(int companyId, Question question) {
        int questionId = questionDao.getMaxQuestionIdValue(companyId) + 1;
        addQuestion(companyId, questionId, question);
        for (Answer answer : question.getAnswers()) {
            addAnswer(companyId, questionId, answer);
        }

        question.setQuestionId(questionId);
        return question;
    }

    @Transactional
    public Question updateQuestionAndAnswers(int companyId, int questionId, Question question) {
        QuestionPrimaryKey key = new QuestionPrimaryKey();
        key.setCompanyId(companyId);
        key.setQuestionId(questionId);

        QuestionDbType questionDbEntry = questionDao.find(key);
        List<AnswerDbType> answerDbEntries = answerDao.getAnswers(companyId, questionId);

        updateQuestion(questionDbEntry, question);
        updateAnswers(companyId, questionId, answerDbEntries, question.getAnswers());

        return question;
    }

    @Transactional
    public void deleteQuestionAndAnswers(int companyId, int questionId) {
        QuestionPrimaryKey key = new QuestionPrimaryKey();
        key.setCompanyId(companyId);
        key.setQuestionId(questionId);

        QuestionDbType questionDbEntry = questionDao.find(key);
        questionDao.delete(questionDbEntry);

        answerDao.deleteAnswersOfQuestion(companyId, questionId);
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

    private void updateAnswers(int companyId, int questionId, List<AnswerDbType> answerDbEntries, List<Answer> inputAnswers) {
        Collections.sort(answerDbEntries, Comparators.COMPARE_DB_ANSWERS);
        Collections.sort(inputAnswers, Comparators.COMPARE_DOMAIN_ANSWERS);

        int dbIndex = 0, inputIndex = 0;
        while (dbIndex < answerDbEntries.size() && inputIndex < inputAnswers.size()) {
            AnswerDbType answerDbEntry = answerDbEntries.get(dbIndex);
            Answer inputAnswer = inputAnswers.get(inputIndex);

            if (inputAnswer.getAnswerId() == answerDbEntry.getId().getAnswerId()) {
                updateAnswer(answerDbEntry, inputAnswer);
                dbIndex++;
                inputIndex++;
            } else if (inputAnswers.get(inputIndex).getAnswerId() < answerDbEntries.get(dbIndex).getId().getAnswerId()) {
                addAnswer(companyId, questionId, inputAnswer);
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
            addAnswer(companyId, questionId, inputAnswers.get(inputIndex));
            inputIndex++;
        }
    }

    private List<Question> matchQuestionAndAnswers(List<QuestionDbType> questionDbEntries, List<AnswerDbType> answerDbEntries) {
        List<Question> questionList = new ArrayList<Question>(questionDbEntries.size());

        Collections.sort(questionDbEntries, Comparators.COMPARE_DB_QUESTIONS);
        Collections.sort(answerDbEntries, Comparators.COMPARE_DB_ANSWERS);

        int qIndex = 0, aIndex = 0;
        while (qIndex < questionDbEntries.size()) {
            QuestionDbType questionDbentry = questionDbEntries.get(qIndex);
            int questionId = questionDbentry.getId().getQuestionId();

            Question question = Conversions.getQuestion(questionDbentry);
            List<Answer> answerList = new ArrayList<Answer>();
            question.setAnswers(answerList);

            while (aIndex < answerDbEntries.size() && answerDbEntries.get(aIndex).getId().getQuestionId() == questionId) {
                answerList.add(Conversions.getAnswer(answerDbEntries.get(aIndex)));
                aIndex++;
            }
            questionList.add(question);
            qIndex++;
        }
        return questionList;
    }

    private void addQuestion(int companyId, int questionId, Question inputQuestion) {
        QuestionDbType questionDbEntry = Conversions.getQuestionDbEntry(companyId, questionId, inputQuestion);
        try {
            questionDao.add(questionDbEntry);
        } catch (Exception e) {
            logger.error("");
        }

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

    private void addAnswer(int companyId, int questionId, Answer inputAnswer) {
        AnswerDbType answerDbEntry = Conversions.getAnswerDbEntry(companyId, questionId, inputAnswer);
        answerDao.add(answerDbEntry);
    }

    private static class Conversions {
        public static AnswerDbType getAnswerDbEntry(int companyId, int questionId, Answer answer) {
            AnswerDbType answerDbEntry = new AnswerDbType();
            AnswerPrimaryKey aKey = new AnswerPrimaryKey();
            answerDbEntry.setId(aKey);

            aKey.setCompanyId(companyId);
            aKey.setQuestionId(questionId);
            aKey.setAnswerId(answer.getAnswerId());

            answerDbEntry.setAnswerString(answer.getAnswer());
            answerDbEntry.setAttributeId(answer.getAttributeId());
            answerDbEntry.setAttainableValue(answer.getAttainedValue());
            answerDbEntry.setMaxValue(answer.getMaxValue());

            return answerDbEntry;
        }

        public static QuestionDbType getQuestionDbEntry(int companyId, int questionId, Question question) {
            QuestionDbType questionDbEntry = new QuestionDbType();
            QuestionPrimaryKey qKey = new QuestionPrimaryKey();
            questionDbEntry.setId(qKey);

            qKey.setCompanyId(companyId);
            qKey.setQuestionId(questionId);

            questionDbEntry.setParentId(question.getParentId());
            questionDbEntry.setFunction(question.getFunction());
            questionDbEntry.setQuestionString(question.getQuestion());

            return questionDbEntry;
        }

        public static Question getQuestion(QuestionDbType questionDbEntry) {
            Question question = new Question();

            question.setFunction(questionDbEntry.getFunction());
            question.setParentId(questionDbEntry.getParentId());
            question.setQuestion(questionDbEntry.getQuestionString());
            question.setQuestionId(questionDbEntry.getId().getQuestionId());

            return question;
        }

        public static Answer getAnswer(AnswerDbType answerDbEntry) {
            Answer answer = new Answer();

            answer.setAnswer(answerDbEntry.getAnswerString());
            answer.setAnswerId(answerDbEntry.getId().getAnswerId());
            answer.setAttainedValue(answerDbEntry.getAttainableValue());
            answer.setMaxValue(answerDbEntry.getMaxValue());
            answer.setAttributeId(answerDbEntry.getAttributeId());

            return answer;
        }

    }
}
