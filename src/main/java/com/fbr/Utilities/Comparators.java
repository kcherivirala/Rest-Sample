package com.fbr.Utilities;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Attribute.Entities.AttributeDbType;
import com.fbr.Dao.Attribute.Entities.AttributeValuesDbType;
import com.fbr.Dao.Company.Entities.BranchDbType;
import com.fbr.Dao.Company.Entities.CompanyDbType;
import com.fbr.Dao.Graph.Entities.GraphAttributesDbType;
import com.fbr.Dao.Graph.Entities.GraphDbType;
import com.fbr.Dao.Graph.Entities.GraphFiltersDbType;
import com.fbr.Dao.Question.Entities.AnswerAttributeDbType;
import com.fbr.Dao.Question.Entities.AnswerDbType;
import com.fbr.Dao.Question.Entities.AnswerGroupDbType;
import com.fbr.Dao.Question.Entities.QuestionDbType;
import com.fbr.Dao.Response.CustomerResponseDao;
import com.fbr.domain.Attribute.Attribute;
import com.fbr.domain.Attribute.AttributeValue;
import com.fbr.domain.Company.Branch;
import com.fbr.domain.Question.Answer;
import com.fbr.domain.Question.AnswerGroup;

import java.util.Comparator;

public class Comparators {
    public static Comparator<QuestionDbType> COMPARE_DB_QUESTIONS = new Comparator<QuestionDbType>() {
        @Override
        public int compare(QuestionDbType first, QuestionDbType second) {
            return first.getId().getQuestionId() - second.getId().getQuestionId();
        }
    };

    public static Comparator<AnswerGroupDbType> COMPARE_DB_ANSWER_GROUPS = new Comparator<AnswerGroupDbType>() {
        @Override
        public int compare(AnswerGroupDbType first, AnswerGroupDbType second) {
            return first.getId().getAnswerGroupId() - second.getId().getAnswerGroupId();
        }
    };

    public static Comparator<AnswerDbType> COMPARE_DB_ANSWERS = new Comparator<AnswerDbType>() {
        @Override
        public int compare(AnswerDbType first, AnswerDbType second) {
                return first.getId().getAnswerId() - second.getId().getAnswerId();
        }
    };

    public static Comparator<AnswerAttributeDbType> COMPARE_DB_ANSWER_ATTRIBUTES = new Comparator<AnswerAttributeDbType>() {
        @Override
        public int compare(AnswerAttributeDbType first, AnswerAttributeDbType second) {
                return first.getId().getAttributeId() - second.getId().getAttributeId();
        }
    };

    public static Comparator<AnswerGroup> COMPARE_DOMAIN_ANSWER_GROUPS = new Comparator<AnswerGroup>() {
        @Override
        public int compare(AnswerGroup first, AnswerGroup second) {
            return first.getAnswerGroupId() - second.getAnswerGroupId();
        }
    };


    public static Comparator<Answer> COMPARE_DOMAIN_ANSWERS = new Comparator<Answer>() {
        @Override
        public int compare(Answer first, Answer second) {
            return first.getAnswerId() - second.getAnswerId();
        }
    };



    public static Comparator<GraphDbType> COMPARE_GRAPHS = new Comparator<GraphDbType>() {
        @Override
        public int compare(GraphDbType first, GraphDbType second) {
            return first.getGraphId().compareTo(second.getGraphId());
        }
    };

    public static Comparator<GraphAttributesDbType> COMPARE_GRAPH_ATTRIBUTES = new Comparator<GraphAttributesDbType>() {
        @Override
        public int compare(GraphAttributesDbType first, GraphAttributesDbType second) {
            if (first.getId().getGraphId().equals(second.getId().getGraphId())) {
                return first.getId().getAttributeId() - second.getId().getAttributeId();
            }
            return first.getId().getGraphId().compareTo(second.getId().getGraphId());
        }
    };

    public static Comparator<GraphFiltersDbType> COMPARE_GRAPH_FILTERS = new Comparator<GraphFiltersDbType>() {
        @Override
        public int compare(GraphFiltersDbType first, GraphFiltersDbType second) {
            if (first.getId().getGraphId().equals(second.getId().getGraphId())) {
                return first.getId().getAttributeId() - second.getId().getAttributeId();
            }
            return first.getId().getGraphId().compareTo(second.getId().getGraphId());
        }
    };

    public static Comparator<AttributeDbType> COMPARE_ATTRIBUTES = new Comparator<AttributeDbType>() {
        @Override
        public int compare(AttributeDbType first, AttributeDbType second) {
            return first.getAttributeId() - second.getAttributeId();
        }
    };

    public static Comparator<AttributeValuesDbType> COMPARE_ATTRIBUTE_VALUES = new Comparator<AttributeValuesDbType>() {
        @Override
        public int compare(AttributeValuesDbType first, AttributeValuesDbType second) {
            if (first.getId().getAttributeId() == second.getId().getAttributeId())
                return first.getId().getValue() - second.getId().getValue();
            else
                return first.getId().getAttributeId() - second.getId().getAttributeId();
        }
    };

    public static Comparator<AttributeValue> COMPARE_DOMAIN_ATTRIBUTE_VALUES = new Comparator<AttributeValue>() {
        @Override
        public int compare(AttributeValue first, AttributeValue second) {
            return first.getValue() - second.getValue();
        }
    };

    public static Comparator<Attribute> COMPARE_DOMAIN_ATTRIBUTES = new Comparator<Attribute>() {
        @Override
        public int compare(Attribute first, Attribute second) {
            return first.getAttributeId() - second.getAttributeId();
        }
    };


    public static Comparator<CustomerResponseDao.CustomerResponseAndValues> COMPARE_RESPONSES = new Comparator<CustomerResponseDao.CustomerResponseAndValues>() {
        @Override
        public int compare(CustomerResponseDao.CustomerResponseAndValues first, CustomerResponseDao.CustomerResponseAndValues second) {
            if (first.getResponse().getCompanyId() == second.getResponse().getCompanyId()) {
                if (first.getResponse().getEndTimestamp().before(second.getResponse().getEndTimestamp())) {
                    return 1;
                }
                return -1;
            } else {
                return first.getResponse().getCompanyId() - second.getResponse().getCompanyId();
            }

        }
    };

    public static Comparator<CompanyDbType> COMPARE_DB_COMPANIES = new Comparator<CompanyDbType>() {
        @Override
        public int compare(CompanyDbType companyDbType, CompanyDbType companyDbType2) {
            return companyDbType.getCompanyId() - companyDbType2.getCompanyId();
        }
    };

    public static Comparator<BranchDbType> COMPARE_DB_BRANCHES = new Comparator<BranchDbType>() {
        @Override
        public int compare(BranchDbType first, BranchDbType second) {
            if (first.getId().getCompanyId() == second.getId().getBranchId()) {
                return first.getId().getBranchId() - second.getId().getBranchId();
            }
            return first.getId().getCompanyId() - second.getId().getCompanyId();
        }
    };

    public static Comparator<Branch> COMPARE_BRANCHES = new Comparator<Branch>() {
        @Override
        public int compare(Branch first, Branch second) {
            return first.getId() - second.getId();
        }
    };
}
