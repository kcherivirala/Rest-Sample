package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Company.Entities.BranchDbType;
import com.fbr.Dao.Response.CustomerResponseDao;
import com.fbr.domain.Attribute.Attribute;
import com.fbr.domain.Graph.Graph;
import com.fbr.domain.Statistic.ConstraintLevelStatistics;
import com.fbr.domain.Statistic.GraphLevelStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service("statisticsService")
public class StatisticsService {
    @Autowired
    private CustomerResponseDao customerResponseDao;
    @Autowired
    private AttributeService attributeService;
    @Autowired
    private GraphService graphService;
    @Autowired
    private CompanyService companyService;

    @PostConstruct
    void preProcessInfo() {

    }

    private void processPerCompanyResponses(int companyId, List<CustomerResponseDao.CustomerResponseAndValues> listResponse) { //sorted based on time
        int i = 0;
        Collections.sort(listResponse, Comparators.COMPARE_RESPONSES);
        List<Attribute> listAttribute = attributeService.getAttributesByCompany(companyId);    //already sorted list
        List<BranchDbType> branches = companyService.getDbBranches(companyId);
        List<Graph> listGraph = graphService.getGraphs(companyId);

        for (Graph graph : listGraph) {
            List<Attribute> filterAttributes = getFilters(graph.getFilterList(), listAttribute);
            int constraints = numberOfConstraints(filterAttributes);

            List<ConstraintLevelStatistics> listConstraintLevelStatistics = getConstraintLevels(constraints, filterAttributes, branches);
            populateStatistics(listConstraintLevelStatistics, listResponse);
            GraphLevelStatistics graphLevelStatistics = new GraphLevelStatistics();

            graphLevelStatistics.setGraphId(graph.getGraphId());
            graphLevelStatistics.setListConstraintLevelStatistics(listConstraintLevelStatistics);
        }
    }

    private List<Attribute> getFilters(List<Integer> listFiltersId, List<Attribute> listAttribute) {
        Collections.sort(listFiltersId);
        List<Attribute> out = new ArrayList<Attribute>(listFiltersId.size());

        int idIndex = 0, attrIndex = 0;
        while (idIndex < listFiltersId.size() && attrIndex < listAttribute.size()) {
            int id = listFiltersId.get(idIndex);
            Attribute attribute = listAttribute.get(attrIndex);

            if (id == attribute.getAttributeId()) {
                out.add(attribute);
                idIndex++;
                attrIndex++;
            } else if (id < attribute.getAttributeId()) {
                idIndex++;
            } else {
                attrIndex++;
            }
        }
        return out;
    }

    private int numberOfConstraints(List<Attribute> filterAttributes) {
        int constraints = 1;
        for (Attribute attribute : filterAttributes) {
            constraints *= attribute.getAttributeValues().size();
        }
        return constraints;
    }

    private List<ConstraintLevelStatistics> getConstraintLevels(int constraints, List<Attribute> filterAttributes, List<BranchDbType> branches) {
        List<ConstraintLevelStatistics> out = new ArrayList<ConstraintLevelStatistics>(constraints * branches.size());


        return out;
    }

    private void populateStatistics(List<ConstraintLevelStatistics> listConstraintLevelStatistics, List<CustomerResponseDao.CustomerResponseAndValues> listResponse) {

    }
}
