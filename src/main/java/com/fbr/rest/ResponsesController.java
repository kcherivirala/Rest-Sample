package com.fbr.rest;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.domain.Response.Response;
import com.fbr.domain.Response.ResponseList;
import com.fbr.services.AggregatorService;
import com.fbr.services.ResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ResponsesController {
    @Autowired
    private ResponseService responseService;
    @Autowired
    private View jsonView_i;

    private static final String DATA_FIELD = "data";
    private static final String ERROR_FIELD = "error";

    @RequestMapping(value = {"/company/{companyId}/branch/{branchId}/responses"}, method = {RequestMethod.POST})
    @ResponseBody
    public Response addResponses(@PathVariable("companyId") int companyId, @PathVariable("branchId") int branchId,
                                 @RequestBody ResponseList responseList, HttpServletResponse httpResponse_p, WebRequest request_p) throws Exception {
        responseService.processResponse(companyId, branchId, responseList.getResponses());

        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        return null;
    }

    /* Exception Handler*/
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllException(Exception ex, final HttpServletRequest httpServletRequest) {
        return new ModelAndView(jsonView_i, ERROR_FIELD, ex.getMessage());
    }
}
