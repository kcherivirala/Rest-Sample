package com.fbr.rest;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.domain.Attribute.Attribute;
import com.fbr.services.AttributeService;
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
public class AttributeController {
    @Autowired
    private AttributeService attributeService;
    @Autowired
    private View jsonView_i;

    private static final String DATA_FIELD = "data";
    private static final String ERROR_FIELD = "error";

    @RequestMapping(value = {"/attributes"}, method = {RequestMethod.POST})
    @ResponseBody
    public Attribute addAttribute(@RequestBody Attribute attribute,
                                  HttpServletResponse httpResponse_p, WebRequest request_p) throws Exception {
        Attribute returnVal = attributeService.addAttributeAndValues(attribute);

        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/attribute/" + returnVal.getAttributeId());
        return returnVal;
    }

    @RequestMapping(value = {"/attribute/{attrId}"}, method = {RequestMethod.PUT})
    @ResponseBody
    public Attribute updateAttributeAndValues(@PathVariable("attrId") int attrId, @RequestBody Attribute attribute,
                                              HttpServletResponse httpResponse_p, WebRequest request_p) throws Exception {
        Attribute dbEntry = attributeService.updateAttributeAndValues(attrId, attribute);

        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        httpResponse_p.setHeader("Location", request_p.getContextPath() + "/attribute/" + dbEntry.getAttributeId());
        return dbEntry;
    }

    @RequestMapping(value = {"/attribute/{attrId}"}, method = RequestMethod.DELETE)
    @ResponseBody
    public Attribute deleteAttributeAndValues(@PathVariable("attrId") int attrId,
                                              HttpServletResponse httpResponse_p) throws Exception {
        attributeService.deleteAttributeAndValues(attrId);
        httpResponse_p.setStatus(HttpStatus.OK.value());
        return null;
    }

    @RequestMapping(value = {"/attribute/{attrId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public Attribute getAttributeAndValues(@PathVariable("attrId") int attrId) throws Exception {
        return attributeService.getAttributeAndValues(attrId);
    }

    @RequestMapping(value = {"/attributes"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<Attribute> getAttributesAndValues() throws Exception {
        return attributeService.getAttributeAndValues();
    }

    @RequestMapping(value = {"/company/{companyId}/attributes"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<Attribute> getAttributesAndValuesForCompany(@PathVariable("attrId") int companyId) throws Exception {
        return attributeService.getAttributesByCompany(companyId);
    }

    /* Exception Handler*/
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllException(Exception ex, final HttpServletRequest httpServletRequest) {
        return new ModelAndView(jsonView_i, ERROR_FIELD, ex.getMessage());
    }
}
