package com.fbr.rest;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.domain.Question.Question;
import com.fbr.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private View jsonView_i;

    private static final String DATA_FIELD = "data";
    private static final String ERROR_FIELD = "error";

    @RequestMapping(value = {"/company/{companyId}/questions"}, method = {RequestMethod.POST})
    @ResponseBody
    public Question addQuestionsAndAnswers(@PathVariable("companyId") int companyId, @RequestBody Question question,
                                           HttpServletResponse httpResponse_p, WebRequest request_p) throws Exception {
        Question returnVal = questionService.addQuestionAndAnswers(companyId, question);
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/questions/company/" + companyId + "/question/" + returnVal.getQuestionId());
        return returnVal;
    }

    @RequestMapping(value = {"/company/{companyId}/question/{questionId}"}, method = {RequestMethod.PUT})
    @ResponseBody
    public Question updateQuestionAndAnswers(@PathVariable("companyId") int companyId, @PathVariable("questionId") int questionId, @RequestBody Question question,
                                             HttpServletResponse httpResponse_p, WebRequest request_p) throws Exception {
        Question returnVal = questionService.updateQuestionAndAnswers(companyId, questionId, question);
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/questions/company/" + companyId + "/question/" + returnVal.getQuestionId());
        return returnVal;

    }

    @RequestMapping(value = {"/company/{companyId}/question/{questionId}"}, method = RequestMethod.DELETE)
    @ResponseBody
    public Question deleteQuestionAndAnswers(@PathVariable("companyId") int companyId, @PathVariable("questionId") int questionId,
                                             HttpServletResponse httpResponse_p) throws Exception {
        questionService.deleteQuestionAndAnswers(companyId, questionId);
        httpResponse_p.setStatus(HttpStatus.OK.value());
        return null;
    }

    @RequestMapping(value = {"/company/{companyId}/question/{questionId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public Question getQuestionAndAnswers(@PathVariable("companyId") int companyId,
                                          @PathVariable("questionId") int questionId) throws Exception {
        return questionService.getQuestionAndAnswers(companyId, questionId);
    }

    @RequestMapping(value = {"/company/{companyId}/questions"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<Question> getQuestionsAndAnswers(@PathVariable("companyId") int companyId) throws Exception {
        return questionService.getQuestionAndAnswers(companyId);
    }

    @RequestMapping(value = {"/company/{companyId}/enabledQuestions"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<Question> getEnabledQuestionsAndAnswers(@PathVariable("companyId") int companyId) throws Exception {
        return questionService.getEnabledQuestionAndAnswers(companyId);
    }

    /* Exception Handler*/
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllException(Exception ex, final HttpServletRequest httpServletRequest) {
        return new ModelAndView(jsonView_i, ERROR_FIELD, ex.getMessage());
    }
}
