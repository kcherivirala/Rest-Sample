package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Attribute.Entities.AttributeDbType;
import com.fbr.Dao.Attribute.Entities.AttributeValuesDbType;
import com.fbr.Dao.Graph.Entities.*;
import com.fbr.Dao.Question.Entities.AnswerDbType;
import com.fbr.Dao.Question.Entities.QuestionDbType;
import com.fbr.Dao.Response.CustomerResponseDao;
import com.fbr.Dao.ResponseAggregateDbType;
import com.fbr.domain.Attribute.Attribute;
import com.fbr.domain.Attribute.AttributeValue;
import com.fbr.domain.Question.Answer;

import java.util.Comparator;

public class Comparators {
    public static Comparator<ResponseAggregateDbType> aggregateComparator = new Comparator<ResponseAggregateDbType>() {
        @Override
        public int compare(ResponseAggregateDbType first, ResponseAggregateDbType second) {
            if (first.getId().getBranchId() < second.getId().getBranchId()) return -1;
            else if (first.getId().getBranchId() > second.getId().getBranchId()) return 1;
            else {
                return first.getId().getAttributeId() - (second.getId().getAttributeId());
            }
        }
    };

    public static Comparator<AnswerDbType> COMPARE_DB_ANSWERS = new Comparator<AnswerDbType>() {
        @Override
        public int compare(AnswerDbType first, AnswerDbType second) {
            if (first.getId().getQuestionId() == second.getId().getQuestionId())
                return first.getId().getAnswerId() - second.getId().getAnswerId();
            return first.getId().getQuestionId() - second.getId().getQuestionId();
        }
    };

    public static Comparator<Answer> COMPARE_DOMAIN_ANSWERS = new Comparator<Answer>() {
        @Override
        public int compare(Answer first, Answer second) {
            return first.getAnswerId() - second.getAnswerId();
        }
    };

    public static Comparator<QuestionDbType> COMPARE_DB_QUESTIONS = new Comparator<QuestionDbType>() {
        @Override
        public int compare(QuestionDbType first, QuestionDbType second) {
            return first.getId().getQuestionId() - second.getId().getQuestionId();
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

    public static Comparator<TrendDbType> COMPARE_TRENDS = new Comparator<TrendDbType>() {
        @Override
        public int compare(TrendDbType first, TrendDbType second) {
            return first.getTrendId().compareTo(second.getTrendId());
        }
    };

    public static Comparator<TrendAttributesDbType> COMPARE_TREND_ATTRIBUTES = new Comparator<TrendAttributesDbType>() {
        @Override
        public int compare(TrendAttributesDbType first, TrendAttributesDbType second) {
            if (first.getId().getTrendId().equals(second.getId().getTrendId())) {
                return first.getId().getAttributeId() - second.getId().getAttributeId();
            }
            return first.getId().getTrendId().compareTo(second.getId().getTrendId());
        }
    };

    public static Comparator<TrendFiltersDbType> COMPARE_TREND_FILTERS = new Comparator<TrendFiltersDbType>() {
        @Override
        public int compare(TrendFiltersDbType first, TrendFiltersDbType second) {
            if (first.getId().getTrendId().equals(second.getId().getTrendId())) {
                return first.getId().getAttributeId() - second.getId().getAttributeId();
            }
            return first.getId().getTrendId().compareTo(second.getId().getTrendId());
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
                if (first.getResponse().getTimestamp().before(second.getResponse().getTimestamp())) {
                    return 1;
                }
                return -1;
            } else {
                return first.getResponse().getCompanyId() - second.getResponse().getCompanyId();
            }

        }
    };
}
