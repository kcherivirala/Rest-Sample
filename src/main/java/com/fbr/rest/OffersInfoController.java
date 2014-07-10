package com.fbr.rest;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.domain.Offers_Info.OffersInfo;
import com.fbr.services.OffersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class OffersInfoController {
    @Autowired
    private OffersService offersService;
    @Autowired
    private View jsonView_i;

    private static final String DATA_FIELD = "data";
    private static final String ERROR_FIELD = "error";

    @RequestMapping(value = {"/company/{companyId}/offersAndInfo"}, method = {RequestMethod.POST})
    @ResponseBody
    public OffersInfo addOffersInfo(@PathVariable("companyId") int companyId, @RequestBody OffersInfo offersInfo,
                                    HttpServletResponse httpResponse_p) throws Exception {
        offersService.addOffersInfo(companyId, offersInfo);
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        return offersInfo;
    }

    @RequestMapping(value = {"/company/{companyId}/offersAndInfo/{offerId}"}, method = {RequestMethod.PUT})
    @ResponseBody
    public OffersInfo updateOffersInfo(@PathVariable("companyId") int companyId, @PathVariable("offerId") int offerId, @RequestBody OffersInfo offersInfo,
                                       HttpServletResponse httpResponse_p) throws Exception {
        offersService.updateOffersInfo(companyId, offerId, offersInfo);
        httpResponse_p.setStatus(HttpStatus.CREATED.value());
        return offersInfo;
    }

    @RequestMapping(value = {"/offersAndInfo"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<OffersInfo> getOffersInfo() throws Exception {
        return offersService.getOffersInfo();
    }

    @RequestMapping(value = {"/company/{companyId}/offersAndInfo"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<OffersInfo> getOffersInfo(@PathVariable("companyId") int companyId) throws Exception {
        return offersService.getOffersInfo(companyId);
    }

    @RequestMapping(value = {"/company/{companyId}/offersAndInfo/{offerId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public OffersInfo getOfferOrInfo(@PathVariable("companyId") int companyId, @PathVariable("offerId") int offerId) throws Exception {
        return offersService.getOfferOrInfo(companyId, offerId);
    }

    @RequestMapping(value = {"/company/{companyId}/branch/{branchId}/offersAndInfo"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<OffersInfo> getOffersInfo(@PathVariable("companyId") int companyId, @PathVariable("branchId") int branchId) throws Exception {
        return offersService.getOffersInfo(companyId, branchId);
    }

    /* Exception Handler*/
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllException(Exception ex, final HttpServletRequest httpServletRequest) {
        return new ModelAndView(jsonView_i, ERROR_FIELD, ex.getMessage());
    }
}
