package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Question.AnswerAttributeDao;
import com.fbr.Dao.Question.AnswerDao;
import com.fbr.Dao.Question.Entities.*;
import com.fbr.Dao.Question.QuestionDao;
import com.fbr.Utilities.Comparators;
import com.fbr.domain.Question.Answer;
import com.fbr.domain.Question.AnswerAttribute;
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
    @Autowired
    private AttributeService attributeService;
    @Autowired
    private AnswerAttributeDao answerAttributeDao;

    /*
    placement of question
    0 -> before
    1 -> normal
    2 -> after
     */

    @Transactional
    public Question addQuestionAndAnswers(int companyId, Question question) throws Exception {
        try {
            logger.info("adding question for company: " + companyId + " and question " + question.getQuestion() + " and list of answers : " + question.getAnswers().size());
            int questionId = questionDao.getMaxQuestionIdValue(companyId) + 1;
            addQuestion(companyId, questionId, question);
            for (Answer answer : question.getAnswers()) {
                addAnswer(companyId, questionId, answer);
            }

            attributeService.resetCompanyAttributes(companyId);
            question.setQuestionId(questionId);
            logger.info("done adding question for company: " + companyId + " and question " + question.getQuestion() + " and list of answers : " + question.getAnswers().size());
            return question;
        } catch (Exception e) {
            logger.error("error adding question : " + question.getQuestion() + " company : " + companyId + " : " + e.getMessage());
            throw new Exception("error adding question : " + question.getQuestion() + " company : " + companyId + " : " + e.getMessage());
        }
    }

    @Transactional
    public Question updateQuestionAndAnswers(int companyId, int questionId, Question question) throws Exception {
        try {
            logger.info("updating question for company: " + companyId + " and question " + question.getQuestion() + " and list of answers : " + question.getAnswers().size());
            QuestionPrimaryKey key = new QuestionPrimaryKey();
            key.setCompanyId(companyId);
            key.setQuestionId(questionId);

            QuestionDbType questionDbEntry = questionDao.find(key);
            List<AnswerDbType> answerDbEntries = answerDao.getAnswers(companyId, questionId);
            List<AnswerAttributeDbType> answerAttributeDbEntries = answerAttributeDao.getAnswerAttributes(companyId, questionId);

            updateQuestion(questionDbEntry, question);
            updateAnswers(companyId, questionId, answerDbEntries, answerAttributeDbEntries, question.getAnswers());

            attributeService.resetCompanyAttributes(companyId);

            attributeService.resetCompanyAttributes(companyId);
            logger.info("done updating question for company: " + companyId + " and question " + question.getQuestion() + " and list of answers : " + question.getAnswers().size());
            return question;
        } catch (Exception e) {
            logger.error("error adding question : " + question.getQuestion() + " company : " + companyId + " : " + e.getMessage());
            throw new Exception("error adding question : " + question.getQuestion() + " company : " + companyId + " : " + e.getMessage());
        }
    }

    @Transactional
    public void deleteQuestionAndAnswers(int companyId, int questionId) throws Exception {
        try {
            logger.info("deleting question for company : " + companyId + " and questionId : " + questionId);
            QuestionPrimaryKey key = new QuestionPrimaryKey();
            key.setCompanyId(companyId);
            key.setQuestionId(questionId);

            QuestionDbType questionDbEntry = questionDao.find(key);
            answerAttributeDao.deleteAnswerAttributesOfQuestion(companyId, questionId);
            answerDao.deleteAnswersOfQuestion(companyId, questionId);
            questionDao.delete(questionDbEntry);

            logger.info("done deleting question for company : " + companyId + " and questionId : " + questionId);
        } catch (Exception e) {
            logger.error("error deleting question : " + questionId + " company : " + companyId + " : " + e.getMessage());
            throw new Exception("error deleting question : " + questionId + " company : " + companyId + " : " + e.getMessage());
        }
    }

    public List<Question> getQuestionAndAnswers(int companyId) throws Exception {
        try {
            logger.info("getting questions for company : " + companyId);
            List<QuestionDbType> questionDbEntries = questionDao.getQuestions(companyId);
            List<AnswerDbType> answerDbEntries = answerDao.getAnswers(companyId);
            List<AnswerAttributeDbType> answerAttributeDbEntries = answerAttributeDao.getAnswerAttributes(companyId);

            List<Question> out = matchQuestionAndAnswers(questionDbEntries, answerDbEntries, answerAttributeDbEntries);
            logger.info("done getting questions for company : " + companyId);
            return out;
        } catch (Exception e) {
            logger.error("error getting question for company : " + companyId + " : " + e.getMessage());
            throw new Exception("error getting question for company : " + companyId + " : " + e.getMessage());
        }
    }

    public List<Question> getEnabledQuestionAndAnswers(int companyId) throws Exception {
        try {
            logger.info("getting questions for company : " + companyId);
            List<QuestionDbType> questionDbEntries = questionDao.getEnabledQuestions(companyId);
            List<AnswerDbType> answerDbEntries = answerDao.getAnswers(companyId);
            List<AnswerAttributeDbType> answerAttributeDbEntries = answerAttributeDao.getAnswerAttributes(companyId);

            List<Question> out = matchQuestionAndAnswers(questionDbEntries, answerDbEntries, answerAttributeDbEntries);
            logger.info("done getting questions for company : " + companyId);
            return out;
        } catch (Exception e) {
            logger.error("error getting question for company : " + companyId + " : " + e.getMessage());
            throw new Exception("error getting question for company : " + companyId + " : " + e.getMessage());
        }
    }

    public Question getQuestionAndAnswers(int companyId, int questionId) throws Exception {
        try {
            logger.info("getting question for company : " + companyId + " and questionId : " + questionId);
            List<QuestionDbType> questionDbEntries = questionDao.getQuestions(companyId, questionId);
            List<AnswerDbType> answerDbEntries = answerDao.getAnswers(companyId, questionId);
            List<AnswerAttributeDbType> answerAttributeDbEntries = answerAttributeDao.getAnswerAttributes(companyId, questionId);

            Question out = matchQuestionAndAnswers(questionDbEntries, answerDbEntries, answerAttributeDbEntries).get(0);
            logger.info("done getting question for company : " + companyId + " and questionId : " + questionId);
            return out;
        } catch (Exception e) {
            logger.error("error getting question for company : " + companyId + " and question : " + questionId + " : " + e.getMessage());
            throw new Exception("error getting question for company : " + companyId + " and question : " + questionId + " : " + e.getMessage());
        }
    }

    /*   private functions */

    private void updateQuestion(QuestionDbType questionDbEntry, Question question) {
        logger.debug("modifying question for company : " + questionDbEntry.getId().getCompanyId() + " and questionID : " + questionDbEntry.getId().getQuestionId());
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
        if (questionDbEntry.isEnabled() != question.isEnabled()) {
            questionDbEntry.setEnabled(question.isEnabled());
            updated = true;
        }
        if (questionDbEntry.getPlacement() != question.getPlacement()) {
            questionDbEntry.setPlacement(question.getPlacement());
            updated = true;
        }
        if (updated)
            questionDao.update(questionDbEntry);
    }

    private void updateAnswers(int companyId, int questionId, List<AnswerDbType> answerDbEntries, List<AnswerAttributeDbType> answerAttributeDbEntries,
                               List<Answer> inputAnswers) {
        Collections.sort(answerDbEntries, Comparators.COMPARE_DB_ANSWERS);
        Collections.sort(inputAnswers, Comparators.COMPARE_DOMAIN_ANSWERS);
        Collections.sort(answerAttributeDbEntries, Comparators.COMPARE_DB_ANSWER_ATTRIBUTES);

        int dbIndex = 0, inputIndex = 0;
        while (dbIndex < answerDbEntries.size() && inputIndex < inputAnswers.size()) {
            AnswerDbType answerDbEntry = answerDbEntries.get(dbIndex);
            Answer inputAnswer = inputAnswers.get(inputIndex);

            if (inputAnswer.getAnswerId() == answerDbEntry.getId().getAnswerId()) {
                logger.debug("modifying : (" + companyId + "," + questionId + "," + inputAnswer.getAnswerId() + ")");
                updateAnswer(answerDbEntry, inputAnswer);
                dbIndex++;
                inputIndex++;
            } else if (inputAnswers.get(inputIndex).getAnswerId() < answerDbEntries.get(dbIndex).getId().getAnswerId()) {
                logger.debug("adding : (" + companyId + "," + questionId + "," + inputAnswer.getAnswerId() + ")");
                addAnswer(companyId, questionId, inputAnswer);
                inputIndex++;
            } else {
                logger.debug("deleting : (" + companyId + "," + questionId + "," + answerDbEntry.getId().getAnswerId() + ")");
                deleteAnswer(answerDbEntry);
                dbIndex++;
            }
        }
        while (dbIndex < answerDbEntries.size()) {
            logger.debug("deleting : (" + companyId + "," + questionId + "," + answerDbEntries.get(dbIndex).getId().getAnswerId() + ")");
            deleteAnswer(answerDbEntries.get(dbIndex));
            dbIndex++;
        }
        while (inputIndex < inputAnswers.size()) {
            logger.debug("adding : (" + companyId + "," + questionId + "," + inputAnswers.get(inputIndex).getAnswerId() + ")");
            addAnswer(companyId, questionId, inputAnswers.get(inputIndex));
            inputIndex++;
        }
    }

    private List<Question> matchQuestionAndAnswers(List<QuestionDbType> questionDbEntries, List<AnswerDbType> answerDbEntries,
                                                   List<AnswerAttributeDbType> answerAttributeDbEntries) {
        List<Question> questionList = new ArrayList<Question>(questionDbEntries.size());

        Collections.sort(questionDbEntries, Comparators.COMPARE_DB_QUESTIONS);
        Collections.sort(answerDbEntries, Comparators.COMPARE_DB_ANSWERS);
        Collections.sort(answerAttributeDbEntries, Comparators.COMPARE_DB_ANSWER_ATTRIBUTES);

        int qIndex = 0, aIndex = 0, attributeIndex = 0;
        while (qIndex < questionDbEntries.size()) {
            QuestionDbType questionDbEntry = questionDbEntries.get(qIndex);
            int questionId = questionDbEntry.getId().getQuestionId();

            Question question = Conversions.getQuestion(questionDbEntry);
            List<Answer> answerList = new ArrayList<Answer>();
            question.setAnswers(answerList);

            while (aIndex < answerDbEntries.size() && answerDbEntries.get(aIndex).getId().getQuestionId() < questionId) {
                aIndex++;
            }
            while (aIndex < answerDbEntries.size() && answerDbEntries.get(aIndex).getId().getQuestionId() == questionId) {
                Answer answer = Conversions.getAnswer(answerDbEntries.get(aIndex));
                answerList.add(answer);

                List<AnswerAttribute> answerAttributeList = new ArrayList<AnswerAttribute>();
                answer.setAnswerAttributeList(answerAttributeList);

                while (attributeIndex < answerAttributeDbEntries.size() && answerAttributeDbEntries.get(attributeIndex).getId().getQuestionId() < questionId) {
                    attributeIndex++;
                }
                while (attributeIndex < answerAttributeDbEntries.size() && answerAttributeDbEntries.get(attributeIndex).getId().getQuestionId() == questionId
                        && answerAttributeDbEntries.get(attributeIndex).getId().getAnswerId() < answer.getAnswerId()) {
                    attributeIndex++;
                }
                while (attributeIndex < answerAttributeDbEntries.size() && answerAttributeDbEntries.get(attributeIndex).getId().getQuestionId() == questionId
                        && answerAttributeDbEntries.get(attributeIndex).getId().getAnswerId() == answer.getAnswerId()) {
                    AnswerAttribute answerAttribute = Conversions.getAnswerAttribute(answerAttributeDbEntries.get(attributeIndex));
                    answerAttributeList.add(answerAttribute);
                    attributeIndex++;
                }

                aIndex++;
            }

            questionList.add(question);
            qIndex++;
        }
        return questionList;
    }


    private void updateAnswer(AnswerDbType answerDbEntry, Answer inputAnswer) {
        boolean updated = false;
        if (!answerDbEntry.getAnswerString().equals(inputAnswer.getAnswer())) {
            answerDbEntry.setAnswerString(inputAnswer.getAnswer());
            updated = true;
        }

        if (updated) {
            answerDao.update(answerDbEntry);
        }

        //updateAnswerAttribute()
    }

    private void deleteAnswer(AnswerDbType answerDbEntry) {
        answerAttributeDao.deleteAnswerAttributesOfQuestion(answerDbEntry.getId().getCompanyId(), answerDbEntry.getId().getQuestionId(), answerDbEntry.getId().getAnswerId());
        answerDao.delete(answerDbEntry);
    }

    private void addQuestion(int companyId, int questionId, Question inputQuestion) {
        QuestionDbType questionDbEntry = Conversions.getQuestionDbEntry(companyId, questionId, inputQuestion);
        try {
            questionDao.add(questionDbEntry);
        } catch (Exception e) {
            logger.error("");
        }

    }

    private void addAnswer(int companyId, int questionId, Answer inputAnswer) {
        int answerId = answerDao.getMaxQuestionIdValue(companyId, questionId) + 1;
        AnswerDbType answerDbEntry = Conversions.getAnswerDbEntry(companyId, questionId, answerId, inputAnswer);
        answerDao.add(answerDbEntry);

        for (AnswerAttribute answerAttribute : inputAnswer.getAnswerAttributeList()) {
            AnswerAttributeDbType answerAttributeDbEntry = Conversions.getAnswerAttributeDbEntry(companyId, questionId, answerId, answerAttribute);
            answerAttributeDao.add(answerAttributeDbEntry);
        }
    }

    public static class Conversions {
        public static AnswerDbType getAnswerDbEntry(int companyId, int questionId, int answerId, Answer answer) {
            AnswerDbType answerDbEntry = new AnswerDbType();
            AnswerPrimaryKey aKey = new AnswerPrimaryKey();
            answerDbEntry.setId(aKey);

            aKey.setCompanyId(companyId);
            aKey.setQuestionId(questionId);
            aKey.setAnswerId(answerId);

            answerDbEntry.setAnswerString(answer.getAnswer());

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

            questionDbEntry.setEnabled(question.isEnabled());
            questionDbEntry.setPlacement(question.getPlacement());

            return questionDbEntry;
        }

        public static Question getQuestion(QuestionDbType questionDbEntry) {
            Question question = new Question();

            question.setFunction(questionDbEntry.getFunction());
            question.setParentId(questionDbEntry.getParentId());
            question.setQuestion(questionDbEntry.getQuestionString());
            question.setQuestionId(questionDbEntry.getId().getQuestionId());

            question.setEnabled(questionDbEntry.isEnabled());
            question.setPlacement(questionDbEntry.getPlacement());

            return question;
        }

        public static Answer getAnswer(AnswerDbType answerDbEntry) {
            Answer answer = new Answer();

            answer.setAnswer(answerDbEntry.getAnswerString());
            answer.setAnswerId(answerDbEntry.getId().getAnswerId());

            return answer;
        }

        public static AnswerAttributeDbType getAnswerAttributeDbEntry(int companyId, int questionId, int answerId, AnswerAttribute answerAttribute) {
            AnswerAttributeDbType dbEntry = new AnswerAttributeDbType();
            AnswerAttributePrimaryKey id = new AnswerAttributePrimaryKey();

            id.setCompanyId(companyId);
            id.setQuestionId(questionId);
            id.setAnswerId(answerId);
            id.setAttributeId(answerAttribute.getAttributeId());

            dbEntry.setId(id);
            dbEntry.setValue(answerAttribute.getAttainedValue());
            dbEntry.setMaxValue(answerAttribute.getMaxValue());

            return dbEntry;
        }

        public static AnswerAttribute getAnswerAttribute(AnswerAttributeDbType dbEntry) {
            AnswerAttribute answerAttribute = new AnswerAttribute();

            answerAttribute.setAttributeId(dbEntry.getId().getAttributeId());
            answerAttribute.setAttainedValue(dbEntry.getValue());
            answerAttribute.setMaxValue(dbEntry.getMaxValue());

            return answerAttribute;
        }
    }
}
