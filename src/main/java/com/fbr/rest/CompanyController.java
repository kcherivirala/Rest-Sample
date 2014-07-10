package com.fbr.rest;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.domain.Company.Company;
import com.fbr.services.CompanyService;
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
public class CompanyController {
    @Autowired
    private CompanyService companyService;
    @Autowired
    private View jsonView_i;

    private static final String DATA_FIELD = "data";
    private static final String ERROR_FIELD = "error";

    @RequestMapping(value = {"/companies"}, method = {RequestMethod.POST})
    @ResponseBody
    public Company addCompany(@RequestBody Company company,
                              HttpServletResponse httpResponse_p, WebRequest request_p) throws Exception {
        Company returnVal = companyService.addCompanyAndBranches(company);
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/company/" + returnVal.getId());
        return returnVal;
    }

    @RequestMapping(value = {"/company/{companyId}"}, method = {RequestMethod.PUT})
    @ResponseBody
    public Company updateCompany(@PathVariable("companyId") int companyId, @RequestBody Company company,
                                 HttpServletResponse httpResponse_p, WebRequest request_p) throws Exception {
        Company returnVal = companyService.updateCompanyAndBranches(companyId, company);
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/company/" + returnVal.getId());
        return returnVal;
    }

    @RequestMapping(value = {"/company/{companyId}"}, method = {RequestMethod.DELETE})
    @ResponseBody
    public Company deleteCompany(@PathVariable("companyId") int companyId,
                                 HttpServletResponse httpResponse_p, WebRequest request_p) throws Exception {
        companyService.deleteCompanyAndBranches(companyId);
        httpResponse_p.setStatus(HttpStatus.OK.value());
        return null;
    }

    @RequestMapping(value = {"/companies"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<Company> getCompanies() throws Exception {
        return companyService.getCompanies();
    }

    @RequestMapping(value = {"/company/{companyId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public Company getCompany(@PathVariable("companyId") int companyId) throws Exception {
        return companyService.getCompany(companyId);
    }

    /* Exception Handler*/
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllException(Exception ex, final HttpServletRequest httpServletRequest) {
        return new ModelAndView(jsonView_i, ERROR_FIELD, ex.getMessage());
    }
}
