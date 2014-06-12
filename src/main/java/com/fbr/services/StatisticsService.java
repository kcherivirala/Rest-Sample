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
import com.fbr.Utilities.Comparators;
import com.fbr.Utilities.FeedbackUtilities;
import com.fbr.domain.Attribute.Attribute;
import com.fbr.domain.Attribute.AttributeValue;
import com.fbr.domain.Graph.Graph;
import com.fbr.domain.Statistic.*;
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

    private static int DATA_DAY_COUNT = 30;    //for trends API
    private static int DATA_MONTH_COUNT = 12;  //for trends API
    //for overview data we have last week last month and last year data.

    @PostConstruct
    public void init() {
        int date = FeedbackUtilities.dateFromCal(Calendar.getInstance());
        int month = FeedbackUtilities.monthFromDate(date);

        List<CompanyDbType> companies = companyDao.findAll();
        listCompanyData = new ArrayList<CompanyData>(companies.size());

        for (CompanyDbType company : companies) {
            listCompanyData.add(processPerCompanyResponses(company.getCompanyId(), company.getName(), date, month));
        }
    }

    public void resetCompanyData(int companyId) {
        int date = FeedbackUtilities.dateFromCal(Calendar.getInstance());
        int month = FeedbackUtilities.monthFromDate(date);

        CompanyDbType company = companyDao.find(companyId);
        CompanyData data = processPerCompanyResponses(company.getCompanyId(), company.getName(), date, month);

        int index = -1;
        for (int i = 0; i < listCompanyData.size(); i++) {
            if (listCompanyData.get(i).companyId == companyId) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            listCompanyData.add(data);
        } else {
            listCompanyData.remove(index);
            listCompanyData.add(data);
        }
    }

    public List<AttributeLevelStatistics> getGraphData(int companyId, String graphId, Map<String, Integer> mapOfFilters) {
        int index = getIndex(listCompanyData, companyId);
        CompanyData companyData = listCompanyData.get(index);

        index = getIndex(companyData.listGraphData, graphId);
        GraphData graphData = companyData.listGraphData.get(index);

        Graph graph = graphService.getGraph(graphId);

        List<AttributeLevelStatistics> listAttributeStatistics = new ArrayList<AttributeLevelStatistics>(graphData.mapOfAttributes.size());
        initialiseAttributeLevelStatistics(listAttributeStatistics, graph.getAttributeList(), FeedbackUtilities.dateFromCal(Calendar.getInstance()), graph.getType());
        populateFromGraphData(listAttributeStatistics, mapOfFilters, graphData);

        return listAttributeStatistics;
    }

    private void populateFromGraphData(List<AttributeLevelStatistics> listAttributeStatistics, Map<String, Integer> mapOfFilters, GraphData graphData) {
        for (ConstraintLevelStatistics constraintLevelStatistics : graphData.graphLevelStatistics.getListConstraintLevelStatistics()) {
            if (checkInclude(constraintLevelStatistics.getConstraints(), mapOfFilters)) {
                populateFromAttributeStatistics(listAttributeStatistics, constraintLevelStatistics.getAttributeLevelStatistics());
            }
        }
    }

    private boolean checkInclude(Map<String, Integer> map, Map<String, Integer> inputMap) {
        if (inputMap == null || inputMap.size() == 0) return true;


        for (String key : inputMap.keySet()) {
            if (!map.containsKey(key))
                return false;
            if (map.get(key) != inputMap.get(key))
                return false;
        }
        return true;
    }

    private void populateFromAttributeStatistics(List<AttributeLevelStatistics> out, List<AttributeLevelStatistics> in) {
        int i = 0;

        for (AttributeLevelStatistics inStatistics : in) {
            AttributeLevelStatistics outStatistics = out.get(i);

            copyFromDailyAttributeStatisticValues(outStatistics.getListDailyAttributeStatisticValues(), inStatistics.getListDailyAttributeStatisticValues());
            copyFromMonthlyAttributeLevelStatisticValues(outStatistics.getListMonthlyAttributeLevelStatisticValues(), inStatistics.getListMonthlyAttributeLevelStatisticValues());

            copyFromListCountPPl(outStatistics.getListCountPPl_7Days(), inStatistics.getListCountPPl_7Days());
            copyFromListCountPPl(outStatistics.getListCountPPl_30Days(), inStatistics.getListCountPPl_30Days());
            copyFromListCountPPl(outStatistics.getListCountPPl_365Days(), inStatistics.getListCountPPl_365Days());

            i++;
        }
    }

    private void copyFromDailyAttributeStatisticValues(List<DailyAttributeStatisticValues> outList, List<DailyAttributeStatisticValues> inList) {
        if (outList == null) return;

        for (int i = 0; i < outList.size(); i++) {
            DailyAttributeStatisticValues out = outList.get(i);
            DailyAttributeStatisticValues in = inList.get(i);

            for (int j = 0; j < out.getListCountPPL().size(); j++) {
                out.getListCountPPL().set(j, out.getListCountPPL().get(j) + in.getListCountPPL().get(j));
            }
        }
    }

    private void copyFromMonthlyAttributeLevelStatisticValues(List<MonthlyAttributeLevelStatisticValues> outList, List<MonthlyAttributeLevelStatisticValues> inList) {
        if (outList == null) return;

        for (int i = 0; i < outList.size(); i++) {
            MonthlyAttributeLevelStatisticValues out = outList.get(i);
            MonthlyAttributeLevelStatisticValues in = inList.get(i);

            for (int j = 0; j < out.getListCountPPL().size(); j++) {
                out.getListCountPPL().set(j, out.getListCountPPL().get(j) + in.getListCountPPL().get(j));
            }
        }
    }

    private void copyFromListCountPPl(List<Integer> outList, List<Integer> inList) {
        if (outList == null) return;

        for (int i = 0; i < outList.size(); i++) {
            outList.set(i, outList.get(i) + inList.get(i));
        }
    }

    public void addNewData() {
        List<CompanyDbType> companies = companyDao.findAll();

        for (CompanyDbType company : companies) {
            addNewData(company);
        }
    }

    private void addNewData(CompanyDbType company) {
        int index = -1;
        for (int i = 0; i < listCompanyData.size(); i++) {
            if (listCompanyData.get(i).companyId == company.getCompanyId()) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            resetCompanyData(company.getCompanyId());
        } else {
            CompanyData companyData = listCompanyData.get(index);
            Date currTimeStamp = companyData.lastUpdatedTimeStamp;
            List<CustomerResponseDao.CustomerResponseAndValues> listResponse = customerResponseDao.getResponses(company.getCompanyId(), currTimeStamp);
            Date lastTime = getLastDate(listResponse);
            addResponsesToCompanyData(companyData, listResponse);

            companyData.lastUpdatedTimeStamp = lastTime;
        }
    }

    private void addResponsesToCompanyData(CompanyData companyData, List<CustomerResponseDao.CustomerResponseAndValues> listResponse) {

    }

    private CompanyData processPerCompanyResponses(int companyId, String companyName, int currentDate, int currentMonth) {
        List<CustomerResponseDao.CustomerResponseAndValues> listResponse = customerResponseDao.getResponses(companyId);
        Collections.sort(listResponse, Comparators.COMPARE_RESPONSES);

        List<Attribute> listAttribute = attributeService.getAttributesByCompany(companyId);    //already sorted list
        List<BranchDbType> branches = companyService.getDbBranches(companyId);
        List<Graph> listGraph = graphService.getGraphs(companyId);

        List<GraphData> listGraphData = new ArrayList<GraphData>(listGraph.size());
        for (Graph graph : listGraph) {
            List<Attribute> filterAttributes = attributeService.getAttributesForIds(graph.getFilterList(), listAttribute);
            List<Attribute> weightedAttributes = attributeService.getAttributesForIds(graph.getAttributeList(), listAttribute);
            int constraints = numberOfConstraints(filterAttributes);

            List<ConstraintLevelStatistics> listConstraintLevelStatistics = new ArrayList<ConstraintLevelStatistics>(constraints * branches.size());
            populateConstraintLevels(listConstraintLevelStatistics, filterAttributes, branches);    //start with index of -1
            initialiseConstraintLevelStatistics(listConstraintLevelStatistics, weightedAttributes, currentDate, graph.getType());


            GraphLevelStatistics graphLevelStatistics = new GraphLevelStatistics();
            graphLevelStatistics.setGraphId(graph.getGraphId());
            graphLevelStatistics.setListConstraintLevelStatistics(listConstraintLevelStatistics);

            GraphData graphData = new GraphData();
            graphData.graphId = graph.getGraphId();
            graphData.type = graph.getType();
            graphData.graphLevelStatistics = graphLevelStatistics;
            graphData.mapOFConstraints = getMapOfConstraints(listConstraintLevelStatistics);
            graphData.mapOfAttributes = getMapOfAttributes(graph.getAttributeList());

            if (graph.getType().equals("trend")) {
                graphData.mapOfDates = getMapOfDates(currentDate);
                graphData.mapOfMonths = getMapOfMonths(currentMonth);
            }


            populateStatistics(graphData, filterAttributes, listResponse, graph.getType());

            listGraphData.add(graphData);
        }

        CompanyData companyData = new CompanyData();
        companyData.companyId = companyId;
        companyData.companyName = companyName;
        companyData.listGraphData = listGraphData;
        companyData.lastUpdatedTimeStamp = new Date();
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

        for (Attribute attribute : filterAttributes) {
            AttributeValue defaultAttrValue = new AttributeValue();
            defaultAttrValue.setMaxValue(-1);
            defaultAttrValue.setValue(-1);
            defaultAttrValue.setName("default");

            attribute.getAttributeValues().add(defaultAttrValue);
        }

        for (BranchDbType branch : branches) {
            mapConstraints.put("branch", branch.getId().getBranchId());
            populateConstraintLevels(listConstraintLevelStatistics, mapConstraints, filterAttributes, 0);
        }

        for (Attribute attribute : filterAttributes) {
            int index = attributeService.getAttributeValueIndex(attribute, -1);
            attribute.getAttributeValues().remove(index);
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
                                                     int currentDate, String graphType) {
        for (ConstraintLevelStatistics constraintLevelStatistics : listConstraintLevelStatistics) {
            List<AttributeLevelStatistics> listAttributeStatistics = new ArrayList<AttributeLevelStatistics>(weightedAttributes.size());
            constraintLevelStatistics.setAttributeLevelStatistics(listAttributeStatistics);

            initialiseAttributeLevelStatistics(listAttributeStatistics, weightedAttributes, currentDate, graphType);
        }
    }

    private void initialiseAttributeLevelStatistics(List<AttributeLevelStatistics> listAttributeStatistics,
                                                    List<Attribute> weightedAttributes, int currentDate, String graphType) {
        int currentMonth = FeedbackUtilities.monthFromDate(currentDate);
        for (Attribute attribute : weightedAttributes) {

            AttributeLevelStatistics attributeLevelStatistics = new AttributeLevelStatistics();

            attributeLevelStatistics.setAttributeId(attribute.getAttributeId());
            attributeLevelStatistics.setName(attribute.getAttributeString());
            attributeLevelStatistics.setListAttributeValue(attribute.getAttributeValues());

            if (graphType.equals("trend")) {
                List<DailyAttributeStatisticValues> listDailyAttributeStatisticValues = new ArrayList<DailyAttributeStatisticValues>(31);
                attributeLevelStatistics.setListDailyAttributeStatisticValues(listDailyAttributeStatisticValues);
                initialiseDailyValues(listDailyAttributeStatisticValues, currentDate, attribute.getAttributeValues().size());

                List<MonthlyAttributeLevelStatisticValues> listMonthlyAttributeLevelStatisticValues = new ArrayList<MonthlyAttributeLevelStatisticValues>(12);
                attributeLevelStatistics.setListMonthlyAttributeLevelStatisticValues(listMonthlyAttributeLevelStatisticValues);
                initialiseMonthlyValues(listMonthlyAttributeLevelStatisticValues, currentMonth, attribute.getAttributeValues().size());
            } else {
                List<Integer> listCountPPl_7Days = new ArrayList<Integer>(attribute.getAttributeValues().size());
                List<Integer> listCountPPl_30Days = new ArrayList<Integer>(attribute.getAttributeValues().size());
                List<Integer> listCountPPl_365Days = new ArrayList<Integer>(attribute.getAttributeValues().size());

                attributeLevelStatistics.setListCountPPl_7Days(listCountPPl_7Days);
                attributeLevelStatistics.setListCountPPl_30Days(listCountPPl_30Days);
                attributeLevelStatistics.setListCountPPl_365Days(listCountPPl_365Days);

                initialiseAttributeStatisticValues(listCountPPl_7Days, listCountPPl_30Days, listCountPPl_365Days, attribute.getAttributeValues().size());
            }

            listAttributeStatistics.add(attributeLevelStatistics);
        }
    }

    private void initialiseAttributeStatisticValues(List<Integer> listCountPPl_7Days, List<Integer> listCountPPl_30Days, List<Integer> listCountPPl_365Days, int count) {
        for (int i = 0; i < count; i++) {
            listCountPPl_7Days.add(0);
            listCountPPl_30Days.add(0);
            listCountPPl_365Days.add(0);
        }
    }

    private void initialiseDailyValues(List<DailyAttributeStatisticValues> listDailyAttributeStatisticValues, int currentDate, int noOfValues) {
        int number = -1 * DATA_DAY_COUNT;
        int date = FeedbackUtilities.addToDate(currentDate, number);  //startDate

        while (date <= currentDate) {
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

    private void initialiseMonthlyValues(List<MonthlyAttributeLevelStatisticValues> listMonthlyAttributeLevelStatisticValues, int currentMonth, int noOfValues) {
        int month = FeedbackUtilities.addToMonth(currentMonth, -DATA_MONTH_COUNT);

        while (month <= currentMonth) {
            MonthlyAttributeLevelStatisticValues monthlyAttributeLevelStatisticValues = new MonthlyAttributeLevelStatisticValues();
            monthlyAttributeLevelStatisticValues.setMonth(month);
            List<Integer> listCountPPL = new ArrayList<Integer>(noOfValues);
            for (int i = 0; i < noOfValues; i++) {
                listCountPPL.add(0);
            }
            monthlyAttributeLevelStatisticValues.setListCountPPL(listCountPPL);

            listMonthlyAttributeLevelStatisticValues.add(monthlyAttributeLevelStatisticValues);

            month = FeedbackUtilities.nextMonth(month);
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

    private Map<Integer, Integer> getMapOfAttributes(List<Attribute> attributes) {
        Map<Integer, Integer> outMap = new HashMap<Integer, Integer>(attributes.size());
        int i = 0;
        for (Attribute attribute : attributes) {
            outMap.put(attribute.getAttributeId(), i);
            i++;
        }
        return outMap;
    }

    private Map<Integer, Integer> getMapOfDates(int currentDate) {
        Map<Integer, Integer> outMap = new HashMap<Integer, Integer>();
        int i = 0;
        int date = FeedbackUtilities.addToDate(currentDate, -DATA_DAY_COUNT);
        while (date <= currentDate) {
            outMap.put(date, i);
            date = FeedbackUtilities.nextDate(date);
            i++;
        }
        return outMap;
    }

    private Map<Integer, Integer> getMapOfMonths(int currentMonth) {
        Map<Integer, Integer> outMap = new HashMap<Integer, Integer>();
        int i = 0;
        int month = FeedbackUtilities.addToMonth(currentMonth, -DATA_MONTH_COUNT);
        while (month <= currentMonth) {
            outMap.put(month, i);
            month = FeedbackUtilities.nextMonth(month);
            i++;
        }
        return outMap;
    }

    private Date getLastDate(List<CustomerResponseDao.CustomerResponseAndValues> listResponse) {
        if (listResponse.size() == 0) return null;

        Date last = listResponse.get(0).getResponse().getTimestamp();
        for (CustomerResponseDao.CustomerResponseAndValues response : listResponse) {
            if (last.after(response.getResponse().getTimestamp()))
                last = response.getResponse().getTimestamp();
        }
        return last;
    }

    private void populateStatistics(GraphData graphData,
                                    List<Attribute> filterAttributes,
                                    List<CustomerResponseDao.CustomerResponseAndValues> listResponse, String graphType) {
        if (graphType.equals("trend"))
            populateFilterGraphStatistics(graphData, filterAttributes, listResponse);
        else if (graphType.equals("normal"))
            populateNormalGraphStatistics(graphData, filterAttributes, listResponse);
    }

    private void populateFilterGraphStatistics(GraphData graphData,
                                               List<Attribute> filterAttributes,
                                               List<CustomerResponseDao.CustomerResponseAndValues> listResponse) {
        for (CustomerResponseDao.CustomerResponseAndValues response : listResponse) {
            Map<String, Integer> mapFilter = getMapConstraintsFromResponse(filterAttributes, response);
            int constraintIndex = graphData.mapOFConstraints.get(mapFilter);

            int date = FeedbackUtilities.dateFromCal(response.getResponse().getTimestamp());
            int month = FeedbackUtilities.monthFromDate(date);

            if (graphData.mapOfDates.get(date) == null || graphData.mapOfMonths.get(month) == null)
                continue;

            int dateIndex = graphData.mapOfDates.get(date);
            int monthIndex = graphData.mapOfMonths.get(month);

            for (CustomerResponseValuesDbType responseValue : response.getResponseValues()) {
                if (graphData.mapOfAttributes.get(responseValue.getId().getAttributeId()) == null) continue;

                int attributeIndex = graphData.mapOfAttributes.get(responseValue.getId().getAttributeId());
                int attributeValueIndex = getAttributeValueIndex(graphData.graphLevelStatistics.getListConstraintLevelStatistics().get(constraintIndex).getAttributeLevelStatistics().get(attributeIndex).getListAttributeValue(), responseValue.getObtainedValue());

                List<Integer> countPPL_daily = graphData.graphLevelStatistics.getListConstraintLevelStatistics().get(constraintIndex).getAttributeLevelStatistics().get(attributeIndex).getListDailyAttributeStatisticValues().get(dateIndex).getListCountPPL();
                countPPL_daily.set(attributeValueIndex, countPPL_daily.get(attributeValueIndex) + 1);

                List<Integer> countPPL_Monthly = graphData.graphLevelStatistics.getListConstraintLevelStatistics().get(constraintIndex).getAttributeLevelStatistics().get(attributeIndex).getListMonthlyAttributeLevelStatisticValues().get(monthIndex).getListCountPPL();
                countPPL_Monthly.set(attributeValueIndex, countPPL_Monthly.get(attributeValueIndex) + 1);
            }
        }
    }

    private void populateNormalGraphStatistics(GraphData graphData,
                                               List<Attribute> filterAttributes,
                                               List<CustomerResponseDao.CustomerResponseAndValues> listResponse) {
        int currentDate = FeedbackUtilities.dateFromCal(Calendar.getInstance());

        for (CustomerResponseDao.CustomerResponseAndValues response : listResponse) {
            Map<String, Integer> mapFilter = getMapConstraintsFromResponse(filterAttributes, response);
            int constraintIndex = graphData.mapOFConstraints.get(mapFilter);

            int date = FeedbackUtilities.dateFromCal(response.getResponse().getTimestamp());

            for (CustomerResponseValuesDbType responseValue : response.getResponseValues()) {
                if (graphData.mapOfAttributes.get(responseValue.getId().getAttributeId()) == null) continue;

                int attributeIndex = graphData.mapOfAttributes.get(responseValue.getId().getAttributeId());
                int attributeValueIndex = getAttributeValueIndex(graphData.graphLevelStatistics.getListConstraintLevelStatistics().get(constraintIndex).getAttributeLevelStatistics().get(attributeIndex).getListAttributeValue(), responseValue.getObtainedValue());

                long differenceInDate = FeedbackUtilities.differenceInDates(currentDate, date);
                if (differenceInDate <= 7) {
                    List<Integer> countPPL = graphData.graphLevelStatistics.getListConstraintLevelStatistics().get(constraintIndex).getAttributeLevelStatistics().get(attributeIndex).getListCountPPl_7Days();
                    countPPL.set(attributeValueIndex, countPPL.get(attributeValueIndex) + 1);
                }
                if (differenceInDate <= 30) {
                    List<Integer> countPPL = graphData.graphLevelStatistics.getListConstraintLevelStatistics().get(constraintIndex).getAttributeLevelStatistics().get(attributeIndex).getListCountPPl_30Days();
                    countPPL.set(attributeValueIndex, countPPL.get(attributeValueIndex) + 1);
                }
                if (differenceInDate <= 365) {
                    List<Integer> countPPL = graphData.graphLevelStatistics.getListConstraintLevelStatistics().get(constraintIndex).getAttributeLevelStatistics().get(attributeIndex).getListCountPPl_365Days();
                    countPPL.set(attributeValueIndex, countPPL.get(attributeValueIndex) + 1);
                }
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

        for (Attribute attribute : filterAttributes) {
            mapFilter.put(attribute.getAttributeString(), -1);
        }

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

    class GraphData {
        String graphId;
        String type;

        Map<Map<String, Integer>, Integer> mapOFConstraints;
        Map<Integer, Integer> mapOfDates;
        Map<Integer, Integer> mapOfMonths;
        Map<Integer, Integer> mapOfAttributes;

        GraphLevelStatistics graphLevelStatistics;
    }

    class CompanyData {
        int companyId;
        String companyName;
        Date lastUpdatedTimeStamp;

        List<GraphData> listGraphData;
    }

    private static int getIndex(List<CompanyData> listCompanyData, int companyId) {
        int i = 0;
        for (CompanyData companyData : listCompanyData) {
            if (companyData.companyId == companyId)
                return i;
            i++;
        }
        return -1;
    }

    private static int getIndex(List<GraphData> listGraphData, String graphId) {
        int i = 0;
        for (GraphData graphData : listGraphData) {
            if (graphData.graphId.equals(graphId))
                return i;
            i++;
        }
        return -1;
    }
}


