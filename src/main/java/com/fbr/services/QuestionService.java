package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Question.AnswerAttributeDao;
import com.fbr.Dao.Question.AnswerDao;
import com.fbr.Dao.Question.AnswerGroupDao;
import com.fbr.Dao.Question.Entities.*;
import com.fbr.Dao.Question.QuestionDao;
import com.fbr.Utilities.Comparators;
import com.fbr.domain.Question.Answer;
import com.fbr.domain.Question.AnswerAttribute;
import com.fbr.domain.Question.AnswerGroup;
import com.fbr.domain.Question.Question;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
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
    private AnswerGroupDao answerGroupDao;
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
            logger.info("adding question for company: " + companyId + " and question " + question.getQuestion() + " and list of answers : " + question.getAnswerGroups().size());
            int questionId = questionDao.getMaxQuestionIdValue(companyId) + 1;
            addQuestion(companyId, questionId, question);
            for (AnswerGroup answerGroup : question.getAnswerGroups()) {
                addAnswerGroup(companyId, questionId, answerGroup);
            }

            attributeService.resetCompanyAttributes(companyId);
            question.setQuestionId(questionId);
            logger.info("done adding question for company: " + companyId + " and question " + question.getQuestion() + " and list of answers : " + question.getAnswerGroups().size());
            return question;
        } catch (Exception e) {
            logger.error("error adding question : " + question.getQuestion() + " company : " + companyId + " : " + e.getMessage());
            throw new Exception("error adding question : " + question.getQuestion() + " company : " + companyId + " : " + e.getMessage());
        }
    }

    @Transactional
    public Question updateQuestionAndAnswers(int companyId, int questionId, Question question) throws Exception {
        try {
            logger.info("updating question for company: " + companyId + " and question " + question.getQuestion() + " and list of groups : " + question.getAnswerGroups().size());
            QuestionPrimaryKey key = new QuestionPrimaryKey();
            key.setCompanyId(companyId);
            key.setQuestionId(questionId);

            QuestionDbType questionDbEntry = questionDao.find(key);

            updateQuestion(companyId, questionId, questionDbEntry, question);

            attributeService.resetCompanyAttributes(companyId);
            logger.info("done updating question for company: " + companyId + " and question " + question.getQuestion() + " and list of answers : " + question.getAnswerGroups().size());
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

            answerAttributeDao.delete(companyId, questionId);
            answerDao.delete(companyId, questionId);
            answerGroupDao.delete(companyId, questionId);
            questionDao.delete(questionDbEntry);

            logger.info("done deleting question for company : " + companyId + " and questionId : " + questionId);
        } catch (Exception e) {
            logger.error("error deleting question : " + questionId + " company : " + companyId + " : " + e.getMessage());
            throw new Exception("error deleting question : " + questionId + " company : " + companyId + " : " + e.getMessage());
        }
    }

    @Transactional
    public List<Question> getQuestionAndAnswers(int companyId) throws Exception {
        try {
            logger.info("getting questions for company : " + companyId);
            List<QuestionDbType> questionDbEntries = questionDao.getQuestions(companyId);

            List<Question> out = Conversions.getQuestions(questionDbEntries);
            logger.info("done getting questions for company : " + companyId);
            return out;
        } catch (Exception e) {
            logger.error("error getting question for company : " + companyId + " : " + e.getMessage());
            throw new Exception("error getting question for company : " + companyId + " : " + e.getMessage());
        }
    }

    @Transactional
    public List<Question> getEnabledQuestionAndAnswers(int companyId) throws Exception {
        try {
            logger.info("getting questions for company : " + companyId);
            List<QuestionDbType> questionDbEntries = questionDao.getEnabledQuestions(companyId);

            List<Question> out = Conversions.getQuestions(questionDbEntries);
            logger.info("done getting questions for company : " + companyId);
            return out;
        } catch (Exception e) {
            logger.error("error getting question for company : " + companyId + " : " + e.getMessage());
            throw new Exception("error getting question for company : " + companyId + " : " + e.getMessage());
        }
    }

    @Transactional
    public Question getQuestionAndAnswers(int companyId, int questionId) throws Exception {
        try {
            logger.info("getting question for company : " + companyId + " and questionId : " + questionId);
            List<QuestionDbType> questionDbEntries = questionDao.getQuestions(companyId, questionId);

            Question out = Conversions.getQuestions(questionDbEntries).get(0);
            logger.info("done getting question for company : " + companyId + " and questionId : " + questionId);
            return out;
        } catch (Exception e) {
            logger.error("error getting question for company : " + companyId + " and question : " + questionId + " : " + e.getMessage());
            throw new Exception("error getting question for company : " + companyId + " and question : " + questionId + " : " + e.getMessage());
        }
    }

    //   private functions

    private void updateQuestion(int companyId, int questionId, QuestionDbType questionDbEntry, Question question) {
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
        if (!questionDbEntry.getLink().equals(question.getLink())) {
            questionDbEntry.setLink(question.getLink());
            updated = true;
        }
        if (updated)
            questionDao.update(questionDbEntry);

        updateAnswerGroups(companyId, questionId, questionDbEntry.getAnswerGroups(), question.getAnswerGroups());
    }

    private void updateAnswerGroups(int companyId, int questionId, List<AnswerGroupDbType> answerGroupDbEntries, List<AnswerGroup> inputAnswerGroups) {
        Collections.sort(answerGroupDbEntries, Comparators.COMPARE_DB_ANSWER_GROUPS);
        Collections.sort(inputAnswerGroups, Comparators.COMPARE_DOMAIN_ANSWER_GROUPS);

        int dbIndex = 0, inputIndex = 0;
        while (dbIndex < answerGroupDbEntries.size() && inputIndex < inputAnswerGroups.size()) {
            AnswerGroupDbType dbEntry = answerGroupDbEntries.get(dbIndex);
            AnswerGroup inputGroup = inputAnswerGroups.get(inputIndex);

            if (dbEntry.getId().getAnswerGroupId() == inputGroup.getAnswerGroupId()) {
                updateAnswerGroup(companyId, questionId, dbEntry, inputGroup);
                dbIndex++;
                inputIndex++;
            } else if (dbEntry.getId().getAnswerGroupId() < inputGroup.getAnswerGroupId()) {
                delete(companyId, questionId, dbEntry);
                dbIndex++;
            } else {
                addAnswerGroup(companyId, questionId, inputGroup);
                inputIndex++;
            }
        }

        while (dbIndex < answerGroupDbEntries.size()) {
            AnswerGroupDbType dbEntry = answerGroupDbEntries.get(dbIndex);
            delete(companyId, questionId, dbEntry);
            dbIndex++;
        }

        while (inputIndex < inputAnswerGroups.size()) {
            AnswerGroup inputGroup = inputAnswerGroups.get(inputIndex);
            addAnswerGroup(companyId, questionId, inputGroup);
            inputIndex++;
        }
    }

    private void updateAnswerGroup(int companyId, int questionId, AnswerGroupDbType dbEntry, AnswerGroup answerGroup) {
        boolean updated = false;
        if (!dbEntry.getAnswerGroupString().equals(answerGroup.getAnswerGroup())) {
            dbEntry.setAnswerGroupString(answerGroup.getAnswerGroup());
            updated = true;
        }
        if (dbEntry.getDisplayEnum() != answerGroup.getDisplayEnum()) {
            dbEntry.setDisplayEnum(answerGroup.getDisplayEnum());
            updated = true;
        }
        if (!dbEntry.getLink().equals(answerGroup.getLink())) {
            dbEntry.setLink(answerGroup.getLink());
            updated = true;
        }
        if (updated) {
            answerGroupDao.update(dbEntry);
        }
        updateAnswers(companyId, questionId, dbEntry.getId().getAnswerGroupId(), dbEntry.getAnswers(), answerGroup.getAnswers());
    }

    private void updateAnswers(int companyId, int questionId, int answerGroupId, List<AnswerDbType> answerDbEntries, List<Answer> inputAnswers) {
        Collections.sort(answerDbEntries, Comparators.COMPARE_DB_ANSWERS);
        Collections.sort(inputAnswers, Comparators.COMPARE_DOMAIN_ANSWERS);

        int dbIndex = 0, inputIndex = 0;
        while (dbIndex < answerDbEntries.size() && inputIndex < inputAnswers.size()) {
            AnswerDbType answerDbEntry = answerDbEntries.get(dbIndex);
            Answer inputAnswer = inputAnswers.get(inputIndex);

            if (inputAnswer.getAnswerId() == answerDbEntry.getId().getAnswerId()) {
                logger.debug("modifying : (" + companyId + "," + questionId + "," + inputAnswer.getAnswerId() + ")");
                updateAnswer(companyId, questionId, answerGroupId, answerDbEntry, answerDbEntry.getAnswerAttributes(), inputAnswer);
                dbIndex++;
                inputIndex++;
            } else if (inputAnswer.getAnswerId() < answerDbEntries.get(dbIndex).getId().getAnswerId()) {
                logger.debug("adding : (" + companyId + "," + questionId + "," + inputAnswer.getAnswerId() + ")");
                addAnswer(companyId, questionId, answerGroupId, inputAnswer);
                inputIndex++;
            } else {
                logger.debug("deleting : (" + companyId + "," + questionId + "," + answerDbEntry.getId().getAnswerId() + ")");
                delete(companyId, questionId, answerGroupId, answerDbEntry);
                dbIndex++;
            }
        }
        while (dbIndex < answerDbEntries.size()) {
            AnswerDbType answerDbEntry = answerDbEntries.get(dbIndex);
            logger.debug("deleting : (" + companyId + "," + questionId + "," + answerDbEntries.get(dbIndex).getId().getAnswerId() + ")");
            delete(companyId, questionId, answerGroupId, answerDbEntry);
            dbIndex++;
        }
        while (inputIndex < inputAnswers.size()) {
            logger.debug("adding : (" + companyId + "," + questionId + "," + inputAnswers.get(inputIndex).getAnswerId() + ")");
            addAnswer(companyId, questionId, answerGroupId, inputAnswers.get(inputIndex));
            inputIndex++;
        }
    }

    private void updateAnswer(int companyId, int questionId, int anwerGroupId, AnswerDbType answerDbEntry, List<AnswerAttributeDbType> answerAttributeDbEntries, Answer inputAnswer) {
        boolean updated = false;
        if (!answerDbEntry.getAnswerString().equals(inputAnswer.getAnswer())) {
            answerDbEntry.setAnswerString(inputAnswer.getAnswer());
            updated = true;
        }
        if (!answerDbEntry.getLink().equals(inputAnswer.getLink())) {
            answerDbEntry.setLink(inputAnswer.getLink());
            updated = true;
        }

        if (updated) {
            answerDao.update(answerDbEntry);
        }

        updateAnswerAttributes(companyId, questionId, anwerGroupId, answerDbEntry.getId().getAnswerId(), answerAttributeDbEntries, inputAnswer);
    }

    private void updateAnswerAttributes(int companyId, int questionId, int answerGroupId, int answerId,
                                        List<AnswerAttributeDbType> answerAttributeDbEntries, Answer inputAnswer) {
        List<AnswerAttribute> inputList = inputAnswer.getAnswerAttributeList();

        int dbIndex = 0, inputIndex = 0;
        while (inputIndex < inputList.size() && dbIndex < answerAttributeDbEntries.size()) {
            AnswerAttributeDbType dbEntry = answerAttributeDbEntries.get(dbIndex);
            AnswerAttribute answerAttribute = inputList.get(inputIndex);

            if (dbEntry.getId().getAttributeId() == answerAttribute.getAttributeId()) {
                updateAnswerAttribute(dbEntry, answerAttribute);
                inputIndex++;
                dbIndex++;
            } else if (dbEntry.getId().getAttributeId() < answerAttribute.getAttributeId()) {
                answerAttributeDao.delete(dbEntry);
                dbIndex++;
            } else {
                answerAttributeDao.add(Conversions.getAnswerAttributeDbEntry(companyId, questionId, answerGroupId, answerId, answerAttribute));
                inputIndex++;
            }
        }
        while (dbIndex < answerAttributeDbEntries.size()) {
            answerAttributeDao.delete(answerAttributeDbEntries.get(dbIndex));
            dbIndex++;
        }
        while (inputIndex < inputList.size()) {
            answerAttributeDao.add(Conversions.getAnswerAttributeDbEntry(companyId, questionId, answerGroupId, answerId, inputList.get(inputIndex)));
            inputIndex++;
        }
    }

    private void updateAnswerAttribute(AnswerAttributeDbType dbEntry, AnswerAttribute answerAttribute) {
        boolean updated = false;
        if (dbEntry.getValue() != answerAttribute.getAttainedValue()) {
            dbEntry.setValue(answerAttribute.getAttainedValue());
            updated = true;
        }
        if (dbEntry.getMaxValue() != answerAttribute.getMaxValue()) {
            dbEntry.setMaxValue(answerAttribute.getMaxValue());
            updated = true;
        }
        if (updated) {
            answerAttributeDao.update(dbEntry);
        }
    }

    private void addQuestion(int companyId, int questionId, Question inputQuestion) {
        QuestionDbType questionDbEntry = Conversions.getQuestionDbEntry(companyId, questionId, inputQuestion);
        try {
            questionDao.add(questionDbEntry);
        } catch (Exception e) {
            logger.error("");
        }

    }

    private void addAnswerGroup(int companyId, int questionId, AnswerGroup answerGroup) {
        int answerGroupId = answerGroupDao.getMaxQuestionIdValue(companyId, questionId) + 1;
        AnswerGroupDbType answerGroupDbType = Conversions.getAnswerGroupDbEntry(companyId, questionId, answerGroupId, answerGroup);
        answerGroupDao.add(answerGroupDbType);

        for (Answer answer : answerGroup.getAnswers()) {
            addAnswer(companyId, questionId, answerGroupId, answer);
        }
    }

    private void addAnswer(int companyId, int questionId, int answerGroupId, Answer inputAnswer) {
        int answerId = answerDao.getMaxQuestionIdValue(companyId, questionId, answerGroupId) + 1;
        AnswerDbType answerDbEntry = Conversions.getAnswerDbEntry(companyId, questionId, answerGroupId, answerId, inputAnswer);
        answerDao.add(answerDbEntry);

        for (AnswerAttribute answerAttribute : inputAnswer.getAnswerAttributeList()) {
            AnswerAttributeDbType answerAttributeDbEntry = Conversions.getAnswerAttributeDbEntry(companyId, questionId, answerGroupId, answerId, answerAttribute);
            answerAttributeDao.add(answerAttributeDbEntry);
        }
    }

    private void delete(int companyId, int questionId, AnswerGroupDbType dbEntry) {
        //deletes the group, answer and attributes
        answerAttributeDao.delete(companyId, questionId, dbEntry.getId().getAnswerGroupId());
        answerDao.delete(companyId, questionId, dbEntry.getId().getAnswerGroupId());
        answerGroupDao.delete(dbEntry);
    }

    private void delete(int companyId, int questionId, int answerGroupId, AnswerDbType dbEntry) {
        //deletes the answer and attributes
        answerAttributeDao.delete(companyId, questionId, answerGroupId, dbEntry.getId().getAnswerId());
        answerDao.delete(dbEntry);

    }

    public static class Conversions {
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
            questionDbEntry.setLink(question.getLink());

            return questionDbEntry;
        }

        public static AnswerGroupDbType getAnswerGroupDbEntry(int companyId, int questionId, int answerGroupId, AnswerGroup answerGroup) {
            AnswerGroupDbType dbEntry = new AnswerGroupDbType();
            AnswerGroupPrimaryKey key = new AnswerGroupPrimaryKey();

            key.setCompanyId(companyId);
            key.setQuestionId(questionId);
            key.setAnswerGroupId(answerGroupId);

            dbEntry.setId(key);
            dbEntry.setAnswerGroupString(answerGroup.getAnswerGroup());
            dbEntry.setLink(answerGroup.getLink());

            return dbEntry;
        }

        public static AnswerDbType getAnswerDbEntry(int companyId, int questionId, int answerGroupId, int answerId, Answer answer) {
            AnswerDbType answerDbEntry = new AnswerDbType();
            AnswerPrimaryKey aKey = new AnswerPrimaryKey();
            answerDbEntry.setId(aKey);

            aKey.setCompanyId(companyId);
            aKey.setQuestionId(questionId);
            aKey.setAnswerGroupId(answerGroupId);
            aKey.setAnswerId(answerId);

            answerDbEntry.setAnswerString(answer.getAnswer());
            answerDbEntry.setLink(answer.getLink());

            return answerDbEntry;
        }

        public static AnswerAttributeDbType getAnswerAttributeDbEntry(int companyId, int questionId, int answerGroupId, int answerId, AnswerAttribute answerAttribute) {
            AnswerAttributeDbType dbEntry = new AnswerAttributeDbType();
            AnswerAttributePrimaryKey id = new AnswerAttributePrimaryKey();

            id.setCompanyId(companyId);
            id.setQuestionId(questionId);
            id.setAnswerGroupId(answerGroupId);
            id.setAnswerId(answerId);
            id.setAttributeId(answerAttribute.getAttributeId());

            dbEntry.setId(id);
            dbEntry.setValue(answerAttribute.getAttainedValue());
            dbEntry.setMaxValue(answerAttribute.getMaxValue());

            return dbEntry;
        }

        public static List<Question> getQuestions(List<QuestionDbType> list) {
            List<Question> outList = new ArrayList<Question>(list.size());
            for (QuestionDbType questionDbType : list) {
                outList.add(getQuestion(questionDbType));
            }

            return outList;
        }

        public static Question getQuestion(QuestionDbType questionDbEntry) {
            Question question = new Question();

            question.setFunction(questionDbEntry.getFunction());
            question.setParentId(questionDbEntry.getParentId());
            question.setQuestion(questionDbEntry.getQuestionString());
            question.setQuestionId(questionDbEntry.getId().getQuestionId());

            question.setEnabled(questionDbEntry.isEnabled());
            question.setPlacement(questionDbEntry.getPlacement());
            question.setLink(questionDbEntry.getLink());

            Hibernate.initialize(questionDbEntry.getAnswerGroups());
            List<AnswerGroup> list = new ArrayList<AnswerGroup>(questionDbEntry.getAnswerGroups().size());
            question.setAnswerGroups(list);


            for (AnswerGroupDbType answerGroupDbType : questionDbEntry.getAnswerGroups()) {
                list.add(getAnswerGroup(answerGroupDbType));
            }

            return question;
        }

        public static AnswerGroup getAnswerGroup(AnswerGroupDbType answerGroupDbEntry) {
            AnswerGroup answerGroup = new AnswerGroup();

            answerGroup.setAnswerGroup(answerGroupDbEntry.getAnswerGroupString());
            answerGroup.setDisplayEnum(answerGroupDbEntry.getDisplayEnum());
            answerGroup.setLink(answerGroupDbEntry.getLink());

            Hibernate.initialize(answerGroupDbEntry.getAnswers());
            List<Answer> list = new ArrayList<Answer>(answerGroupDbEntry.getAnswers().size());
            answerGroup.setAnswers(list);


            for (AnswerDbType answerDbType : answerGroupDbEntry.getAnswers()) {
                list.add(getAnswer(answerDbType));
            }

            return answerGroup;
        }

        public static Answer getAnswer(AnswerDbType answerDbEntry) {
            Answer answer = new Answer();

            answer.setAnswer(answerDbEntry.getAnswerString());
            answer.setAnswerId(answerDbEntry.getId().getAnswerId());
            answer.setLink(answerDbEntry.getLink());

            Hibernate.initialize(answerDbEntry.getAnswerAttributes());
            List<AnswerAttribute> list = new ArrayList<AnswerAttribute>(answerDbEntry.getAnswerAttributes().size());
            answer.setAnswerAttributeList(list);


            for (AnswerAttributeDbType answerAttributeDbType : answerDbEntry.getAnswerAttributes()) {
                list.add(getAnswerAttribute(answerAttributeDbType));
            }

            return answer;
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