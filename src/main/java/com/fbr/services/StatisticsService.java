package com.fbr.services;

/*
 *  ***********************************************************
 *   Copyright (c) 2013 VMware, Inc.  All rights reserved.
 *  ***********************************************************
 */

import com.fbr.Dao.Company.CompanyDao;
import com.fbr.Dao.Company.Entities.BranchDbType;
import com.fbr.Dao.Company.Entities.CompanyDbType;
import com.fbr.Dao.Response.CustomerResponseDao;
import com.fbr.Dao.Response.Entities.CustomerResponseValuesDbType;
import com.fbr.domain.Attribute.Attribute;
import com.fbr.domain.Attribute.AttributeValue;
import com.fbr.domain.Graph.Graph;
import com.fbr.domain.Statistic.AttributeLevelStatistics;
import com.fbr.domain.Statistic.ConstraintLevelStatistics;
import com.fbr.domain.Statistic.DailyAttributeStatisticValues;
import com.fbr.domain.Statistic.GraphLevelStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

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
    @Autowired
    private CompanyDao companyDao;

    private List<CompanyData> listCompanyData;

    @PostConstruct
    public void init(){
        List<CompanyDbType> companies = companyDao.findAll();
        listCompanyData = new ArrayList<CompanyData>(companies.size());

        for(CompanyDbType company: companies){
            listCompanyData.add(processPerCompanyResponses(company.getCompanyId(), company.getName(), 20140401,20140501,30));
        }
    }

    public void resetCompanyData(int companyId){
        CompanyDbType company =  companyDao.find(companyId);
        listCompanyData.add(processPerCompanyResponses(company.getCompanyId(), company.getName(), 20140428,20140501,5));
    }

    private CompanyData processPerCompanyResponses(int companyId, String companyName, int startDate, int endDate, int noOfDays) {
        List<CustomerResponseDao.CustomerResponseAndValues> listResponse = customerResponseDao.getResponses(companyId);
        Collections.sort(listResponse, Comparators.COMPARE_RESPONSES);

        List<Attribute> listAttribute = attributeService.getAttributesByCompany(companyId);    //already sorted list
        List<BranchDbType> branches = companyService.getDbBranches(companyId);
        List<Graph> listGraph = graphService.getGraphs(companyId);

        List<GraphData> listGraphData = new ArrayList<GraphData>(listGraph.size());
        for (Graph graph : listGraph) {
            List<Attribute> filterAttributes = attributeService.getAttributes(graph.getFilterList(), listAttribute);
            List<Attribute> weightedAttributes = attributeService.getAttributes(graph.getAttributeList(), listAttribute);
            int constraints = numberOfConstraints(filterAttributes);

            List<ConstraintLevelStatistics> listConstraintLevelStatistics = new ArrayList<ConstraintLevelStatistics>(constraints * branches.size());
            populateConstraintLevels(listConstraintLevelStatistics, filterAttributes, branches);    //start with index of -1
            initialiseConstraintLevelStatistics(listConstraintLevelStatistics, weightedAttributes, startDate, endDate, noOfDays);


            GraphLevelStatistics graphLevelStatistics = new GraphLevelStatistics();
            graphLevelStatistics.setGraphId(graph.getGraphId());
            graphLevelStatistics.setListConstraintLevelStatistics(listConstraintLevelStatistics);

            GraphData graphData = new GraphData();
            graphData.graphId = graph.getGraphId();
            graphData.graphLevelStatistics = graphLevelStatistics;
            graphData.mapOFConstraints = getMapOfConstraints(listConstraintLevelStatistics);
            graphData.mapOfAttributes = getMapOfAttributes(graph.getAttributeList());
            graphData.mapOfDates = getMapOfDates(startDate, endDate);


            populateStatistics(graphData, filterAttributes, listResponse);

            listGraphData.add(graphData);
        }

        CompanyData companyData = new CompanyData();
        companyData.companyId = companyId;
        companyData.companyName = companyName;
        companyData.listGraphData = listGraphData;
        return companyData;
    }


    private int numberOfConstraints(List<Attribute> filterAttributes) {
        int constraints = 1;
        for (Attribute attribute : filterAttributes) {
            constraints *= attribute.getAttributeValues().size();
        }
        return constraints;
    }

    private void populateConstraintLevels(List<ConstraintLevelStatistics> listConstraintLevelStatistics, List<Attribute> filterAttributes, List<BranchDbType> branches) {
        Map<String, Integer> mapConstraints = new HashMap<String, Integer>();

        for (BranchDbType branch : branches) {
            mapConstraints.put("branch", branch.getId().getBranchId());
            populateConstraintLevels(listConstraintLevelStatistics, mapConstraints, filterAttributes, 0);
        }

    }

    private void populateConstraintLevels(List<ConstraintLevelStatistics> listConstraintLevelStatistics, Map<String, Integer> mapConstraints,
                                          List<Attribute> filterAttributes, int index) {
        Attribute attribute = filterAttributes.get(index);

        for (AttributeValue attributeValue : attribute.getAttributeValues()) {
            mapConstraints.put(attribute.getAttributeString(), attributeValue.getValue());

            if (index == filterAttributes.size() - 1) {
                //create a new MAP from mapConstraints so as to set in teh constraint.
                Map<String, Integer> newMap = createNewMap(mapConstraints);

                ConstraintLevelStatistics constraintLevelStatistics = new ConstraintLevelStatistics();
                constraintLevelStatistics.setConstraints(newMap);
                listConstraintLevelStatistics.add(constraintLevelStatistics);
            } else {
                populateConstraintLevels(listConstraintLevelStatistics, mapConstraints, filterAttributes, index + 1);
            }
        }
    }

    private void initialiseConstraintLevelStatistics(List<ConstraintLevelStatistics> listConstraintLevelStatistics, List<Attribute> weightedAttributes,
                                                     int startDate, int endDate, int noOfDays) {
        for (ConstraintLevelStatistics constraintLevelStatistics : listConstraintLevelStatistics) {
            List<AttributeLevelStatistics> listAttributeStatistics = new ArrayList<AttributeLevelStatistics>(weightedAttributes.size());
            constraintLevelStatistics.setAttributeLevelStatistics(listAttributeStatistics);

            initialiseAttributeLevelStatistics(listAttributeStatistics, weightedAttributes, startDate, endDate, noOfDays);
        }
    }

    private void initialiseAttributeLevelStatistics(List<AttributeLevelStatistics> listAttributeStatistics,
                                                    List<Attribute> weightedAttributes, int startDate, int endDate, int noOfDays) {
        for (Attribute attribute : weightedAttributes) {

            AttributeLevelStatistics attributeLevelStatistics = new AttributeLevelStatistics();

            attributeLevelStatistics.setAttributeId(attribute.getAttributeId());
            attributeLevelStatistics.setName(attribute.getAttributeString());
            attributeLevelStatistics.setListAttributeValue(attribute.getAttributeValues());

            List<DailyAttributeStatisticValues> listDailyAttributeStatisticValues = new ArrayList<DailyAttributeStatisticValues>(noOfDays);
            attributeLevelStatistics.setListDailyAttributeStatisticValues(listDailyAttributeStatisticValues);
            initialiseDailyValues(listDailyAttributeStatisticValues, startDate, endDate, attribute.getAttributeValues().size());

            listAttributeStatistics.add(attributeLevelStatistics);
        }
    }

    private void initialiseDailyValues(List<DailyAttributeStatisticValues> listDailyAttributeStatisticValues, int startDate, int endDate, int noOfValues) {
        int date = startDate;
        while (date <= endDate) {
            DailyAttributeStatisticValues dailyAttributeStatisticValues = new DailyAttributeStatisticValues();
            dailyAttributeStatisticValues.setDate(date);
            List<Integer> listCountPPL = new ArrayList<Integer>(noOfValues);
            for (int i = 0; i < noOfValues; i++) {
                listCountPPL.add(0);
            }
            dailyAttributeStatisticValues.setListCountPPL(listCountPPL);

            listDailyAttributeStatisticValues.add(dailyAttributeStatisticValues);

            date = FeedbackUtilities.nextDate(date);
        }
    }

    private Map<Map<String, Integer>, Integer> getMapOfConstraints(List<ConstraintLevelStatistics> listConstraintLevelStatistics) {
        Map<Map<String, Integer>, Integer> mapOfMaps = new HashMap<Map<String, Integer>, Integer>();
        int index = 0;
        for (ConstraintLevelStatistics constraintLevelStatistics : listConstraintLevelStatistics) {
            mapOfMaps.put(constraintLevelStatistics.getConstraints(), index);
            index++;
        }

        return mapOfMaps;
    }

    private Map<String, Integer> createNewMap(Map<String, Integer> map) {
        Map<String, Integer> newMap = new HashMap<String, Integer>(map.size());
        for (String key : map.keySet()) {
            newMap.put(key, map.get(key));
        }
        return newMap;
    }

    private Map<Integer, Integer> getMapOfAttributes(List<Integer> attributeIds) {
        Map<Integer, Integer> outMap = new HashMap<Integer, Integer>(attributeIds.size());
        int i = 0;
        for (int attributeId : attributeIds) {
            outMap.put(attributeId, i);
            i++;
        }
        return outMap;
    }

    private Map<Integer, Integer> getMapOfDates(int startDate, int endDate) {
        Map<Integer, Integer> outMap = new HashMap<Integer, Integer>();
        int i = 0;
        int date = startDate;
        while (date < endDate) {
            outMap.put(date, i);
            date = FeedbackUtilities.nextDate(date);
            i++;
        }
        return outMap;
    }


    private void populateStatistics(GraphData graphData,
                                    List<Attribute> filterAttributes,
                                    List<CustomerResponseDao.CustomerResponseAndValues> listResponse) {
        for (CustomerResponseDao.CustomerResponseAndValues response : listResponse) {
            Map<String, Integer> mapFilter = getMapConstraintsFromResponse(filterAttributes, response);
            int constraintIndex = graphData.mapOFConstraints.get(mapFilter);

            int date = FeedbackUtilities.dateFromCal(response.getResponse().getTimestamp());
            int dateIndex = graphData.mapOfDates.get(date);

            for (CustomerResponseValuesDbType responseValue : response.getResponseValues()) {
                if(graphData.mapOfAttributes.get(responseValue.getId().getAttributeId()) == null) continue;

                int attributeIndex = graphData.mapOfAttributes.get(responseValue.getId().getAttributeId());
                int attributeValueIndex = getAttributeValueIndex(graphData.graphLevelStatistics.getListConstraintLevelStatistics().get(constraintIndex).getAttributeLevelStatistics().get(attributeIndex).getListAttributeValue(), responseValue.getObtainedValue());
                List<Integer> countPPL = graphData.graphLevelStatistics.getListConstraintLevelStatistics().get(constraintIndex).getAttributeLevelStatistics().get(attributeIndex).getListDailyAttributeStatisticValues().get(dateIndex).getListCountPPL();
                countPPL.set(attributeValueIndex, countPPL.get(attributeValueIndex) + 1);
            }
        }
    }

    private int getAttributeValueIndex(List<AttributeValue> attributeValueList, int value) {
        int i = 0;
        for (AttributeValue attributeValue : attributeValueList) {
            if (value == attributeValue.getValue()) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private Map<String, Integer> getMapConstraintsFromResponse(List<Attribute> filterAttributes, CustomerResponseDao.CustomerResponseAndValues response) {
        Map<String, Integer> mapFilter = new HashMap<String, Integer>();
        mapFilter.put("branch", response.getResponse().getBranchId());
        for (CustomerResponseValuesDbType responseValue : response.getResponseValues()) {
            for (Attribute attribute : filterAttributes) {
                if (attribute.getAttributeId() == responseValue.getId().getAttributeId()) {
                    mapFilter.put(attribute.getAttributeString(), responseValue.getObtainedValue());
                    break;
                }
            }
        }
        return mapFilter;
    }

}

class GraphData {
    String graphId;

    Map<Map<String, Integer>, Integer> mapOFConstraints;
    Map<Integer, Integer> mapOfDates;
    Map<Integer, Integer> mapOfAttributes;

    GraphLevelStatistics graphLevelStatistics;
}

class CompanyData {
    int companyId;
    String companyName;

    List<GraphData> listGraphData;
}
