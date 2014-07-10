package com.fbr.rest;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.domain.Response.Response;
import com.fbr.domain.Statistic.AttributeLevelStatistics;
import com.fbr.domain.Statistic.DashboardInfo;
import com.fbr.services.AttributeService;
import com.fbr.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private AttributeService attributeService;

    @Autowired
    private View jsonView_i;

    private static final String DATA_FIELD = "data";
    private static final String ERROR_FIELD = "error";

    @RequestMapping(value = {"/company/{companyId}/refresh"}, method = {RequestMethod.PUT})
    @ResponseBody
    public Response resetCompanyData(@PathVariable("companyId") int companyId,
                                     HttpServletResponse httpResponse_p) throws Exception {
        statisticsService.resetCompanyData(companyId);
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        return null;
    }

    @RequestMapping(value = {"/company/{companyId}/graph/{graphId}/statistics"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<AttributeLevelStatistics> getStatistics(@RequestParam(required = false) Map<String, String> reqParams,
                                                        @PathVariable("companyId") int companyId, @PathVariable("graphId") String graphId) throws Exception {
        Map<String, Integer> mapOfFilters = attributeService.getMapOfInputArgumentFilters(companyId, reqParams);
        return statisticsService.getGraphData(companyId, graphId, mapOfFilters);
    }


    @RequestMapping(value = {"/company/{companyId}/dashboard"}, method = {RequestMethod.GET})
    @ResponseBody
    public DashboardInfo getDashboard(@PathVariable("companyId") int companyId) throws Exception {
        return statisticsService.getDashboardInfo(companyId);
    }

    @RequestMapping(value = {"/company/{companyId}/branch/{branchId}/dashboard"}, method = {RequestMethod.GET})
    @ResponseBody
    public DashboardInfo getDashboard(@PathVariable("companyId") int companyId, @PathVariable("branchId") int branchId) throws Exception {
        return statisticsService.getDashboardInfo(companyId, branchId);
    }

    @RequestMapping(value = {"/company/{companyId}/hourlyRefresh"}, method = {RequestMethod.PUT})
    @ResponseBody
    public Response trendRefresh(@PathVariable("companyId") int companyId,
                                 HttpServletResponse httpResponse_p) throws Exception {

        statisticsService.refreshHourlyResponses(companyId);
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        return null;
    }

    @RequestMapping(value = {"/company/{companyId}/dailyRefresh"}, method = {RequestMethod.PUT})
    @ResponseBody
    public Response normalRefresh(@PathVariable("companyId") int companyId,
                                  HttpServletResponse httpResponse_p) throws Exception {
        statisticsService.refreshDailyLevelResponses(companyId);
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        return null;
    }

    /* Exception Handler*/
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllException(Exception ex, final HttpServletRequest httpServletRequest) {
        return new ModelAndView(jsonView_i, ERROR_FIELD, ex.getMessage());
    }
}
