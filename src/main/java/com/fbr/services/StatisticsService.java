package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Response.CustomerResponseDao;
import com.fbr.domain.Attribute.Attribute;
import com.fbr.domain.Graph.Graph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service("statisticsService")
public class StatisticsService {
    @Autowired
    private CustomerResponseDao customerResponseDao;
    @Autowired
    private AttributeService attributeService;
    @Autowired
    private GraphService graphService;

    @PostConstruct
    void preProcessInfo() {

    }

    private int processPerCompanyResponses(int companyId, List<CustomerResponseDao.CustomerResponseAndValues> listResponse) { //sorted based on time
        int i=0;
        Collections.sort(listResponse, Comparators.COMPARE_RESPONSES);
        List<Attribute> listAttribute = attributeService.getAttributesByCompany(companyId);
        List<Graph> listGraph = graphService.getGraphs(companyId);


        for(Graph graph: listGraph){
            List<Integer> listFilterId = graph.getFilterList();

            List<Attribute> filterAttributes = getFilters(listFilterId, listAttribute);



            i = 0;
            while (i < listResponse.size() && listResponse.get(i).getResponse().getCompanyId()==companyId) {
                CustomerResponseDao.CustomerResponseAndValues responseAndValues = listResponse.get(i);
            }
        }





        return i;
    }

    private List<Attribute> getFilters(List<Integer> listFiltersId, List<Attribute> listAttribute){
        List<Attribute> out = new ArrayList<Attribute>(listFiltersId.size());

        return out;
    }
}
