package com.fbr.rest;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.domain.Graph.Graph;
import com.fbr.services.GraphService;
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
public class GraphController {
    @Autowired
    private GraphService graphService;
    @Autowired
    private View jsonView_i;

    private static final String DATA_FIELD = "data";
    private static final String ERROR_FIELD = "error";

    @RequestMapping(value = {"/company/{companyId}/graphs"}, method = {RequestMethod.POST})
    @ResponseBody
    public Graph addGraph(@PathVariable("companyId") int companyId, @RequestBody Graph graph,
                          HttpServletResponse httpResponse_p, WebRequest request_p) throws Exception {
        Graph returnVal = graphService.addGraph(companyId, graph);
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/graph/" + returnVal.getGraphId());
        return returnVal;
    }

    @RequestMapping(value = {"/company/{companyId}/graph/{graphId}"}, method = {RequestMethod.PUT})
    @ResponseBody
    public Graph updateGraph(@PathVariable("companyId") int companyId, @PathVariable("graphId") String graphId, @RequestBody Graph graph,
                             HttpServletResponse httpResponse_p, WebRequest request_p) throws Exception {
        Graph returnVal = graphService.updateGraph(companyId, graphId, graph);
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/graph/" + graphId);
        return returnVal;
    }

    @RequestMapping(value = {"/company/{companyId}/graph/{graphId}"}, method = {RequestMethod.DELETE})
    @ResponseBody
    public Graph deleteGraph(@PathVariable("companyId") int companyId, @PathVariable("graphId") String graphId,
                             HttpServletResponse httpResponse_p) throws Exception {
        graphService.deleteGraph(companyId, graphId);
        httpResponse_p.setStatus(HttpStatus.OK.value());
        return null;
    }

    @RequestMapping(value = {"/company/{companyId}/graphs"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<Graph> getGraphs(@PathVariable("companyId") int companyId) throws Exception {
        return graphService.getGraphs(companyId);
    }

    @RequestMapping(value = {"/company/{companyId}/graph/{graphId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public Graph getGraph(@PathVariable("companyId") int companyId,
                          @PathVariable("graphId") String graphId) throws Exception {
        return graphService.getGraph(companyId, graphId);
    }

    /* Exception Handler*/
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllException(Exception ex, final HttpServletRequest httpServletRequest) {
        return new ModelAndView(jsonView_i, ERROR_FIELD, ex.getMessage());
    }
}
