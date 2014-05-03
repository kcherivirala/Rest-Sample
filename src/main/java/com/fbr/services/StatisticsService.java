package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Response.CustomerResponseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service("statisticsService")
public class StatisticsService {
    @Autowired
    private CustomerResponseDao customerResponseDao;

    @PostConstruct
    void preProcessInfo() {
        List<CustomerResponseDao.CustomerResponseAndValues> listResponse = customerResponseDao.getResponses();
        Collections.sort(listResponse, Comparators.COMPARE_RESPONSES);

    }

    private int processPerCompanyResponses(int index, List<CustomerResponseDao.CustomerResponseAndValues> listResponse) { //sorted based on time
        int i = index;
        while (i < listResponse.size()) {

        }
        return i;
    }

    private static class Comparators {
        private static Comparator<CustomerResponseDao.CustomerResponseAndValues> COMPARE_RESPONSES = new Comparator<CustomerResponseDao.CustomerResponseAndValues>() {
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
}
