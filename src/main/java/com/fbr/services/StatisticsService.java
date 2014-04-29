package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.CustomerResponseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service("statisticsService")
public class StatisticsService {
    @Autowired
    private CustomerResponseDao customerResponseDao;

    @PostConstruct
    void preProcessInfo(){
        List<CustomerResponseDao.CustomerResponse> listResponse = customerResponseDao.getResponses();
    }
}
